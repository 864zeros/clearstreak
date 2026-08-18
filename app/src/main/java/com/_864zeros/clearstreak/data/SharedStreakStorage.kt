package com._864zeros.clearstreak.data

import android.content.Context
import android.content.SharedPreferences

class SharedStreakStorage(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun savePrimaryStreakData(
        journeyTitle: String,
        currentStreakDays: Long,
        nextMilestoneName: String,
        nextMilestoneDays: Long
    ) {
        prefs.edit()
            .putString(KEY_PRIMARY_TITLE, journeyTitle)
            .putLong(KEY_PRIMARY_STREAK_DAYS, currentStreakDays)
            .putString(KEY_NEXT_MILESTONE_NAME, nextMilestoneName)
            .putLong(KEY_NEXT_MILESTONE_DAYS, nextMilestoneDays)
            .putLong(KEY_LAST_UPDATED, System.currentTimeMillis())
            .apply()
    }

    fun getPrimaryStreakDays(): Long = prefs.getLong(KEY_PRIMARY_STREAK_DAYS, 0L)
    fun getPrimaryTitle(): String = prefs.getString(KEY_PRIMARY_TITLE, "My Recovery") ?: "My Recovery"
    fun getNextMilestoneName(): String = prefs.getString(KEY_NEXT_MILESTONE_NAME, "1 Week") ?: "1 Week"
    fun getNextMilestoneDays(): Long = prefs.getLong(KEY_NEXT_MILESTONE_DAYS, 7L)

    companion object {
        private const val PREFS_NAME = "clearstreak_widget_shared_prefs"
        private const val KEY_PRIMARY_TITLE = "primary_title"
        private const val KEY_PRIMARY_STREAK_DAYS = "primary_streak_days"
        private const val KEY_NEXT_MILESTONE_NAME = "next_milestone_name"
        private const val KEY_NEXT_MILESTONE_DAYS = "next_milestone_days"
        private const val KEY_LAST_UPDATED = "last_updated"
    }
}
