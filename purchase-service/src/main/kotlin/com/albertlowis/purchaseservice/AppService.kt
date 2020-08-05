package com.albertlowis.purchaseservice

import com.albertlowis.purchasestreams.PurchaseStreams

fun main(args: Array<String>) {
    println("hello purchase service")
    PurchaseProducer().produce()
    //TODO: Wire purchase service REST endpoint

    PurchaseStreams().runService()
}
