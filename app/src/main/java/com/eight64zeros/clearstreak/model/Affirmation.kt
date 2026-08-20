package com.eight64zeros.clearstreak.model

/**
 * A short in-app affirmation, shown as a gentle encouragement in the Home bell banner
 * (e.g. "You are awesome no matter what happens today.").
 *
 * [faith] gates Bible-derived lines behind the faith-reflections setting.
 * [sourceLabel]/[reference] are provenance only (e.g. "Alcoholics Anonymous (1939)" + "p.86",
 * or "Psalms" + "46:1", or "864zeros original" + "").
 */
data class Affirmation(
    val id: String,
    val text: String,
    val faith: Boolean,
    val sourceLabel: String,
    val reference: String
) {
    /** Human-readable provenance (not shown on the banner; kept for a future browse view). */
    val citation: String
        get() = listOf(sourceLabel, reference).filter { it.isNotBlank() }.joinToString(", ")
}
