package com.albertlowis.itemsservice.items

import com.sksamuel.avro4k.Avro
import org.apache.avro.generic.GenericRecordBuilder
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

class ItemsProducer(
    schemaRegistryUrl: String? = null
) {
    private val producer: Producer<String, String>
    private val TOPIC_NAME = "bps-items"

    init {
        val props = Properties()
        props["bootstrap.servers"] = "localhost:9092"
        props["key.serializer"] = StringSerializer::class.java
        props["value.serializer"] = StringSerializer::class.java
        schemaRegistryUrl?.let { props["schema.registry.url"] = it }

        producer = KafkaProducer<String, String>(props)
    }

    fun produce() {
        val item1 = Item(
            itemId = "1",
            description = "",
            quantity = 1
        )

        val result1 = sendToTopic(item1)
        result1.get()

        val item2 = Item(
            itemId = "2",
            description = "",
            quantity = 2
        )

        val result2 = sendToTopic(item2)
        result2.get()

        val item3 = Item(
            itemId = "3",
            description = "",
            quantity = 3
        )

        val result3 = sendToTopic(item3)
        result3.get()
    }

    private fun sendToTopic(item: Item) =
        producer.send(ProducerRecord(TOPIC_NAME, item.toString()))

    private fun Item.toGenericRecord() = GenericRecordBuilder(
        Avro.default.schema(Item.serializer())
    ).let {
        it.set("itemId", this.itemId)
        it.set("description", this.description)
        it.set("quantity", this.quantity)
    }.build()
}
