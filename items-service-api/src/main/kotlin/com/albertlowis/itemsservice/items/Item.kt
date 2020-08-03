package com.albertlowis.itemsservice.items

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val itemId: String,
    val description: String,
    val quantity: Int
)
