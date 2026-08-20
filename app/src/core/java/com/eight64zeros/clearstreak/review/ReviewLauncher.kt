package com.eight64zeros.clearstreak.review

import android.app.Activity

/**
 * Core flavor: no Play In-App Review library (air-gapped/dogfood build). The review action simply
 * hands off to the Play listing via an external intent.
 */
fun launchReview(activity: Activity) {
    openPlayListing(activity)
}
