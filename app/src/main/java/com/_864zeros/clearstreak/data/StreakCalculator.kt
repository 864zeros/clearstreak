package com._864zeros.clearstreak.data

import com._864zeros.clearstreak.model.CheckIn
import com._864zeros.clearstreak.model.Journey
import com._864zeros.clearstreak.model.StreakStats
import kotlin.math.max

object StreakCalculator {

    val MILESTONES = listOf(
        1L to "Day 1 (24 Hours)",
        3L to "3 Days (72 Hours)",
        7L to "1 Week",
        14L to "2 Weeks",
        30L to "1 Month (30 Days)",
        60L to "2 Months",
        90L to "3 Months (90 Days)",
        180L to "6 Months",
        365L to "1 Year (365 Days)",
        500L to "500 Days",
        730L to "2 Years",
        1000L to "1,000 Days",
        1825L to "5 Years"
    )

    fun calculateStats(
        journey: Journey,
        checkIns: List<CheckIn>,
        currentEpochSeconds: Long = System.currentTimeMillis() / 1000
    ): StreakStats {
        val sortedSlips = checkIns
            .filter { it.journeyId == journey.id && it.isSlip }
            .map { it.timestamp }
            .sorted()

        // Active streak starts either at journey.startTimestamp or the latest slip timestamp
        val activeStreakStart = if (sortedSlips.isNotEmpty()) {
            sortedSlips.last()
        } else {
            journey.startTimestamp
        }

        val currentSecondsElapsed = max(0L, currentEpochSeconds - activeStreakStart)
        val currentStreakDays = currentSecondsElapsed / 86400
        val currentStreakHours = (currentSecondsElapsed % 86400) / 3600

        // Calculate all historical segments to determine Longest Streak & Cumulative Days
        val boundaries = mutableListOf(journey.startTimestamp)
        boundaries.addAll(sortedSlips)
        boundaries.add(currentEpochSeconds)

        var longestStreakSeconds = 0L
        var totalSoberSeconds = 0L

        for (i in 0 until boundaries.size - 1) {
            val start = boundaries[i]
            val end = boundaries[i + 1]
            if (end > start) {
                val duration = end - start
                longestStreakSeconds = max(longestStreakSeconds, duration)
                totalSoberSeconds += duration
            }
        }

        val longestStreakDays = longestStreakSeconds / 86400
        val cumulativeDaysClear = totalSoberSeconds / 86400

        val totalMoneySaved = (totalSoberSeconds.toDouble() / 86400.0) * journey.dailyCostSavings

        // Milestone calculation
        val nextMilestone = MILESTONES.firstOrNull { it.first > currentStreakDays }
            ?: (currentStreakDays + 30L to "${currentStreakDays + 30L} Days")

        val prevMilestoneDays = MILESTONES.lastOrNull { it.first <= currentStreakDays }?.first ?: 0L
        val milestoneSpan = nextMilestone.first - prevMilestoneDays
        val milestoneProgress = if (milestoneSpan > 0) {
            ((currentStreakDays - prevMilestoneDays).toFloat() / milestoneSpan.toFloat()).coerceIn(0.0f, 1.0f)
        } else {
            1.0f
        }

        return StreakStats(
            currentStreakDays = currentStreakDays,
            currentStreakHours = currentStreakHours,
            longestStreakDays = longestStreakDays,
            cumulativeDaysClear = cumulativeDaysClear,
            totalMoneySaved = totalMoneySaved,
            nextMilestoneDays = nextMilestone.first,
            milestoneProgress = milestoneProgress,
            nextMilestoneName = nextMilestone.second
        )
    }
}
