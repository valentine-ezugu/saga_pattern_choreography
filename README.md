# 🧩 Saga Pattern with Kafka - Choreography Style

This project demonstrates the **Saga pattern** using **Choreography** across multiple microservices, coordinated using **Apache Kafka**.

The services are:
- `orderService`
- `paymentService`
- (optional) `shippingService` – coming soon

---

## 🚀 Tech Stack

- Java 17
- Spring Boot 3
- Spring Kafka
- Apache Kafka + Zookeeper (via Docker Compose)
- H2 (in-memory DB)
- Maven

---

## 🔄 What is Saga Choreography?

In **choreography-based sagas**, services:
- Communicate by **emitting and consuming events** via a message broker (Kafka)
- Are **loosely coupled** (no direct REST calls)
- React to events and **independently perform actions**
- Use **compensation** events when something fails

🧠 There is **no central orchestrator** — just event-driven flows.

---

## 🧪 Example Saga Flow (Order → Payment)

1. `OrderService` creates an order and emits `OrderCreatedEvent` to Kafka
2. `PaymentService` consumes the event, processes payment, and emits:
    - `PaymentSuccessEvent` or
    - `PaymentFailedEvent`
3. `OrderService` listens to `payment-topic` and:
    - Marks order as `COMPLETED` if success
    - Calls `cancelOrder` if payment failed (compensation)

📝 Each service logs saga steps in a `SagaStep` table.

---

## 🧠 Kafka Concepts Used (Deep Dive)

### ✅ Topic
A stream of messages. E.g., `order-topic`, `payment-topic`

### ✅ Partition
Kafka splits a topic into partitions to enable parallelism and ordering.  
Key rule:
```text
partition = hash(key) % numPartitions
