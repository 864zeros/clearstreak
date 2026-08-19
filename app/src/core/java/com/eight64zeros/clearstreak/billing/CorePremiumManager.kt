package com.eight64zeros.clearstreak.billing

import android.app.Activity
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Core flavor: air-gapped, no INTERNET permission and no billing library.
 * Everything is unlocked (this is the operator / dogfood build), so there is no paywall.
 */
fun createPremiumManager(context: Context): PremiumManager = CorePremiumManager()

private class CorePremiumManager : PremiumManager {
    private val _state = MutableStateFlow(
        PremiumState(
            isUnlocked = true,
            priceText = UNLOCK_PRICE_FALLBACK,
            status = PremiumStatus.READY
        )
    )
    override val state: StateFlow<PremiumState> = _state.asStateFlow()

    override fun launchPurchase(activity: Activity) { /* no-op: core is always unlocked */ }
    override fun restorePurchases() { /* no-op */ }
    override fun dispose() { /* no-op */ }
}
