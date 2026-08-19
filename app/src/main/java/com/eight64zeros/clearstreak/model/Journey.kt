package com.eight64zeros.clearstreak.model

import java.util.UUID

data class Journey(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: JourneyCategory,
    val customLabel: String? = null, // user-set label for Behavioral/Custom categories
    val startTimestamp: Long, // Epoch seconds
    val dailyCostSavings: Double = 0.0,
    val isArchived: Boolean = false,
    val suppressGameTools: Boolean = false, // Gaming/screen recovery: hide in-app mini-games (blueprint §7)
    val createdAt: Long = System.currentTimeMillis() / 1000
) {
    /** Label shown for the category — the user's custom label when set, else the category name. */
    val categoryLabel: String
        get() = customLabel?.takeIf { it.isNotBlank() } ?: category.displayName
}
