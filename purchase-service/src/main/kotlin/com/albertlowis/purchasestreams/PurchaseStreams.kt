package com.albertlowis.purchasestreams

import com.albertlowis.purchaseevent.PURCHASE_BOOTSTRAP_SERVER
import com.albertlowis.purchaseevent.PURCHASE_STREAMS_SERVICE_APP_ID
import com.albertlowis.purchaseevent.PURCHASE_STREAMS_STATE_DIR
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import java.time.Duration
import java.util.*

class PurchaseStreams {

    fun runService() {
        val stream = buildStream()

        stream.start()

        Runtime.getRuntime().addShutdownHook(Thread(Runnable {
            stream.close(Duration.ofMillis(20_000L))
        }))

        Thread.currentThread().join()
    }

    private fun buildStream(): KafkaStreams {
        val streamsBuilder = StreamsBuilder()

        //KTable of items added

        //KStream of purchases

        //join


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

    //transform class
}