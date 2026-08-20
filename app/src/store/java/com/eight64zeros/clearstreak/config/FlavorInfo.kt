package com.eight64zeros.clearstreak.config

/**
 * Flavor-specific facts surfaced in privacy copy. The **store** flavor adds
 * `android.permission.INTERNET` **solely** for Google Play Billing — no recovery data is ever sent.
 */
object FlavorInfo {
    const val hasNetwork = true
    const val privacyIcon = "🏠"
    const val privacyTitle = "Your recovery stays on your device"
    const val privacyBody =
        "The only time this app touches the internet is Google's purchase check for the one-time unlock. " +
        "Your journal, check-ins, and journeys never leave this phone — there's no account or server for them to go to."
}
