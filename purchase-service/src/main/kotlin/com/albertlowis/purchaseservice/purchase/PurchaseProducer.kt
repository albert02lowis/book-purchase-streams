package com.albertlowis.purchaseservice.purchase

import com.albertlowis.purchaseevent.Purchase
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

class PurchaseProducer (
    schemaRegistryUrl: String? = null
) {
    private val producer: Producer<String, String>
    private val TOPIC_NAME = "bps-purchase"

    init {
        val props = Properties()
        props["bootstrap.servers"] = "localhost:9092"
        props["key.serializer"] = StringSerializer::class.java
        props["value.serializer"] = StringSerializer::class.java
        schemaRegistryUrl?.let { props["schema.registry.url"] = it }

        producer = KafkaProducer<String, String>(props)
    }

    fun produce() {
        val purchase1 = Purchase(
            itemId = "1",
            quantity = 1,
            purchasedBy = "userId1"
        )

        val result1 = sendToTopic(purchase1)
        result1.get()

        val purchase21 = Purchase(
            itemId = "2",
            quantity = 1,
            purchasedBy = "userId21"
        )
        val result21 = sendToTopic(purchase21)

        val purchase22 = Purchase(
            itemId = "2",
            quantity = 2,
            purchasedBy = "userId22"
        )
        val result22 = sendToTopic(purchase22)

        result21.get()
        result22.get()

        val purchase3 = Purchase(
            itemId = "3",
            quantity = 1,
            purchasedBy = "userId3"
        )

        val result3 = sendToTopic(purchase3)
        result3.get()
    }

    private fun sendToTopic(message: Purchase) =
        producer.send(ProducerRecord(TOPIC_NAME, message.toString()))
}
