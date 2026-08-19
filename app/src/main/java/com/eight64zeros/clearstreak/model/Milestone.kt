package com.eight64zeros.clearstreak.model

enum class MilestoneTier { DAILY, WEEKLY, MONTHLY, YEARLY }

/**
 * A recovery milestone. Achievements are permanent (Data Over Shame): once the
 * personal-best streak reaches [days], the badge is earned and never revoked.
 */
data class Milestone(
    val days: Long,
    val name: String,
    val tier: MilestoneTier,
    val badge: String
)
