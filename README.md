Book-Purchase-Streams
---

This is my exploration on creating services by using [Kafka Streams](https://kafka.apache.org/documentation/streams/) as much as possible.
After some thinking I wanted to build something simple, yet can be applied to many scenarios. In the end I came out with a flow which I call as "book-purchase".


## Book-Purchase

Book-Purchase is a flow that is very common in a lot of use cases.
The most straightforward case is for online shopping; when we have a limited quantity of items that users can purchase.

Users can select an item to be purchased (booking), and proceed to make the payment (purchase).
After the purchase is finished the item's quantity is decreased.

Multiple users can book an item at the same time, making this an interesting problem where concurrent purchases should not be valid if an item is out of stock.

If we think about it, this flow is applicable to many other use cases such as booking a seat in the theatre, booking a meeting room, etc.

The essence of this problem is multiple transactions trying to claim the same resource. The traditional approach is done via locking, but locking a specific resource has a disadvantage that other transactions get blocked, and blocked transactions means thread/processes are just waiting doing nothing, and if the blocked process crashes, the user needs to retry.
So let's see what we can do with Kafka.

## High-Level Design

This project is broken down into several sub-modules/services.

### Items Service

Items service is the first thing that I will build:

##### Data model:
```
data class Item(
    val itemId: String,
    val description: String,
    val quantity: Int
)
```
##### APIs:

- Insert item
- Update item quantity
- Update item description

##### Basic design:

Since we are just interested in the latest information about items, items will be inserted into Kafka (itemId as key) with log compaction enabled and retention set to forever -> this makes Kafka acting like a materialized view of items.

And then we can try to use ksqlDB to query information about items.

### Purchase Service

##### Data model:
```
data class Purchase(
    val itemId: String,
    val quantity: Int,
    val purchasedBy: String,
    val purchaseStatus: String
)

enum class PurchaseStatus {
    
    CREATED,
    SUCCESSFUL,
    FAILED
}
```

##### APIs:

- Make purchase

##### Basic design:

Purchases will be recorded in kafka with log compaction disabled and retention set to forever.
When user makes a purchase, we will append Purchase object with status as CREATED.
If the purchase is successful, we will append again with status SUCCESSFUL, else FAILED.

I will try to use Kafka Streams to join the purchase stream with the item materialized view to increment/decrement the item quantity. Purchase is successful if there is enough quantity left for the purchase.