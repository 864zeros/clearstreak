package com._864zeros.clearstreak.model

import java.util.UUID

data class CheckIn(
    val id: String = UUID.randomUUID().toString(),
    val journeyId: String,
    val timestamp: Long = System.currentTimeMillis() / 1000,
    val moodScore: Int? = null, // 1 to 5
    val urgeLevel: UrgeLevel,
    val haltTrigger: HaltTrigger? = null,
    val noteEncrypted: String? = null, // Decrypted in memory when unlocked
    val isSlip: Boolean = false,
    val isCrisisIntercept: Boolean = false,
    val copingCardId: String? = null
)
