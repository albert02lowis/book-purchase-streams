package com.albertlowis.purchaseevent

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer

const val PURCHASE_BOOTSTRAP_SERVER = "localhost:9092"
const val PURCHASE_STREAMS_STATE_DIR = "/tmp/kafka-streams"
const val PURCHASE_STREAMS_SERVICE_APP_ID = "BpsPurchaseStreams"

class PurchaseCreatedEvent {

    companion object {
        const val TOPIC_NAME = "bps-purchase-created"
        val KEY_SER = StringSerializer()
        val KEY_DES = StringDeserializer()
        val VAL_SER = StringSerializer()
        val VAL_DES = StringDeserializer()
    }
}

class PurchaseFinishedEvent {

    companion object {
        const val TOPIC_NAME = "bps-purchase-finished"
        val KEY_SER = StringSerializer()
        val KEY_DES = StringDeserializer()
        val VAL_SER = StringSerializer()
        val VAL_DES = StringDeserializer()
    }
}
