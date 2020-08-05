package com.albertlowis.purchaseevent

import org.apache.kafka.common.serialization.Serdes

const val PURCHASE_BOOTSTRAP_SERVER = "localhost:9092"
const val PURCHASE_STREAMS_STATE_DIR = "/tmp/kafka-streams"
const val PURCHASE_STREAMS_SERVICE_APP_ID = "BpsPurchaseStreams"

class PurchaseCreatedEvent {

    companion object {
        const val TOPIC_NAME = "bps-purchase-created"
        val KEY_SERDE = Serdes.StringSerde() //purchaseId
        val VAL_SERDE = PurchaseSerde() //purchase class
    }
}

class PurchaseFinishedEvent {

    companion object {
        const val TOPIC_NAME = "bps-purchase-finished"
        val KEY_SERDE = Serdes.StringSerde() //purchaseId
        val VAL_SERDE = Serdes.StringSerde() //result
    }
}
