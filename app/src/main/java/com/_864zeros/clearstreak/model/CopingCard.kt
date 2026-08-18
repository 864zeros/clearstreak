package com._864zeros.clearstreak.model

import java.util.UUID

data class CopingCard(
    val id: String = UUID.randomUUID().toString(),
    val triggerCategory: String, // HALT trigger name or GENERAL
    val minUrgeLevel: UrgeLevel,
    val actionText: String,
    val rationale: String? = null,
    val isFavorite: Boolean = false,
    val isUserCreated: Boolean = false
)
