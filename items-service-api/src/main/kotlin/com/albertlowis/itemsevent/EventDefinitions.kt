package com.albertlowis.itemsevent

import org.apache.kafka.common.serialization.Serdes

const val ITEMS_BOOTSTRAP_SERVER = "localhost:9092"

class ItemsAddedEvent {

    companion object {
        const val TOPIC_NAME = "bps-items-added"
        val KEY_SERDE = Serdes.StringSerde() //itemId
        val VAL_SERDE = ItemSerde() //item class
    }
}
