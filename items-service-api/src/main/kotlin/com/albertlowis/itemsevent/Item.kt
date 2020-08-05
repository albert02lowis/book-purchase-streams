package com.albertlowis.itemsevent

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val itemId: String,
    val description: String,
    val quantity: Int
)
