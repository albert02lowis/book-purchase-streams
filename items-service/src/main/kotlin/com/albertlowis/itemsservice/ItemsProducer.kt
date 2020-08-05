package com.albertlowis.itemsservice

import com.albertlowis.itemsevent.ITEMS_BOOTSTRAP_SERVER
import com.albertlowis.itemsevent.Item
import com.albertlowis.itemsevent.ItemsAddedEvent
import com.sksamuel.avro4k.Avro
import org.apache.avro.generic.GenericRecordBuilder
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.*

class ItemsProducer(
    schemaRegistryUrl: String? = null
) {
    private val producer: Producer<String, String>

    init {
        val props = Properties()
        props["bootstrap.servers"] = ITEMS_BOOTSTRAP_SERVER
        props["key.serializer"] = ItemsAddedEvent.KEY_SERDE.serializer()::class.java
        props["value.serializer"] = ItemsAddedEvent.VAL_SERDE.serializer()::class.java
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
        producer.send(ProducerRecord(ItemsAddedEvent.TOPIC_NAME, item.itemId, item.toString()))

    private fun Item.toGenericRecord() = GenericRecordBuilder(
        Avro.default.schema(Item.serializer())
    ).let {
        it.set("itemId", this.itemId)
        it.set("description", this.description)
        it.set("quantity", this.quantity)
    }.build()
}
