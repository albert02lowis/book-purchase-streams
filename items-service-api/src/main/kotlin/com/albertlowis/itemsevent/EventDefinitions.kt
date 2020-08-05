package com.albertlowis.itemsevent

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer

const val ITEMS_BOOTSTRAP_SERVER = "localhost:9092"

class ItemsAddedEvent {

    companion object {
        const val TOPIC_NAME = "bps-items-added"
        val KEY_SER = StringSerializer()
        val KEY_DES = StringDeserializer()
        val VAL_SER = StringSerializer()
        val VAL_DES = StringDeserializer()
    }
}
