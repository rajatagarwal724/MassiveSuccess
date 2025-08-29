# Glassdoor System Design

## 1. Introduction

Glassdoor is a platform where employees can anonymously review companies, share salary information, and access job listings. The system must handle sensitive data while maintaining user anonymity, provide robust search capabilities, and scale to millions of users and companies.

## 2. Requirements

### 2.1 Functional Requirements
- **Company Reviews**: Submit and view anonymous company reviews
- **Salary Information**: Share and browse salary data by role/company
- **Job Listings**: Post and search job opportunities
- **Company Profiles**: Comprehensive company information and ratings
- **User Profiles**: Professional profiles with privacy controls
- **Search & Discovery**: Advanced search across reviews, salaries, jobs
- **Analytics Dashboard**: Company insights and market trends

### 2.2 Non-Functional Requirements
- **Scalability**: Support 100M+ users, 1M+ companies
- **Anonymity**: Protect reviewer identity while preventing abuse
- **Performance**: <200ms search response time
- **Availability**: 99.9% uptime
- **Data Integrity**: Accurate salary and review data
- **Global Scale**: Support multiple countries and currencies

### 2.3 Extended Requirements
- **Mobile Apps**: iOS/Android applications
- **API Platform**: Third-party integrations
- **Content Moderation**: Automated and manual review systems
- **Employer Branding**: Premium features for companies
- **Notifications**: Email/push notifications for relevant content

## 3. Capacity Estimation

### 3.1 Scale Assumptions
- **Users**: 100M registered users, 10M DAU
- **Companies**: 1M companies with profiles
- **Reviews**: 50M total reviews, 100K new reviews/day
- **Salary Reports**: 20M salary data points, 50K new/day
- **Job Listings**: 5M active jobs, 500K new jobs/day
- **Searches**: 50M searches/day

### 3.2 Storage Requirements
```
User Data: 100M users × 2KB = 200GB
Company Data: 1M companies × 50KB = 50GB
Reviews: 50M reviews × 5KB = 250GB
Salary Data: 20M records × 1KB = 20GB
Job Listings: 5M jobs × 10KB = 50GB
Search Indexes: ~2x primary data = 1.14TB
Total: ~1.7TB (with replication: ~5TB)
```

### 3.3 Throughput Requirements
```
Read Operations:
- Page views: 10M DAU × 20 pages = 200M/day = 2,315 QPS
- Search queries: 50M/day = 580 QPS
- API calls: 100M/day = 1,160 QPS

Write Operations:
- New reviews: 100K/day = 1.2 QPS
- Salary reports: 50K/day = 0.6 QPS
- Job postings: 500K/day = 5.8 QPS
- User activities: 50M/day = 580 QPS
```

## 4. High-Level Design

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Web Client    │────│   CDN/Cache      │────│  Load Balancer  │
│   Mobile App    │    │                  │    │                 │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                                         │
                       ┌─────────────────────────────────┼─────────────────────────────────┐
                       │                                 │                                 │
              ┌─────────────────┐              ┌─────────────────┐              ┌─────────────────┐
              │  API Gateway    │              │  Search Service │              │  Auth Service   │
              └─────────────────┘              └─────────────────┘              └─────────────────┘
                       │                                 │                                 │
       ┌───────────────┼───────────────┐                │                ┌────────────────┼────────────────┐
       │               │               │                │                │                │                │
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│Review Service│ │Salary Service│ │ Job Service │ │Company Service│ │User Service │ │Analytics Svc│ │Content Mod  │
└─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘
       │               │               │                │                │                │                │
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│Review DB    │ │Salary DB    │ │ Job DB      │ │Company DB   │ │User DB      │ │Analytics DB │ │ML Models    │
│(PostgreSQL) │ │(PostgreSQL) │ │(PostgreSQL) │ │(PostgreSQL) │ │(PostgreSQL) │ │(ClickHouse) │ │             │
└─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘
                                                                                                                │
                                ┌─────────────────────────────────────────────────────────────────────────────┘
                                │
                       ┌─────────────────┐              ┌─────────────────┐              ┌─────────────────┐
                       │ Elasticsearch   │              │ Redis Cache     │              │ Message Queue   │
                       │   (Search)      │              │                 │              │   (Kafka)       │
                       └─────────────────┘              └─────────────────┘              └─────────────────┘
```

## 5. Detailed Component Design

### 5.1 Review Service

**Core Functionality:**
- Anonymous review submission with identity protection
- Review aggregation and rating calculations
- Spam and fake review detection
- Review moderation workflow

**Data Model:**
```sql
-- Reviews Table
CREATE TABLE reviews (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL,
    anonymous_user_hash VARCHAR(64) NOT NULL, -- Hashed user identifier
    overall_rating INTEGER CHECK (overall_rating >= 1 AND overall_rating <= 5),
    work_life_balance_rating INTEGER,
    culture_values_rating INTEGER,
    career_opportunities_rating INTEGER,
    compensation_benefits_rating INTEGER,
    senior_management_rating INTEGER,
    review_title VARCHAR(200),
    pros TEXT,
    cons TEXT,
    advice_to_management TEXT,
    job_title VARCHAR(100),
    employment_status VARCHAR(50), -- Current Employee, Former Employee
    employment_length VARCHAR(50), -- Less than 1 year, 1-3 years, etc.
    location VARCHAR(100),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    is_approved BOOLEAN DEFAULT FALSE,
    moderation_status VARCHAR(20) DEFAULT 'PENDING',
    helpful_count INTEGER DEFAULT 0,
    not_helpful_count INTEGER DEFAULT 0
);

-- Indexes for performance
CREATE INDEX idx_reviews_company_approved ON reviews(company_id, is_approved, created_at);
CREATE INDEX idx_reviews_moderation ON reviews(moderation_status, created_at);
```

**Anonymity Protection:**
```java
public class AnonymityService {
    
    public String generateAnonymousHash(String userId, String companyId) {
        // Combine user ID with company ID and salt to create anonymous hash
        String input = userId + ":" + companyId + ":" + SALT;
        return SHA256.hash(input);
    }
    
    public boolean canUserReview(String userId, String companyId) {
        String hash = generateAnonymousHash(userId, companyId);
        // Check if user already reviewed this company
        return !reviewRepository.existsByAnonymousHashAndCompanyId(hash, companyId);
    }
}
```

### 5.2 Salary Service

**Core Functionality:**
- Anonymous salary data collection
- Salary range calculations and statistics
- Market trend analysis
- Compensation package breakdown

**Data Model:**
```sql
CREATE TABLE salary_reports (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL,
    anonymous_user_hash VARCHAR(64) NOT NULL,
    job_title VARCHAR(100) NOT NULL,
    job_level VARCHAR(50), -- Entry, Mid, Senior, Executive
    department VARCHAR(100),
    location VARCHAR(100),
    base_salary INTEGER,
    bonus INTEGER,
    equity_value INTEGER,
    total_compensation INTEGER,
    years_of_experience INTEGER,
    years_at_company INTEGER,
    currency VARCHAR(3) DEFAULT 'USD',
    employment_type VARCHAR(50), -- Full-time, Part-time, Contract
    created_at TIMESTAMP DEFAULT NOW(),
    is_approved BOOLEAN DEFAULT FALSE
);

-- Indexes for salary analytics
CREATE INDEX idx_salary_company_title ON salary_reports(company_id, job_title, is_approved);
CREATE INDEX idx_salary_location_title ON salary_reports(location, job_title, is_approved);
CREATE INDEX idx_salary_analytics ON salary_reports(job_title, location, years_of_experience, is_approved);
```

**Salary Analytics:**
```java
public class SalaryAnalyticsService {
    
    public SalaryStatistics calculateSalaryStats(String jobTitle, String location) {
        List<SalaryReport> reports = salaryRepository.findByJobTitleAndLocation(
            jobTitle, location);
        
        return SalaryStatistics.builder()
            .median(calculateMedian(reports))
            .percentile25(calculatePercentile(reports, 25))
            .percentile75(calculatePercentile(reports, 75))
            .average(calculateAverage(reports))
            .sampleSize(reports.size())
            .build();
    }
}
```

### 5.3 Search Service

**Search Architecture:**
```
Search Query → API Gateway → Search Service → Elasticsearch → Results
                                    ↓
                            Result Ranking & Filtering
                                    ↓
                            Cache Results (Redis)
```

**Elasticsearch Schema:**
```json
{
  "mappings": {
    "properties": {
      "type": {"type": "keyword"}, // review, salary, job, company
      "company_id": {"type": "keyword"},
      "company_name": {
        "type": "text",
        "analyzer": "standard",
        "fields": {
          "keyword": {"type": "keyword"}
        }
      },
      "job_title": {
        "type": "text",
        "analyzer": "standard"
      },
      "location": {"type": "keyword"},
      "content": {
        "type": "text",
        "analyzer": "english"
      },
      "rating": {"type": "float"},
      "salary_range": {
        "type": "integer_range"
      },
      "created_at": {"type": "date"},
      "tags": {"type": "keyword"}
    }
  }
}
```

**Search API:**
```java
@RestController
public class SearchController {
    
    @GetMapping("/search")
    public SearchResponse search(
        @RequestParam String query,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String location,
        @RequestParam(required = false) String company,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        
        SearchRequest request = SearchRequest.builder()
            .query(query)
            .type(type)
            .location(location)
            .company(company)
            .page(page)
            .size(size)
            .build();
            
        return searchService.search(request);
    }
}
```

### 5.4 Content Moderation System

**Multi-Layer Moderation:**
```
Content Submission → Automated Filters → ML Classification → Human Review → Publication
```

**Automated Filters:**
```java
public class ContentModerationService {
    
    public ModerationResult moderateReview(Review review) {
        // 1. Profanity filter
        if (profanityFilter.containsProfanity(review.getContent())) {
            return ModerationResult.REJECTED("Contains inappropriate language");
        }
        
        // 2. Spam detection
        if (spamDetector.isSpam(review)) {
            return ModerationResult.REJECTED("Detected as spam");
        }
        
        // 3. ML-based classification
        double toxicityScore = mlClassifier.getToxicityScore(review.getContent());
        if (toxicityScore > TOXICITY_THRESHOLD) {
            return ModerationResult.FLAGGED("High toxicity score: " + toxicityScore);
        }
        
        // 4. Duplicate detection
        if (duplicateDetector.isDuplicate(review)) {
            return ModerationResult.REJECTED("Duplicate content detected");
        }
        
        return ModerationResult.APPROVED();
    }
}
```

## 6. Scalability Strategies

### 6.1 Database Scaling

**Read Replicas:**
```
Master DB (Writes) → Read Replica 1 (Analytics Queries)
                  → Read Replica 2 (Search Indexing)
                  → Read Replica 3 (General Reads)
```

**Sharding Strategy:**
```sql
-- Shard by company_id for reviews and salaries
-- Ensures all data for a company is co-located
Shard 1: company_id % 4 = 0
Shard 2: company_id % 4 = 1  
Shard 3: company_id % 4 = 2
Shard 4: company_id % 4 = 3
```

### 6.2 Caching Strategy

**Multi-Level Caching:**
```
L1: Application Cache (Caffeine) - 1 minute TTL
L2: Redis Cache - 15 minutes TTL
L3: CDN Cache - 1 hour TTL for static content
```

**Cache Keys:**
```
Company Profile: "company:{company_id}"
Company Reviews: "reviews:{company_id}:page:{page}"
Salary Stats: "salary_stats:{job_title}:{location}"
Search Results: "search:{query_hash}:page:{page}"
```

### 6.3 Search Scaling

**Elasticsearch Cluster:**
```
Index Strategy: Time-based indices
- reviews_2024_01, reviews_2024_02, etc.
- Allows for efficient data lifecycle management

Sharding: 5 primary shards per index
Replication: 1 replica per shard
Node Types: Master, Data, Coordinating nodes
```

## 7. Security and Privacy

### 7.1 Anonymity Protection

**User Identity Protection:**
```java
public class AnonymityManager {
    
    // Generate consistent but anonymous identifier
    public String createAnonymousId(String userId, String companyId) {
        return HMAC_SHA256(userId + companyId, SECRET_KEY);
    }
    
    // Prevent correlation attacks
    public boolean validateReviewFrequency(String anonymousId) {
        int reviewCount = getReviewCountInLastMonth(anonymousId);
        return reviewCount <= MAX_REVIEWS_PER_MONTH;
    }
}
```

**Data Minimization:**
```sql
-- Store only necessary data, hash sensitive information
CREATE TABLE user_employment_verification (
    id UUID PRIMARY KEY,
    user_hash VARCHAR(64), -- Hashed user ID
    company_hash VARCHAR(64), -- Hashed company ID  
    employment_verified BOOLEAN,
    verification_date TIMESTAMP,
    -- No direct user or company identifiers stored
);
```

### 7.2 Fraud Prevention

**Review Authenticity:**
```java
public class FraudDetectionService {
    
    public FraudRisk assessReviewRisk(Review review, UserContext context) {
        double riskScore = 0.0;
        
        // IP-based analysis
        if (isVPNOrProxy(context.getIpAddress())) {
            riskScore += 0.3;
        }
        
        // Behavioral analysis
        if (hasUnusualPostingPattern(context.getUserId())) {
            riskScore += 0.4;
        }
        
        // Content analysis
        if (isSuspiciousContent(review.getContent())) {
            riskScore += 0.5;
        }
        
        return FraudRisk.fromScore(riskScore);
    }
}
```

## 8. Analytics and Insights

### 8.1 Real-time Analytics

**ClickHouse Schema:**
```sql
CREATE TABLE user_events (
    event_id UUID,
    user_id String,
    event_type String, -- page_view, search, review_submit
    company_id String,
    job_title String,
    location String,
    timestamp DateTime,
    session_id String,
    user_agent String,
    ip_address String
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(timestamp)
ORDER BY (timestamp, user_id);
```

**Analytics Queries:**
```sql
-- Company popularity trends
SELECT 
    company_id,
    toStartOfDay(timestamp) as date,
    count() as page_views
FROM user_events 
WHERE event_type = 'company_view'
    AND timestamp >= now() - INTERVAL 30 DAY
GROUP BY company_id, date
ORDER BY date DESC, page_views DESC;

-- Salary search trends
SELECT 
    job_title,
    location,
    count() as search_count
FROM user_events 
WHERE event_type = 'salary_search'
    AND timestamp >= now() - INTERVAL 7 DAY
GROUP BY job_title, location
ORDER BY search_count DESC;
```

### 8.2 Business Intelligence

**Company Insights Dashboard:**
```java
public class CompanyInsightsService {
    
    public CompanyInsights generateInsights(String companyId) {
        return CompanyInsights.builder()
            .overallRating(calculateOverallRating(companyId))
            .ratingTrend(calculateRatingTrend(companyId, 12)) // 12 months
            .reviewVolume(getReviewVolume(companyId))
            .salaryCompetitiveness(analyzeSalaryCompetitiveness(companyId))
            .topConcerns(extractTopConcerns(companyId))
            .competitorComparison(compareWithCompetitors(companyId))
            .build();
    }
}
```

## 9. Monitoring and Observability

### 9.1 Key Metrics

**Business Metrics:**
```
- Daily Active Users (DAU)
- Review submission rate
- Search conversion rate
- Company profile completeness
- User engagement metrics
```

**Technical Metrics:**
```
- API response times (p50, p95, p99)
- Database query performance
- Search query latency
- Cache hit rates
- Error rates by service
```

### 9.2 Alerting

**Critical Alerts:**
```yaml
alerts:
  - name: HighErrorRate
    condition: error_rate > 5%
    duration: 5m
    
  - name: SlowSearchQueries  
    condition: search_latency_p95 > 1s
    duration: 2m
    
  - name: DatabaseConnections
    condition: db_connections > 80%
    duration: 1m
```

## 10. Future Enhancements

### 10.1 Advanced Features
- **AI-Powered Insights**: ML-driven company culture analysis
- **Video Reviews**: Support for video testimonials
- **Real-time Chat**: Connect with current employees
- **Blockchain Verification**: Immutable employment verification

### 10.2 Global Expansion
- **Multi-language Support**: Localized content and search
- **Regional Compliance**: GDPR, local privacy laws
- **Currency Normalization**: Cross-country salary comparisons
- **Cultural Adaptation**: Region-specific review categories

This design provides a comprehensive foundation for a Glassdoor-like platform that prioritizes user anonymity while delivering valuable insights about companies and compensation.
