-- See which indexes are being used
SELECT 
    table_name,
    index_name,
    cardinality,
    sub_part,
    packed,
    nullable,
    index_type
FROM information_schema.statistics 
WHERE table_schema = 'your_database'
ORDER BY table_name, index_name;

-- Check for unused indexes
SELECT 
    s.table_name,
    s.index_name,
    s.cardinality
FROM information_schema.statistics s
LEFT JOIN performance_schema.table_io_waits_summary_by_index_usage i
    ON s.table_name = i.object_name 
    AND s.index_name = i.index_name
WHERE s.table_schema = 'your_database'
    AND i.count_star IS NULL;

# MySQL Index Types: Deep Dive

## 1. B-Tree Index (Default in MySQL)

### Characteristics
- **Default index type** for InnoDB and MyISAM
- **Balanced tree structure** with logarithmic search time
- **Supports range queries** and sorting operations
- **Self-balancing** - maintains height balance automatically

### Use Cases
```sql
-- Perfect for equality and range queries
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_orders_date ON orders(order_date);

-- Supports these queries efficiently:
SELECT * FROM users WHERE email = 'user@example.com';
SELECT * FROM orders WHERE order_date BETWEEN '2024-01-01' AND '2024-01-31';
SELECT * FROM users ORDER BY email;
```

### B-Tree Structure
```
        [M]
       /   \
    [G]     [T]
   /   \   /   \
[A]   [J] [P]   [Z]
```

### Performance Characteristics
- **Search**: O(log n) - logarithmic time complexity
- **Insert/Delete**: O(log n) - requires rebalancing
- **Range Queries**: Excellent - sequential access
- **Storage**: Moderate overhead (20-30% of table size)

## 2. Hash Index

### Characteristics
- **O(1) lookup time** for equality comparisons
- **No support for range queries** or ORDER BY
- **Fixed-size buckets** with potential collisions
- **Memory-based** in MySQL (MEMORY storage engine)

### Use Cases
```sql
-- Only for exact match queries
CREATE TABLE temp_data (
    id INT,
    data VARCHAR(100),
    INDEX USING HASH (id)
) ENGINE=MEMORY;

-- Efficient for:
SELECT * FROM temp_data WHERE id = 123;

-- NOT efficient for:
SELECT * FROM temp_data WHERE id > 100;  -- Range query
SELECT * FROM temp_data ORDER BY id;     -- Sorting
```

### Hash Index Structure
```
Hash Function: h(key) = key % 10

Bucket 0: [10, 20, 30]
Bucket 1: [1, 11, 21]
Bucket 2: [2, 12, 22]
...
Bucket 9: [9, 19, 29]
```

### Performance Characteristics
- **Search**: O(1) - constant time for exact matches
- **Insert/Delete**: O(1) - direct bucket access
- **Range Queries**: Poor - requires scanning all buckets
- **Storage**: Low overhead, but collision handling

## 3. Full-Text Index

### Characteristics
- **Specialized for text search** operations
- **Supports natural language** and boolean search modes
- **Built on B-Tree** with inverted index structure
- **Language-aware** stemming and stop words

### Use Cases
```sql
-- Text search optimization
CREATE FULLTEXT INDEX idx_articles_content ON articles(title, content);

-- Natural language search
SELECT * FROM articles 
WHERE MATCH(title, content) AGAINST('database optimization' IN NATURAL LANGUAGE MODE);

-- Boolean search
SELECT * FROM articles 
WHERE MATCH(title, content) AGAINST('+mysql -oracle' IN BOOLEAN MODE);
```

### Full-Text Index Structure
```
Term: "database"
Documents: [doc1, doc3, doc7, doc12]
Positions: [(1,5), (3,2), (7,1), (12,8)]

Term: "optimization"  
Documents: [doc1, doc5, doc9]
Positions: [(1,12), (5,3), (9,6)]
```

### Performance Characteristics
- **Search**: Fast for text queries
- **Insert/Update**: Slower due to index maintenance
- **Storage**: High overhead for large text fields
- **Relevance Ranking**: Built-in scoring algorithms

## 4. Spatial Index (R-Tree)

### Characteristics
- **Optimized for geometric data** (points, lines, polygons)
- **R-Tree structure** for spatial queries
- **Supports spatial operations** (intersection, containment, proximity)
- **Requires spatial data types** (GEOMETRY, POINT, etc.)

### Use Cases
```sql
-- Geographic data optimization
CREATE SPATIAL INDEX idx_locations_coords ON locations(coordinates);

-- Spatial queries
SELECT * FROM locations 
WHERE ST_Contains(polygon, coordinates);

SELECT * FROM locations 
WHERE ST_Distance_Sphere(point1, coordinates) < 1000;
```

### R-Tree Structure
```
        [MBR1, MBR2]
       /            \
[MBR1.1, MBR1.2]  [MBR2.1, MBR2.2]
```

### Performance Characteristics
- **Spatial Search**: Excellent for geographic queries
- **Range Queries**: Good within spatial bounds
- **Storage**: Moderate overhead
- **Complexity**: O(log n) for spatial operations

## 5. Clustered vs Non-Clustered Indexes

### Clustered Index (InnoDB Primary Key)
```sql
-- Automatically created on PRIMARY KEY
CREATE TABLE users (
    user_id INT PRIMARY KEY,  -- Clustered index
    email VARCHAR(255),
    name VARCHAR(100)
);

-- Data is physically ordered by user_id
-- Only ONE clustered index per table
```

### Non-Clustered Index (Secondary Indexes)
```sql
-- Points to clustered index, not data directly
CREATE INDEX idx_users_email ON users(email);

-- Structure: email -> user_id -> data row
-- Multiple non-clustered indexes allowed
```

### Performance Comparison
| Aspect | Clustered | Non-Clustered |
|--------|-----------|---------------|
| **Lookup Speed** | Fastest | Slightly slower |
| **Range Queries** | Excellent | Good |
| **Storage** | No extra storage | Additional storage |
| **Updates** | Expensive | Less expensive |

## 6. Index Type Selection Guidelines

### Choose B-Tree When:
- **General-purpose indexing** needed
- **Range queries** are common
- **Sorting operations** required
- **Most use cases** (default choice)

### Choose Hash When:
- **Exact match queries** only
- **Memory-based tables** (MEMORY engine)
- **Temporary data** with high lookup frequency
- **No range queries** needed

### Choose Full-Text When:
- **Text search** is primary requirement
- **Natural language queries** needed
- **Content management** systems
- **Search functionality** is critical

### Choose Spatial When:
- **Geographic data** involved
- **Location-based queries** needed
- **GIS applications**
- **Proximity searches** required

## 7. Index Type Performance Comparison

### Query Performance Matrix
```
Query Type          | B-Tree | Hash | Full-Text | Spatial
--------------------|--------|------|-----------|---------
Equality (=)        | Good   | Best | Poor      | Good
Range (<, >, BETWEEN)| Best  | Poor | Poor      | Good
ORDER BY            | Best   | Poor | Poor      | Poor
LIKE '%pattern%'    | Poor   | Poor | Best      | Poor
Text Search         | Poor   | Poor | Best      | Poor
Spatial Operations  | Poor   | Poor | Poor      | Best
```

### Storage Overhead
```
Index Type    | Storage Overhead | Maintenance Cost
--------------|------------------|------------------
B-Tree        | 20-30%          | Medium
Hash          | 10-15%          | Low
Full-Text     | 50-100%         | High
Spatial       | 30-50%          | Medium
```

## 8. Practical Examples

### E-commerce System
```sql
-- Product search with multiple index types
CREATE TABLE products (
    product_id INT PRIMARY KEY,                    -- Clustered B-Tree
    name VARCHAR(255),
    price DECIMAL(10,2),
    category_id INT,
    location POINT,                                -- Spatial data
    description TEXT,
    
    INDEX idx_price_category (price, category_id), -- B-Tree for filtering
    INDEX idx_name_hash USING HASH (name),         -- Hash for exact name lookup
    FULLTEXT INDEX idx_description (description),  -- Full-text for search
    SPATIAL INDEX idx_location (location)          -- Spatial for location queries
);
```

### Social Media System
```sql
-- User relationships with optimized indexes
CREATE TABLE follows (
    follow_id BIGINT PRIMARY KEY,                  -- Clustered B-Tree
    follower_id BIGINT,
    followee_id BIGINT,
    created_at TIMESTAMP,
    
    INDEX idx_follower (follower_id),              -- B-Tree for user's following
    INDEX idx_followee (followee_id),              -- B-Tree for user's followers
    INDEX idx_followee_date (followee_id, created_at) -- B-Tree for pagination
);
```

This comprehensive overview covers all major MySQL index types with their characteristics, use cases, and performance implications.

# MySQL Indexes: Complete Guide

## 1. Types of MySQL Indexes

### Primary Index (Clustered)
```sql
-- Automatically created with PRIMARY KEY
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,  -- Creates clustered index
    email VARCHAR(255) UNIQUE,
    name VARCHAR(100)
);
```

### Secondary Indexes (Non-Clustered)
```sql
-- Single column index
CREATE INDEX idx_users_email ON users(email);

-- Composite index (multi-column)
CREATE INDEX idx_users_name_email ON users(name, email);

-- Unique index
CREATE UNIQUE INDEX idx_users_email_unique ON users(email);
```

### Specialized Index Types

#### Full-Text Index
```sql
-- For text search capabilities
CREATE FULLTEXT INDEX idx_articles_content ON articles(title, content);

-- Usage
SELECT * FROM articles 
WHERE MATCH(title, content) AGAINST('search term' IN NATURAL LANGUAGE MODE);
```

#### Spatial Index
```sql
-- For geographic data
CREATE SPATIAL INDEX idx_locations_coords ON locations(coordinates);

-- Usage
SELECT * FROM locations 
WHERE ST_Contains(polygon, coordinates);
```

## 2. Index Design Principles

### Cardinality Matters
```sql
-- High cardinality (good for indexing)
CREATE INDEX idx_users_email ON users(email);  -- Many unique values

-- Low cardinality (consider carefully)
CREATE INDEX idx_users_gender ON users(gender);  -- Few unique values (M/F)
```

### Selectivity Guidelines
```sql
-- Good selectivity examples
CREATE INDEX idx_orders_status_date ON orders(status, order_date);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Poor selectivity examples
CREATE INDEX idx_orders_status ON orders(status);  -- Only a few status values
```

## 3. Composite Index Strategy

### Leftmost Prefix Rule
```sql
-- This index: (status, user_id, created_at)
CREATE INDEX idx_orders_composite ON orders(status, user_id, created_at);

-- These queries can use the index:
SELECT * FROM orders WHERE status = 'PENDING';
SELECT * FROM orders WHERE status = 'PENDING' AND user_id = 123;
SELECT * FROM orders WHERE status = 'PENDING' AND user_id = 123 AND created_at > '2024-01-01';

-- These queries CANNOT use the index effectively:
SELECT * FROM orders WHERE user_id = 123;  -- Missing status
SELECT * FROM orders WHERE created_at > '2024-01-01';  -- Missing status and user_id
```

### Column Order Strategy
```sql
-- For ticket booking system
CREATE INDEX idx_bookings_user_date ON bookings(user_id, booking_date);
CREATE INDEX idx_showtimes_movie_date ON showtimes(movie_id, start_datetime);
CREATE INDEX idx_tickets_booking_seat ON tickets(booking_id, seat_id);
```

## 4. Covering Indexes

### Include All Needed Columns
```sql
-- Instead of this query requiring table lookup:
SELECT user_id, name, email FROM users WHERE status = 'ACTIVE';

-- Create covering index:
CREATE INDEX idx_users_status_covering ON users(status, user_id, name, email);
```

### Benefits
- Eliminates table access
- Reduces I/O operations
- Improves query performance significantly

## 5. Index Optimization for Common Systems

### Digital Wallet System
```sql
-- Based on typical wallet schema
CREATE INDEX idx_transactions_wallet_date ON transactions(source_wallet_id, created_at);
CREATE INDEX idx_transactions_status_date ON transactions(transaction_status, created_at);
CREATE INDEX idx_ledger_entries_wallet_date ON ledger_entries(wallet_id, created_at);
CREATE INDEX idx_transaction_limits_user_type ON transaction_limits(user_id, transaction_type);
```

### URL Shortener System
```sql
-- Optimize for access patterns
CREATE INDEX idx_url_mappings_short_url ON url_mappings(short_url);
CREATE INDEX idx_url_mappings_user_created ON url_mappings(user_id, created_at);
CREATE INDEX idx_url_clicks_short_url_date ON url_clicks(short_url, clicked_at);
```

### Social Media Followers System
```sql
-- Handle the celebrity problem
CREATE INDEX idx_follows_follower ON follows(follower_id);
CREATE INDEX idx_follows_followee ON follows(followee_id);
CREATE INDEX idx_follows_followee_date ON follows(followee_id, created_at);  -- For pagination
```

## 6. Index Maintenance and Monitoring

### Analyze Query Performance
```sql
-- Use EXPLAIN to see index usage
EXPLAIN SELECT * FROM orders 
WHERE user_id = 123 AND status = 'PENDING' 
ORDER BY created_at DESC;

-- Look for:
-- - type: 'index' or 'range' (good)
-- - type: 'ALL' (table scan, bad)
-- - key: shows which index is used
-- - rows: estimated rows to examine
```

### Index Fragmentation
```sql
-- Check for fragmented indexes
SELECT 
    table_name,
    index_name,
    cardinality,
    sub_part
FROM information_schema.statistics 
WHERE table_schema = 'your_database'
    AND cardinality IS NOT NULL
ORDER BY cardinality DESC;

-- Rebuild fragmented indexes
OPTIMIZE TABLE table_name;
```

## 7. Common Index Anti-Patterns

### Over-Indexing
```sql
-- Don't create indexes on every column
CREATE INDEX idx_users_everything ON users(id, name, email, phone, created_at, updated_at);
-- This is usually counterproductive
```

### Indexing Low-Cardinality Columns
```sql
-- Avoid indexing columns with few unique values
CREATE INDEX idx_orders_status ON orders(status);  -- Only 5-10 status values
```

### Ignoring Query Patterns
```sql
-- Don't create indexes without understanding usage
CREATE INDEX idx_users_random ON users(updated_at);  -- If never queried by updated_at
```

## 8. Advanced Indexing Strategies

### Partial Indexes (MySQL 8.0+)
```sql
-- Index only active records
CREATE INDEX idx_active_orders ON orders(user_id, created_at) 
WHERE status = 'ACTIVE';
```

### Functional Indexes
```sql
-- Index on computed values
CREATE INDEX idx_users_email_lower ON users((LOWER(email)));
```

### Index Compression
```sql
-- For large indexes
CREATE INDEX idx_large_table_compressed ON large_table(column1, column2) 
ROW_FORMAT=COMPRESSED;
```

## 9. Performance Tuning Checklist

### Before Creating Indexes
1. **Analyze query patterns** - What queries are slow?
2. **Check cardinality** - How many unique values?
3. **Consider write load** - Indexes slow down INSERT/UPDATE/DELETE
4. **Monitor storage** - Indexes consume disk space

### After Creating Indexes
1. **Verify usage** - Check EXPLAIN output
2. **Monitor performance** - Measure query improvement
3. **Check maintenance** - Index rebuilds during maintenance windows
4. **Remove unused indexes** - Clean up unnecessary indexes

## 10. Index Best Practices for High-Volume Systems

### High-Volume Systems
```sql
-- Use covering indexes for frequently accessed data
CREATE INDEX idx_orders_covering ON orders(status, user_id) 
INCLUDE (order_date, total_amount);

-- Partition large tables
CREATE TABLE orders (
    order_id BIGINT PRIMARY KEY,
    user_id BIGINT,
    order_date DATE,
    status VARCHAR(20)
) PARTITION BY RANGE (YEAR(order_date));
```

### Real-Time Systems
```sql
-- Optimize for time-based queries
CREATE INDEX idx_events_timestamp ON events(timestamp);
CREATE INDEX idx_notifications_user_time ON notifications(user_id, created_at);
```

### E-commerce Systems
```sql
-- Product search optimization
CREATE INDEX idx_products_category_price ON products(category_id, price);
CREATE INDEX idx_products_search ON products(name, brand, category_id);
CREATE FULLTEXT INDEX idx_products_fulltext ON products(name, description);
```

## 11. Monitoring Index Performance

### Index Usage Statistics
```sql
-- Check index hit ratio
SELECT 
    table_schema,
    table_name,
    index_name,
    cardinality,
    sub_part,
    packed,
    nullable,
    index_type
FROM information_schema.statistics 
WHERE table_schema = 'your_database'
ORDER BY table_name, index_name;
```

### Slow Query Analysis
```sql
-- Enable slow query log
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;  -- Log queries taking > 2 seconds

-- Analyze slow queries
SELECT 
    sql_text,
    exec_count,
    avg_timer_wait/1000000000 as avg_time_seconds
FROM performance_schema.events_statements_summary_by_digest
WHERE avg_timer_wait > 2000000000  -- > 2 seconds
ORDER BY avg_timer_wait DESC;
```

## 12. Index Maintenance Commands

### Regular Maintenance
```sql
-- Analyze table statistics
ANALYZE TABLE table_name;

-- Optimize table (defragments indexes)
OPTIMIZE TABLE table_name;

-- Check table status
CHECK TABLE table_name;

-- Repair table if needed
REPAIR TABLE table_name;
```

### Index Rebuilding
```sql
-- Drop and recreate index
DROP INDEX index_name ON table_name;
CREATE INDEX index_name ON table_name(column1, column2);

-- Or use ALTER TABLE
ALTER TABLE table_name DROP INDEX index_name;
ALTER TABLE table_name ADD INDEX index_name (column1, column2);
```

This comprehensive guide covers all aspects of MySQL indexing from basic concepts to advanced optimization strategies for high-performance systems.