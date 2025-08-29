1. Multiple servers uploading and streaming music and load is distributed using consistent hashing based on the number of files on each server equally. Any concerns ?
(Follow-up: How would you improve this system ?)

**Answer:**

### **Concerns with Current Approach:**

**File Count vs File Size Mismatch:**
- **Problem**: Equal file count ≠ equal storage/bandwidth load
- **Example**: Server A has 1000 small songs (3MB each), Server B has 1000 large songs (10MB each)
- **Result**: Server B handles 3x more data than Server A despite same file count

**Hotspot Issues:**
- **Popular Content**: Viral songs create uneven traffic distribution
- **Geographic Patterns**: Regional music preferences cause localized hotspots
- **Time-based Patterns**: Peak hours overwhelm certain servers

**Consistent Hashing Limitations:**
- **Hash Function**: File count-based hashing doesn't consider actual resource usage
- **Rebalancing**: Adding/removing servers causes significant data movement
- **Cold Start**: New servers start empty while others remain overloaded

### **How to Improve the System:**

**1. Weighted Consistent Hashing:**
```
Hash Key = f(file_size, popularity_score, bandwidth_usage)
```
- Consider file size, not just count
- Factor in historical access patterns
- Weight by server capacity (CPU, memory, bandwidth)

**2. Multi-tier Architecture:**
```
Hot Tier (SSD) → Warm Tier (HDD) → Cold Tier (Archive)
```
- **Hot**: Recently uploaded, trending songs
- **Warm**: Popular catalog, frequently accessed
- **Cold**: Archive, rarely accessed content

**3. Content Delivery Network (CDN):**
- **Edge Caching**: Cache popular content closer to users
- **Geographic Distribution**: Regional CDN nodes
- **Dynamic Caching**: ML-based prediction of trending content

**4. Load Balancing Improvements:**
```
Load Balancer → Health Check → Weighted Round Robin
                     ↓
            Monitor: CPU, Memory, Bandwidth, Disk I/O
```

**5. Real-time Monitoring & Auto-scaling:**
- **Metrics**: Track actual resource usage, not just file count
- **Alerts**: Threshold-based scaling triggers
- **Predictive Scaling**: ML models for traffic prediction

---

2. What are the estimation required in next 6 months for a system which accepts web URLS from users, parses them and derives information using machine learning models ?

**Answer:**

### **Capacity Planning Estimations:**

**Traffic Estimations:**
- **Daily Active Users (DAU)**: Estimate based on target market
- **URLs per User per Day**: Average user behavior analysis
- **Peak Traffic Multiplier**: 3-5x average during peak hours
- **Growth Rate**: Monthly user acquisition rate

**Example Calculations:**
```
Assumptions:
- 100K DAU initially, growing to 500K in 6 months
- 10 URLs per user per day average
- Peak traffic: 5x average
- Processing time: 2-3 seconds per URL

Daily URLs: 500K users × 10 URLs = 5M URLs/day
Peak QPS: (5M × 5) / (24 × 3600) = ~289 QPS
```

**Infrastructure Requirements:**

**1. Web Servers/API Gateway:**
- **Instances**: 10-15 instances (auto-scaling 5-25)
- **Specs**: 4 vCPU, 8GB RAM per instance
- **Load Balancer**: Application Load Balancer with health checks

**2. URL Processing Pipeline:**
```
API Gateway → Queue (SQS/Kafka) → Workers → ML Models → Database
```
- **Queue Capacity**: Handle 10x peak load (2,890 messages/sec)
- **Worker Instances**: 20-30 processing workers
- **Worker Specs**: 8 vCPU, 16GB RAM (CPU-intensive parsing)

**3. Machine Learning Infrastructure:**
- **Model Serving**: 5-10 ML model servers
- **GPU Requirements**: 2-4 GPU instances for deep learning models
- **Model Storage**: 500GB-1TB for model artifacts
- **Inference Time**: Target <500ms per URL

**4. Database & Storage:**
- **Primary DB**: PostgreSQL cluster (Master + 2 Read Replicas)
- **Cache Layer**: Redis cluster (32GB memory)
- **Object Storage**: 10-50TB for crawled content/images
- **Search Index**: Elasticsearch cluster for URL metadata

**5. Monitoring & Logging:**
- **Metrics Storage**: Prometheus + Grafana
- **Log Storage**: 1-5TB/month log retention
- **APM Tools**: DataDog/New Relic for performance monitoring

### **Cost Estimations (AWS/GCP):**

**Monthly Infrastructure Costs:**
```
Compute (EC2/Compute Engine):     $3,000-5,000
Database (RDS/Cloud SQL):         $1,500-2,500
Storage (S3/Cloud Storage):       $500-1,000
Load Balancer & Networking:       $300-500
Monitoring & Logging:             $200-400
ML/GPU Instances:                 $2,000-4,000
CDN (CloudFront/Cloud CDN):       $300-800

Total Monthly: $7,800-14,200
6-Month Total: $47K-85K
```

**Scaling Considerations:**
- **Auto-scaling**: Configure based on queue depth and CPU utilization
- **Geographic Distribution**: Multi-region deployment for global users
- **Disaster Recovery**: Cross-region backups and failover
- **Performance SLA**: 99.9% uptime, <2s response time target

**Team & Operational Costs:**
- **DevOps Engineer**: $120K-150K annually
- **ML Engineer**: $140K-180K annually  
- **Backend Engineers**: 2-3 engineers at $130K-160K each
- **On-call Support**: 24/7 monitoring and incident response

3. How to handle a large file that cannot fit on a single machine ?

**Answer:**

**File Chunking/Splitting Approaches:**
- **Horizontal Splitting**: Split file into fixed-size chunks (e.g., 64MB, 128MB blocks)
- **Logical Splitting**: Split based on content boundaries (e.g., by lines, records, or natural delimiters)
- **Hash-based Partitioning**: Use hash function on key fields to distribute data evenly

**Storage Solutions:**
- **Distributed File Systems**: HDFS, GFS, Amazon S3 with multipart upload
- **Object Storage**: Store chunks as separate objects with metadata tracking
- **Database Sharding**: Split across multiple database instances based on keys

**Processing Strategies:**
- **MapReduce/Spark**: Process chunks in parallel across multiple nodes
- **Stream Processing**: Process file as a continuous stream (Apache Kafka, Flink)
- **Batch Processing**: Queue chunks for processing by worker nodes

**Key Considerations:**
- **Metadata Management**: Track chunk locations, order, and reassembly information
- **Fault Tolerance**: Replicate chunks across multiple nodes (typically 3x replication)
- **Consistency**: Ensure data integrity during splitting and reassembly
- **Network Optimization**: Minimize data movement, process data where it's stored
- **Load Balancing**: Distribute chunks evenly to prevent hotspots

**Example Architecture:**
```
Large File → Splitter Service → Multiple Storage Nodes
                ↓
           Metadata Store (chunk locations, order)
                ↓
         Processing Cluster (parallel processing)
                ↓
           Results Aggregation
```

**Real-world Examples:**
- **HDFS**: Automatically splits files into 128MB blocks
- **MongoDB GridFS**: Splits files into 255KB chunks
- **AWS S3**: Multipart upload for files >100MB

4. What changes would you make to the system when your app is going from single country to multiple countries internationally ?

**Answer:**

**Infrastructure & Performance:**
- **CDN (Content Delivery Network)**: Deploy CloudFlare, AWS CloudFront, or Akamai for faster content delivery
- **Multi-Region Deployment**: Set up servers in different geographic regions (US, EU, APAC)
- **Load Balancing**: Implement geo-based routing to direct users to nearest data centers
- **Database Replication**: Master-slave or multi-master setup across regions with eventual consistency
- **Caching Strategy**: Regional Redis/Memcached clusters to reduce latency

**Localization & Internationalization (i18n):**
- **Language Support**: Implement multi-language UI with resource bundles
- **Currency Handling**: Support multiple currencies with real-time exchange rates
- **Date/Time Formats**: Handle different date, time, and number formats per locale
- **Cultural Adaptation**: Adapt UI/UX for right-to-left languages, cultural preferences
- **Content Localization**: Translate content, images, and marketing materials

**Legal & Compliance:**
- **Data Privacy Laws**: GDPR (Europe), CCPA (California), LGPD (Brazil) compliance
- **Data Residency**: Store user data in specific regions as per local laws
- **Terms of Service**: Localized legal agreements and privacy policies
- **Tax Compliance**: Handle VAT, GST, and other regional tax requirements
- **Content Regulations**: Comply with local content restrictions and censorship laws

**Technical Architecture Changes:**
- **API Gateway**: Implement regional API gateways with rate limiting per region
- **Microservices**: Break monolith into services that can be deployed independently per region
- **Event-Driven Architecture**: Use message queues (Kafka/RabbitMQ) for cross-region communication
- **Circuit Breakers**: Implement fault tolerance for cross-region service calls
- **Monitoring**: Set up region-specific monitoring and alerting (DataDog, New Relic)

**Business Logic Adaptations:**
- **Payment Gateways**: Integrate local payment methods (Alipay, UPI, SEPA)
- **Shipping & Logistics**: Partner with local delivery services
- **Customer Support**: 24/7 support across time zones with local language support
- **Marketing Channels**: Adapt to local social media platforms and marketing strategies

**Security Considerations:**
- **Regional Security Standards**: Comply with local cybersecurity requirements
- **Encryption**: End-to-end encryption with region-specific key management
- **Authentication**: Support local identity providers and SSO systems
- **Network Security**: Regional firewalls and DDoS protection

**Example Multi-Region Architecture:**
```
User Request → DNS/CDN → Regional Load Balancer → Regional API Gateway
                                    ↓
                            Regional Microservices
                                    ↓
                            Regional Database + Cache
                                    ↓
                        Cross-Region Data Sync (Async)
```

**Deployment Strategy:**
- **Blue-Green Deployments**: Per region to minimize downtime
- **Feature Flags**: Enable/disable features per region
- **A/B Testing**: Region-specific experiments and rollouts
- **Gradual Rollout**: Launch in one region first, then expand

5. What are advantages and disadvantages of pre-loading hints vs loading from the server for a puzzle game ?

**Answer:**

## **Pre-loading Hints (Client-side)**

### **Advantages:**
- **Instant Response**: No network latency, hints appear immediately when requested
- **Offline Capability**: Game works without internet connection
- **Reduced Server Load**: No API calls for hints, lower infrastructure costs
- **Better User Experience**: Smooth gameplay without loading delays
- **Predictable Performance**: Not affected by network issues or server downtime
- **Lower Data Usage**: One-time download vs multiple API calls

### **Disadvantages:**
- **Larger App Size**: Hints stored locally increase download/install size
- **Security Risk**: Hints visible in client code, easier to extract/cheat
- **No Dynamic Updates**: Can't update hints without app update
- **Memory Usage**: All hints loaded in memory, potential performance impact
- **Spoiler Risk**: Advanced users can access all hints at once
- **Version Management**: Difficult to fix hint errors without app updates

## **Server-side Loading**

### **Advantages:**
- **Better Security**: Hints protected on server, harder to extract
- **Dynamic Content**: Can update hints, add new ones without app updates
- **Smaller App Size**: Minimal client storage, faster downloads
- **Analytics**: Track hint usage patterns and user behavior
- **Personalization**: Adaptive hints based on user progress/difficulty
- **A/B Testing**: Test different hint strategies in real-time
- **Content Control**: Can disable inappropriate hints instantly

### **Disadvantages:**
- **Network Dependency**: Requires internet connection
- **Latency Issues**: Delay between request and hint display
- **Server Costs**: Infrastructure costs for API hosting and bandwidth
- **Poor Offline Experience**: Game unusable without connection
- **Potential Failures**: Server downtime affects hint functionality
- **Data Usage**: Continuous API calls consume user's data

## **Hybrid Approach (Recommended)**

### **Best of Both Worlds:**
```
Basic Hints (Pre-loaded) + Advanced Hints (Server-side)
```

**Implementation Strategy:**
- **Level 1 Hints**: Pre-load basic hints for immediate access
- **Level 2+ Hints**: Load from server for advanced help
- **Caching**: Cache server hints locally for offline replay
- **Progressive Loading**: Download hints for next few levels in background
- **Fallback**: Use pre-loaded hints if server unavailable

### **Technical Implementation:**
```javascript
// Pseudo-code for hybrid approach
function getHint(level, hintLevel) {
    if (hintLevel === 1 && preloadedHints[level]) {
        return preloadedHints[level]; // Instant
    }
    
    // Check cache first
    if (cachedHints[level][hintLevel]) {
        return cachedHints[level][hintLevel];
    }
    
    // Fetch from server with fallback
    return fetchHintFromServer(level, hintLevel)
        .catch(() => getFallbackHint(level));
}
```

## **Decision Matrix:**

| Factor | Pre-loading | Server-side | Hybrid |
|--------|-------------|-------------|---------|
| **Performance** | ⭐⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐ |
| **Security** | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **Flexibility** | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **Offline Support** | ⭐⭐⭐⭐⭐ | ⭐ | ⭐⭐⭐ |
| **App Size** | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ |
| **Maintenance** | ⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |

**Recommendation**: Use hybrid approach for optimal balance of performance, security, and user experience.


1.) We are working on a clone of Facebook. We want to add a numeric count to every post showing how many friends the post's author has at the time of viewing the post. The database schema looks like this:
USER

user_id (primary key)
name
created_date
USER RELATIONSHIP

friendship_id (primary key, unique to each relationship)
user_id (indexed)
user2_id (indexed)
start_date
Focusing on the database, how would you implement the friend-count feature? Note that we will soon be more popular than Facebook, so the solution needs to scale.

**Answer:**

### **Approach 1: Real-time Count (Not Scalable)**
```sql
SELECT COUNT(*) FROM USER_RELATIONSHIP 
WHERE user_id = ? OR user2_id = ?
```
**Problems**: Expensive JOIN on every post view, doesn't scale.

### **Approach 2: Denormalized Friend Count (Recommended)**

**Add to USER table:**
```sql
ALTER TABLE USER ADD COLUMN friend_count INT DEFAULT 0;
CREATE INDEX idx_user_friend_count ON USER(friend_count);
```

**Update Strategy:**
- **Increment/Decrement**: Update `friend_count` when friendships are added/removed
- **Batch Updates**: Use triggers or background jobs for consistency
- **Event-Driven**: Use message queues (Kafka) to handle friend count updates asynchronously

**Implementation:**
```sql
-- When friendship is created
UPDATE USER SET friend_count = friend_count + 1 WHERE user_id IN (?, ?);

-- When friendship is deleted  
UPDATE USER SET friend_count = friend_count - 1 WHERE user_id IN (?, ?);
```

### **Approach 3: Caching Layer (For Scale)**
- **Redis Cache**: Cache friend counts with TTL
- **Cache Key**: `friend_count:user_id`
- **Cache Strategy**: Write-through or write-behind
- **Fallback**: Query database if cache miss

### **Approach 4: Hybrid (Best for Facebook Scale)**
```
Read Path: Cache → Denormalized Table → Real-time Count (fallback)
Write Path: Database → Message Queue → Cache Update
```

**Benefits:**
- **O(1) reads** from cache/denormalized table
- **Eventual consistency** acceptable for friend counts
- **Horizontal scaling** with sharded caches

---

2.) We are working on a clone of Google Docs. The software has the following features and limitations:

Multiple users may work on a single document at the same time.
A document must be handled by a single server, no matter how many users there are.
We have a fixed number of servers which will be sufficient to handle our expected load properly.
Our load balancer uses a round-robin system to permanently assign documents to each server, so that each will have an equal number of documents. Do you have any concerns about this load balancing system? In those cases, how would you fix the scalability issue?

**Answer:**

### **Concerns with Round-Robin Document Assignment:**

**1. Uneven Load Distribution:**
- **Problem**: Documents have vastly different usage patterns
- **Example**: Popular documents (company wikis) vs personal notes
- **Result**: Some servers overloaded while others idle

**2. Collaborative Document Hotspots:**
- **Problem**: Multiple users on same document = high server load
- **Example**: 100 users editing same document on one server
- **Result**: Server becomes bottleneck, poor performance

**3. Memory and State Issues:**
- **Problem**: Active documents consume server memory
- **Result**: Servers with many active docs run out of memory

**4. No Load Balancing Intelligence:**
- **Problem**: Round-robin ignores actual server load
- **Result**: New documents assigned to already overloaded servers

### **Solutions:**

**1. Weighted Load Balancing:**
```
Load Balancer → Monitor server metrics → Assign based on:
- CPU utilization
- Memory usage  
- Active document count
- Network I/O
```

**2. Document-Based Sharding:**
```
Hash(document_id) % num_servers → Consistent assignment
```
- **Benefits**: Same document always goes to same server
- **Add**: Virtual nodes for better distribution

**3. Dynamic Load Monitoring:**
```
Real-time Metrics → Load Balancer → Smart Routing
```
- **Monitor**: Active users per document, server resources
- **Route**: New documents to least loaded servers
- **Migrate**: Move inactive documents during low usage

**4. Hybrid Approach (Recommended):**
```
Consistent Hashing + Load-based Routing + Document Migration
```

**Implementation:**
- **Primary**: Use consistent hashing for document assignment
- **Override**: Route to different server if primary is overloaded
- **Background**: Migrate documents during off-peak hours
- **Caching**: Use Redis for document metadata and user sessions

---

3.) Which consistency model is more appropriate for each of these applications: strong or eventual consistency? Why?

**Answer:**

### **Video Stream Metadata API (20ms response time)**
**Recommendation: Eventual Consistency**

**Reasoning:**
- **Performance Critical**: 20ms SLA requires low latency
- **Acceptable Staleness**: Slightly outdated view counts/ratings acceptable
- **High Read Volume**: Video metadata read frequently
- **Global Distribution**: CDN/edge caches needed for speed

**Implementation:**
- **Read Replicas**: Serve from nearest replica
- **Cache Heavily**: Redis/CDN for metadata
- **Async Updates**: Update view counts asynchronously

---

### **Web Analytics Platform (Click Recording)**
**Recommendation: Eventual Consistency**

**Reasoning:**
- **High Write Volume**: Millions of clicks per second
- **Batch Processing**: Analytics typically processed in batches
- **Acceptable Delays**: Real-time analytics not critical
- **Cost Efficiency**: Strong consistency too expensive at scale

**Implementation:**
- **Message Queues**: Kafka for click streams
- **Batch Processing**: Spark/Flink for aggregation
- **Data Lake**: Store raw clicks, process later

---

### **Banking System (Deposits/Payments)**
**Recommendation: Strong Consistency**

**Reasoning:**
- **Financial Accuracy**: Account balances must be exact
- **Regulatory Requirements**: Banking regulations mandate consistency
- **User Trust**: Incorrect balances destroy customer confidence
- **Audit Trail**: Transactions must be immediately consistent

**Implementation:**
- **ACID Transactions**: Database transactions for transfers
- **Two-Phase Commit**: For distributed transactions
- **Synchronous Replication**: Master-slave with sync writes
- **Compensating Transactions**: For rollbacks/corrections

**Trade-off**: Accept higher latency for correctness

---

4.) Another scenario was that there was a bug on an application, and you ended up having a lot of failed requests. You have a database that stores all IDs, and you also have large log files from about 500 different production servers that log the IDs of the successful requests. How would you come up with a solution to find the IDs that were missing?

**Answer:**

### **Approach 1: Distributed Processing (Recommended)**

**Step 1: Extract Successful IDs from Logs**
```bash
# Parallel processing across servers
for server in server1..server500; do
    ssh $server "grep 'SUCCESS' /var/log/app.log | awk '{print $id_field}' | sort -u" > success_ids_$server.txt &
done
wait
```

**Step 2: Merge and Deduplicate**
```bash
# Combine all successful IDs
cat success_ids_*.txt | sort -u > all_successful_ids.txt
```

**Step 3: Database Comparison**
```sql
-- Create temp table with successful IDs
CREATE TEMP TABLE successful_ids (id VARCHAR(255));
LOAD DATA INFILE 'all_successful_ids.txt' INTO TABLE successful_ids;

-- Find missing IDs
SELECT db.id FROM all_ids_table db
LEFT JOIN successful_ids s ON db.id = s.id  
WHERE s.id IS NULL;
```

### **Approach 2: Distributed Computing (For Large Scale)**

**Using Apache Spark:**
```python
# Read all log files
logs_df = spark.read.text("hdfs://logs/server*/app.log")

# Extract successful IDs
successful_ids = logs_df.filter(col("value").contains("SUCCESS")) \
                       .select(regexp_extract(col("value"), id_pattern, 1).alias("id")) \
                       .distinct()

# Read database IDs
db_ids = spark.read.jdbc(url, "all_ids_table")

# Find missing IDs
missing_ids = db_ids.join(successful_ids, "id", "left_anti")
```

### **Approach 3: Streaming Solution (Real-time)**

**For Future Prevention:**
```
Application → Kafka → Stream Processor → Missing ID Alerts
```

**Implementation:**
- **Kafka Topics**: `successful_requests`, `all_requests`
- **Stream Processing**: Compare streams in real-time
- **Alerting**: Immediate notification of missing IDs

### **Approach 4: Optimized File Processing**

**For 500 Servers:**
```bash
# Parallel processing with GNU parallel
parallel -j 50 "ssh {} 'grep SUCCESS /var/log/app.log | cut -d\" \" -f3'" ::: server{1..500} | sort -u > successful_ids.txt

# Use database tools for comparison
mysql -e "SELECT id FROM all_ids WHERE id NOT IN (SELECT id FROM temp_successful_ids)" > missing_ids.txt
```

### **Performance Considerations:**

**1. Memory Management:**
- Use external sorting for large files
- Stream processing to avoid loading everything in memory

**2. Network Optimization:**
- Compress log files before transfer
- Use rsync for efficient file copying

**3. Database Optimization:**
- Index the ID columns
- Use batch inserts for temp tables
- Consider partitioning for large tables

**4. Monitoring:**
- Track progress across all 500 servers
- Handle server failures gracefully
- Implement checkpointing for resumability


Problem Solving (DSA): (30 minutes, Medium level questions)

Given a board with numbers from 0 to lastNumber, startPosition, teleporters like. ("3,1", "5,10", "8,2") and maxValue of a die. Also, if after rollin the die, a number greater than lastNumber is reached then teleport to the lastNumber. Find all final positions which can be reached by rolling the die once (1 to maxValue). (Working solution of this one).
Modified first problem, given teleporters, lastNumber, maxValue of die and startPosition, is it possible to reach the lastNumber by rolling the die any number of times. (Only solution approach was required without code).