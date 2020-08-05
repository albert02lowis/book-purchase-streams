package com.albertlowis.purchaseservice.purchase

import kotlinx.serialization.Serializable

@Serializable
data class Purchase(
    val itemId: String,
    val quantity: Int,
    val purchasedBy: String
)
