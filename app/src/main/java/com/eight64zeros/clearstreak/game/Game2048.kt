package com.eight64zeros.clearstreak.game

import kotlin.random.Random

const val BOARD_SIZE = 4

enum class SwipeDir { LEFT, RIGHT, UP, DOWN }

/**
 * Pure, self-contained 2048 engine. No Android or ClearStreak dependencies — the
 * game core is portable and could be lifted into a standalone game app. Every
 * operation returns a new immutable state.
 */
data class Game2048State(
    val grid: List<List<Int>>,
    val score: Int = 0,
    val won: Boolean = false,
    val over: Boolean = false
) {
    companion object {
        fun new(random: Random = Random.Default): Game2048State {
            val empty = List(BOARD_SIZE) { List(BOARD_SIZE) { 0 } }
            return Game2048State(empty).spawn(random).spawn(random)
        }
    }
}

/** Places a 2 (90%) or 4 (10%) on a random empty cell. */
fun Game2048State.spawn(random: Random = Random.Default): Game2048State {
    val empties = buildList {
        for (r in 0 until BOARD_SIZE) for (c in 0 until BOARD_SIZE) if (grid[r][c] == 0) add(r to c)
    }
    if (empties.isEmpty()) return this
    val (r, c) = empties[random.nextInt(empties.size)]
    val value = if (random.nextInt(10) == 0) 4 else 2
    val newGrid = grid.mapIndexed { ri, row ->
        row.mapIndexed { ci, v -> if (ri == r && ci == c) value else v }
    }
    return copy(grid = newGrid)
}

/** Slides + merges in [dir]; spawns a new tile if the grid changed. */
fun Game2048State.moved(dir: SwipeDir, random: Random = Random.Default): Game2048State {
    val oriented = when (dir) {
        SwipeDir.LEFT -> grid
        SwipeDir.RIGHT -> grid.map { it.reversed() }
        SwipeDir.UP -> transpose(grid)
        SwipeDir.DOWN -> transpose(grid).map { it.reversed() }
    }
    var gained = 0
    val slid = oriented.map { row ->
        val (newRow, g) = slideLeft(row)
        gained += g
        newRow
    }
    val newGrid = when (dir) {
        SwipeDir.LEFT -> slid
        SwipeDir.RIGHT -> slid.map { it.reversed() }
        SwipeDir.UP -> transpose(slid)
        SwipeDir.DOWN -> transpose(slid.map { it.reversed() })
    }
    if (newGrid == grid) return this
    val spawned = copy(
        grid = newGrid,
        score = score + gained,
        won = won || newGrid.any { row -> row.any { it >= 2048 } }
    ).spawn(random)
    return spawned.copy(over = !spawned.hasMoves())
}

/** True if any empty cell or any adjacent equal pair remains. */
fun Game2048State.hasMoves(): Boolean {
    for (r in 0 until BOARD_SIZE) for (c in 0 until BOARD_SIZE) {
        if (grid[r][c] == 0) return true
        if (c + 1 < BOARD_SIZE && grid[r][c] == grid[r][c + 1]) return true
        if (r + 1 < BOARD_SIZE && grid[r][c] == grid[r + 1][c]) return true
    }
    return false
}

private fun slideLeft(row: List<Int>): Pair<List<Int>, Int> {
    val nums = row.filter { it != 0 }
    val merged = mutableListOf<Int>()
    var gained = 0
    var i = 0
    while (i < nums.size) {
        if (i + 1 < nums.size && nums[i] == nums[i + 1]) {
            val v = nums[i] * 2
            merged.add(v)
            gained += v
            i += 2
        } else {
            merged.add(nums[i])
            i += 1
        }
    }
    while (merged.size < BOARD_SIZE) merged.add(0)
    return merged to gained
}

private fun transpose(g: List<List<Int>>): List<List<Int>> =
    List(g.size) { c -> List(g.size) { r -> g[r][c] } }
