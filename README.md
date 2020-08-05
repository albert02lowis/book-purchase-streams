Book-Purchase-Streams
---

This is my exploration on creating services by using [Kafka Streams](https://kafka.apache.org/documentation/streams/).
I wanted to build something simple, yet can be applied to many scenarios, so I came out with this "book-purchase" flow.


## Book-Purchase

Book-Purchase is a common flow in a lot of use cases.
The most straightforward case is for online shopping; when we have a limited quantity of items that users can purchase.

Users can select an item to be purchased (booking), and proceed to make the payment (purchase).
After the purchase is finished the item's quantity is decreased. The seller can also restock the item quantity to make it available again.

Multiple users can book an item at the same time, making this an interesting problem if the item is running out of stock, but there are multiple concurrent purchases vying for the same item - we need to make sure no user over-purchases the item.

If we think about it, this flow is applicable to many other use cases such as booking a seat in the theatre, booking a meeting room, etc.

The essence of this problem is multiple transactions trying to claim the same resource. The traditional approach is done via locking, but locking a specific resource has a disadvantage that other transactions get blocked, and blocked transactions means thread/processes are just waiting doing nothing, and if the blocked process crashes, the user needs to retry.
So let's see what we can do with Kafka to make this more fault tolerant.

## High-Level Design

This project is broken down into several sub-modules/services.

![alt text](https://github.com/albert02lowis/book-purchase-streams/blob/master/design.png?raw=true)

### Items Service

##### Data model:
```
data class Item(
    val itemId: String,
    val description: String,
    val quantity: Int
)
```
##### APIs:

- Insert item with quantity

##### Basic design:

We will append items into a topic; this represents the event when items are restocked/added.

### Purchase Service

##### Data model:
```
data class Purchase(
    val purchaseId: String,
    val itemId: String,
    val quantity: Int,
    val purchasedBy: String
)

data class PurchaseResult(
    val resultMessage: String
)
```

##### APIs:

- Make purchase with certain quantity

##### Basic design:

When user makes a purchase, we will append Purchase object to a topic, this topic represents a purchase created event.
An application built on Kafka streams will validate this purchase topic against the items topic by joining and using state store, the state store will keep track how many items have been booked by previous purchases.

The purchase result (success/fail) will be appended another topic, this represents purchase finished event.

### How to setup and run

#### Prerequisites

- Zookeeper
- Kafka
- Gradle

#### Running

Run Zookeeper and then Kafka 
```
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties
```
Create the topics
```
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic bps-items-added --config retention.ms=600000
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic bps-purchase-created --config retention.ms=600000
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic bps-purchase-finished --config retention.ms=600000
```
Run Kafka Console Consumer to see the end result
```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic bps-purchase-finished --from-beginning
```
Run the Items Service
```
./gradlew :items-service:run
```
Run the Purchase Service and after this runs we should see the result in the console consumer soon.
```
./gradlew :purchase-service:run
```

### TODOs:

- Wire with REST endpoints instead of hardcoding the items and purchase producer.
- Make blocking purchase API that waits for purchase finished topic after appending purchase created topic. 
- Containerize this to make setup and running easier.
