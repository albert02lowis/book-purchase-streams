package com.albertlowis.purchasestreams

import com.albertlowis.itemsevent.Item
import com.albertlowis.itemsevent.ItemSerde
import com.albertlowis.itemsevent.ItemsAddedEvent
import com.albertlowis.purchaseevent.*
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Joined
import org.apache.kafka.streams.kstream.Produced
import org.apache.kafka.streams.state.Stores
import java.time.Duration
import java.util.*

class PurchaseStreams {

    fun runService() {
        val stream = buildStream()
        stream.cleanUp() //delete existing state stores (since we are just playing around)

        stream.start() //start stream in background; does not block

        Runtime.getRuntime().addShutdownHook(Thread(Runnable {
            stream.close(Duration.ofMillis(20_000L)) //stop stream on shutdown
        }))
        Thread.currentThread().join() //blocks current thread
    }

    private fun buildStream(): KafkaStreams {
        val streamsBuilder = StreamsBuilder()

        //KTable of items added
        val itemsAddedEvent = streamsBuilder
            .table(
                ItemsAddedEvent.TOPIC_NAME,
                Consumed.with(
                    ItemsAddedEvent.KEY_SERDE,
                    ItemsAddedEvent.VAL_SERDE
                )
            ) //itemId to item object

        //KStream of purchases
        val purchaseCreatedEvent = streamsBuilder
            .stream(
                PurchaseCreatedEvent.TOPIC_NAME,
                Consumed.with(
                    PurchaseCreatedEvent.KEY_SERDE,
                    PurchaseCreatedEvent.VAL_SERDE
                )
            ) //purchaseId to purchase object
        val itemIdToPurchase = purchaseCreatedEvent
            .selectKey { purchaseId, purchase -> purchase.itemId } //re-key to itemId

        //State Store of booked items by previous purchases
        val bookedItemStore = Stores
            .keyValueStoreBuilder(
                Stores.persistentKeyValueStore(BookedItemStore.STORE_NAME),
                ItemsAddedEvent.KEY_SERDE,
                Serdes.Integer()
            )
        streamsBuilder.addStateStore(bookedItemStore)

        //join purchase to item
        itemIdToPurchase
            .join(
                itemsAddedEvent,
                { left: Purchase, right: Item -> KeyValue(left, right) },
                Joined.with(
                    Serdes.StringSerde(),
                    PurchaseSerde(),
                    ItemSerde()
                )
            )
            .transform(PurchaseValidatorTransformerSupplier(), BookedItemStore.STORE_NAME)
            .to(
                PurchaseFinishedEvent.TOPIC_NAME,
                Produced.with(
                    PurchaseFinishedEvent.KEY_SERDE,
                    PurchaseFinishedEvent.VAL_SERDE
                )
            )

        return KafkaStreams(
            streamsBuilder.build(),
            getStreamProperties()
        )
    }

    private fun getStreamProperties() = Properties().apply {
        put(StreamsConfig.APPLICATION_ID_CONFIG, PURCHASE_STREAMS_SERVICE_APP_ID)
        put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, PURCHASE_BOOTSTRAP_SERVER)
        put(StreamsConfig.STATE_DIR_CONFIG, PURCHASE_STREAMS_STATE_DIR)
        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, "exactly_once")
        put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1)
    }
}