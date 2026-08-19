package com.eight64zeros.clearstreak.data

import com.eight64zeros.clearstreak.model.CheckIn
import com.eight64zeros.clearstreak.model.Journey
import com.eight64zeros.clearstreak.model.Milestone
import com.eight64zeros.clearstreak.model.MilestoneTier
import com.eight64zeros.clearstreak.model.StreakStats
import kotlin.math.max

object StreakCalculator {

    val MILESTONES = listOf(
        Milestone(1L, "Day 1", MilestoneTier.DAILY, "🌱"),
        Milestone(3L, "3 Days", MilestoneTier.DAILY, "🌿"),
        Milestone(7L, "1 Week", MilestoneTier.WEEKLY, "🥉"),
        Milestone(14L, "2 Weeks", MilestoneTier.WEEKLY, "🎗️"),
        Milestone(30L, "1 Month", MilestoneTier.MONTHLY, "🥈"),
        Milestone(60L, "2 Months", MilestoneTier.MONTHLY, "🎖️"),
        Milestone(90L, "3 Months", MilestoneTier.MONTHLY, "🏅"),
        Milestone(180L, "6 Months", MilestoneTier.MONTHLY, "🌟"),
        Milestone(365L, "1 Year", MilestoneTier.YEARLY, "🥇"),
        Milestone(500L, "500 Days", MilestoneTier.YEARLY, "💠"),
        Milestone(730L, "2 Years", MilestoneTier.YEARLY, "🏆"),
        Milestone(1000L, "1,000 Days", MilestoneTier.YEARLY, "💎"),
        Milestone(1825L, "5 Years", MilestoneTier.YEARLY, "👑")
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
        val nextMilestone = MILESTONES.firstOrNull { it.days > currentStreakDays }
            ?: Milestone(currentStreakDays + 30L, "${currentStreakDays + 30L} Days", MilestoneTier.YEARLY, "🏆")

        val prevMilestoneDays = MILESTONES.lastOrNull { it.days <= currentStreakDays }?.days ?: 0L
        val milestoneSpan = nextMilestone.days - prevMilestoneDays
        val milestoneProgress = if (milestoneSpan > 0) {
            ((currentStreakDays - prevMilestoneDays).toFloat() / milestoneSpan.toFloat()).coerceIn(0.0f, 1.0f)
        } else {
            1.0f
        }

        // Achievements are permanent (Data Over Shame): earned once the
        // personal-best streak has reached the milestone, never revoked on a slip.
        val achievedMilestones = MILESTONES.filter { it.days <= longestStreakDays }

        return StreakStats(
            currentStreakDays = currentStreakDays,
            currentStreakHours = currentStreakHours,
            longestStreakDays = longestStreakDays,
            cumulativeDaysClear = cumulativeDaysClear,
            totalMoneySaved = totalMoneySaved,
            nextMilestoneDays = nextMilestone.days,
            milestoneProgress = milestoneProgress,
            nextMilestoneName = nextMilestone.name,
            achievedMilestones = achievedMilestones
        )
    }

    /**
     * Non-shaming framing shown around a slip/reset. The personal record and
     * cumulative history are permanent, so the copy never uses "failure" or "lost".
     */
    fun slipFraming(stats: StreakStats): String {
        val record = stats.longestStreakDays
        val current = stats.currentStreakDays
        return when {
            record <= 0L -> "Every fresh start counts. Your journey begins now."
            current == 0L -> "Your record of $record ${dayWord(record)} is safe — it can never be erased. A new start begins now."
            current > record -> "New personal best: $current ${dayWord(current)} and climbing."
            else -> {
                val toBeat = record - current + 1
                "Your record is $record ${dayWord(record)}. You're $current in — $toBeat to beat it."
            }
        }
    }

    private fun dayWord(n: Long): String = if (n == 1L) "day" else "days"
}
