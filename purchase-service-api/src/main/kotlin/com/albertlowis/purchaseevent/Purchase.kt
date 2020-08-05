package com.albertlowis.purchaseevent

import kotlinx.serialization.Serializable

@Serializable
data class Purchase(
    val purchaseId: String,
    val itemId: String,
    val quantity: Int,
    val purchasedBy: String
)
