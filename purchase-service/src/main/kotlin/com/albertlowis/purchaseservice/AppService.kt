package com.albertlowis.purchaseservice

fun main(args: Array<String>) {
    println("hello purchase service")
    PurchaseProducer().produce()
    //TODO: Wire purchase service REST endpoint
}
