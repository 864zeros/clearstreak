package com.eight64zeros.clearstreak.game

import kotlin.random.Random

/**
 * Pure, self-contained sequence-memory engine (the "Pattern Echo" game). No
 * Android or ClearStreak dependencies — portable to a standalone game app.
 * Timing/animation is a UI concern; this holds only the logic.
 */
data class PatternEchoState(
    val sequence: List<Int>,   // quadrant indices 0..3
    val inputIndex: Int = 0,    // correct inputs so far this round
    val round: Int = 1,         // = sequence.size
    val over: Boolean = false
) {
    companion object {
        const val PADS = 4
        fun new(random: Random = Random.Default): PatternEchoState =
            PatternEchoState(sequence = listOf(random.nextInt(PADS)))
    }
}

/**
 * Registers a pad tap during the input phase. A wrong pad ends the game; the
 * final correct tap of a round extends the sequence and advances the round.
 */
fun PatternEchoState.tap(pad: Int, random: Random = Random.Default): PatternEchoState {
    if (over) return this
    if (pad != sequence[inputIndex]) {
        return copy(over = true)
    }
    val nextIndex = inputIndex + 1
    return if (nextIndex >= sequence.size) {
        copy(
            sequence = sequence + random.nextInt(PatternEchoState.PADS),
            inputIndex = 0,
            round = round + 1
        )
    } else {
        copy(inputIndex = nextIndex)
    }
}
