package com.eight64zeros.clearstreak.model

/**
 * A daily scripture verse (public-domain KJV). [emotion] is a HALT-aligned tag
 * (STRESSED / LONELY / TIRED / HOPELESS / ANGRY / GENERAL) for future
 * mood-matched surfacing; [day] is its 1–365 slot in the perpetual calendar.
 */
data class DailyVerse(
    val day: Int,
    val emotion: String,
    val citation: String,
    val text: String
)
