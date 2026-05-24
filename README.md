# TitanGuard — Real-Time Fraud Detection & Alert Engine

A backend system that evaluates financial transactions for fraud in real time using an asynchronous, event-driven architecture. 
The API accepts a transaction and immediately returns `202 Accepted`, while a Kafka consumer evaluates fraud rules in 
the background — keeping the hot path fast and non-blocking.
 
---

## How it works

![Project Flow](Project%20Flow.png)

---

## Tech Stack

| Layer                | Technology              | Purpose                            |
|----------------------|-------------------------|------------------------------------|
| Language             | Java 21                 | Core application language          |
| Framework            | Spring Boot 4.x         | REST API + Kafka consumer          |
| Message Broker       | Apache Kafka (KRaft)    | Async transaction ingestion        |
| Cache / Rate Limiter | Redis                   | Per-user transaction rate tracking |
| Database             | MongoDB                 | Fraud alerts + transaction storage |
| Containerisation     | Docker + Docker Compose | Local infrastructure setup         |
| API Testing          | Postman                 | Manual endpoint verification       |

---

## API Endpoints

### Transactions

| Method | Endpoint                    | Description                         | Response       |
|--------|-----------------------------|-------------------------------------|----------------|
| `POST` | `/api/v1/transactions`      | Submit a transaction for evaluation | `202 Accepted` |
| `GET`  | `/api/v1/transactions/{id}` | Get transaction status by ID        | `200 OK`       |

---

### Request — Submit Transaction

```http
POST /api/v1/transactions
Content-Type: application/json
 
{
  "userId": "user_101",
  "amount": 15000
}
```

### Response

```http
HTTP/1.1 202 Accepted
 
{
    "success": true,
    "message": "Transaction accepted into processing pipeline.",
    "data": {
        "transactionId": "1dd81b4a-72c4-4d65-a559-508cd6ef9a99",
        "userId": "user-101",
        "amount": 50.0,
        "status": "PENDING"
    },
    "timestamp": "2026-05-24T05:01:41.480211200Z"
}
```

### Request — Retrieve Transaction Status

```http
POST /api/v1/transactions/{id}
```

### Response

```http
HTTP/1.1 200 Ok
 
{
    "success": true,
    "message": "Transaction fetched",
    "data": {
        "transactionId": "c7a61615-ac45-4173-b825-3f19f622c273",
        "userId": "user_101",
        "transactionStatus": "PENDING",
        "initialTransactionTime": "2026-05-24T06:41:41.391Z"
    },
    "timestamp": "2026-05-24T06:41:59.321047200Z"
}
```
---