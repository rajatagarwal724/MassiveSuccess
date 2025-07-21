# 3-Day Donation App System Design

## 1. Problem Statement

Design a donation platform that can handle a short-term charitable campaign (3 days) capable of processing $100 million in donations from 10 million users. The platform should integrate with third-party payment services and provide real-time tracking of donation goals.

## 2. Requirements

### Functional Requirements
- User registration and authentication
- Processing donations through third-party payment services
- Real-time donation tracking and goal visualization
- Campaign management with specific start and end times
- Donor receipt generation and acknowledgment
- Basic user profiles and donation history
- Donation leaderboard (optional)
- Social sharing capabilities

### Non-Functional Requirements
- **High Availability**: 99.99% uptime during the 3-day campaign
- **Scalability**: Support 10M users and $100M in donations over 3 days
- **Performance**: < 1s response time for most operations
- **Security**: PCI-compliant payment handling
- **Reliability**: Zero data loss for donation transactions
- **Geographic Distribution**: Global access with low latency
- **Compliance**: GDPR, CCPA, and other relevant regulations

## 3. Back-of-the-Envelope Calculations

### Traffic Estimation
- **Users**: 10 million total
- **Campaign Duration**: 3 days = 72 hours = 259,200 seconds
- **Average Donation Amount**: $100M / 10M users = $10 per user (average)

#### Read vs Write Operations
- **Writes (Donations)**: 
  - Assuming each user donates once: 10M donations over 3 days
  - Peak Rate: Assuming 50% of donations occur during peak 6-hour windows each day
  - Peak QPS = (10M × 0.5) / (3 days × 6 hours × 3,600 seconds) ≈ 77 donations/second
  - Average QPS = 10M / 259,200 seconds ≈ 38.6 donations/second

- **Reads (Page Views, Dashboards)**:
  - Assuming 5:1 read-to-write ratio
  - Peak Read QPS ≈ 77 × 5 = 385 reads/second
  - Average Read QPS ≈ 38.6 × 5 = 193 reads/second

### Storage Estimation
- **User Data**: 
  - ~1KB per user × 10M users = 10GB
- **Donation Records**:
  - ~2KB per donation × 10M donations = 20GB
- **Logs and Analytics**:
  - ~5KB per donation × 10M donations = 50GB
- **Total Storage**: ~80GB + indices and overhead ≈ 100GB

### Bandwidth Estimation
- **Incoming Traffic**:
  - Average: 38.6 donations/second × 2KB = 77.2 KB/s
  - Peak: 77 donations/second × 2KB = 154 KB/s
- **Outgoing Traffic**:
  - Average: 193 reads/second × 10KB (page size) = 1.93 MB/s
  - Peak: 385 reads/second × 10KB = 3.85 MB/s

### Memory Estimation (for Caching)
- **Active Users**: Assuming 20% of users are active simultaneously
  - 10M × 0.2 = 2M active users
- **Cache per User**: ~2KB (session data + minimal profile)
- **Total Cache Requirement**: 2M × 2KB = 4GB
- **Donation Counter Cache**: ~100MB
- **Total Memory**: ~5GB distributed across servers

## 4. High-Level Design

### System Components
1. **Client Tier**: Web and Mobile apps
2. **Application Tier**: 
   - Authentication Service
   - Donation Processing Service
   - Campaign Management Service
   - Analytics and Dashboard Service
3. **Data Tier**:
   - Primary Database (User data, Donation records)
   - Cache Layer (Redis/Memcached)
   - Analytics Database
4. **External Interfaces**:
   - Payment Gateway Integration
   - Email/Notification Service
   - CDN for static assets

### High-Level Architecture Diagram
```
┌─────────────┐     ┌─────────────┐     ┌─────────────────┐
│  Web Client │     │ Mobile App  │     │  Admin Portal   │
└──────┬──────┘     └──────┬──────┘     └────────┬────────┘
       │                   │                     │
       └───────────┬───────┴─────────────┬──────┘
                   │                     │
                   v                     v
┌────────────────────────────────────────────────┐
│                  API Gateway                   │
└────────┬─────────────┬──────────────┬─────────┘
         │             │              │
┌────────▼────────┐    │         ┌────▼─────────────┐
│ Authentication  │    │         │  Campaign Mgmt   │
│    Service      │    │         │    Service       │
└────────┬────────┘    │         └──────────────────┘
         │             │
┌────────▼────────┐    │         ┌──────────────────┐
│ User Profile    │    │         │  Analytics &     │
│   Service       │    │         │ Dashboard Svc    │
└─────────────────┘    │         └────────┬─────────┘
                       │                  │
                  ┌────▼──────────┐  ┌────▼─────────┐
                  │   Donation    │  │ Real-time    │
                  │   Service     │  │ Counter Svc  │
                  └───────┬───────┘  └──────────────┘
                          │
                          v
                  ┌───────────────┐    ┌─────────────┐
                  │  3rd Party    │    │ Notification│
                  │Payment Gateway│    │  Service    │
                  └───────────────┘    └─────────────┘

┌─────────────────┐  ┌─────────────┐  ┌─────────────┐
│ Primary DB      │  │  Redis      │  │ Analytics   │
│ (PostgreSQL)    │  │  Cache      │  │    DB       │
└─────────────────┘  └─────────────┘  └─────────────┘
```

## 5. Detailed Component Design

### 5.1 API Gateway
- Acts as the entry point for all client requests
- Handles routing, load balancing, and rate limiting
- Implements basic security measures like DDOS protection
- APIs:
  ```
  GET /campaigns/{id}
  GET /campaigns/{id}/stats
  POST /donations
  GET /users/{id}/donations
  POST /users/register
  POST /users/login
  ```

### 5.2 Authentication Service
- Handles user registration and login
- Issues and validates JWT tokens
- Implements OAuth for social login options
- Maintains user sessions

### 5.3 User Profile Service
- Manages user data and profiles
- Handles user preferences and settings
- Maintains donation history per user

### 5.4 Donation Service
- Core service for processing donations
- Integrates with third-party payment gateway
- Implements idempotent donation processing
- Publishes events to message queue for asynchronous processing

### 5.5 Campaign Management Service
- Configures campaign details, goals, and timeframes
- Manages campaign status (setup, active, completed)
- Provides campaign configuration to other services

### 5.6 Analytics & Dashboard Service
- Processes donation events for aggregated metrics
- Generates real-time and historical reports
- Provides data for campaign dashboards
- Calculates leaderboards and statistics

### 5.7 Real-time Counter Service
- Maintains atomic counters for donations and amount
- Provides real-time updates to clients via WebSocket
- Uses Redis for distributed counters and fast operations

### 5.8 Notification Service
- Sends donation receipts and acknowledgments
- Handles email, SMS, and push notifications
- Processes events from message queue asynchronously

## 6. Data Model

### Users Table
```
id: UUID (PK)
email: VARCHAR(255)
name: VARCHAR(255)
hashed_password: VARCHAR(255)
created_at: TIMESTAMP
updated_at: TIMESTAMP
```

### Campaigns Table
```
id: UUID (PK)
name: VARCHAR(255)
description: TEXT
goal_amount: DECIMAL(16,2)
current_amount: DECIMAL(16,2)
start_time: TIMESTAMP
end_time: TIMESTAMP
status: ENUM('pending', 'active', 'completed')
created_at: TIMESTAMP
updated_at: TIMESTAMP
```

### Donations Table
```
id: UUID (PK)
user_id: UUID (FK to Users.id)
campaign_id: UUID (FK to Campaigns.id)
amount: DECIMAL(12,2)
payment_provider: VARCHAR(50)
payment_id: VARCHAR(255)
status: ENUM('pending', 'completed', 'failed')
donation_time: TIMESTAMP
receipt_sent: BOOLEAN
created_at: TIMESTAMP
updated_at: TIMESTAMP
```

### Payment_Transactions Table
```
id: UUID (PK)
donation_id: UUID (FK to Donations.id)
provider_transaction_id: VARCHAR(255)
status: ENUM('initiated', 'pending', 'completed', 'failed')
request_payload: JSON
response_payload: JSON
created_at: TIMESTAMP
updated_at: TIMESTAMP
```

## 7. Database Selection

### Primary Database: PostgreSQL
- Relational database for ACID-compliant transactions
- Strong consistency model for financial transactions
- Mature and battle-tested for high-value transactions
- Good support for complex queries and reporting
- Sharding considerations: 
  - By user_id for user data
  - By donation_id for donation records

### Cache Layer: Redis
- In-memory data store for fast access
- Used for session management and counters
- Supports atomic operations for donation counters
- Persistence configuration to prevent data loss
- Cluster configuration for high availability

### Analytics Database: ClickHouse
- Column-oriented database optimized for analytics
- Efficient for aggregations and time-series data
- Used for dashboard metrics and reporting
- Asynchronously populated from main database

## 8. Communication Patterns

### Synchronous Communication
- **REST APIs**:
  - User authentication and basic CRUD operations
  - Campaign information retrieval
  - Initial donation submission
- **gRPC**:
  - Internal service-to-service communication
  - Low-latency requirements (e.g., payment validation)

### Asynchronous Communication
- **Kafka** message queue for:
  - Donation events after initial processing
  - Receipt generation and email notifications
  - Updating analytics databases
  - Event sourcing for system resilience

### Real-time Updates
- **WebSockets**:
  - Push real-time donation counter updates to clients
  - Live dashboard updates
  - Donation notifications and achievements

## 9. Payment Processing Flow

```
┌─────────┐     ┌─────────────┐     ┌───────────────┐     ┌──────────────┐
│  User   │────>│  Donation   │────>│  3rd Party    │────>│  Payment     │
│         │     │  Service    │     │  Payment      │     │  Provider    │
└─────────┘     └──────┬──────┘     │  Gateway      │     └──────┬───────┘
                       │            └───────┬───────┘            │
                       │                    │                    │
                       │                    │  Payment           │
                       │                    │  Confirmation      │
                       │            ┌───────▼───────┐            │
                       │            │  Webhook      │<───────────┘
                       └────────────│  Handler      │
                                    └───────┬───────┘
                                            │
                           ┌────────────────▼────────────────┐
                           │                                 │
                     ┌─────▼──────┐                   ┌──────▼─────┐
                     │  Update    │                   │            │
                     │  Database  │                   │  Message   │
                     └─────┬──────┘                   │  Queue     │
                           │                          └──────┬─────┘
                     ┌─────▼──────┐                          │
                     │  Update    │                   ┌──────▼─────┐
                     │  Counters  │                   │ Notification│
                     └────────────┘                   │  Service   │
                                                      └────────────┘
```

1. User initiates donation through client app
2. Donation Service validates request and creates pending donation record
3. Request is forwarded to 3rd party payment gateway
4. User completes payment on gateway
5. Payment gateway sends confirmation via webhook
6. Webhook handler updates donation status in database
7. Counter service is updated synchronously
8. Asynchronous events are published to message queue
9. Notification service processes events to send receipts

## 10. Scalability and Performance Optimization

### Horizontal Scaling
- Stateless application services on Kubernetes
- Autoscaling based on CPU/memory metrics
- Regional deployment for global distribution
- Load balancing with health checks

### Caching Strategy
- **Application Cache**:
  - Campaign details and configuration
  - User profiles for authenticated users
  - Donation counters and statistics
- **Database Cache**:
  - Query result caching
  - Connection pooling
  - Read replicas for scaling reads

### Performance Optimizations
- CDN for static assets and campaign images
- Edge caching for campaign landing pages
- Pre-computation of statistics and leaderboards
- Database indexing on frequently queried fields:
  ```
  CREATE INDEX idx_donations_user_id ON donations(user_id);
  CREATE INDEX idx_donations_campaign_id ON donations(campaign_id);
  CREATE INDEX idx_donations_status ON donations(status);
  CREATE INDEX idx_donations_donation_time ON donations(donation_time);
  ```
- Database partitioning by donation_time for efficient queries

## 11. Fault Tolerance and Reliability

### Data Redundancy
- Database replication with synchronous primary-secondary setup
- Regular backups with point-in-time recovery
- Cross-region replication for disaster recovery

### Graceful Degradation
- Circuit breakers for failing external services
- Fallback mechanisms for payment processing
- Static donation pages during backend issues

### Idempotency
- Unique idempotency keys for donation requests
- De-duplication logic for payment confirmation
- Retry mechanisms with exponential backoff

### Monitoring and Alerting
- Real-time metrics for donation flow
- SLO-based alerting for critical paths
- Distributed tracing for latency analysis

## 12. Security Considerations

### Payment Security
- No storage of credit card data (handled by third party)
- Tokenization for recurring donation references
- PCI-DSS compliance through payment provider

### Application Security
- Input validation and parameterized queries
- Rate limiting to prevent abuse
- HTTPS/TLS for all communications
- OWASP top 10 protections

### Data Protection
- Encryption at rest for sensitive user data
- Role-based access control for admin functions
- PII handling compliant with GDPR/CCPA

## 13. Deployment Architecture

### Kubernetes-based Deployment
- Microservices deployed as containerized applications
- Helm charts for configuration management
- Horizontal Pod Autoscaling for traffic spikes
- Multi-region deployment for global coverage

### CI/CD Pipeline
- Automated testing for all services
- Blue-green deployments for zero downtime
- Canary releases for critical components
- Automated rollback mechanisms

## 14. Monitoring and Observability

### Metrics Collection
- Service-level metrics (latency, throughput, error rate)
- Business metrics (donation rate, conversion, goal progress)
- System metrics (CPU, memory, network, disk)

### Logging Strategy
- Centralized logging with Elasticsearch
- Structured logging with correlation IDs
- Log retention policies aligned with compliance requirements

### Alerting
- Critical path alerts (payment processing, auth failures)
- Anomaly detection for unusual traffic patterns
- On-call rotation during the 3-day campaign

## 15. Potential Bottlenecks and Mitigations

### Payment Processing
- **Bottleneck**: Third-party payment gateway limits
- **Mitigation**: Multiple payment providers with failover

### Database Write Operations
- **Bottleneck**: High write load during donation peaks
- **Mitigation**: Write sharding, connection pooling, optimized indexes

### Counter Updates
- **Bottleneck**: Atomic counter updates causing contention
- **Mitigation**: Distributed counters with eventual consistency

### Receipt Generation
- **Bottleneck**: Email sending during donation spikes
- **Mitigation**: Asynchronous processing with priority queues

## 16. Trade-offs and Design Decisions

### Consistency vs Availability
- **Decision**: Strong consistency for financial transactions
- **Rationale**: Zero data loss requirement for donations
- **Trade-off**: Slightly higher latency for payment confirmation

### Monolithic vs Microservices
- **Decision**: Microservices architecture
- **Rationale**: Independent scaling of components, team autonomy
- **Trade-off**: Increased operational complexity

### SQL vs NoSQL
- **Decision**: Hybrid approach (PostgreSQL + Redis + ClickHouse)
- **Rationale**: ACID for transactions, high performance for counters
- **Trade-off**: Multiple data stores to maintain and synchronize

### Synchronous vs Asynchronous Processing
- **Decision**: Synchronous for critical path, async for non-critical
- **Rationale**: Balance between user experience and system resilience
- **Trade-off**: More complex recovery mechanisms for async failures

## 17. Future Extensions

### Multi-Campaign Support
- Extend to support multiple concurrent campaigns
- Campaign categories and discovery features

### Advanced Analytics
- Donor behavior analysis
- A/B testing framework for donation pages
- Machine learning for fraud detection

### Social Features
- Team fundraising capabilities
- Social sharing incentives
- Donor community features

## 18. Conclusion

The proposed system design for the 3-Day Donation App prioritizes reliability, scalability, and security while efficiently handling 10 million users and $100 million in donations over a short time period. By leveraging a microservices architecture with appropriate synchronous and asynchronous communication patterns, the system can maintain high performance under peak loads while ensuring donation data integrity. The third-party payment integration reduces security concerns while allowing the application to focus on providing an excellent user experience and reliable donation tracking.
