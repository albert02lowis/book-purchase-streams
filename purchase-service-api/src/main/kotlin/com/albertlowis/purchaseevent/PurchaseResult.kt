package com.albertlowis.purchaseevent

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer

data class PurchaseResult(
    val result: String
) {
    companion object {

        fun Success() = PurchaseResult("SUCCESS")
        fun Failed() = PurchaseResult("FAILED")
    }
}

class PurchaseResultSerde : Serde<PurchaseResult> {
    private val jsonMapper = ObjectMapper().apply { registerKotlinModule() }

    override fun deserializer(): Deserializer<PurchaseResult> {
        return PurchaseResultDeserializer(jsonMapper)
    }

    override fun serializer(): Serializer<PurchaseResult> {
        return PurchaseResultSerializer(jsonMapper)
    }
}

class PurchaseResultSerializer(
    private val jsonMapper: ObjectMapper
) : Serializer<PurchaseResult> {

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
    }

    override fun serialize(topic: String?, data: PurchaseResult?): ByteArray? {
        if (data == null) return null
        return jsonMapper.writeValueAsBytes(data)
    }

    override fun close() {
    }
}

class PurchaseResultDeserializer(
    private val jsonMapper: ObjectMapper
) : Deserializer<PurchaseResult> {

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
    }

    override fun deserialize(topic: String?, data: ByteArray?): PurchaseResult? {
        if (data == null) return null
        return jsonMapper.readValue(data, PurchaseResult::class.java)
    }

    override fun close() {
    }
}
