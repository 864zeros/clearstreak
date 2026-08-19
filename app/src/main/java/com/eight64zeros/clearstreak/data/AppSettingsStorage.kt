package com.eight64zeros.clearstreak.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Small local preferences for user-facing app options (non-sensitive).
 */
class AppSettingsStorage(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var showDailyVerseOnHome: Boolean
        get() = prefs.getBoolean(KEY_VERSE_ON_HOME, true)
        set(value) = prefs.edit().putBoolean(KEY_VERSE_ON_HOME, value).apply()

    var showDailyPassageOnHome: Boolean
        get() = prefs.getBoolean(KEY_PASSAGE_ON_HOME, false)
        set(value) = prefs.edit().putBoolean(KEY_PASSAGE_ON_HOME, value).apply()

    /** Opt-in: show the optional faith line under passages/verses. */
    var showFaithReflections: Boolean
        get() = prefs.getBoolean(KEY_FAITH, false)
        set(value) = prefs.edit().putBoolean(KEY_FAITH, value).apply()

    companion object {
        private const val PREFS_NAME = "clearstreak_app_settings"
        private const val KEY_VERSE_ON_HOME = "verse_on_home"
        private const val KEY_PASSAGE_ON_HOME = "passage_on_home"
        private const val KEY_FAITH = "faith_reflections"
    }
}
