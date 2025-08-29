# Netflix System Design

## 1. Introduction

Netflix is a global streaming platform serving 230+ million subscribers across 190+ countries. The system must handle massive scale video streaming, personalized content recommendations, global content distribution, and real-time user interactions.

## 2. Requirements

### 2.1 Functional Requirements
- **Video Streaming**: Stream videos in multiple qualities (4K, 1080p, 720p, 480p)
- **Content Management**: Upload, encode, and manage video content
- **User Management**: User profiles, authentication, subscription management
- **Search & Discovery**: Search content and browse categories
- **Personalization**: Personalized recommendations and content curation
- **Multi-device Support**: Web, mobile, TV, gaming consoles
- **Offline Viewing**: Download content for offline consumption

### 2.2 Non-Functional Requirements
- **Scale**: 230M+ subscribers, 1B+ hours watched daily
- **Global Reach**: 190+ countries with localized content
- **Performance**: <3 seconds video start time, minimal buffering
- **Availability**: 99.9% uptime globally
- **Bandwidth Efficiency**: Adaptive bitrate streaming
- **Storage**: Petabytes of video content

## 3. Capacity Estimation

### 3.1 Scale Assumptions
- **Subscribers**: 230M global subscribers
- **Concurrent Users**: 50M peak concurrent viewers
- **Content Library**: 50K titles, 500K hours of content
- **Daily Viewing**: 1B hours watched per day
- **Average Video Size**: 1GB per hour (compressed)

### 3.2 Storage Requirements
```
Video Content:
- Raw content: 500K hours × 50GB = 25PB
- Encoded versions (multiple qualities): 25PB × 4 = 100PB
- Global replication (3 regions): 100PB × 3 = 300PB

Metadata:
- Video metadata: 50K titles × 10KB = 500MB
- User data: 230M users × 5KB = 1.15TB
- Total Storage: ~300PB + metadata
```

### 3.3 Bandwidth Requirements
```
Peak Concurrent Streaming:
- 50M concurrent users
- Average bitrate: 5 Mbps (adaptive)
- Total bandwidth: 50M × 5 Mbps = 250 Tbps
```

## 4. High-Level Design

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Client Apps   │────│   Global CDN     │────│  Load Balancer  │
│ (Web/Mobile/TV) │    │   (CloudFront +  │    │                 │
└─────────────────┘    │   Netflix CDN)   │    └─────────────────┘
                       └──────────────────┘             │
                                                        │
              ┌─────────────────┐              ┌─────────────────┐              ┌─────────────────┐
              │  API Gateway    │              │ Recommendation  │              │  Auth Service   │
              │   (Zuul)        │              │    Service      │              │                 │
              └─────────────────┘              └─────────────────┘              └─────────────────┘
                       │                                 │                                 │
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│Video Service│ │Search Service│ │User Service │ │Content Mgmt │ │Analytics Svc│ │Encoding Svc │
└─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘
       │               │               │                │                │                │
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│Video Storage│ │Search Index │ │User DB      │ │Content DB   │ │Analytics DB │ │Encoding     │
│   (S3)      │ │(Elasticsearch)│(Cassandra)  │ │(MySQL)      │ │(Cassandra)  │ │Queue        │
└─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘
```

## 5. Key Components

### 5.1 Video Streaming Architecture

**Adaptive Bitrate Streaming (ABR):**
```
Original Video → Encoding Service → Multiple Bitrates → HLS/DASH Segments → CDN
```

**Quality Profiles:**
- UHD 4K: 3840×2160, 15 Mbps
- FHD 1080p: 1920×1080, 8 Mbps  
- HD 720p: 1280×720, 5 Mbps
- SD 480p: 854×480, 2.5 Mbps

### 5.2 Content Delivery Network

**Multi-Tier CDN:**
- **Tier 1**: Origin Servers (AWS S3)
- **Tier 2**: Regional CDN (AWS CloudFront)
- **Tier 3**: Netflix Open Connect (ISP-level caches)

### 5.3 Recommendation System

**Multi-Algorithm Approach:**
- Collaborative Filtering
- Content-Based Filtering
- Deep Learning Models
- Contextual Bandits

### 5.4 Database Design

**Database Selection:**
- **User Data**: MySQL (ACID compliance)
- **Viewing History**: Cassandra (time-series, high writes)
- **Content Metadata**: MySQL (complex queries)
- **Search**: Elasticsearch (full-text search)
- **Cache**: Redis (session data, recommendations)

## 6. Scalability Strategies

### 6.1 Microservices Architecture
- Domain-driven service decomposition
- Asynchronous messaging for non-critical operations
- Circuit breakers for fault tolerance

### 6.2 Caching Strategy
```
L1: Application Cache (1 minute TTL)
L2: Redis Cache (15 minutes TTL)
L3: CDN Cache (24 hours TTL)
L4: Browser Cache (1 hour TTL)
```

### 6.3 Database Scaling
- Read replicas for different workloads
- Cassandra for time-series data
- Sharding by user_id for user data

## 7. Performance Optimizations

### 7.1 Video Streaming
- Predictive caching of popular content
- Adaptive bitrate based on network conditions
- Pre-loading of likely next content

### 7.2 Recommendations
- Real-time model updates
- Feature store for ML models
- A/B testing for algorithm improvements

## 8. Security & Reliability

### 8.1 Content Protection
- DRM (Digital Rights Management)
- Geo-restrictions
- Token-based streaming access

### 8.2 Fault Tolerance
- Circuit breaker patterns
- Graceful degradation
- Multi-region deployment
- Chaos engineering

## 9. Monitoring

### 9.1 Key Metrics
**Business**: MAU, churn rate, engagement
**Technical**: Video start time, rebuffering rate, API latency
**Infrastructure**: CPU, memory, network utilization

### 9.2 Observability
- Distributed tracing
- Real-time dashboards
- Alerting on SLA violations

## 10. Future Enhancements

- AI-generated content
- Interactive content experiences
- VR/AR streaming
- Edge computing for ultra-low latency
- Blockchain for content rights management

This design provides a scalable foundation for a global streaming platform handling millions of concurrent users while maintaining high performance and availability.
