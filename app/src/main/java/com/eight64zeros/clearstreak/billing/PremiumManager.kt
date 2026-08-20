package com.eight64zeros.clearstreak.billing

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

/**
 * Product ID for the one-time "unlock everything" purchase.
 *
 * This EXACT id must be created in the Play Console as a **one-time (in-app) product**
 * priced at $9.99. Until it exists there, the store flavor falls back to [UNLOCK_PRICE_FALLBACK]
 * and purchases can't be tested.
 */
const val PRODUCT_UNLOCK = "clearstreak_unlock"

/** Shown before Play returns the localized/formatted price (and on the air-gapped core flavor). */
const val UNLOCK_PRICE_FALLBACK = "$14.99"

enum class PremiumStatus {
    /** Connecting to Play / querying the product. */
    CONNECTING,

    /** Connected; price loaded; ready to buy or already owned. */
    READY,

    /** A purchase is awaiting payment approval (e.g. slow card, parental approval). */
    PURCHASE_PENDING,

    /** Billing isn't available (no Play Services, product not published yet, offline). */
    UNAVAILABLE
}

/**
 * Flavor-agnostic view of the paid unlock. Shared UI depends only on this — never on the
 * Play Billing library directly.
 */
data class PremiumState(
    val isUnlocked: Boolean = false,
    /** Localized, formatted price from Play (e.g. "$9.99", "£8.49") or the fallback. */
    val priceText: String = UNLOCK_PRICE_FALLBACK,
    val status: PremiumStatus = PremiumStatus.CONNECTING,
    /** Transient, user-facing note (error / pending). Cleared on the next successful step. */
    val message: String? = null
)

/**
 * The unlock/entitlement surface. The **store** flavor backs this with Google Play Billing;
 * the air-gapped **core** flavor uses a stub that reports everything unlocked (operator/dogfood build).
 *
 * Each flavor supplies a top-level `createPremiumManager(context)` factory that returns its impl.
 */
interface PremiumManager {
    val state: StateFlow<PremiumState>

    /** Launch the Play purchase sheet for the one-time unlock. No-op if already unlocked. */
    fun launchPurchase(activity: Activity)

    /** Re-query Play for an existing entitlement (after reinstall or on a new device). */
    fun restorePurchases()

    /** Release resources (end the billing connection). Call from Activity.onDestroy. */
    fun dispose()
}
