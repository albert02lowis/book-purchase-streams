package com.albertlowis.itemsevent

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.serialization.Serializable
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer

@Serializable
data class Item(
    val itemId: String,
    val description: String,
    val quantity: Int
)

class ItemSerde : Serde<Item> {

    override fun deserializer(): Deserializer<Item> {
        return ItemDeserializer()
    }

    override fun serializer(): Serializer<Item> {
        return ItemSerializer()
    }
}

class ItemSerializer : Serializer<Item> {

    private val jsonMapper = ObjectMapper().apply { registerKotlinModule() }

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
    }

    override fun serialize(topic: String?, data: Item?): ByteArray? {
        if (data == null) return null
        return jsonMapper.writeValueAsBytes(data)
    }

    override fun close() {
    }
}

class ItemDeserializer : Deserializer<Item> {

    private val jsonMapper = ObjectMapper().apply { registerKotlinModule() }

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
    }

    override fun deserialize(topic: String?, data: ByteArray?): Item? {
        if (data == null) return null
        return jsonMapper.readValue(data, Item::class.java)
    }

    override fun close() {
    }
}
