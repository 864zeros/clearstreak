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

    /** Opt-in: show the Affirmation card (with a Random button) at the bottom of each Reflect segment. */
    var showAffirmations: Boolean
        get() = prefs.getBoolean(KEY_AFFIRMATIONS, false)
        set(value) = prefs.edit().putBoolean(KEY_AFFIRMATIONS, value).apply()

    /** Active compartmentalized Focus Pack ID (e.g. stoic_meditations, franklin_virtues, james_habit). */
    var activeFocusPackId: String
        get() = prefs.getString(KEY_ACTIVE_FOCUS_PACK, "stoic_meditations") ?: "stoic_meditations"
        set(value) = prefs.edit().putString(KEY_ACTIVE_FOCUS_PACK, value).apply()

    /** Show the daily Focus Pack card on the Home Dashboard. */
    var showFocusPackOnHome: Boolean
        get() = prefs.getBoolean(KEY_FOCUS_PACK_ON_HOME, true)
        set(value) = prefs.edit().putBoolean(KEY_FOCUS_PACK_ON_HOME, value).apply()

    /**
     * Local cache of the Play Billing one-time-unlock entitlement. Play is the source of truth;
     * this lets premium features work instantly offline and before the billing client reconnects.
     */
    var isPremiumUnlocked: Boolean
        get() = prefs.getBoolean(KEY_PREMIUM, false)
        set(value) = prefs.edit().putBoolean(KEY_PREMIUM, value).apply()

    companion object {
        private const val PREFS_NAME = "clearstreak_app_settings"
        private const val KEY_VERSE_ON_HOME = "verse_on_home"
        private const val KEY_PASSAGE_ON_HOME = "passage_on_home"
        private const val KEY_FAITH = "faith_reflections"
        private const val KEY_AFFIRMATIONS = "show_affirmations"
        private const val KEY_ACTIVE_FOCUS_PACK = "active_focus_pack"
        private const val KEY_FOCUS_PACK_ON_HOME = "focus_pack_on_home"
        private const val KEY_PREMIUM = "premium_unlocked"
    }
}
