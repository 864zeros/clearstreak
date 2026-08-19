package com.eight64zeros.clearstreak.game

import kotlin.random.Random

// Non-canonical well (deliberately not 10x20) — trade-dress differentiation.
const val BD_WIDTH = 8
const val BD_HEIGHT = 16

data class Cell(val r: Int, val c: Int)

// Polyomino shapes are uncopyrightable math; rendering is monochrome (see board).
private val PIECES: List<List<Cell>> = listOf(
    listOf(Cell(0, 0), Cell(0, 1), Cell(0, 2), Cell(0, 3)), // I
    listOf(Cell(0, 0), Cell(0, 1), Cell(1, 0), Cell(1, 1)), // O
    listOf(Cell(0, 0), Cell(0, 1), Cell(0, 2), Cell(1, 1)), // T
    listOf(Cell(0, 1), Cell(0, 2), Cell(1, 0), Cell(1, 1)), // S
    listOf(Cell(0, 0), Cell(0, 1), Cell(1, 1), Cell(1, 2)), // Z
    listOf(Cell(0, 0), Cell(1, 0), Cell(1, 1), Cell(1, 2)), // J
    listOf(Cell(0, 2), Cell(1, 0), Cell(1, 1), Cell(1, 2))  // L
)

/**
 * Pure, self-contained falling-block engine. No Android or ClearStreak
 * dependencies — portable to a standalone game app. Immutable state; the UI
 * drives gravity by calling [step] on a timer.
 */
data class BlockDropState(
    val grid: List<List<Int>>,
    val piece: List<Cell>,
    val pos: Cell,
    val score: Int = 0,
    val lines: Int = 0,
    val over: Boolean = false
) {
    companion object {
        fun new(random: Random = Random.Default): BlockDropState {
            val empty = List(BD_HEIGHT) { List(BD_WIDTH) { 0 } }
            return spawn(BlockDropState(empty, emptyList(), Cell(0, 0)), random)
        }
    }
}

/** Absolute grid cells occupied by the active piece (for rendering + tests). */
fun BlockDropState.activeCells(): List<Cell> = piece.map { Cell(it.r + pos.r, it.c + pos.c) }

fun BlockDropState.moveLeft(): BlockDropState = tryMove(Cell(pos.r, pos.c - 1))
fun BlockDropState.moveRight(): BlockDropState = tryMove(Cell(pos.r, pos.c + 1))

fun BlockDropState.rotate(): BlockDropState {
    if (over) return this
    val rotated = rotateCw(piece)
    for (dc in intArrayOf(0, -1, 1, -2, 2)) { // simple wall kicks
        val p = Cell(pos.r, pos.c + dc)
        if (!collides(grid, absolute(rotated, p))) return copy(piece = rotated, pos = p)
    }
    return this
}

/** One gravity tick: fall one row, or lock + clear + spawn if blocked. */
fun BlockDropState.step(random: Random = Random.Default): BlockDropState {
    if (over) return this
    val down = Cell(pos.r + 1, pos.c)
    return if (!collides(grid, absolute(piece, down))) copy(pos = down) else lockAndSpawn(random)
}

fun BlockDropState.hardDrop(random: Random = Random.Default): BlockDropState {
    if (over) return this
    var p = pos
    while (!collides(grid, absolute(piece, Cell(p.r + 1, p.c)))) p = Cell(p.r + 1, p.c)
    return copy(pos = p).lockAndSpawn(random)
}

private fun BlockDropState.tryMove(newPos: Cell): BlockDropState {
    if (over) return this
    return if (!collides(grid, absolute(piece, newPos))) copy(pos = newPos) else this
}

private fun BlockDropState.lockAndSpawn(random: Random): BlockDropState {
    val newGrid = grid.map { it.toMutableList() }
    for (cell in absolute(piece, pos)) {
        if (cell.r in 0 until BD_HEIGHT && cell.c in 0 until BD_WIDTH) newGrid[cell.r][cell.c] = 1
    }
    val remaining = newGrid.filter { row -> row.any { it == 0 } }
    val cleared = BD_HEIGHT - remaining.size
    val rebuilt = buildList {
        repeat(cleared) { add(List(BD_WIDTH) { 0 }) }
        addAll(remaining)
    }
    val gained = when (cleared) { 0 -> 0; 1 -> 100; 2 -> 300; 3 -> 500; else -> 800 }
    return spawn(copy(grid = rebuilt, score = score + gained, lines = lines + cleared), random)
}

private fun rotateCw(cells: List<Cell>): List<Cell> {
    val maxR = cells.maxOf { it.r }
    return normalize(cells.map { Cell(it.c, maxR - it.r) })
}

private fun normalize(cells: List<Cell>): List<Cell> {
    val minR = cells.minOf { it.r }
    val minC = cells.minOf { it.c }
    return cells.map { Cell(it.r - minR, it.c - minC) }
}

private fun absolute(piece: List<Cell>, pos: Cell): List<Cell> =
    piece.map { Cell(it.r + pos.r, it.c + pos.c) }

private fun collides(grid: List<List<Int>>, cells: List<Cell>): Boolean {
    for (cell in cells) {
        if (cell.c < 0 || cell.c >= BD_WIDTH || cell.r >= BD_HEIGHT) return true
        if (cell.r >= 0 && grid[cell.r][cell.c] != 0) return true
    }
    return false
}

private fun spawn(state: BlockDropState, random: Random): BlockDropState {
    val piece = normalize(PIECES[random.nextInt(PIECES.size)])
    val width = piece.maxOf { it.c } + 1
    val pos = Cell(0, (BD_WIDTH - width) / 2)
    val over = state.over || collides(state.grid, absolute(piece, pos))
    return state.copy(piece = piece, pos = pos, over = over)
}
