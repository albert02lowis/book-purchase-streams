package com.albertlowis.purchaseservice

import com.albertlowis.purchaseevent.PURCHASE_BOOTSTRAP_SERVER
import com.albertlowis.purchaseevent.Purchase
import com.albertlowis.purchaseevent.PurchaseCreatedEvent
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.*

class PurchaseProducer (
    schemaRegistryUrl: String? = null
) {
    private val producer: Producer<String, String>

    init {
        val props = Properties()
        props["bootstrap.servers"] = PURCHASE_BOOTSTRAP_SERVER
        props["key.serializer"] = PurchaseCreatedEvent.KEY_SERDE.serializer()::class.java
        props["value.serializer"] = PurchaseCreatedEvent.VAL_SERDE.serializer()::class.java
        schemaRegistryUrl?.let { props["schema.registry.url"] = it }

        producer = KafkaProducer<String, String>(props)
    }

    fun produce() {
        val purchase1 = Purchase(
            purchaseId = "1",
            itemId = "1",
            quantity = 1,
            purchasedBy = "userId1"
        )

        val result1 = sendToTopic(purchase1)
        result1.get()

        val purchase21 = Purchase(
            purchaseId = "21",
            itemId = "2",
            quantity = 1,
            purchasedBy = "userId21"
        )
        val result21 = sendToTopic(purchase21)

        val purchase22 = Purchase(
            purchaseId = "22",
            itemId = "2",
            quantity = 2,
            purchasedBy = "userId22"
        )
        val result22 = sendToTopic(purchase22)

        result21.get()
        result22.get()

        val purchase3 = Purchase(
            purchaseId = "3",
            itemId = "3",
            quantity = 1,
            purchasedBy = "userId3"
        )

        val result3 = sendToTopic(purchase3)
        result3.get()
    }

    private fun sendToTopic(message: Purchase) =
        producer.send(ProducerRecord(PurchaseCreatedEvent.TOPIC_NAME, message.purchaseId, message.toString()))
}
