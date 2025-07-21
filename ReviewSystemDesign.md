# Comprehensive Review System Design

## Table of Contents
1. [System Overview](#system-overview)
2. [Requirements](#requirements)
3. [High-Level Architecture](#high-level-architecture)
4. [Database Design](#database-design)
5. [API Design](#api-design)
6. [Caching Strategy](#caching-strategy)
7. [Scalability & Performance](#scalability--performance)
8. [Security & Privacy](#security--privacy)
9. [Analytics & Monitoring](#analytics--monitoring)
10. [Implementation Details](#implementation-details)

## System Overview

The Review System is designed to handle user-generated reviews for products, services, or content. It supports:
- **Multi-tenant architecture** (products, restaurants, movies, etc.)
- **Real-time review submission and retrieval**
- **Advanced filtering and sorting**
- **Spam detection and content moderation**
- **Analytics and insights**
- **High availability and scalability**

### Key Metrics
- **Scale**: 100M+ users, 1B+ reviews
- **Throughput**: 10K+ reviews/second (peak)
- **Latency**: <100ms for read operations, <500ms for write operations
- **Availability**: 99.99% uptime

## Requirements

### Functional Requirements

#### Core Features
1. **Review Management**
   - Submit reviews with rating (1-5 stars), text, images
   - Edit/delete own reviews (within time window)
   - View reviews with pagination and filtering
   - Support multiple review types (product, service, content)

2. **User Interactions**
   - Like/dislike reviews (helpful votes)
   - Reply to reviews (business responses)
   - Report inappropriate content
   - Follow other reviewers

3. **Advanced Features**
   - Review verification (verified purchase/experience)
   - Review summarization using AI
   - Sentiment analysis
   - Review recommendations
   - Bulk import/export for businesses

#### Business Features
1. **Analytics Dashboard**
   - Review metrics and trends
   - User engagement analytics
   - Sentiment analysis reports
   - Competitive analysis

2. **Moderation Tools**
   - Automated spam detection
   - Manual review queue
   - Content policy enforcement
   - User reputation system

### Non-Functional Requirements

1. **Performance**
   - Read latency: <100ms (P95)
   - Write latency: <500ms (P95)
   - Throughput: 10K+ QPS

2. **Scalability**
   - Horizontal scaling capability
   - Handle traffic spikes (Black Friday, viral content)
   - Auto-scaling based on load

3. **Reliability**
   - 99.99% availability
   - Data durability: 99.999999999%
   - Graceful degradation under load

4. **Security**
   - Authentication and authorization
   - Rate limiting and DDoS protection
   - Data encryption (transit and rest)
   - PII protection and GDPR compliance

## High-Level Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Mobile Apps   │    │   Web Client    │    │  Admin Portal   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │   Load Balancer │
                    │   (AWS ALB)     │
                    └─────────────────┘
                                 │
                    ┌─────────────────┐
                    │   API Gateway   │
                    │   (Rate Limit,  │
                    │   Auth, Routing)│
                    └─────────────────┘
                                 │
         ┌───────────────────────┼───────────────────────┐
         │                       │                       │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Review Service │    │  User Service   │    │ Analytics Service│
│  (Read/Write)   │    │  (Auth/Profile) │    │  (Metrics/ML)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
              ┌─────────────────────────────────────┐
              │              Data Layer             │
              │  ┌─────────────┐  ┌─────────────┐  │
              │  │   Redis     │  │ Elasticsearch│  │
              │  │   Cache     │  │   Search     │  │
              │  └─────────────┘  └─────────────┘  │
              │  ┌─────────────┐  ┌─────────────┐  │
              │  │  PostgreSQL │  │   Apache    │  │
              │  │ (Primary DB)│  │   Kafka     │  │
              │  └─────────────┘  └─────────────┘  │
              └─────────────────────────────────────┘
```

### Service Architecture

#### 1. Review Service (Core)
```java
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
        @RequestBody CreateReviewRequest request,
        @AuthenticationPrincipal User user) {
        
        ReviewResponse response = reviewService.createReview(request, user);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<ReviewListResponse> getReviews(
        @PathVariable String entityType,
        @PathVariable String entityId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "newest") String sortBy) {
        
        ReviewListResponse response = reviewService.getReviews(
            entityType, entityId, page, size, sortBy);
        return ResponseEntity.ok(response);
    }
}
```

#### 2. User Service
- Authentication & authorization
- User profile management
- Review history and preferences
- Reputation scoring

#### 3. Analytics Service
- Real-time metrics collection
- ML-based sentiment analysis
- Review summarization
- Fraud detection

#### 4. Moderation Service
- Content filtering
- Manual review queue
- Policy enforcement
- Automated spam detection

## Database Design

### Primary Database (PostgreSQL)

#### 1. Users Table
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    profile_image_url VARCHAR(500),
    reputation_score INTEGER DEFAULT 0,
    total_reviews INTEGER DEFAULT 0,
    total_helpful_votes INTEGER DEFAULT 0,
    is_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Indexes
    INDEX idx_users_email (email),
    INDEX idx_users_username (username),
    INDEX idx_users_reputation (reputation_score DESC)
);
```

#### 2. Reviews Table
```sql
CREATE TABLE reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    entity_type VARCHAR(50) NOT NULL, -- 'product', 'restaurant', 'movie'
    entity_id VARCHAR(100) NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    title VARCHAR(200),
    content TEXT,
    images JSONB, -- Array of image URLs
    is_verified BOOLEAN DEFAULT FALSE, -- Verified purchase/experience
    is_featured BOOLEAN DEFAULT FALSE,
    helpful_votes INTEGER DEFAULT 0,
    total_votes INTEGER DEFAULT 0,
    sentiment_score DECIMAL(3,2), -- -1.00 to 1.00
    language VARCHAR(10) DEFAULT 'en',
    moderation_status VARCHAR(20) DEFAULT 'pending', -- pending, approved, rejected
    moderation_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Indexes
    INDEX idx_reviews_entity (entity_type, entity_id),
    INDEX idx_reviews_user (user_id),
    INDEX idx_reviews_rating (rating),
    INDEX idx_reviews_created (created_at DESC),
    INDEX idx_reviews_helpful (helpful_votes DESC),
    INDEX idx_reviews_composite (entity_type, entity_id, created_at DESC),
    
    -- Prevent duplicate reviews per user per entity
    UNIQUE INDEX idx_reviews_unique_user_entity (user_id, entity_type, entity_id)
);
```

#### 3. Review Votes Table
```sql
CREATE TABLE review_votes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    review_id UUID NOT NULL REFERENCES reviews(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id),
    vote_type VARCHAR(10) NOT NULL CHECK (vote_type IN ('helpful', 'not_helpful')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Indexes
    INDEX idx_review_votes_review (review_id),
    INDEX idx_review_votes_user (user_id),
    
    -- Prevent duplicate votes
    UNIQUE INDEX idx_review_votes_unique (review_id, user_id)
);
```

#### 4. Review Replies Table
```sql
CREATE TABLE review_replies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    review_id UUID NOT NULL REFERENCES reviews(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id),
    content TEXT NOT NULL,
    reply_type VARCHAR(20) DEFAULT 'user', -- 'user', 'business', 'admin'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Indexes
    INDEX idx_review_replies_review (review_id, created_at),
    INDEX idx_review_replies_user (user_id)
);
```

#### 5. Entity Aggregates Table (Materialized View)
```sql
CREATE MATERIALIZED VIEW entity_review_aggregates AS
SELECT 
    entity_type,
    entity_id,
    COUNT(*) as total_reviews,
    AVG(rating) as average_rating,
    COUNT(CASE WHEN rating = 5 THEN 1 END) as five_star_count,
    COUNT(CASE WHEN rating = 4 THEN 1 END) as four_star_count,
    COUNT(CASE WHEN rating = 3 THEN 1 END) as three_star_count,
    COUNT(CASE WHEN rating = 2 THEN 1 END) as two_star_count,
    COUNT(CASE WHEN rating = 1 THEN 1 END) as one_star_count,
    MAX(created_at) as last_review_date
FROM reviews 
WHERE moderation_status = 'approved'
GROUP BY entity_type, entity_id;

-- Refresh strategy for real-time updates
CREATE UNIQUE INDEX ON entity_review_aggregates (entity_type, entity_id);
```

### Search Database (Elasticsearch)

#### Review Search Index
```json
{
  "mappings": {
    "properties": {
      "id": {"type": "keyword"},
      "user_id": {"type": "keyword"},
      "entity_type": {"type": "keyword"},
      "entity_id": {"type": "keyword"},
      "rating": {"type": "integer"},
      "title": {
        "type": "text",
        "analyzer": "standard",
        "fields": {
          "keyword": {"type": "keyword"}
        }
      },
      "content": {
        "type": "text",
        "analyzer": "standard"
      },
      "sentiment_score": {"type": "float"},
      "helpful_votes": {"type": "integer"},
      "is_verified": {"type": "boolean"},
      "created_at": {"type": "date"},
      "tags": {"type": "keyword"}
    }
  },
  "settings": {
    "number_of_shards": 5,
    "number_of_replicas": 1,
    "analysis": {
      "analyzer": {
        "review_analyzer": {
          "type": "custom",
          "tokenizer": "standard",
          "filter": ["lowercase", "stop", "snowball"]
        }
      }
    }
  }
}
```

### Cache Strategy (Redis)

#### Cache Keys and TTL
```redis
# Hot entity aggregates (1 hour TTL)
entity:aggregate:{entity_type}:{entity_id} -> JSON

# User review lists (30 minutes TTL)
user:reviews:{user_id}:page:{page} -> JSON

# Top reviews for entities (2 hours TTL)
entity:top_reviews:{entity_type}:{entity_id} -> JSON

# User reputation cache (1 hour TTL)
user:reputation:{user_id} -> INTEGER

# Rate limiting (5 minutes TTL)
rate_limit:user:{user_id}:reviews -> INTEGER
```

## API Design

### RESTful API Endpoints

#### Review Operations
```yaml
# Create Review
POST /api/v1/reviews
Content-Type: application/json
Authorization: Bearer {token}

{
  "entity_type": "product",
  "entity_id": "12345",
  "rating": 5,
  "title": "Great product!",
  "content": "This product exceeded my expectations...",
  "images": ["https://cdn.example.com/image1.jpg"]
}

# Get Reviews for Entity
GET /api/v1/reviews/{entity_type}/{entity_id}
Parameters:
  - page: integer (default: 0)
  - size: integer (default: 20, max: 100)
  - sort: string (newest, oldest, highest_rated, lowest_rated, most_helpful)
  - filter_rating: integer (1-5)
  - filter_verified: boolean
  - search: string

Response:
{
  "reviews": [...],
  "pagination": {
    "page": 0,
    "size": 20,
    "total_pages": 50,
    "total_elements": 1000
  },
  "aggregates": {
    "average_rating": 4.2,
    "total_reviews": 1000,
    "rating_distribution": {
      "5": 400,
      "4": 300,
      "3": 200,
      "2": 70,
      "1": 30
    }
  }
}

# Update Review
PUT /api/v1/reviews/{review_id}
Authorization: Bearer {token}

# Delete Review
DELETE /api/v1/reviews/{review_id}
Authorization: Bearer {token}

# Vote on Review
POST /api/v1/reviews/{review_id}/votes
Authorization: Bearer {token}

{
  "vote_type": "helpful"
}
```

#### Advanced Search
```yaml
# Advanced Search
GET /api/v1/reviews/search
Parameters:
  - q: string (search query)
  - entity_type: string
  - entity_ids: string[] (comma separated)
  - rating_min: integer
  - rating_max: integer
  - sentiment: string (positive, negative, neutral)
  - date_from: string (ISO date)
  - date_to: string (ISO date)
  - verified_only: boolean
  - sort: string
  - page: integer
  - size: integer
```

#### Analytics APIs
```yaml
# Entity Analytics
GET /api/v1/analytics/entities/{entity_type}/{entity_id}
Authorization: Bearer {admin_token}

Response:
{
  "review_trends": {
    "daily_reviews": [...],
    "rating_trends": [...],
    "sentiment_trends": [...]
  },
  "user_segments": {
    "verified_users": 0.75,
    "repeat_reviewers": 0.15
  },
  "top_keywords": [
    {"keyword": "quality", "frequency": 150},
    {"keyword": "fast shipping", "frequency": 120}
  ]
}
```

## Caching Strategy

### Multi-Layer Caching

#### 1. CDN Layer (CloudFront)
- Static assets (images, CSS, JS)
- API responses for public data
- TTL: 24 hours for assets, 5 minutes for API responses

#### 2. Application Cache (Redis Cluster)
```java
@Service
public class ReviewCacheService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String ENTITY_REVIEWS_KEY = "entity:reviews:%s:%s:page:%d";
    private static final String ENTITY_AGGREGATE_KEY = "entity:aggregate:%s:%s";
    
    public ReviewListResponse getCachedReviews(
        String entityType, String entityId, int page) {
        
        String key = String.format(ENTITY_REVIEWS_KEY, entityType, entityId, page);
        return (ReviewListResponse) redisTemplate.opsForValue().get(key);
    }
    
    public void cacheReviews(String entityType, String entityId, 
                           int page, ReviewListResponse reviews) {
        String key = String.format(ENTITY_REVIEWS_KEY, entityType, entityId, page);
        redisTemplate.opsForValue().set(key, reviews, Duration.ofMinutes(30));
    }
    
    public void invalidateEntityCache(String entityType, String entityId) {
        // Invalidate all pages for this entity
        String pattern = String.format("entity:reviews:%s:%s:*", entityType, entityId);
        Set<String> keys = redisTemplate.keys(pattern);
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        
        // Invalidate aggregate cache
        String aggregateKey = String.format(ENTITY_AGGREGATE_KEY, entityType, entityId);
        redisTemplate.delete(aggregateKey);
    }
}
```

#### 3. Database Query Cache
- PostgreSQL query result caching
- Connection pooling with HikariCP
- Read replicas for read-heavy workloads

### Cache Invalidation Strategy

```java
@Component
public class ReviewCacheInvalidator {
    
    @EventListener
    public void handleReviewCreated(ReviewCreatedEvent event) {
        // Invalidate entity caches
        cacheService.invalidateEntityCache(
            event.getEntityType(), 
            event.getEntityId()
        );
        
        // Invalidate user caches
        cacheService.invalidateUserCache(event.getUserId());
        
        // Trigger async aggregate recalculation
        aggregateService.recalculateAsync(
            event.getEntityType(), 
            event.getEntityId()
        );
    }
    
    @EventListener
    public void handleReviewVoted(ReviewVotedEvent event) {
        // Invalidate specific review caches
        cacheService.invalidateReviewCache(event.getReviewId());
    }
}
```

## Scalability & Performance

### Horizontal Scaling

#### 1. Database Sharding
```sql
-- Shard by entity_type and entity_id hash
CREATE TABLE reviews_shard_1 (
    LIKE reviews INCLUDING ALL
) INHERITS (reviews);

CREATE TABLE reviews_shard_2 (
    LIKE reviews INCLUDING ALL
) INHERITS (reviews);

-- Partition function
CREATE OR REPLACE FUNCTION review_partition_function()
RETURNS TRIGGER AS $$
DECLARE
    shard_num INTEGER;
BEGIN
    shard_num := abs(hashtext(NEW.entity_type || NEW.entity_id)) % 4;
    
    IF shard_num = 0 THEN
        INSERT INTO reviews_shard_0 VALUES (NEW.*);
    ELSIF shard_num = 1 THEN
        INSERT INTO reviews_shard_1 VALUES (NEW.*);
    -- ... other shards
    END IF;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;
```

#### 2. Read Replicas
```yaml
# Database Configuration
spring:
  datasource:
    master:
      jdbc-url: jdbc:postgresql://master-db:5432/reviews
      username: app_user
      password: ${DB_PASSWORD}
      maximum-pool-size: 20
    
    replica:
      jdbc-url: jdbc:postgresql://replica-db:5432/reviews
      username: app_user
      password: ${DB_PASSWORD}
      maximum-pool-size: 50
```

#### 3. Service Auto-scaling
```yaml
# Kubernetes HPA Configuration
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: review-service-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: review-service
  minReplicas: 3
  maxReplicas: 100
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

### Performance Optimizations

#### 1. Database Optimizations
```sql
-- Composite indexes for common queries
CREATE INDEX CONCURRENTLY idx_reviews_entity_rating_date 
ON reviews (entity_type, entity_id, rating DESC, created_at DESC);

-- Partial indexes for active reviews
CREATE INDEX CONCURRENTLY idx_reviews_active 
ON reviews (entity_type, entity_id, created_at DESC) 
WHERE moderation_status = 'approved';

-- BRIN indexes for time-series data
CREATE INDEX CONCURRENTLY idx_reviews_created_brin 
ON reviews USING BRIN (created_at);
```

#### 2. Query Optimization
```java
@Repository
public class ReviewRepository {
    
    // Use LIMIT and OFFSET efficiently
    @Query(value = """
        SELECT r.*, u.username, u.profile_image_url 
        FROM reviews r 
        JOIN users u ON r.user_id = u.id 
        WHERE r.entity_type = :entityType 
        AND r.entity_id = :entityId 
        AND r.moderation_status = 'approved'
        ORDER BY r.created_at DESC 
        LIMIT :size OFFSET :offset
        """, nativeQuery = true)
    List<ReviewWithUser> findReviewsWithPagination(
        @Param("entityType") String entityType,
        @Param("entityId") String entityId,
        @Param("size") int size,
        @Param("offset") int offset
    );
    
    // Use COUNT query optimization
    @Query("SELECT COUNT(r) FROM Review r WHERE r.entityType = :entityType AND r.entityId = :entityId")
    long countByEntity(@Param("entityType") String entityType, @Param("entityId") String entityId);
}
```

## Security & Privacy

### Authentication & Authorization

#### 1. JWT Token Management
```java
@Service
public class JwtTokenService {
    
    private final String SECRET_KEY = "${jwt.secret}";
    private final long ACCESS_TOKEN_VALIDITY = 15 * 60 * 1000; // 15 minutes
    private final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000; // 7 days
    
    public String generateAccessToken(User user) {
        return Jwts.builder()
            .setSubject(user.getId().toString())
            .claim("username", user.getUsername())
            .claim("roles", user.getRoles())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

#### 2. Rate Limiting
```java
@Component
public class RateLimitingService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    public boolean isAllowed(String userId, String operation) {
        String key = String.format("rate_limit:%s:%s", userId, operation);
        String current = redisTemplate.opsForValue().get(key);
        
        if (current == null) {
            redisTemplate.opsForValue().set(key, "1", Duration.ofMinutes(5));
            return true;
        }
        
        int count = Integer.parseInt(current);
        int limit = getLimit(operation);
        
        if (count < limit) {
            redisTemplate.opsForValue().increment(key);
            return true;
        }
        
        return false;
    }
    
    private int getLimit(String operation) {
        return switch (operation) {
            case "review_create" -> 10; // 10 reviews per 5 minutes
            case "review_vote" -> 100;  // 100 votes per 5 minutes
            default -> 50;
        };
    }
}
```

### Data Protection

#### 1. PII Encryption
```java
@Entity
public class User {
    
    @Column(name = "email")
    @Convert(converter = EncryptedStringConverter.class)
    private String email;
    
    @Column(name = "phone")
    @Convert(converter = EncryptedStringConverter.class)
    private String phone;
}

@Converter
public class EncryptedStringConverter implements AttributeConverter<String, String> {
    
    @Autowired
    private EncryptionService encryptionService;
    
    @Override
    public String convertToDatabaseColumn(String attribute) {
        return encryptionService.encrypt(attribute);
    }
    
    @Override
    public String convertToEntityAttribute(String dbData) {
        return encryptionService.decrypt(dbData);
    }
}
```

#### 2. Content Moderation
```java
@Service
public class ContentModerationService {
    
    public ModerationResult moderateReview(String content) {
        // Check for profanity
        if (containsProfanity(content)) {
            return ModerationResult.rejected("Contains inappropriate language");
        }
        
        // Check for spam patterns
        if (isSpam(content)) {
            return ModerationResult.rejected("Detected as spam");
        }
        
        // AI-based sentiment analysis
        SentimentScore sentiment = aiService.analyzeSentiment(content);
        
        // Flag for manual review if needed
        if (sentiment.getScore() < -0.8 || containsSuspiciousPatterns(content)) {
            return ModerationResult.flagged("Requires manual review");
        }
        
        return ModerationResult.approved();
    }
}
```

## Analytics & Monitoring

### Real-time Metrics

#### 1. Application Metrics
```java
@Component
public class ReviewMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter reviewsCreated;
    private final Timer reviewCreationTime;
    private final Gauge activeUsers;
    
    public ReviewMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.reviewsCreated = Counter.builder("reviews.created")
            .tag("service", "review-service")
            .register(meterRegistry);
        this.reviewCreationTime = Timer.builder("reviews.creation.time")
            .register(meterRegistry);
    }
    
    public void recordReviewCreated(String entityType) {
        reviewsCreated.increment(
            Tags.of("entity_type", entityType)
        );
    }
    
    public void recordReviewCreationTime(Duration duration) {
        reviewCreationTime.record(duration);
    }
}
```

#### 2. Business Metrics
```sql
-- Daily review metrics
SELECT 
    DATE(created_at) as date,
    COUNT(*) as total_reviews,
    AVG(rating) as avg_rating,
    COUNT(DISTINCT user_id) as unique_reviewers,
    COUNT(CASE WHEN is_verified = true THEN 1 END) as verified_reviews
FROM reviews 
WHERE created_at >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY DATE(created_at)
ORDER BY date DESC;

-- Entity performance metrics
SELECT 
    entity_type,
    entity_id,
    COUNT(*) as total_reviews,
    AVG(rating) as avg_rating,
    SUM(helpful_votes) as total_helpful_votes
FROM reviews 
WHERE moderation_status = 'approved'
GROUP BY entity_type, entity_id
HAVING COUNT(*) > 10
ORDER BY avg_rating DESC, total_reviews DESC;
```

### Monitoring & Alerting

#### 1. Health Checks
```java
@Component
public class ReviewServiceHealthIndicator implements HealthIndicator {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Override
    public Health health() {
        try {
            // Check database connectivity
            reviewRepository.findById(UUID.randomUUID());
            
            // Check Redis connectivity
            redisTemplate.opsForValue().get("health-check");
            
            // Check recent activity
            long recentReviews = reviewRepository.countRecentReviews(Duration.ofMinutes(5));
            
            return Health.up()
                .withDetail("database", "UP")
                .withDetail("cache", "UP")
                .withDetail("recent_reviews", recentReviews)
                .build();
                
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

#### 2. Alerting Rules
```yaml
# Prometheus Alert Rules
groups:
- name: review-service-alerts
  rules:
  - alert: HighErrorRate
    expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.1
    for: 2m
    labels:
      severity: critical
    annotations:
      summary: "High error rate in review service"
      
  - alert: DatabaseConnectionIssue
    expr: up{job="review-service-db"} == 0
    for: 1m
    labels:
      severity: critical
    annotations:
      summary: "Database connection issue"
      
  - alert: LowReviewVolume
    expr: rate(reviews_created_total[1h]) < 10
    for: 15m
    labels:
      severity: warning
    annotations:
      summary: "Unusually low review creation rate"
```

## Implementation Details

### Technology Stack

#### Backend Services
- **Language**: Java 17 with Spring Boot 3.x
- **Database**: PostgreSQL 14+ (primary), Redis 7+ (cache)
- **Search**: Elasticsearch 8.x
- **Message Queue**: Apache Kafka
- **API Gateway**: Spring Cloud Gateway
- **Monitoring**: Prometheus + Grafana
- **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana)

#### Infrastructure
- **Cloud**: AWS (EKS, RDS, ElastiCache, S3)
- **Containers**: Docker + Kubernetes
- **CI/CD**: GitLab CI/CD or GitHub Actions
- **Load Balancer**: AWS ALB
- **CDN**: CloudFront

### Deployment Architecture

```yaml
# Kubernetes Deployment
apiVersion: apps/v1
kind: Deployment
metadata:
  name: review-service
spec:
  replicas: 5
  selector:
    matchLabels:
      app: review-service
  template:
    metadata:
      labels:
        app: review-service
    spec:
      containers:
      - name: review-service
        image: review-service:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: DB_HOST
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: host
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
```

### Configuration Management

```yaml
# application-production.yml
spring:
  datasource:
    master:
      jdbc-url: ${DB_MASTER_URL}
      username: ${DB_USERNAME}
      password: ${DB_PASSWORD}
      hikari:
        maximum-pool-size: 20
        minimum-idle: 5
        idle-timeout: 300000
    replica:
      jdbc-url: ${DB_REPLICA_URL}
      username: ${DB_USERNAME}
      password: ${DB_PASSWORD}
      hikari:
        maximum-pool-size: 50
        minimum-idle: 10
  
  redis:
    cluster:
      nodes: ${REDIS_CLUSTER_NODES}
      max-redirects: 3
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 50
        max-idle: 10
        min-idle: 5

  kafka:
    bootstrap-servers: ${KAFKA_BROKERS}
    producer:
      acks: all
      retries: 3
      properties:
        enable.idempotence: true

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true

logging:
  level:
    com.reviewsystem: INFO
    org.springframework.security: DEBUG
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
```

This comprehensive review system design covers all aspects needed for a production-ready, scalable solution that can handle millions of users and reviews while maintaining high performance and reliability. 