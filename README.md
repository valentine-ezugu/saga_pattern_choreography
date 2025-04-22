#  Saga Pattern with Kafka - Choreography Style

This project demonstrates the **Saga pattern** using **Choreography** across multiple microservices, coordinated using **Apache Kafka**.

The services are:
- `orderService`
- `paymentService`
- (optional) `shippingService` – coming soon

---

##  Tech Stack

- Java 17
- Spring Boot 3
- Spring Kafka
- Apache Kafka + Zookeeper (via Docker Compose)
- H2 (in-memory DB)
- Maven

---

##  What is Saga Choreography?

In **choreography-based sagas**, services:
- Communicate by **emitting and consuming events** via a message broker (Kafka)
- Are **loosely coupled** (no direct REST calls)
- React to events and **independently perform actions**
- Use **compensation** events when something fails

🧠 There is **no central orchestrator** — just event-driven flows.

---

##  Example Saga Flow (Order → Payment)

1. `OrderService` creates an order and emits `OrderCreatedEvent` to Kafka
2. `PaymentService` consumes the event, processes payment, and emits:
    - `PaymentSuccessEvent` or
    - `PaymentFailedEvent`
3. `OrderService` listens to `payment-topic` and:
    - Marks order as `COMPLETED` if success
    - Calls `cancelOrder` if payment failed (compensation)

  Each service logs saga steps in a `SagaStep` table.

---


###  Topic
`order-topic`, `payment-success-topic, 'payment-failed-topic`
Enable idempotent producer-that guarantees only one message is consumed 
no duplicate

### Retry config
PRODUCER RETRY
- spring.kafka.producer.properties.enable.idempotence=true
- spring.kafka.producer.retries=5 enable auto retry
  
CONSUMER RETRY
- retry.topic.enabled=true
- retry.topic.attempts=3
- retry.topic.backoff.delay=1000


- Auto created topics
  - payment-topic-retry-1000ms
  - payment-topic-dlt
After first attempt on topic payment-topic if failure occurs spring kafka redirects 
to payment-topic-1000ms - so here it attempts and delays for 1 second and tries again
After 3 retries message is sent to dead letter topic to receive message
   
HOW TO VIEW DLT
 - you can use kafka ui  to view the DLT's or you can implement the DLT
  - to handle it manually - create a listner with topic payment-topic-dlt if your topic 
  - is payment-topic

###   Partition
Kafka splits a topic into partitions to enable parallelism and ordering.  
Key rule:
```text
partition = hash(key) % numPartitions
