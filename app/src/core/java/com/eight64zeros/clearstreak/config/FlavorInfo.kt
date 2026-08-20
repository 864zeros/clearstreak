package com.eight64zeros.clearstreak.config

/**
 * Flavor-specific facts surfaced in privacy copy. The **core** flavor is fully air-gapped —
 * `android.permission.INTERNET` is removed entirely.
 */
object FlavorInfo {
    const val hasNetwork = false
    const val privacyIcon = "✈️"
    const val privacyTitle = "It works completely offline"
    const val privacyBody =
        "The app has no internet access at all, so nothing you write can ever be sent, shared, or leaked online."
}
