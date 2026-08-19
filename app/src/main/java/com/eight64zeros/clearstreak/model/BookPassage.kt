package com.eight64zeros.clearstreak.model

/**
 * A moment-of-need passage — original 864zeros content re-authored from the
 * public-domain 1939 Alcoholics Anonymous. [surfaceText] is the primary
 * (plain 2026) rendering; [readerText] is a light-touch alternate voice.
 * [source*] fields are provenance/citation only (no verbatim reader ships).
 */
data class BookPassage(
    val id: String,
    val readerText: String,
    val surfaceText: String,
    val faithOptional: String?,
    val moments: List<String>,
    val halt: String,
    val urgeLevel: String,
    val stage: String,
    val function: String,
    val appliesTo: List<String>,
    val readingTime: String,
    val chapterTitle: String,
    val page: Int
) {
    /** Human-readable citation to the public-domain source. */
    val citation: String
        get() = "Adapted from Alcoholics Anonymous (1939)" +
            (if (chapterTitle.isNotBlank()) ", $chapterTitle" else "") +
            (if (page > 0) ", p.$page" else "")
}
