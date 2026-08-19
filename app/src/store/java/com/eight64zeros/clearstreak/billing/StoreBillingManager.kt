package com.eight64zeros.clearstreak.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.eight64zeros.clearstreak.data.AppSettingsStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Store flavor: real Google Play Billing implementation. */
fun createPremiumManager(context: Context): PremiumManager =
    StoreBillingManager(context.applicationContext)

private class StoreBillingManager(
    private val appContext: Context
) : PremiumManager, PurchasesUpdatedListener {

    private val settings = AppSettingsStorage(appContext)

    // Seed from the local cache so premium features work instantly offline / before Play reconnects.
    private val _state = MutableStateFlow(PremiumState(isUnlocked = settings.isPremiumUnlocked))
    override val state: StateFlow<PremiumState> = _state.asStateFlow()

    private var productDetails: ProductDetails? = null

    private val billingClient: BillingClient = BillingClient.newBuilder(appContext)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .enableAutoServiceReconnection()
        .build()

    init {
        connect()
    }

    private fun connect() {
        _state.value = _state.value.copy(status = PremiumStatus.CONNECTING)
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProduct()
                    queryEntitlement()
                } else {
                    _state.value = _state.value.copy(status = PremiumStatus.UNAVAILABLE)
                }
            }

            override fun onBillingServiceDisconnected() {
                // Auto-reconnection is enabled; nothing to do here.
            }
        })
    }

    private fun queryProduct() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_UNLOCK)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(params) { result, productDetailsResult ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                val details = productDetailsResult.productDetailsList.firstOrNull()
                productDetails = details
                _state.value = _state.value.copy(
                    status = PremiumStatus.READY,
                    priceText = details?.oneTimePurchaseOfferDetails?.formattedPrice
                        ?: UNLOCK_PRICE_FALLBACK
                )
            } else {
                // Product not published yet, or store unreachable — keep the fallback price.
                _state.value = _state.value.copy(status = PremiumStatus.UNAVAILABLE)
            }
        }
    }

    /** Silently re-check ownership on launch and on demand ("Restore purchase"). */
    private fun queryEntitlement() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient.queryPurchasesAsync(params) { result, purchases ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                val owns = purchases.any { it.isUnlockPurchase() }
                purchases.forEach { maybeAcknowledge(it) }
                setUnlocked(owns)
            }
        }
    }

    override fun launchPurchase(activity: Activity) {
        if (_state.value.isUnlocked) return
        val details = productDetails
        if (details == null) {
            _state.value = _state.value.copy(
                message = "Still connecting to the store — try again in a moment."
            )
            connect()
            return
        }
        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    // No offer token — one-time non-consumable product.
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(details)
                        .build()
                )
            )
            .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }

    override fun restorePurchases() {
        _state.value = _state.value.copy(message = null)
        queryEntitlement()
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    if (!purchase.products.contains(PRODUCT_UNLOCK)) return@forEach
                    when (purchase.purchaseState) {
                        Purchase.PurchaseState.PURCHASED -> {
                            maybeAcknowledge(purchase)
                            setUnlocked(true)
                        }
                        Purchase.PurchaseState.PENDING -> {
                            _state.value = _state.value.copy(
                                status = PremiumStatus.PURCHASE_PENDING,
                                message = "Purchase pending — we'll unlock as soon as it clears."
                            )
                        }
                    }
                }
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> setUnlocked(true)
            BillingClient.BillingResponseCode.USER_CANCELED ->
                _state.value = _state.value.copy(message = null)
            else ->
                _state.value = _state.value.copy(message = "Purchase didn't complete. Nothing was charged.")
        }
    }

    private fun maybeAcknowledge(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            // Entitlement is granted locally regardless; ack tells Play not to auto-refund.
            billingClient.acknowledgePurchase(params) { /* no-op */ }
        }
    }

    private fun setUnlocked(unlocked: Boolean) {
        settings.isPremiumUnlocked = unlocked
        _state.value = _state.value.copy(
            isUnlocked = unlocked,
            status = PremiumStatus.READY,
            message = null
        )
    }

    override fun dispose() {
        if (billingClient.isReady) billingClient.endConnection()
    }

    private fun Purchase.isUnlockPurchase(): Boolean =
        products.contains(PRODUCT_UNLOCK) && purchaseState == Purchase.PurchaseState.PURCHASED
}
