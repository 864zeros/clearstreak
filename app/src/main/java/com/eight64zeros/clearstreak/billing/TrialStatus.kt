package com.eight64zeros.clearstreak.billing

import android.content.Context
import android.content.pm.PackageManager

/** Length of the free trial before the one-time unlock is required. */
const val TRIAL_DAYS = 7

data class TrialStatus(
    val daysRemaining: Int,
    val isActive: Boolean
)

/**
 * Computes the free-trial window from the app's install time, so it survives a "clear data"
 * (resets only on a true reinstall). Fully offline — the privacy stance rules out server-side
 * trial enforcement, so a determined reinstall can reset the week; that's an accepted tradeoff.
 */
fun computeTrialStatus(context: Context): TrialStatus {
    val installedAt = try {
        @Suppress("DEPRECATION")
        context.packageManager
            .getPackageInfo(context.packageName, 0)
            .firstInstallTime
    } catch (e: PackageManager.NameNotFoundException) {
        System.currentTimeMillis()
    }
    val elapsedDays = ((System.currentTimeMillis() - installedAt) / 86_400_000L).toInt()
    val remaining = (TRIAL_DAYS - elapsedDays).coerceIn(0, TRIAL_DAYS)
    return TrialStatus(daysRemaining = remaining, isActive = remaining > 0)
}
