package com.albertlowis.purchaseevent

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer

data class PurchaseResult(
    val resultMessage: String
) {
    companion object {

        fun Success(
            itemId: String,
            purchaseId: String,
            itemQuantity: Int,
            alreadyBooked: Int,
            purchaseQuantity: Int,
            remainingQuantity: Int
        ) = ResultWithInfo(
            res = "SUCCESS",
            itemId = itemId,
            purchaseId = purchaseId,
            itemQuantity = itemQuantity,
            alreadyBooked = alreadyBooked,
            purchaseQuantity = purchaseQuantity,
            remainingQuantity = remainingQuantity
        )

        fun Failed(
            itemId: String,
            purchaseId: String,
            itemQuantity: Int,
            alreadyBooked: Int,
            purchaseQuantity: Int,
            remainingQuantity: Int
        ) = ResultWithInfo(
            res = "FAILED",
            itemId = itemId,
            purchaseId = purchaseId,
            itemQuantity = itemQuantity,
            alreadyBooked = alreadyBooked,
            purchaseQuantity = purchaseQuantity,
            remainingQuantity = remainingQuantity
        )

        fun ResultWithInfo(
            res: String,
            itemId: String,
            purchaseId: String,
            itemQuantity: Int,
            alreadyBooked: Int,
            purchaseQuantity: Int,
            remainingQuantity: Int
        ) = PurchaseResult("$purchaseId - $itemId $res quantity: $itemQuantity alreadyBooked: $alreadyBooked purchaseQuantity: $purchaseQuantity remaining: $remainingQuantity")
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
