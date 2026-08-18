package com._864zeros.clearstreak.model

data class StreakStats(
    val currentStreakDays: Long,
    val currentStreakHours: Long,
    val longestStreakDays: Long,
    val cumulativeDaysClear: Long,
    val totalMoneySaved: Double,
    val nextMilestoneDays: Long,
    val milestoneProgress: Float, // 0.0f to 1.0f
    val nextMilestoneName: String
)
