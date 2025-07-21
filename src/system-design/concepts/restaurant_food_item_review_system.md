# System Design: Restaurant Food Item Review & Rating System with Rewards (DoorDash Style)

## Table of Contents
1. [Overview](#overview)
2. [Requirements](#requirements)
3. [High-Level Architecture](#high-level-architecture)
4. [Database Design](#database-design)
5. [API Design](#api-design)
6. [Review Reward System](#review-reward-system)
7. [Caching Strategy](#caching-strategy)
8. [Scalability & Performance](#scalability--performance)
9. [Security Considerations](#security-considerations)
10. [Analytics & ML Features](#analytics--ml-features)
11. [Implementation Details](#implementation-details)

## Overview

This system is specifically designed to collect, store, and analyze granular reviews and ratings for individual food items at restaurants, rather than just overall restaurant ratings. This granularity provides more actionable insights for both consumers and restaurant owners, allowing users to discover the best dishes at any restaurant and helping restaurants improve specific menu items.

### Key Metrics
- **Scale**: 
  - 100K+ restaurants
  - 10M+ food items
  - 50M+ users
  - 500M+ food item reviews
- **Throughput**: 1K+ reviews/second during peak hours
- **Latency**: <100ms for read operations, <300ms for write operations
- **Availability**: 99.95% uptime

## Requirements

### Functional Requirements

#### Core Features
1. **Food Item Review Management**
   - Submit reviews with:
     - Star rating (1-5 stars) 
     - Text review
     - Photos of the food item
     - Tags (e.g., "spicy", "value for money", "authentic")
     - Price evaluation (too expensive, worth it, great value)
   - Edit/delete own reviews (within 48-hour window)
   - View reviews with pagination, sorting, and filtering
   
2. **Restaurant & Menu Management**
   - Restaurant profile with location, cuisine types, hours
   - Menu management with categories and items
   - Seasonal menu tracking and menu version history
   - Support for price variations and customizations
   
3. **User Interactions**
   - Upvote/downvote reviews
   - Follow users for their food recommendations
   - Save/bookmark favorite dishes
   - Create food wishlists for future visits
   - Share reviews on social media

4. **Advanced Features**
   - Review verification (verified purchase)
   - Quality-based reward system for reviewers
   - Dish recommendations based on user preferences
   - Personalized food discovery
   - Dietary preference filtering (vegetarian, vegan, gluten-free, etc.)
   - Popular dish trends by location/season

#### Business Features
1. **Restaurant Dashboard**
   - Item-level rating analytics
   - Review sentiment analysis
   - Competitive analysis against similar restaurants
   - Price sensitivity analysis
   - Menu optimization recommendations

2. **Moderation & Quality**
   - Automated spam detection
   - Fake review identification
   - Content moderation for photos and text
   - Reviewer reputation system

### Non-Functional Requirements

1. **Performance**
   - Read latency: <100ms (P95)
   - Write latency: <300ms (P95)
   - Search latency: <200ms (P95)

2. **Scalability**
   - Horizontal scaling for all services
   - Handle traffic spikes during meal times
   - Efficient handling of viral food trends

3. **Availability & Reliability**
   - 99.95% service availability
   - Graceful degradation during partial outages
   - Data durability for reviews and photos

4. **Security & Privacy**
   - User authentication and authorization
   - Restaurant owner verification
   - Protection against review manipulation
   - GDPR and CCPA compliance

## High-Level Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Mobile Apps   │    │   Web Client    │    │ Restaurant Portal│
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │   CDN / Edge    │
                    │   (Cloudfront)  │
                    └─────────────────┘
                                 │
                    ┌─────────────────┐
                    │   API Gateway   │
                    │   (Auth, Rate   │
                    │   Limiting)     │
                    └─────────────────┘
                                 │
         ┌───────────────────────┼───────────────────────┐
         │                       │                       │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Review Service │    │Restaurant Service│    │ Discovery Service│
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
         ┌───────────────────────┼───────────────────────┐
         │                       │                       │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  SQL Database   │    │  Redis Cache    │    │  Elasticsearch  │
│  (PostgreSQL)   │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Kafka Streams  │    │  Object Storage │    │   ML Pipeline   │
│  (Event Bus)    │    │  (S3/Photos)    │    │ (Recommendations)│
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Service Architecture

#### 1. Review Service
- Handles food item review CRUD operations
- Manages review moderation queue
- Processes review analytics
- Review feed generation

#### 2. Restaurant Service
- Restaurant and menu management
- Food item metadata and categorization
- Business owner dashboards
- Menu versioning and tracking

#### 3. Discovery Service
- Food and restaurant search
- Personalized recommendations
- Trending dishes detection
- Location-based discovery

#### 4. Analytics Service
- Review sentiment analysis
- Price sensitivity analysis
- Competitive benchmarking
- Menu optimization insights

#### 5. User Service
- User authentication and profiles
- Social features (following, activity)
- Preference management
- Notification management

## Database Design

### Primary Database (PostgreSQL)

#### 1. Restaurants Table
```sql
CREATE TABLE restaurants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    address JSONB NOT NULL,
    geo_location POINT,
    cuisine_types VARCHAR[] NOT NULL,
    price_tier SMALLINT NOT NULL CHECK (price_tier BETWEEN 1 AND 4),
    operating_hours JSONB NOT NULL,
    website VARCHAR(255),
    phone VARCHAR(20),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    is_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Indexes
    INDEX idx_restaurants_geo (geo_location) USING GIST,
    INDEX idx_restaurants_cuisine (cuisine_types),
    INDEX idx_restaurants_price (price_tier)
);
```

#### 2. Menu Items Table
```sql
CREATE TABLE menu_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurant_id UUID NOT NULL REFERENCES restaurants(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(10,2) NOT NULL,
    image_urls TEXT[],
    category VARCHAR(100) NOT NULL,
    dietary_attributes VARCHAR[] DEFAULT '{}',
    is_seasonal BOOLEAN DEFAULT FALSE,
    available_from TIMESTAMP WITH TIME ZONE,
    available_until TIMESTAMP WITH TIME ZONE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    -- Composite index for restaurant menu lookups
    UNIQUE (restaurant_id, name, category),
    
    -- Indexes
    INDEX idx_menu_items_restaurant (restaurant_id),
    INDEX idx_menu_items_category (category),
    INDEX idx_menu_items_dietary (dietary_attributes),
    INDEX idx_menu_items_available (available_from, available_until)
);
```

#### 3. Food Item Reviews Table
```sql
CREATE TABLE food_item_reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    menu_item_id UUID NOT NULL REFERENCES menu_items(id),
    restaurant_id UUID NOT NULL REFERENCES restaurants(id),
    rating SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    review_text TEXT,
    price_value_rating SMALLINT CHECK (price_value_rating BETWEEN 1 AND 3), -- 1=overpriced, 2=fair, 3=great value
    tags VARCHAR[],
    photo_urls TEXT[],
    visit_date DATE,
    upvotes INT DEFAULT 0,
    downvotes INT DEFAULT 0,
    quality_score FLOAT DEFAULT 0.0,
    moderation_status VARCHAR(20) DEFAULT 'approved', -- 'pending', 'approved', 'rejected'
    is_verified_purchase BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    -- Prevent duplicate reviews from same user for same item
    UNIQUE (user_id, menu_item_id),
    
    -- Indexes
    INDEX idx_reviews_menu_item (menu_item_id, created_at DESC),
    INDEX idx_reviews_restaurant (restaurant_id, created_at DESC),
    INDEX idx_reviews_user (user_id, created_at DESC),
    INDEX idx_reviews_rating (menu_item_id, rating DESC),
    INDEX idx_reviews_quality (quality_score DESC),
    INDEX idx_reviews_moderation (moderation_status, created_at)
);
```

#### 4. Review Votes Table
```sql
CREATE TABLE review_votes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    review_id UUID NOT NULL REFERENCES food_item_reviews(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id),
    vote_type VARCHAR(10) NOT NULL CHECK (vote_type IN ('upvote', 'downvote')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    -- Prevent multiple votes from same user
    UNIQUE (review_id, user_id)
);
```

#### 5. Food Item Aggregates Table (Materialized View)
```sql
CREATE MATERIALIZED VIEW food_item_aggregates AS
SELECT 
    menu_item_id,
    restaurant_id,
    COUNT(*) as review_count,
    AVG(rating) as average_rating,
    percentile_cont(0.5) WITHIN GROUP (ORDER BY rating) as median_rating,
    MODE() WITHIN GROUP (ORDER BY price_value_rating) as price_value_consensus,
    COUNT(CASE WHEN rating >= 4 THEN 1 END) as high_ratings,
    COUNT(CASE WHEN rating <= 2 THEN 1 END) as low_ratings,
    array_agg(DISTINCT tags) FILTER (WHERE tags IS NOT NULL) as common_tags,
    COUNT(CASE WHEN photo_urls IS NOT NULL AND array_length(photo_urls, 1) > 0 THEN 1 END) as photo_count,
    MAX(created_at) as last_review_date,
    AVG(quality_score) as avg_quality_score
FROM food_item_reviews
WHERE moderation_status = 'approved'
GROUP BY menu_item_id, restaurant_id;

-- Create unique index for refresh efficiency
CREATE UNIQUE INDEX ON food_item_aggregates (menu_item_id);
```

### Search Database (Elasticsearch)

#### Food Item Search Index
```json
{
  "mappings": {
    "properties": {
      "id": {"type": "keyword"},
      "restaurant_id": {"type": "keyword"},
      "name": {"type": "text", "analyzer": "english", "fields": {"raw": {"type": "keyword"}}},
      "description": {"type": "text", "analyzer": "english"},
      "category": {"type": "keyword"},
      "price": {"type": "float"},
      "average_rating": {"type": "float"},
      "review_count": {"type": "integer"},
      "dietary_attributes": {"type": "keyword"},
      "tags": {"type": "keyword"},
      "price_value_rating": {"type": "float"},
      "photo_count": {"type": "integer"},
      "is_popular": {"type": "boolean"},
      "location": {"type": "geo_point"},
      "created_at": {"type": "date"},
      "updated_at": {"type": "date"}
    }
  }
}
```

### Cache Structure (Redis)

#### Key Patterns
```
# Top rated items by restaurant (1 hour TTL)
restaurant:{restaurant_id}:top_items -> ZSET[menu_item_id -> score]

# Popular items by location (30 min TTL)
popular:items:{location_grid} -> ZSET[menu_item_id -> popularity_score]

# Recent reviews (15 min TTL)
recent_reviews:item:{menu_item_id} -> LIST[review_ids]

# User's food preferences (1 day TTL)
user:{user_id}:preferences -> HASH{cuisine -> score, ingredient -> score}

# Item rating distribution (2 hours TTL)
rating_dist:item:{menu_item_id} -> HASH{1 -> count, 2 -> count, ...}

# Search autocomplete (1 hour TTL)
autocomplete:food_items -> ZSET[term -> score]
```

## API Design

### RESTful API Endpoints

#### Review Management
```
POST   /api/v1/reviews                    # Create a food item review
GET    /api/v1/reviews/{id}               # Get review by ID
PUT    /api/v1/reviews/{id}               # Update a review (owner only)
DELETE /api/v1/reviews/{id}               # Delete a review (owner only)
GET    /api/v1/menu-items/{id}/reviews    # Get reviews for a menu item
POST   /api/v1/reviews/{id}/vote          # Upvote/downvote a review
GET    /api/v1/users/{id}/reviews         # Get user's reviews
```

#### Restaurant & Menu Management
```
GET    /api/v1/restaurants                # Search/list restaurants
GET    /api/v1/restaurants/{id}           # Get restaurant details
GET    /api/v1/restaurants/{id}/menu      # Get restaurant menu
GET    /api/v1/restaurants/{id}/popular   # Get popular items at restaurant
POST   /api/v1/restaurants                # Create restaurant (admin/owner)
PUT    /api/v1/restaurants/{id}           # Update restaurant (owner only)
POST   /api/v1/menu-items                 # Add menu item (owner only)
PUT    /api/v1/menu-items/{id}            # Update menu item (owner only)
```

#### Discovery API
```
GET    /api/v1/discovery/nearby           # Discover restaurants nearby
GET    /api/v1/discovery/trending         # Get trending dishes
GET    /api/v1/discovery/recommended      # Get personalized recommendations
GET    /api/v1/search                     # Search for restaurants or dishes
GET    /api/v1/tags                       # Get popular food tags
```

#### Analytics API (Restaurant Owners)
```
GET    /api/v1/analytics/menu-performance   # Menu item performance metrics
GET    /api/v1/analytics/sentiment          # Review sentiment analysis
GET    /api/v1/analytics/competitive        # Competitive analysis
GET    /api/v1/analytics/price-sensitivity  # Price sensitivity reports
```

#### Rewards API
```
GET    /api/v1/users/{id}/rewards           # Get user's reward points
GET    /api/v1/users/{id}/reward-history    # Get user's reward history
GET    /api/v1/rewards/leaderboard          # Get top reviewers leaderboard
```

### GraphQL API (Alternative/Complementary)

```graphql
type Query {
  # Reviews
  review(id: ID!): Review
  menuItemReviews(menuItemId: ID!, filter: ReviewFilter, page: PageInput): ReviewConnection
  userReviews(userId: ID!, filter: ReviewFilter, page: PageInput): ReviewConnection
  
  # Restaurants & Menu
  restaurant(id: ID!): Restaurant
  restaurants(filter: RestaurantFilter, page: PageInput): RestaurantConnection
  menuItem(id: ID!): MenuItem
  restaurantMenu(restaurantId: ID!, categories: [String]): [MenuItem]
  popularItems(restaurantId: ID!): [MenuItem]
  
  # Discovery
  nearbyRestaurants(lat: Float!, lng: Float!, radius: Int): [Restaurant]
  searchItems(query: String!, filter: SearchFilter): SearchResultConnection
  recommendedItems(userId: ID!): [MenuItem]
}

type Mutation {
  createReview(input: CreateReviewInput!): Review
  updateReview(id: ID!, input: UpdateReviewInput!): Review
  deleteReview(id: ID!): Boolean
  voteReview(id: ID!, vote: VoteType!): Review  # vote: UPVOTE or DOWNVOTE
  
  # Restaurant owner mutations
  createMenuItem(input: CreateMenuItemInput!): MenuItem
  updateMenuItem(id: ID!, input: UpdateMenuItemInput!): MenuItem
  updateRestaurant(id: ID!, input: UpdateRestaurantInput!): Restaurant
}
```

## Caching Strategy

### Multi-level Caching

1. **Browser/Client Caching**
   - Static assets: 1 day cache with versioning
   - Restaurant/menu data: 1 hour with ETag validation

2. **CDN Caching**
   - Images and static content: 7 days
   - API responses for popular items: 15 minutes

3. **API Gateway Cache**
   - Authentication tokens: 15 minutes
   - Rate limit counters: 1 minute

4. **Application Cache (Redis)**
   - Restaurant menus: 30 minutes
   - Popular/trending items: 15 minutes
   - Review aggregates: 1 hour with on-update invalidation
   - User preferences: 24 hours
   - Search results for common queries: 10 minutes

5. **Database Cache**
   - Materialized views for aggregates: refresh every 5 minutes
   - Query result cache: 5 minutes for complex analytics queries

### Cache Invalidation Strategy

1. **Time-Based Expiration**
   - Default strategy for most caches

2. **Event-Based Invalidation**
   - Review submission triggers invalidation of:
     - Menu item rating cache
     - Restaurant popular items
     - User's recent activity

3. **Versioned Cache Keys**
   - Append version to cache keys for restaurant menus and easily invalidate all caches when menu changes

## Scalability & Performance

### Read/Write Scaling

1. **Database Scaling**
   - Primary-replica setup for PostgreSQL
   - Read replicas for review queries
   - Write sharding based on restaurant_id hash

2. **Service Scaling**
   - Stateless services for horizontal scaling
   - Service-specific scaling policies
     - Review service: CPU-based
     - Search service: Memory and query-load based
     - Restaurant service: Request-count based

3. **Caching Tier Scaling**
   - Redis cluster with sharding
   - Separate instances for different data types

### Performance Optimizations

1. **Query Optimization**
   - Denormalized data for common access patterns
   - Materialized views for aggregates
   - Composite indexes for common filters

2. **Asynchronous Processing**
   - Offload intensive tasks to background jobs:
     - Image processing/resizing
     - Review analysis
     - Recommendation generation
     - Email notifications

3. **Connection Pooling**
   - Database connection pools
   - HTTP client connection pools for external services

## Security Considerations

1. **Authentication & Authorization**
   - OAuth 2.0 / JWT for user auth
   - Role-based access control
   - Restaurant owner verification process

2. **Rate Limiting & Abuse Prevention**
   - IP-based rate limits
   - User-based rate limits for reviews
   - CAPTCHA for suspicious activity
   - Fraud detection for review manipulation

3. **Data Protection**
   - Encryption at rest for PII
   - TLS for all communications
   - PII minimization in logs
   - GDPR compliance features

## Review Reward System

### 1. Review Quality Scoring

The review quality score is calculated using a weighted algorithm that considers multiple factors:

```
QualityScore = (0.3 * LengthScore) +
              (0.2 * PhotoScore) + 
              (0.25 * CommunityScore) + 
              (0.15 * DetailsScore) +
              (0.1 * HistoryScore)
```

Where:
- **LengthScore**: Based on review text length and level of detail (0-1)
- **PhotoScore**: Quality and quantity of attached photos (0-1)
- **CommunityScore**: Ratio of upvotes to total votes (0-1)
- **DetailsScore**: Presence of specific details like taste, portion size, etc. (0-1)
- **HistoryScore**: User's historical review quality (0-1)

### 2. Reward Point System

```sql
CREATE TABLE user_rewards (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    total_points INT DEFAULT 0,
    lifetime_points INT DEFAULT 0,
    current_tier VARCHAR(20) DEFAULT 'BRONZE', -- BRONZE, SILVER, GOLD, PLATINUM
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    -- Index
    UNIQUE (user_id)
);

CREATE TABLE reward_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    review_id UUID REFERENCES food_item_reviews(id),
    points INT NOT NULL,
    reason VARCHAR(50) NOT NULL, -- 'REVIEW_CREATED', 'UPVOTES_MILESTONE', 'STREAK_BONUS'
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    -- Indexes
    INDEX idx_reward_user (user_id, created_at DESC),
    INDEX idx_reward_review (review_id)
);
```

### 3. Point Allocation Rules

Reward points are allocated based on several actions:

1. **Base Review Points**:
   - New review submission: 5-10 points depending on length
   - Adding photos: +5 points per photo (up to 3 photos)

2. **Quality-Based Rewards**:
   - Quality score thresholds:
     - 0.8-1.0: 20 additional points
     - 0.6-0.79: 10 additional points
     - 0.4-0.59: 5 additional points

3. **Community Reception Bonuses**:
   - Every 5 upvotes: +3 points
   - Featured review (high quality): +25 points

4. **Consistency Bonuses**:
   - Weekly review streak: +5 points per week
   - Monthly top reviewer: +50 points

### 4. Reward Processing Flow

1. **Review Submission**:
   - User submits a review for a menu item
   - Initial quality score calculated based on content
   - Base points awarded immediately

2. **Background Processing**:
   - Asynchronous job evaluates review quality in detail
   - Additional points may be awarded based on full analysis

3. **Community Feedback Processing**:
   - As upvotes accumulate, milestone rewards are triggered
   - Scheduled jobs check for upvote thresholds

4. **Tier Progression**:
   - Bronze: 0-100 points
   - Silver: 101-500 points
   - Gold: 501-2000 points
   - Platinum: 2001+ points

### 5. Reward Benefits

1. **Monetary Rewards**:
   - Points can be converted to platform credits
   - 100 points = $1 in delivery credits

2. **Status Benefits**:
   - Higher visibility for reviews
   - Early access to new features
   - Priority support

3. **Special Perks**:
   - Restaurant sampling invitations for top reviewers
   - Exclusive events and tastings
   - Recognition badges on profile

### 6. Anti-Gaming Measures

1. **Review Velocity Limits**:
   - Maximum of 5 rewarded reviews per restaurant per month

2. **Quality Verification**:
   - ML-based spam and fake review detection
   - Manual spot checks on suspicious activity

3. **Upvote Pattern Analysis**:
   - Detection of coordinated voting rings
   - IP tracking for suspicious voting patterns

4. **Reward Caps**:
   - Daily and weekly earning caps
   - Diminishing returns on multiple reviews of similar items

## Analytics & ML Features

### Real-time Analytics

1. **Operational Metrics**
   - Review submission rate
   - User engagement rate
   - Search query volume
   - API response times

2. **Business Insights**
   - Review sentiment trends
   - Popular dish clustering
   - Price sensitivity analysis
   - Competitive benchmarking

### Machine Learning Applications

1. **Review Analysis**
   - Sentiment analysis
   - Fake review detection
   - Review summarization
   - Keyword extraction

2. **Recommendation Engine**
   - Collaborative filtering
   - Content-based recommendations
   - Hybrid recommendation model
   - Contextual recommendations (time, weather, location)

3. **Trend Detection**
   - Emerging dish trends
   - Cuisine popularity shifts
   - Seasonal preference patterns

## Implementation Details

### Technology Stack

#### Backend Services
- **Language**: Java (Spring Boot) or Node.js
- **Database**: PostgreSQL 14+ (primary), Redis (cache)
- **Search**: Elasticsearch
- **Message Queue**: Apache Kafka
- **Object Storage**: AWS S3
- **ML Framework**: TensorFlow or PyTorch

#### Infrastructure
- **Cloud**: AWS
- **Containers**: Docker + Kubernetes
- **CDN**: CloudFront
- **Monitoring**: Prometheus + Grafana
- **Logging**: ELK Stack
- **CI/CD**: GitHub Actions

### Deployment Architecture

```yaml
# Kubernetes Service Config (Example)
apiVersion: v1
kind: Service
metadata:
  name: review-service
  labels:
    app: review-service
spec:
  ports:
  - port: 80
    targetPort: 8080
  selector:
    app: review-service
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: review-service
spec:
  replicas: 3
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
        image: food-review/review-service:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: DB_URL
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: url
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
```

### Data Replication & Backup Strategy

1. **Database Replication**
   - Synchronous replication for critical writes
   - Asynchronous replication for read replicas
   - Multi-region replication for disaster recovery

2. **Backup Strategy**
   - Daily full backups of PostgreSQL
   - Point-in-time recovery with WAL archiving
   - 30-day retention policy
   - Monthly backup verification

3. **Data Recovery Plan**
   - RTO (Recovery Time Objective): 1 hour
   - RPO (Recovery Point Objective): 5 minutes
   - Automated recovery procedures
   - Regular DR testing

This design provides a comprehensive foundation for building a restaurant food item review system that can scale to millions of users while providing valuable insights to both diners and restaurant owners. The granular nature of food item reviews enables a richer data ecosystem than traditional restaurant-level reviews.
