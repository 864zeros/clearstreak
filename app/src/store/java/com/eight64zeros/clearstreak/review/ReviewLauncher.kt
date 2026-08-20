package com.eight64zeros.clearstreak.review

import android.app.Activity
import com.google.android.play.core.review.ReviewManagerFactory

/**
 * Store flavor: launches the in-context Google Play In-App Review flow. Play decides whether the
 * rating sheet actually appears (quota-limited), so we fall back to the store listing on failure.
 * We never read which stars were tapped — that would be sentiment-gating (a policy violation).
 */
fun launchReview(activity: Activity) {
    val manager = ReviewManagerFactory.create(activity)
    manager.requestReviewFlow().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            manager.launchReviewFlow(activity, task.result)
                .addOnCompleteListener { /* completed regardless of whether a review was left */ }
        } else {
            openPlayListing(activity)
        }
    }
}
