package com.eight64zeros.clearstreak.model

/**
 * A short in-app affirmation, shown as a gentle encouragement in the Home bell banner.
 *
 * Two pillars, all original 864zeros writing derived from citable public-domain sources:
 *  - [PILLAR_RECOVERY]: secular, technique-aligned; derived from the 1939 Big Book (PD).
 *    Always shown.
 *  - [PILLAR_SPIRITUAL]: devotional; derived from the KJV (PD). Shown only when the user has
 *    faith reflections enabled.
 *
 * [citation] is display provenance; [scriptureText] is the exact KJV verse (spiritual only).
 */
data class Affirmation(
    val id: String,
    val text: String,
    val pillar: String,
    val citation: String,
    val scriptureText: String? = null
) {
    val isFaith: Boolean get() = pillar == PILLAR_SPIRITUAL

    companion object {
        const val PILLAR_RECOVERY = "recovery"
        const val PILLAR_SPIRITUAL = "spiritual"
    }
}
