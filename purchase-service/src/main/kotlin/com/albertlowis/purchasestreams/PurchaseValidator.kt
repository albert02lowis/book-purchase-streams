package com.albertlowis.purchasestreams

import com.albertlowis.itemsevent.Item
import com.albertlowis.purchaseevent.BookedItemStore
import com.albertlowis.purchaseevent.Purchase
import com.albertlowis.purchaseevent.PurchaseResult
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Transformer
import org.apache.kafka.streams.kstream.TransformerSupplier
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore

class PurchaseValidatorTransformerSupplier : TransformerSupplier<String, KeyValue<Purchase, Item>, KeyValue<String, PurchaseResult>> {

    override fun get(): Transformer<String, KeyValue<Purchase, Item>, KeyValue<String, PurchaseResult>> {
        return PurchaseValidatorTransformer()
    }
}

class PurchaseValidatorTransformer : Transformer<String, KeyValue<Purchase, Item>, KeyValue<String, PurchaseResult>> {

    private var bookedItemStore: KeyValueStore<String, Long>? = null

    override fun init(context: ProcessorContext) {
        bookedItemStore = context.getStateStore(
            BookedItemStore.STORE_NAME
        ) as KeyValueStore<String, Long>
    }

    override fun transform(
        itemId: String?,
        purchaseToItem: KeyValue<Purchase, Item>?
    ): KeyValue<String, PurchaseResult> {
        requireNotNull(purchaseToItem)

        val purchase = purchaseToItem.key
        val item = purchaseToItem.value

        val previouslyBookedItemQuantity = bookedItemStore!!.get(itemId) ?: 0

        val totalItemsNeeded = previouslyBookedItemQuantity + purchase.quantity

        return if (item.quantity >= totalItemsNeeded) {
            KeyValue(purchase.purchaseId, PurchaseResult.Success())
        } else {
            KeyValue(purchase.purchaseId, PurchaseResult.Failed())
        }
    }

    override fun close() {
    }
}