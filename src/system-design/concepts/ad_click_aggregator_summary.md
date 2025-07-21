# Ad Click Aggregator System Design Summary

## 1. System Objective
Design a scalable, fault-tolerant Ad Click Aggregator system to efficiently ingest, process, aggregate, and report ad click events. The system must ensure low latency, high throughput, strong reliability, and extensibility.

## 2. Key Non-Functional Requirements (NFRs)
- **Latency**:
    - Click to aggregated report availability: 1-5 minutes.
    - Query latency for reports: Sub-second to a few seconds.
- **Throughput**:
    - Ingestion: Target 100,000s clicks/sec, peak 1,000,000 clicks/sec.
    - Reporting API: 100s of queries per second (QPS).
- **Scalability**: All components must be horizontally scalable.
- **Reliability**:
    - System Availability: Aim for 99.99%.
    - Data Processing: Exactly-once semantics for critical aggregation pipelines.
- **Data Retention**:
    - Raw click data: 1-2 years (archival).
    - Aggregated data: 2-3 years (hot/warm), longer in cold storage if needed.

## 3. High-Level Architecture & Core Technology Choices

- **Ingestion Layer**:
    - **Components**: Load Balancers (e.g., AWS ALB/NLB) distributing traffic to a fleet of stateless Ingestion Service instances (e.g., microservices on Kubernetes, EC2 Auto Scaling Group).
    - **Endpoints**: Supports pixel tracking (HTTP GET) and server-to-server (HTTP POST) ingestion.
    - **Function**: Validates, minimally processes (e.g., timestamping), and forwards click events to the message queue.

- **Message Queue**:
    - **Technology**: Apache Kafka.
    - **Purpose**: Acts as a durable, scalable buffer between ingestion and stream processing. Decouples components and handles backpressure.
    - **Configuration**: Partitioned topics for parallelism, replication for fault tolerance.

- **Stream Processing Layer**:
    - **Technology**: Apache Flink.
    - **Purpose**: Real-time data enrichment (geo-lookup, user-agent parsing), aggregation (windowed counts, unique user estimation), basic rule-based fraud detection, and data transformation.
    - **Features**: Event time processing, stateful computations, checkpointing for fault tolerance, exactly-once semantics.

- **Data Storage Layer**:
    - **Raw Click Storage**:
        - **Technology**: Cloud Object Storage (e.g., AWS S3, Google Cloud Storage).
        - **Format**: Columnar formats like Apache Parquet or ORC for efficient querying by batch analytics/ML.
        - **Partitioning**: By date/time (e.g., `year/month/day/hour`).
    - **Aggregated Data Storage**:
        - **Technology**: OLAP Database (Apache Druid preferred for V1; ClickHouse as a strong alternative).
        - **Purpose**: Stores time-series aggregated metrics (e.g., clicks per minute/hour/day per dimension).
        - **Schema**: Optimized for fast analytical queries, rollups, and low-latency data ingestion.
    - **Metadata Storage**:
        - **Technology**: Relational Database (e.g., PostgreSQL, MySQL).
        - **Purpose**: Stores metadata about campaigns, ads, advertisers, publishers. Used for enrichment in the stream processor.

- **Reporting & Querying Layer**:
    - **Components**: API Gateway (for routing, auth, rate limiting) -> Query Service Layer -> OLAP Database.
    - **APIs**: RESTful or GraphQL APIs for clients to query aggregated data.
    - **Integration**: Supports BI tools (e.g., Apache Superset, Tableau) and custom dashboards.

## 4. Core Data Models / Schemas

- **Raw Click Event (Illustrative)**:
    - `click_id` (UUID), `timestamp` (event time), `user_id` (anonymized/pseudonymized), `ip_address` (partially anonymized after geo-enrichment), `user_agent_raw`, `ad_id`, `campaign_id`, `publisher_id`, `impression_id` (optional), `click_url`, `cost_data` (optional), `geo_country`, `geo_city`, `device_type`, `os_type`, `browser_type`, `fraud_flags` (array of detected fraud rule IDs).

- **Aggregated Data Schema (OLAP - Illustrative for Hourly Rollup)**:
    - **Dimensions**: `timestamp_hour` (DATETIME), `ad_id` (STRING), `campaign_id` (STRING), `publisher_id` (STRING), `country` (STRING), `device_type` (STRING).
    - **Metrics**: `click_count` (LONG), `unique_users_hll` (HyperLogLog), `total_cost` (DOUBLE).

- **Metadata Schema (Relational - Illustrative)**:
    - `Campaigns`: `campaign_id`, `advertiser_id`, `name`, `start_date`, `end_date`, `budget`.
    - `Ads`: `ad_id`, `campaign_id`, `creative_id`, `ad_type`.
    - `Advertisers`: `advertiser_id`, `name`.
    - `Publishers`: `publisher_id`, `name`, `domain`.

## 5. Key Scalability & Reliability Principles

- **Scalability**:
    - **Horizontal Scaling**: All major components (Ingestion Service, Kafka, Flink, Druid/ClickHouse, Query Service) are designed to scale horizontally by adding more instances/nodes.
    - **Partitioning/Sharding**: Data is partitioned in Kafka and sharded in OLAP stores to distribute load.
    - **Auto-scaling**: Cloud-based compute resources can leverage auto-scaling based on load.
- **Reliability**:
    - **Redundancy**: Components deployed across multiple Availability Zones (AZs).
    - **Kafka**: Data replication across brokers.
    - **Flink**: Checkpointing to durable storage (e.g., S3) for state recovery. High Availability (HA) setup for JobManager.
    - **Data Stores**: Durable storage options, replication, and backup/restore procedures.
    - **Dead Letter Queues (DLQs)**: For handling unprocessable messages.
    - **Data Reprocessing**: Capability to reprocess raw data from object storage if needed.
    - **Exactly-Once Semantics (EOS)**: Implemented in Flink for critical aggregation pipelines to prevent data loss or duplication.

## 6. Fraud Detection & Security

- **Fraud Detection (V1 - Basic)**:
    - Rule-based checks implemented within the Flink pipeline (e.g., IP blacklisting, user-agent filtering, click velocity/frequency checks per user/IP).
    - Raw click data stored for future advanced ML-based fraud detection.
- **Security**:
    - **Data in Transit**: HTTPS/TLS for all external and sensitive internal communications.
    - **Data at Rest**: Encryption for data in object storage, OLAP databases, and metadata stores (e.g., SSE-S3, TDE).
    - **Access Control**: Strong authentication (OAuth 2.0, API Keys) and authorization (RBAC) for APIs. IAM roles for cloud resources.
    - **Network Security**: VPCs, private/public subnets, security groups/firewalls. Web Application Firewall (WAF) for public endpoints.
    - **Application Security**: Input validation, secrets management (e.g., HashiCorp Vault, AWS Secrets Manager).
    - **Compliance**: PII handling (anonymization/pseudonymization), audit logging.
