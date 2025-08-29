# System Design: Gaming App Session Management (V2 - From Scratch)

---

## 1. Introduction

This document outlines the system design for a highly available, scalable, and low-latency session management system for a large-scale multiplayer online game. The system is responsible for managing the lifecycle of user sessions, from authentication and login to in-game state persistence and graceful termination. It must support millions of concurrent users with real-time requirements.

## 2. Requirements

### 2.1. Functional Requirements (FRs)

*   **FR1: User Authentication:** Users must be able to log in with credentials (username/password, OAuth).
*   **FR2: Session Creation & Management:** A unique, secure session must be created for each logged-in user.
*   **FR3: Real-time Presence:** The system must track the online/offline/in-game status of users in real-time.
*   **FR4: Multi-Device Support:** Users should be able to have one active session at a time, with a smooth transition between devices (e.g., log in on PC, get logged out on mobile).
*   **FR5: Session State Persistence:** Critical session data (e.g., player inventory, location, match state) must be persisted.
*   **FR6: Graceful & Forceful Termination:** Support for both graceful logout and forceful session termination (e.g., by an admin or due to network loss).

### 2.2. Non-Functional Requirements (NFRs)

*   **NFR1: High Availability:** The system must be highly available (99.99% uptime) to prevent disrupting gameplay.
*   **NFR2: Low Latency:** Session-related operations (e.g., state updates, presence checks) should be very fast (< 50ms at the 99th percentile).
*   **NFR3: High Scalability:** The system must scale to handle 10 million concurrent users.
*   **NFR4: Strong Consistency:** Session state must be strongly consistent to avoid data conflicts (e.g., item duplication).
*   **NFR5: Security:** The system must be secure against common threats like session hijacking, replay attacks, and DDoS.
*   **NFR6: Durability:** Session data must not be lost in case of failures.

## 3. Capacity Estimation & System Constraints

*   **Total Users:** 100 Million
*   **Daily Active Users (DAU):** 20 Million (20% of total)
*   **Peak Concurrent Users (CCU):** 10 Million
*   **Session Duration:** Average 2 hours.
*   **Session Data Size:** ~10 KB per session (player profile, inventory, state).
*   **Real-time Updates per User:** 1 update every 10 seconds (e.g., location, health).

**Calculations:**

*   **Peak QPS (Session Service):**
    *   Login QPS: 10M users / 2 hours = ~1,400 logins/sec.
    *   State Update QPS: 10M users / 10s = 1,000,000 updates/sec.
    *   Presence Update QPS: 1M updates/sec (tied to state updates).
*   **Storage (Hot Cache - Redis):** 10M sessions * 10 KB/session = **100 GB**.
*   **Storage (Persistent DB):** 100M users * 10 KB/session = **1 TB** (for user profile data, not just active session).
*   **Bandwidth (Real-time Updates):** 1M updates/sec * 1 KB/update (payload size) = **1 GB/s**.

## 4. High-Level Architecture

```text
+------------------------+
|      Game Client       |
| (PC/Mobile/Console)    |
+-----------+------------+
            |
 (HTTPS)    |    (WebSocket)
            |
+-----------v------------+      +------------------------+
|      Network Edge      |      |   Real-time Layer      |
|                        |      |                        |
|  +------------------+  |      |  +------------------+  |
|  |  Load Balancer   |  |      |  | WebSocket Service|  |
|  +------------------+  |      |  +------------------+  |
|           |            |      |      ^      | (2)     |
|           v            |      |      |      |         |
|  +------------------+  |      |      |      |         |
|  |   API Gateway    |  |      |      +------+         |
|  +------------------+  |      |                       |
|   (1) |      |       | |      |                       |
+-------+------+-------+-+      +-----------+-----------+
        |      |       |                     |
        |      |       |                     |
+-------v------v-------v---------------------v-----------+
|                  Core Services                         |
|                                                        |
| +----------+   +-------------+   +----------+   +-----------+ |
| |   Auth   |   |   Session   |   | Presence |   | Matchmaking | |
| | Service  |   |   Service   |   | Service  |   |  Service    | |
| +----+-----+   +-----+-------+   +----+-----+   +-----------+ |
|      | (3)          | (4)              | (5)                 |
+------|--------------|------------------|---------------------+
       |              |                  |
       |              |                  |
+------v--------------v------------------v---------------------+
|                     Data Stores                            |
|                                                            |
|  +-----------------+  +-----------------+  +---------------+ |
|  |  Redis Cluster  |  | Cassandra Cluster |  | Kafka Bus   | |
|  +-----------------+  +-----------------+  +---------------+ |
|                                                            |
+------------------------------------------------------------+

--- Data Flows ---
(1) API Gateway routes HTTP traffic (login, etc.) to appropriate services.
(2) WebSocket Service maintains persistent connections with clients for real-time events. It uses Redis Pub/Sub for scaling.
(3) Auth Service: Validates against Cassandra, stores refresh tokens in Redis.
(4) Session Service: Manages session state in Redis, persists events to Cassandra via Kafka.
(5) Presence Service: Consumes Kafka events (from WebSocket Svc) to update presence status in Redis.
```

## 5. API Design

### 5.1. REST APIs (via API Gateway)

*   `POST /v1/auth/login`
    *   **Request:** `{ "username": "...", "password": "..." }`
    *   **Response:** `{ "accessToken": "...", "refreshToken": "...", "websocketUrl": "wss://..." }`
*   `POST /v1/auth/refresh`
    *   **Request:** `{ "refreshToken": "..." }`
    *   **Response:** `{ "accessToken": "..." }`
*   `GET /v1/sessions/@me`
    *   **Description:** Get current user's session details.
    *   **Headers:** `Authorization: Bearer <accessToken>`
    *   **Response:** `{ "sessionId": "...", "userId": "...", "state": { ... } }`

### 5.2. WebSocket Events (Real-time)

*Connection URL from login response.*

*   **Client -> Server**
    *   `{ "event": "UPDATE_STATE", "payload": { "new_location": "..." } }`
    *   `{ "event": "HEARTBEAT", "payload": { "timestamp": "..." } }`
*   **Server -> Client**
    *   `{ "event": "STATE_UPDATED", "payload": { ... } }`
    *   `{ "event": "PRESENCE_UPDATE", "payload": { "userId": "...", "status": "online" } }`
    *   `{ "event": "SESSION_TERMINATED", "payload": { "reason": "LOGGED_IN_ELSEWHERE" } }`

## 6. Component Deep Dive

### 6.1. API Gateway
*   **Role:** Single entry point for all HTTP requests.
*   **Functions:** SSL termination, rate limiting, authentication (JWT validation), request routing.
*   **Technology:** NGINX, Kong, AWS API Gateway.

### 6.2. Authentication Service
*   **Role:** Manages user identity.
*   **Flow:**
    1.  Validates user credentials against the **Cassandra** `users` table.
    2.  Generates a short-lived JWT `accessToken` (e.g., 15 mins) and a long-lived `refreshToken`.
    3.  Stores the `refreshToken` in **Redis** for quick validation.

### 6.3. Session Service
*   **Role:** Core logic for session lifecycle.
*   **Flow (on login):**
    1.  Receives user details from the Auth service (or via validated JWT).
    2.  Checks **Redis** for an existing session for the `userId`.
    3.  If one exists, terminates the old session (sends `SESSION_TERMINATED` via WebSocket).
    4.  Creates a new `sessionId`.
    5.  Stores the session object in **Redis** (hot cache) with a TTL (e.g., 2.5 hours).
    6.  Asynchronously persists the session creation event to **Cassandra** via **Kafka** for audit and recovery.

### 6.4. WebSocket Service
*   **Role:** Manages persistent WebSocket connections for real-time, bidirectional communication.
*   **Connection Management:** Maintains a mapping of `connectionId` to `sessionId`/`userId` in its local memory, backed by **Redis**. This allows any WebSocket node to push messages to any user.
*   **Scaling:** Horizontally scaled. A **Redis Pub/Sub** mechanism is used to route messages. When a service needs to send a message to a user, it publishes to a channel (`user:<userId>`). The specific WebSocket server holding the connection for that user subscribes to the channel and forwards the message.

### 6.5. Presence Service
*   **Role:** Tracks user online/offline/in-game status.
*   **Mechanism:** It relies on WebSocket heartbeats. The WebSocket service publishes `connect` / `disconnect` / `heartbeat` events to a Kafka topic. The Presence service consumes these events, updates the user's status and `lastSeen` timestamp in **Redis**.

## 7. Data Schema

### 7.1. Redis (Cache)

*   **Key:** `session:<sessionId>`
    *   **Type:** Hash
    *   **Fields:** `userId`, `clientIp`, `device`, `createdAt`, `expiresAt`, `gameState` (JSON blob)
*   **Key:** `user_to_session:<userId>`
    *   **Type:** String
    *   **Value:** `sessionId` (Used for quick lookup and enforcing one session per user)
*   **Key:** `presence:<userId>`
    *   **Type:** String
    *   **Value:** `ONLINE | OFFLINE | IN_GAME`

### 7.2. Cassandra (Persistent Storage)

```sql
-- Stores user credentials and profile
CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    username TEXT,
    hashed_password TEXT,
    email TEXT,
    created_at TIMESTAMP,
    -- Other profile data
    STATIC INDEX ON (username)
);

-- Append-only log of session events for audit/analytics
CREATE TABLE session_history (
    user_id UUID,
    session_id UUID,
    event_type TEXT, -- CREATED, TERMINATED
    event_timestamp TIMESTAMP,
    client_ip TEXT,
    device TEXT,
    PRIMARY KEY ((user_id), event_timestamp)
) WITH CLUSTERING ORDER BY (event_timestamp DESC);
```

## 8. Scalability & High Availability

*   **Stateless Services:** All core services (Auth, Session, Presence) are stateless, allowing for simple horizontal scaling behind the load balancer.
*   **WebSocket Scaling:** The WebSocket service is stateful regarding connections but uses Redis Pub/Sub to decouple message routing from connection handling, enabling horizontal scaling.
*   **Database Scaling:**
    *   **Redis:** Deployed as a cluster with sharding (by key) and replication for HA.
    *   **Cassandra:** Natively supports multi-datacenter replication and horizontal scaling by adding more nodes. The partition key (`user_id`) ensures even data distribution.
*   **Fault Tolerance:**
    *   If a service node fails, the load balancer redirects traffic to healthy nodes.
    *   If a WebSocket node fails, clients will automatically reconnect (with backoff) to a new node, re-authenticate, and resume their session.
    *   If a Redis node fails, a replica is promoted. Some cache misses may occur, requiring a read from Cassandra, but the system remains operational.
    *   Cassandra's multi-DC replication ensures durability and availability even if a whole datacenter goes down.

## 9. Security Considerations

*   **Authentication:** JWTs prevent the need to send credentials with every request. Short-lived access tokens minimize the impact of a leak.
*   **Authorization:** API Gateway and services validate the JWT signature and claims on every protected request.
*   **Session Hijacking:** Use of TLS on all communication. Binding session to IP address can be an optional, stricter security measure.
*   **Anti-Cheat:** The server is the source of truth for game state. Client actions are validated server-side. Suspicious activity patterns (e.g., impossible speed) are flagged and published to Kafka for an offline analysis/banning system.
*   **DDoS Protection:** Use a service like Cloudflare or AWS Shield at the edge.

## 10. Monitoring & Telemetry

*   **Metrics:**
    *   **Latency:** p95, p99 latencies for all API endpoints.
    *   **Error Rates:** HTTP 5xx rates, WebSocket disconnect reasons.
    *   **System:** CPU/Memory utilization of services and databases.
    *   **Business:** Concurrent users (CCU), new sessions/sec, average session duration.
*   **Tools:**
    *   **Logging:** ELK Stack (Elasticsearch, Logstash, Kibana).
    *   **Metrics:** Prometheus.
    *   **Dashboards:** Grafana.
    *   **Alerting:** Alertmanager.
