# TitanGuard — Real-Time Fraud Detection Engine

TitanGuard is a high-performance, event-driven backend system designed to evaluate financial transactions for fraud in 
real time. By leveraging an asynchronous architecture, the system ensures that transaction ingestion remains non-blocking 
and lightning-fast, while complex fraud analysis happens seamlessly in the background.

---

## Key Features

- **Asynchronous Processing:** Transactions are accepted immediately via a REST API and queued for background evaluation using **Apache Kafka**.
- **Real-Time Fraud Evaluation:** A dedicated rule engine inspects transactions for high-value anomalies and suspicious activity patterns.
- **Intelligent Rate Limiting:** Integrated **Redis-based rate limiting** prevents system abuse and identifies high-frequency transaction bursts.
- **Automated Alerts:** Fraudulent or suspicious transactions are automatically flagged and persisted in **MongoDB** for further investigation.
- **Fault Tolerance:** Robust error handling with **Dead Letter Queue (DLQ)** support to ensure no transaction data is lost during processing failures.

---

## Architecture Overview

![Project Flow](project-flow.png)

TitanGuard follows a modern, event-driven microservices pattern:

1.  **Ingestion:** A Spring Boot REST API receives a transaction request.
2.  **Rate Limiting:** Redis validates the request frequency against per-user limits.
3.  **Producers:** Validated requests are published to a Kafka topic (`raw_transaction`).
4.  **Consumers:** Background workers consume transaction events and run them through the **Fraud Rule Engine**.
5.  **Rule Engine:** Evaluates transactions based on:
    *   **Velocity:** Maximum transaction count per time window.
    *   **Value:** High-value transaction thresholds.
    *   **Timing:** Transactions occurring during "suspicious" hours (e.g., 1 AM - 4 AM).
    *   **Daily Total:** Sum of a user's approved transactions in a day.
6.  **Persistence:** Final statuses and fraud alerts are stored in MongoDB.

---

## Tech Stack

| Layer                | Technology              | Purpose                                   |
|----------------------|-------------------------|-------------------------------------------|
| **Language**         | Java 21                 | Core application development              |
| **Framework**        | Spring Boot 3.x         | API Development & Kafka Orchestration     |
| **Message Broker**   | Apache Kafka (KRaft)    | Distributed event streaming & ingestion   |
| **Cache / Tracking** | Redis                   | Rate limiting & transaction windowing     |
| **Database**         | MongoDB                 | Storage for transactions and fraud alerts |
| **Containerization** | Docker + Docker Compose | Simplified infrastructure deployment      |

---

## API Reference

### Transactions Ingestion
`POST /api/v1/transactions`

**Description:** Submits a new transaction for fraud evaluation.

**Request Body:**
```json
{
  "userId": "user_101",
  "amount": 15000.0
}
```

**Response:** `202 Accepted`
```json
{
    "success": true,
    "message": "Transaction accepted into processing pipeline.",
    "data": {
        "transactionId": "1dd81b4a-72c4-4d65-a559-508cd6ef9a99",
        "userId": "user_101",
        "amount": 15000.0,
        "status": "PENDING"
    },
    "timestamp": "2026-05-24T05:01:41.480Z"
}
```

### Transaction Status
`GET /api/v1/transactions/{id}`

**Description:** Retrieves the current status of a transaction (PENDING, APPROVED, FRAUDULENT, FAILED).

**Response:** `200 OK`

---

## ⚙️ Setup & Installation

### Prerequisites
- JDK 21+
- Docker & Docker Compose
- Maven (optional, if not using the included wrapper)

### Running with Docker Compose
1. Clone the repository.
2. Start everything (backend, Kafka, MongoDB, Redis):
   ```bash
   docker-compose up -d --build
   ```
   The API will be available on `http://localhost:8100`.

### Running the backend locally
Start only the infrastructure and run the app from your IDE or the command line:
```bash
docker-compose up -d mongodb redis kafka
cd backend
./mvnw spring-boot:run
```
The default config points to localhost, so no environment variables are needed.
To override anything, copy `.env.example` to `.env` and edit it.

### Running the tests
```bash
cd backend
./mvnw test
```

### Default Configuration
- **Server Port:** 8100
- **Rate Limit:** 5 requests per 60s per user
- **Fraud Rules (Dev):**
  - High Value Limit: > 50,000
  - Window Max Count: 3 transactions per 60s
  - Suspicious Hours: 01:00 - 04:00 UTC
  - Daily Total Limit: 50,000

---

## 📂 Project Structure

- `com.reon.titan_backend.controller`: REST endpoints for transaction management.
- `com.reon.titan_backend.kafka`: Producers and consumers for event-driven flow.
- `com.reon.titan_backend.rule`: Logic for fraud detection.
- `com.reon.titan_backend.service`: Core business logic and database interactions.
- `com.reon.titan_backend.document`: MongoDB entity definitions.
- `com.reon.titan_backend.dto`: Data Transfer Objects for API and Kafka events.
