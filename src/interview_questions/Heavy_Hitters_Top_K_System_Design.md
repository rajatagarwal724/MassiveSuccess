# Heavy Hitters / Top K System Design

## Table of Contents
1. [Problem Statement](#problem-statement)
2. [Requirements](#requirements)
3. [High-Level Architecture](#high-level-architecture)
4. [Core Components](#core-components)
5. [Data Flow](#data-flow)
6. [Algorithms and Data Structures](#algorithms-and-data-structures)
7. [Storage Design](#storage-design)
8. [API Design](#api-design)
9. [Scalability and Performance](#scalability-and-performance)
10. [Fault Tolerance](#fault-tolerance)
11. [Monitoring and Observability](#monitoring-and-observability)
12. [Implementation Considerations](#implementation-considerations)

## Problem Statement

Design a distributed system that can identify the Top K most frequent items (Heavy Hitters) from massive, high-velocity data streams in real-time. The system should handle billions of events per day across multiple data centers while providing low-latency query responses.

**Examples of Use Cases:**
- Top trending hashtags on social media
- Most frequently accessed URLs on a CDN
- Most popular search queries
- Top selling products in e-commerce
- Most active users in a gaming platform

## Requirements

### Functional Requirements
- **Real-time Processing**: Process millions of events per second
- **Top K Queries**: Return top K most frequent items for different time windows
- **Multiple Data Types**: Support various data types (strings, numbers, URLs, etc.)
- **Time Window Queries**: Support sliding windows (last hour, day, week)
- **Multi-dimensional Analysis**: Group by different dimensions (region, category, etc.)
- **Historical Data**: Provide historical top K data
- **Approximate Results**: Accept approximate results for better performance

### Non-Functional Requirements
- **Scale**: Handle 10M+ events per second globally
- **Latency**: Query response time < 100ms (P99)
- **Availability**: 99.99% uptime
- **Consistency**: Eventually consistent results acceptable
- **Storage**: Efficient storage for high-frequency data
- **Cost**: Optimize for storage and compute costs

### Constraints
- Data retention: 1 year for detailed data, longer for aggregated data
- Memory usage: Optimize for memory efficiency
- Network bandwidth: Minimize cross-datacenter traffic
- Geographic distribution: Support multiple regions

## High-Level Architecture

The system follows a lambda architecture pattern with real-time and batch processing layers:

### System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                 HEAVY HITTERS / TOP-K SYSTEM                            │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────┐    ┌──────────────────┐    ┌────────────────────┐    ┌─────────────────┐
│   DATA SOURCES  │    │  INGESTION LAYER │    │  STREAM PROCESSING │    │  STORAGE LAYER  │
│                 │    │                  │    │                    │    │                 │
│ ┌─────────────┐ │    │ ┌──────────────┐ │    │ ┌────────────────┐ │    │ ┌─────────────┐ │
│ │Web Apps     │ │    │ │Load Balancer │ │    │ │Apache Flink    │ │    │ │Redis Cluster│ │
│ │Mobile Apps  │─┼────┤ │              │─┼────┤ │Stream Processor│─┼────┤ │Hot Cache    │ │
│ │IoT Devices  │ │    │ │Event         │ │    │ │                │ │    │ │             │ │
│ │External APIs│ │    │ │Collectors    │ │    │ │Count-Min Sketch│ │    │ │TimescaleDB  │ │
│ │System Logs  │ │    │ │              │ │    │ │Window Manager  │ │    │ │Time Series  │ │
│ └─────────────┘ │    │ │Apache Kafka  │ │    │ │Top-K Computer  │ │    │ │             │ │
│                 │    │ │Message Queue │ │    │ └────────────────┘ │    │ │Object Store │ │
│                 │    │ └──────────────┘ │    │                    │    │ │S3/GCS       │ │
│                 │    │                  │    │                    │    │ │             │ │
│                 │    │                  │    │                    │    │ │Cassandra    │ │
│                 │    │                  │    │                    │    │ │Distributed  │ │
└─────────────────┘    └──────────────────┘    └────────────────────┘    └─────────────────┘
         │                        │                         │                        │
         └────────────────────────┼─────────────────────────┼────────────────────────┘
                                  │                         │
                                  ▼                         ▼
┌─────────────────┐    ┌──────────────────┐              ┌────────────────────┐
│ BATCH PROCESSING│    │    API LAYER     │              │     MONITORING     │
│                 │    │                  │              │                    │
│ ┌─────────────┐ │    │ ┌──────────────┐ │              │ ┌────────────────┐ │
│ │Apache Spark │ │    │ │API Gateway   │ │              │ │Prometheus      │ │
│ │Batch Jobs   │ │    │ │              │ │              │ │Metrics         │ │
│ │             │ │    │ │Query Service │ │              │ │                │ │
│ │HDFS         │ │    │ │              │ │              │ │ELK Stack       │ │
│ │Data Lake    │ │    │ │Aggregation   │ │              │ │Logs            │ │
│ └─────────────┘ │    │ │Service       │ │              │ │                │ │
│                 │    │ │              │ │              │ │Jaeger Tracing  │ │
│                 │    │ │Query Cache   │ │              │ │                │ │
│                 │    │ └──────────────┘ │              │ │AlertManager    │ │
└─────────────────┘    └──────────────────┘              └────────────────────┘
                                 │
                                 ▼
                    ┌──────────────────────┐
                    │       CLIENTS        │
                    │                      │
                    │ ┌──────────────────┐ │
                    │ │Analytics         │ │
                    │ │Dashboard         │ │
                    │ │                  │ │
                    │ │Mobile Apps       │ │
                    │ │                  │ │
                    │ │API Clients       │ │
                    │ └──────────────────┘ │
                    └──────────────────────┘

Data Flow:
[Sources] → [Load Balancer] → [Event Collectors] → [Kafka] → [Flink Processing] 
                                     ↓                              ↓
                               [Batch Processing] ←── [Object Storage] → [Cache/DB]
                                     ↓                              ↓
                               [Long-term Storage] ←────────── [API Layer] → [Clients]
```

### Data Processing Flow

```
REAL-TIME DATA INGESTION FLOW:
═══════════════════════════════

Event Sources    Kafka Queue    Flink Processor    Storage Layer
     │               │              │                 │
     │ 1. Stream      │              │                 │
     │    Events      │              │                 │
     ├───────────────►│              │                 │
     │               │              │                 │
     │               │ 2. Consume   │                 │
     │               │    Events    │                 │
     │               ├─────────────►│                 │
     │               │              │                 │
     │               │              │ 3. Update       │
     │               │              │    Count-Min    │
     │               │              │    Sketch       │
     │               │              │                 │
     │               │              │ 4. Compute      │
     │               │              │    Top-K        │
     │               │              │                 │
     │               │              │ 5. Store Hot    │
     │               │              │    Results      │
     │               │              ├────────────────►│ Redis Cache
     │               │              │                 │
     │               │              │ 6. Store        │
     │               │              │    Aggregated   │
     │               │              │    Data         │
     │               │              ├────────────────►│ TimescaleDB
     │               │              │                 │

QUERY PROCESSING FLOW:
═════════════════════

Client       API Gateway    Query Service    Redis Cache    TimescaleDB
  │              │              │               │              │
  │ 1. GET       │              │               │              │
  │ /topk?k=10   │              │               │              │
  ├─────────────►│              │               │              │
  │              │              │               │              │
  │              │ 2. Forward   │               │              │
  │              │    Request   │               │              │
  │              ├─────────────►│               │              │
  │              │              │               │              │
  │              │              │ 3. Check      │              │
  │              │              │    Hot Cache  │              │
  │              │              ├──────────────►│              │
  │              │              │               │              │
  │              │              │ ┌─ CACHE HIT ─┐              │
  │              │              │ │4a. Return   │              │
  │              │              │ │   Cached    │              │
  │              │              │ │   Results   │              │
  │              │              │◄┤             │              │
  │              │              │ └─────────────┘              │
  │              │              │                              │
  │              │              │ ┌─ CACHE MISS ┐             │
  │              │              │ │4b. Query     │             │
  │              │              │ │   Historical │             │
  │              │              │ │   Data       │             │
  │              │              │ ├─────────────────────────────┤
  │              │              │ │             │              │
  │              │              │ │5b. Return   │              │
  │              │              │ │   Aggregated│              │
  │              │              │ │   Data      │              │
  │              │              │◄┤             │              │
  │              │              │ │             │              │
  │              │              │ │6b. Compute  │              │
  │              │              │ │   Top-K     │              │
  │              │              │ │             │              │
  │              │              │ │7b. Cache    │              │
  │              │              │ │   Results   │              │
  │              │              │ ├──────────────►             │
  │              │              │ └─────────────┘              │
  │              │              │                              │
  │              │ 5. Top-K     │                              │
  │              │    Results   │                              │
  │              │◄─────────────┤                              │
  │              │              │                              │
  │ 6. JSON      │              │                              │
  │    Response  │              │                              │
  │◄─────────────┤              │                              │
  │              │              │                              │

BACKGROUND PROCESSES:
═══════════════════

Flink Processor          Redis Cache          TimescaleDB
      │                       │                    │
      │ Periodic Window       │                    │
      │ Processing            │                    │
      │                       │                    │
      │ Update Cached         │                    │
      │ Results               │                    │
      ├──────────────────────►│                    │
      │                       │                    │
      │                       │ Background         │
      │                       │ Compaction         │
      │                       │                    │
      │                       ├───────────────────►│
      │                       │                    │
```

### Processing Pipeline Architecture

```
DATA PARTITIONING & PROCESSING PIPELINE:
═══════════════════════════════════════

┌─────────────┐    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Raw Events  │    │ Hash Function   │    │ Data Partitions │    │ Time Windows    │
│             │    │                 │    │                 │    │                 │
│ item_id:    │    │                 │    │ ┌─────────────┐ │    │ ┌─────────────┐ │
│ "hashtag1"  ├───►│ hash(item_id) % ├───►│ │Partition 1  │ ├───►│ │Window 1     │ │
│ timestamp:  │    │ num_partitions  │    │ │Items A-D    │ │    │ │00:00-01:00  │ │
│ 1234567890  │    │                 │    │ └─────────────┘ │    │ └─────────────┘ │
│             │    │                 │    │                 │    │                 │
│ item_id:    │    │                 │    │ ┌─────────────┐ │    │ ┌─────────────┐ │
│ "product2"  │    │                 │    │ │Partition 2  │ │    │ │Window 2     │ │
│ timestamp:  │    │                 │    │ │Items E-H    │ │    │ │01:00-02:00  │ │
│ 1234567891  │    │                 │    │ └─────────────┘ │    │ └─────────────┘ │
│             │    │                 │    │                 │    │                 │
│     ...     │    │                 │    │ ┌─────────────┐ │    │ ┌─────────────┐ │
│             │    │                 │    │ │Partition N  │ │    │ │Window N     │ │
│             │    │                 │    │ │Items ...    │ │    │ │...          │ │
│             │    │                 │    │ └─────────────┘ │    │ └─────────────┘ │
└─────────────┘    └─────────────────┘    └─────────────────┘    └─────────────────┘
       │                     │                       │                       │
       └─────────────────────┼───────────────────────┼───────────────────────┘
                             │                       │
                             ▼                       ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           COUNT-MIN SKETCH PROCESSING                           │
└─────────────────────────────────────────────────────────────────────────────────┘

Window 1           Window 2           Window 3
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│Count-Min    │    │Count-Min    │    │Count-Min    │
│Sketch 1     │    │Sketch 2     │    │Sketch 3     │
│             │    │             │    │             │
│ [w×d matrix]│    │ [w×d matrix]│    │ [w×d matrix]│
│             │    │             │    │             │
│ h1: [1,3,7,2│    │ h1: [2,1,4,3│    │ h1: [5,2,1,8│
│ h2: [2,1,4,5│    │ h2: [1,6,2,7│    │ h2: [3,4,6,2│
│ h3: [4,6,1,3│    │ h3: [3,2,8,1│    │ h3: [1,7,3,5│
│ ...       ] │    │ ...       ] │    │ ...       ] │
└─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │
       ▼                   ▼                   ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│Min Heap     │    │Min Heap     │    │Min Heap     │
│Size K       │    │Size K       │    │Size K       │
│             │    │             │    │             │
│   Root      │    │   Root      │    │   Root      │
│  /    \     │    │  /    \     │    │  /    \     │
│ N1    N2    │    │ N4    N5    │    │ N7    N8    │
│/  \  /  \   │    │/  \  /  \   │    │/  \  /  \   │
│N3 ...  N6   │    │N6 ...  N9   │    │N9 ... N12   │
└─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │
       └───────────────────┼───────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          RESULT AGGREGATION                                     │
│                                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐      │
│  │   Merge     │    │  Priority   │    │   Final     │    │  Storage    │      │
│  │ Algorithm   │───►│ Sorting     │───►│  Top-K      │───►│   Tiers     │      │
│  │             │    │             │    │  Results    │    │             │      │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘      │
└─────────────────────────────────────────────────────────────────────────────────┘
                                                                    │
                                                                    ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              STORAGE TIERS                                      │
│                                                                                 │
│ ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│ │    L1:      │  │    L2:      │  │    L3:      │  │    L4:      │             │
│ │   Memory    │  │   Redis     │  │ TimescaleDB │  │   S3/GCS    │             │
│ │             │  │             │  │             │  │             │             │
│ │ Current     │  │ Recent      │  │ Historical  │  │ Archive     │             │
│ │ Window      │  │ Windows     │  │ Data        │  │ Storage     │             │
│ │             │  │             │  │             │  │             │             │
│ │ <1ms        │  │ <10ms       │  │ <100ms      │  │ <1s         │             │
│ │ access      │  │ access      │  │ access      │  │ access      │             │
│ └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘             │
│       ↑                ↑                ↑                ↑                     │
│   Hot Data        Warm Data       Cool Data        Cold Data                   │
└─────────────────────────────────────────────────────────────────────────────────┘

Performance Characteristics:
- Memory Usage: O(1/ε × log(1/δ)) per partition
- Time Complexity: O(1) for updates and queries
- Space Efficiency: 99.9% compression vs exact counting
- Accuracy: 99%+ with configurable error bounds (ε=0.01, δ=0.01)
```

### Key Architectural Principles
1. **Microservices Architecture**: Loosely coupled, independently deployable services
2. **Event-Driven Design**: Asynchronous message passing
3. **Horizontal Scalability**: Scale out rather than scale up
4. **Data Locality**: Process data close to where it's generated
5. **Graceful Degradation**: System continues to function with reduced capabilities

## Core Components

### 1. Data Ingestion Layer

**Event Collectors**
- Distributed across multiple data centers
- Handle various protocols (HTTP, gRPC, message queues)
- Perform initial validation and enrichment
- Route events to appropriate processing streams

**Message Queue (Apache Kafka)**
- Partitioned topics for parallel processing
- Configurable retention policies
- Exactly-once delivery semantics
- Cross-datacenter replication

### 2. Stream Processing Engine

**Real-time Processors (Apache Flink/Storm)**
- Stateful stream processing
- Windowing operations (tumbling, sliding, session)
- Fault-tolerant checkpointing
- Dynamic scaling based on load

**Count-Min Sketch Service**
- Probabilistic data structure for frequency estimation
- Memory-efficient approximation
- Configurable error bounds
- Periodic merge operations

### 3. Storage Layer

**In-Memory Cache (Redis Cluster)**
- Hot data for sub-millisecond access
- Distributed across multiple nodes
- Automatic failover and replication
- TTL-based eviction policies

**Time-Series Database (InfluxDB/TimescaleDB)**
- Optimized for time-based queries
- Automatic data compaction
- Multi-level aggregations
- Retention policies

**Object Storage (S3/GCS)**
- Long-term storage for historical data
- Cost-effective for infrequently accessed data
- Compression and archival strategies
- Cross-region replication

### 4. API Gateway and Services

**Query Service**
- RESTful API for top K queries
- GraphQL support for complex queries
- Response caching and rate limiting
- Authentication and authorization

**Aggregation Service**
- Combine results from multiple sources
- Handle different time windows
- Merge approximate results
- Real-time result ranking

## Data Flow

### Real-time Path
1. Events arrive at ingestion layer
2. Events are validated and enriched
3. Events are published to Kafka topics
4. Stream processors consume events
5. Frequency counts are updated in memory
6. Top K results are computed and cached
7. Results are served via API

### Batch Path
1. Raw events are stored in object storage
2. Batch jobs process historical data
3. Accurate counts are computed
4. Results are stored in time-series DB
5. Corrections are applied to real-time results

## Algorithms and Data Structures

### Algorithm Flow Diagram

```
COUNT-MIN SKETCH ALGORITHM FLOW:
═══════════════════════════════

START
  │
  ▼
┌─────────────────────────────────┐
│ Input: Event(item_id, timestamp)│
└─────────────────────────────────┘
  │
  ▼
┌─────────────────────────────────┐
│ Apply k hash functions:         │
│ h1(item_id), h2(item_id), ...   │
│ hk(item_id)                     │
└─────────────────────────────────┘
  │
  ▼
┌─────────────────────────────────┐
│ Update sketch matrix:           │
│ for i = 1 to k:                 │
│   sketch[i][hi(item_id)] += 1   │
└─────────────────────────────────┘
  │
  ▼
┌─────────────────────────────────┐
│ Estimate frequency:             │
│ freq = min(sketch[i][hi])       │
│ for i = 1 to k                  │
└─────────────────────────────────┘
  │
  ▼
┌─────────────────────────────────┐
│ Is frequency > threshold?       │
│ (Heavy Hitter Check)            │
└─────────────────┬───────────────┘
                  │
        ┌─────────┴─────────┐
       YES                 NO
        │                   │
        ▼                   ▼
┌─────────────────┐ ┌─────────────────┐
│ Add to          │ │ Continue        │
│ candidates      │ │ processing      │
└─────────────────┘ └─────────────────┘
        │                   │
        ▼                   │
┌─────────────────┐         │
│ Update Top-K    │         │
│ heap            │         │
└─────────────────┘         │
        │                   │
        └───────┬───────────┘
                │
                ▼
┌─────────────────────────────────┐
│ Process next event              │
└─────────────────────────────────┘
                │
                └── (loop back to input)

HEAVY HITTERS DETECTION:
═══════════════════════

┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Candidate Items │───►│ Validate against│───►│ Filter heavy    │───►│ Output Top-K    │
│ from CMS        │    │ threshold n/k   │    │ hitters         │    │ list            │
└─────────────────┘    └─────────────────┘    └─────────────────┘    └─────────────────┘

WINDOW MANAGEMENT:
═════════════════

┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Window Timer    │───►│ Expire old      │───►│ Slide window    │───►│ Recompute       │
│ (periodic)      │    │ windows         │    │ boundary        │    │ Top-K           │
└─────────────────┘    └─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │ Merge           │
                       │ overlapping     │
                       │ windows         │
                       └─────────────────┘

ERROR BOUNDS CALCULATION:
════════════════════════

Input Parameters:
┌─────────────────┐    ┌─────────────────┐
│ ε: Relative     │    │ δ: Failure      │
│ Error (0.01)    │    │ Probability     │
│                 │    │ (0.01)          │
└─────────────────┘    └─────────────────┘
        │                       │
        ▼                       ▼
┌─────────────────┐    ┌─────────────────┐
│ Width:          │    │ Depth:          │
│ w = ⌈e/ε⌉      │    │ d = ⌈ln(1/δ)⌉ │
│ w ≈ 272         │    │ d ≈ 5          │
└─────────────────┘    └─────────────────┘
        │                       │
        └───────┬───────────────┘
                │
                ▼
┌─────────────────────────────────┐
│ Memory Usage:                   │
│ O(1/ε × log(1/δ))              │
│ ≈ 272 × 5 = 1,360 integers     │
│ ≈ 5.4 KB per sketch            │
└─────────────────────────────────┘

PERFORMANCE CHARACTERISTICS:
═══════════════════════════

┌──────────────────────────────────────────────────────────────┐
│ Operation          │ Time Complexity │ Space Complexity      │
├────────────────────┼─────────────────┼──────────────────────┤
│ Insert/Update      │ O(k) = O(1)     │ O(1/ε × log(1/δ))   │
│ Query Frequency    │ O(k) = O(1)     │ O(1)                │
│ Top-K Update       │ O(log K)        │ O(K)                │
│ Window Slide       │ O(K)            │ O(K)                │
│ Heavy Hitter Check │ O(1)            │ O(1)                │
└──────────────────────────────────────────────────────────────┘

Accuracy Guarantees:
- Frequency estimation error: ≤ ε × total_events
- False positive rate: ≤ δ
- False negative rate: 0 (never misses actual heavy hitters)
```

### Count-Min Sketch
- **Purpose**: Approximate frequency counting
- **Space Complexity**: O(ε⁻¹ log δ⁻¹)
- **Time Complexity**: O(1) for updates and queries
- **Error Bounds**: Configurable epsilon and delta parameters

### Heavy Hitters Algorithm
- **Purpose**: Identify items above frequency threshold
- **Implementation**: Misra-Gries algorithm with Count-Min Sketch
- **Space Complexity**: O(k) where k is number of heavy hitters
- **Accuracy**: Guaranteed to find all items with frequency > n/k

### Top K Computation
- **Data Structure**: Min-heap of size K
- **Updates**: O(log K) for each candidate item
- **Merging**: Combine multiple Top K lists efficiently
- **Approximate Merging**: Use sampling for large-scale merging

### Time Window Management
- **Sliding Windows**: Exponential histogram for approximate sliding windows
- **Tumbling Windows**: Simple bucket-based approach
- **Session Windows**: Gap-based window detection

## Storage Design

### Data Partitioning Strategy
```
Partition Key: hash(item_id) % num_partitions
Time Bucket: timestamp / window_size
Storage Key: partition_key + time_bucket + item_id
```

### Data Models

**Real-time Counts**
```json
{
  "item_id": "string",
  "count": "long",
  "last_updated": "timestamp",
  "window_start": "timestamp",
  "window_end": "timestamp",
  "metadata": {
    "category": "string",
    "region": "string"
  }
}
```

**Aggregated Results**
```json
{
  "window_id": "string",
  "top_k": [
    {
      "item_id": "string",
      "count": "long",
      "rank": "int"
    }
  ],
  "total_events": "long",
  "accuracy_score": "double"
}
```

### Caching Strategy
- **L1 Cache**: Current window top K results in application memory
- **L2 Cache**: Recent windows in Redis cluster
- **L3 Cache**: Historical data in time-series database
- **Cache Invalidation**: TTL-based with write-through policy

## API Design

### RESTful Endpoints

```http
GET /api/v1/topk?k=10&window=1h&category=hashtags
GET /api/v1/topk/historical?k=20&start=2023-01-01&end=2023-01-31
GET /api/v1/count/{item_id}?window=24h
POST /api/v1/events (for real-time event ingestion)
```

### Response Format
```json
{
  "results": [
    {
      "item": "string",
      "count": 1234567,
      "rank": 1,
      "confidence": 0.95
    }
  ],
  "metadata": {
    "window": "1h",
    "total_events": 50000000,
    "accuracy": "approximate",
    "generated_at": "2023-12-01T10:30:00Z"
  }
}
```

### GraphQL Schema
```graphql
type Query {
  topK(k: Int!, window: TimeWindow!, filters: FilterInput): TopKResult
  itemCount(itemId: String!, window: TimeWindow!): CountResult
}

type TopKResult {
  items: [TopKItem!]!
  metadata: ResultMetadata!
}

type TopKItem {
  id: String!
  count: Long!
  rank: Int!
  confidence: Float!
}
```

## Scalability and Performance

### Deployment Architecture

```
MULTI-REGION DEPLOYMENT ARCHITECTURE:
═══════════════════════════════════

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                GLOBAL SERVICES                                          │
│                                                                                         │
│ ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐     │
│ │ Global Load     │  │ CDN/Edge        │  │ DNS Service     │  │ Monitoring      │     │
│ │ Balancer        │  │ Locations       │  │ (Route 53)      │  │ Stack           │     │
│ │ (CloudFlare)    │  │ (CloudFront)    │  │                 │  │ (Datadog)       │     │
│ └─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘     │
└─────────────────────────────────────────────────────────────────────────────────────────┘
            │                        │                        │                │
            └────────────────────────┼────────────────────────┼────────────────┘
                                     │                        │
            ┌────────────────────────┼────────────────────────┼────────────────┐
            │                        │                        │                │
            ▼                        ▼                        ▼                ▼

┌─────────────────────────────────────────┐    ┌─────────────────────────────────────────┐
│            REGION 1 - US EAST           │    │           REGION 2 - EUROPE            │
│                                         │    │                                         │
│  ┌─────────────────────────────────────┐ │    │  ┌─────────────────────────────────────┐ │
│  │         AVAILABILITY ZONE 1a        │ │    │  │         AVAILABILITY ZONE 2a        │ │
│  │                                     │ │    │  │                                     │ │
│  │ ┌─────────────────┐ ┌─────────────┐ │ │    │  │ ┌─────────────────┐ ┌─────────────┐ │ │
│  │ │ Kafka Cluster   │ │Flink Cluster│ │ │    │  │ │ Kafka Cluster   │ │Flink Cluster│ │ │
│  │ │ (3 brokers)     │ │(4 task mgrs)│ │◄┼────┼──┤ │ (3 brokers)     │ │(4 task mgrs)│ │ │
│  │ │                 │ │             │ │ │    │  │ │                 │ │             │ │ │
│  │ │ Redis Cluster   │ │             │ │ │    │  │ │ Redis Cluster   │ │             │ │ │
│  │ │ (6 nodes)       │ │             │ │ │    │  │ │ (6 nodes)       │ │             │ │ │
│  │ └─────────────────┘ └─────────────┘ │ │    │  │ └─────────────────┘ └─────────────┘ │ │
│  └─────────────────────────────────────┘ │    │  └─────────────────────────────────────┘ │
│                                          │    │                                          │
│  ┌─────────────────────────────────────┐ │    │  ┌─────────────────────────────────────┐ │
│  │         AVAILABILITY ZONE 1b        │ │    │  │         AVAILABILITY ZONE 2b        │ │
│  │                                     │ │    │  │                                     │ │
│  │ ┌─────────────────┐ ┌─────────────┐ │ │    │  │ ┌─────────────────┐ ┌─────────────┐ │ │
│  │ │ Kafka Cluster   │ │Flink Cluster│ │ │    │  │ │ Kafka Cluster   │ │Flink Cluster│ │ │
│  │ │ (3 brokers)     │ │(4 task mgrs)│ │ │    │  │ │ (3 brokers)     │ │(4 task mgrs)│ │ │
│  │ │                 │ │             │ │ │    │  │ │                 │ │             │ │ │
│  │ │ Redis Cluster   │ │             │ │ │    │  │ │ Redis Cluster   │ │             │ │ │
│  │ │ (6 nodes)       │ │             │ │ │    │  │ │ (6 nodes)       │ │             │ │ │
│  │ └─────────────────┘ └─────────────┘ │ │    │  │ └─────────────────┘ └─────────────┘ │ │
│  └─────────────────────────────────────┘ │    │  └─────────────────────────────────────┘ │
│                                          │    │                                          │
│  ┌─────────────────────────────────────┐ │    │  ┌─────────────────────────────────────┐ │
│  │         AVAILABILITY ZONE 1c        │ │    │  │         AVAILABILITY ZONE 2c        │ │
│  │                                     │ │    │  │                                     │ │
│  │ ┌─────────────────┐ ┌─────────────┐ │ │    │  │ ┌─────────────────┐ ┌─────────────┐ │ │
│  │ │ TimescaleDB     │ │Object Store │ │ │    │  │ │ TimescaleDB     │ │Object Store │ │ │
│  │ │ PRIMARY         │ │S3 US-East-1 │ │┼────┼──┤ │ REPLICA         │ │S3 EU-West-1│ │ │
│  │ │ (Master)        │ │             │ │ │    │  │ │ (Read-only)     │ │             │ │ │
│  │ │                 │ │             │ │ │    │  │ │                 │ │             │ │ │
│  │ └─────────────────┘ └─────────────┘ │ │    │  │ └─────────────────┘ └─────────────┘ │ │
│  └─────────────────────────────────────┘ │    │  └─────────────────────────────────────┘ │
└─────────────────────────────────────────┘    └─────────────────────────────────────────┘
            │                                                    │
            └──────────────── CROSS-REGION REPLICATION ──────────────┘

CROSS-REGION REPLICATION DETAILS:
════════════════════════════════

Kafka Replication:
┌─────────────────┐              ┌─────────────────┐
│ US-East Kafka   │ ──────────── │ EU-West Kafka   │
│ Topics:         │   MirrorMaker │ Topics:         │
│ - events        │  ◄─────────►  │ - events        │
│ - topk-results  │   (5 min lag) │ - topk-results  │
└─────────────────┘              └─────────────────┘

Database Replication:
┌─────────────────┐              ┌─────────────────┐
│ TimescaleDB     │              │ TimescaleDB     │
│ PRIMARY         │ ──────────── │ REPLICA         │
│ (US-East)       │  Async WAL   │ (EU-West)       │
│ Read/Write      │  Streaming   │ Read-Only       │
│ RPO: 15 sec     │ ◄─────────── │ RPO: 30 sec     │
└─────────────────┘              └─────────────────┘

Object Storage Sync:
┌─────────────────┐              ┌─────────────────┐
│ S3 US-East-1    │              │ S3 EU-West-1    │
│ - Raw events    │ ──────────── │ - Raw events    │
│ - Batch results │ Cross-Region │ - Batch results │
│ - Backups       │ Replication  │ - Backups       │
│ Sync: 1 hour    │ ◄─────────── │ Sync: 1 hour    │
└─────────────────┘              └─────────────────┘

TRAFFIC ROUTING & FAILOVER:
═════════════════════════

Global Users
     │
     ▼
┌─────────────────┐
│ DNS Service     │ ── Geo-routing based on user location
│ (Route 53)      │    Health checks every 30 seconds
└─────────────────┘
     │
     ▼
┌─────────────────┐
│ Global Load     │ ── Weighted routing (80% primary, 20% secondary)
│ Balancer        │    Automatic failover on health check failure
└─────────────────┘
     │
     ├─── 60% ──────────► Region 1 (US-East)   [PRIMARY]
     │
     └─── 40% ──────────► Region 2 (EU-West)   [SECONDARY]

Health Check Endpoints:
- /health/ready  (application ready)
- /health/live   (application alive)
- /metrics       (detailed metrics)

MONITORING & OBSERVABILITY:
═════════════════════════

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                            GLOBAL MONITORING STACK                                      │
│                                                                                         │
│ ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐     │
│ │ Prometheus      │  │ Grafana         │  │ ELK Stack       │  │ Jaeger          │     │
│ │ (Metrics)       │  │ (Dashboards)    │  │ (Logs)          │  │ (Tracing)       │     │
│ │                 │  │                 │  │                 │  │                 │     │
│ │ - System metrics│  │ - Business KPIs │  │ - App logs      │  │ - Request traces│     │
│ │ - App metrics   │  │ - SLA tracking  │  │ - Error logs    │  │ - Latency       │     │
│ │ - Custom metrics│  │ - Alerts        │  │ - Audit logs    │  │ - Dependencies  │     │
│ └─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘     │
└─────────────────────────────────────────────────────────────────────────────────────────┘

Deployment Characteristics:
- Total Compute: 200+ instances across regions
- Network Bandwidth: 100 Gbps aggregate
- Storage: 500TB+ distributed across regions
- Availability: 99.99% (52 minutes downtime/year)
- RTO (Recovery Time): < 5 minutes
- RPO (Recovery Point): < 1 minute
```

### Horizontal Scaling Strategies

**Ingestion Layer**
- Auto-scaling based on queue depth
- Geographic distribution of collectors
- Load balancing with consistent hashing
- Backpressure handling

**Processing Layer**
- Dynamic partitioning based on load
- Stateful stream processing with checkpoints
- Resource allocation based on data skew
- Elastic scaling with Kubernetes

**Storage Layer**
- Sharding across multiple nodes
- Read replicas for query distribution
- Data tiering based on access patterns
- Compression for historical data

### Performance Optimizations

**Memory Management**
- Off-heap storage for large datasets
- Memory-mapped files for persistence
- Garbage collection tuning
- Buffer pool optimization

**Network Optimization**
- Protocol buffer serialization
- Compression for network traffic
- Connection pooling and reuse
- Batch processing for efficiency

**Query Optimization**
- Result pre-computation for common queries
- Index optimization for time-range queries
- Query result caching
- Parallel query execution

## Fault Tolerance

### Failure Scenarios and Mitigation

**Node Failures**
- Automatic failover with leader election
- Data replication across multiple nodes
- Circuit breaker pattern for service calls
- Graceful degradation of service quality

**Network Partitions**
- Eventually consistent data model
- Conflict resolution strategies
- Partition tolerance with AP consistency
- Cross-datacenter synchronization

**Data Corruption**
- Checksums for data integrity
- Backup and restore procedures
- Point-in-time recovery capabilities
- Data validation and anomaly detection

### Disaster Recovery
- Multi-region deployment
- Automated backup procedures
- RTO: 4 hours, RPO: 1 hour
- Regular disaster recovery testing

## Monitoring and Observability

### Monitoring Dashboard Overview

```
COMPREHENSIVE MONITORING & ALERTING SYSTEM:
═══════════════════════════════════════════

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                MONITORING DASHBOARD                                     │
│                                                                                         │
│ ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐     │
│ │  SYSTEM HEALTH  │  │ APPLICATION     │  │ BUSINESS        │  │ INFRASTRUCTURE  │     │
│ │                 │  │ METRICS         │  │ METRICS         │  │ METRICS         │     │
│ │ • CPU Usage     │  │ • Events/Sec    │  │ • Top-K         │  │ • Kafka         │     │
│ │   Per Service   │  │   Ingestion     │  │   Accuracy      │  │   Consumer Lag  │     │
│ │                 │  │   Rate          │  │   vs Ground     │  │                 │     │
│ │ • Memory Usage  │  │                 │  │   Truth         │  │ • Redis Cache   │     │
│ │   JVM Heap      │  │ • Query         │  │                 │  │   Hit Rate      │     │
│ │   Off-heap      │  │   Latency       │  │ • Data          │  │                 │     │
│ │                 │  │   P50,P95,P99   │  │   Freshness     │  │ • Database      │     │
│ │ • Network I/O   │  │                 │  │   End-to-end    │  │   Connections   │     │
│ │   Bandwidth     │  │ • Error Rate    │  │   Latency       │  │   Pool Usage    │     │
│ │   Usage         │  │   By Service    │  │                 │  │                 │     │
│ │                 │  │                 │  │ • Data          │  │ • Queue Depth   │     │
│ │ • Disk I/O      │  │ • Throughput    │  │   Coverage      │  │   Processing    │     │
│ │   Read/Write    │  │   Queries/Sec   │  │   Completeness  │  │   Backlog       │     │
│ │   Operations    │  │                 │  │                 │  │                 │     │
│ │                 │  │                 │  │ • Data Drift    │  │                 │     │
│ │                 │  │                 │  │   Pattern       │  │                 │     │
│ │                 │  │                 │  │   Changes       │  │                 │     │
│ └─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘     │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

### Key Metrics

**System Metrics**
- Events per second processed
- Query response latency (P50, P95, P99)
- Memory and CPU utilization
- Network bandwidth usage

**Business Metrics**
- Top K accuracy compared to ground truth
- Data freshness and lag
- Query success rate
- User engagement metrics

**Error Metrics**
- Error rates by service
- Failed event processing count
- Data quality issues
- SLA violations

### Alerting Strategy
- Tiered alerting (info, warning, critical)
- Automatic escalation procedures
- Runbook integration
- Post-incident analysis

### Observability Tools
- **Metrics**: Prometheus + Grafana
- **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **Tracing**: Jaeger for distributed tracing
- **APM**: Application Performance Monitoring

## Implementation Considerations

### Technology Stack

**Core Processing**
- **Stream Processing**: Apache Flink for stateful operations
- **Message Queue**: Apache Kafka for reliable event delivery
- **Cache**: Redis Cluster for low-latency access
- **Database**: TimescaleDB for time-series data

**Infrastructure**
- **Orchestration**: Kubernetes for container management
- **Service Mesh**: Istio for service communication
- **Load Balancing**: Envoy proxy for intelligent routing
- **Storage**: Apache Cassandra for distributed storage

### Development Best Practices

**Code Quality**
- Comprehensive unit and integration testing
- Code review process with automated checks
- Performance testing and benchmarking
- Documentation and API specifications

**Deployment Strategy**
- Blue-green deployments for zero downtime
- Feature flags for gradual rollouts
- Automated rollback procedures
- Canary deployments for risk mitigation

**Security Considerations**
- End-to-end encryption for sensitive data
- Authentication and authorization mechanisms
- Rate limiting and DDoS protection
- Regular security audits and penetration testing

### Cost Optimization

**Resource Management**
- Auto-scaling policies for cost efficiency
- Resource quotas and limits
- Spot instances for non-critical workloads
- Data lifecycle management

**Storage Optimization**
- Tiered storage strategy
- Data compression and archival
- Intelligent caching policies
- Storage cost monitoring and alerts

## Trade-offs and Design Decisions

### Accuracy vs. Performance
- **Choice**: Approximate algorithms (Count-Min Sketch) over exact counting
- **Rationale**: Memory efficiency and constant-time operations
- **Trade-off**: Slight accuracy loss for massive scalability gains
- **Mitigation**: Configurable error bounds and batch correction jobs

### Consistency vs. Availability
- **Choice**: Eventually consistent system (AP in CAP theorem)
- **Rationale**: Global scale requires partition tolerance
- **Trade-off**: Temporary inconsistencies during network partitions
- **Mitigation**: Conflict resolution and anti-entropy processes

### Latency vs. Throughput
- **Choice**: Prioritize throughput for ingestion, latency for queries
- **Rationale**: Different SLAs for different use cases
- **Trade-off**: Some buffering delay in the ingestion path
- **Mitigation**: Configurable batching and multiple processing paths

## Performance Benchmarks

### Expected Performance Characteristics

| Metric | Target | Scale |
|--------|--------|-------|
| Ingestion Rate | 10M events/sec | Global |
| Query Latency (P99) | < 100ms | Single region |
| Query Latency (P95) | < 50ms | Single region |
| Memory per Partition | < 1GB | Count-Min Sketch |
| Storage per Day | 100TB | Raw events |
| Compressed Storage | 20TB | After compression |
| Cross-region Sync | < 5 minutes | RPO |
| Failover Time | < 30 seconds | RTO |

### Capacity Planning

**Single Partition Capacity**
- Events per second: 100K
- Memory usage: 512MB (Count-Min Sketch + Top-K)
- CPU utilization: 60% (4 cores)
- Network bandwidth: 100 Mbps

**Scaling Calculations**
```
Target: 10M events/sec
Partitions needed: 10M / 100K = 100 partitions
Total memory: 100 × 512MB = 51.2GB
Total CPU cores: 100 × 4 = 400 cores
Total bandwidth: 100 × 100Mbps = 10Gbps
```

## Alternative Approaches

### Exact Counting Solutions
- **Apache Druid**: Real-time OLAP with exact counts
- **ClickHouse**: Column-oriented database for analytics
- **Pros**: Perfect accuracy, SQL interface
- **Cons**: Higher memory usage, slower for massive scale

### Sampling-Based Approaches  
- **Reservoir Sampling**: Maintain random sample of events
- **Sticky Sampling**: Biased sampling towards frequent items
- **Pros**: Simple implementation, bounded memory
- **Cons**: Less accurate for rare items, requires tuning

### Hybrid Approaches
- **Lossy Counting + Count-Min Sketch**: Combine techniques
- **Hierarchical Heavy Hitters**: Multi-level aggregation
- **Pros**: Better accuracy-memory trade-offs
- **Cons**: Increased complexity, harder to tune

## Security Considerations

### Data Privacy
- **PII Handling**: Hash or tokenize sensitive identifiers
- **Data Encryption**: End-to-end encryption for sensitive streams
- **Access Controls**: Role-based access to different data views
- **Audit Logging**: Track all data access and modifications

### API Security
- **Authentication**: OAuth 2.0 / JWT tokens
- **Authorization**: Fine-grained permissions per endpoint
- **Rate Limiting**: Prevent abuse and DoS attacks
- **Input Validation**: Sanitize all user inputs

### Infrastructure Security
- **Network Isolation**: VPC with private subnets
- **Encryption in Transit**: TLS 1.3 for all communications
- **Encryption at Rest**: Encrypt all stored data
- **Secret Management**: Use HSM/KMS for key management

## Future Enhancements

### Advanced Analytics
- **Trend Analysis**: Detect rising/falling trends
- **Anomaly Detection**: ML-based outlier identification
- **Seasonality**: Handle periodic patterns in data
- **Multi-dimensional Top-K**: Group by multiple attributes

### Machine Learning Integration
- **Predictive Top-K**: Forecast future heavy hitters
- **Smart Sampling**: ML-guided sampling strategies
- **Auto-tuning**: Optimize parameters automatically
- **Pattern Recognition**: Identify data distribution changes

### Extended Functionality
- **Geo-distributed Top-K**: Location-aware aggregation
- **Real-time Dashboards**: Interactive visualization
- **Custom Metrics**: User-defined heavy hitter criteria
- **Streaming SQL**: SQL interface for stream processing

---

*This comprehensive system design provides a robust foundation for building a production-ready Heavy Hitters/Top K system that can scale to handle billions of events while maintaining high performance, reliability, and accuracy.*
