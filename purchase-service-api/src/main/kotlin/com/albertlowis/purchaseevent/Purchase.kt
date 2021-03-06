package com.albertlowis.purchaseevent

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.serialization.Serializable
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer

@Serializable
data class Purchase(
    val purchaseId: String,
    val itemId: String,
    val quantity: Int,
    val purchasedBy: String
)

class PurchaseSerde : Serde<Purchase> {

    override fun deserializer(): Deserializer<Purchase> {
        return PurchaseDeserializer()
    }

    override fun serializer(): Serializer<Purchase> {
        return PurchaseSerializer()
    }
}

class PurchaseSerializer : Serializer<Purchase> {

    private val jsonMapper = ObjectMapper().apply { registerKotlinModule() }

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
    }

    override fun serialize(topic: String?, data: Purchase?): ByteArray? {
        if (data == null) return null
        return jsonMapper.writeValueAsBytes(data)
    }

    override fun close() {
    }
}

class PurchaseDeserializer : Deserializer<Purchase> {

    private val jsonMapper = ObjectMapper().apply { registerKotlinModule() }

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
    }

    override fun deserialize(topic: String?, data: ByteArray?): Purchase? {
        if (data == null) return null
        return jsonMapper.readValue(data, Purchase::class.java)
    }

    override fun close() {
    }
}
