# Log-Structured Merge Tree (LSM Tree)

## Basics: Commit Log and SSTables

### 1. Commit Log
A Commit Log (also called write ahead log) is an append-only file that records all write operations before applying them to the database.

If a crash occurs before the actual update, the system can replay the commit log to recover.

#### Why Commit Log?
1. Ensures Durability (no data loss in case of failure) and auditing
2. Supports replay based recovery
3. Write heavy workload become sequential (faster than random writes)

### 2. SSTables (Sorted String Table) - Immutable On-Disk Storage
An SSTable is a read-optimized, Immutable file where key-value pairs are Stored in a Sorted order on Disk.


#### Properties:
- Keys are always Sorted (facilitating efficient key range queries)
- Immutable (reduces fragmentation, avoids random writes)
- Indexes and Bloom Filters (used to speed up lookups)

#### Why are SSTables Immutable?
Since SSTables never modify existing data, updates are written as new SSTables and old ones are periodically compacted (merged).

**Limitations:**
- ❌ Requires compaction to remove stale data
- ✅ Write operations are always sequential, which is fast on disk

## How does Cassandra SSTable handle delete operations?

Cassandra handles delete operations through a special mechanism called "tombstones":

### 1. Delete Operation Process
- Instead of removing data from SSTables (which isn't possible due to immutability)
- A special marker called a "tombstone" is written for the deleted key
- This tombstone is written as a new record in a new SSTable

### 2. Read Operation Behavior
- If a tombstone is found for a key, the data is considered deleted
- If no tombstone exists, the most recent value is returned
- For multiple values of the same key, the most recent one takes precedence

### 3. Data Cleanup
- Actual removal of deleted data happens during compaction
- Both the tombstone and the deleted data are removed during this process
- This is why compaction is essential for managing storage space

### 4. Benefits of this Design
- Maintains SSTable immutability
- Ensures consistency in distributed systems
- Handles concurrent operations effectively
- Supports distributed deletes across nodes

### 5. Trade-offs
- Deleted data occupies space until compaction
- Requires proper compaction strategy configuration
- May impact read performance if too many tombstones accumulate

MemTable: The In-Memory Write Buffer
A MemTable is an in-memory data structure that holds recent writes before those are flushed to SSTables.

🔹 Key Properties:

Stores recent key-value writes in memory.
Uses Red-Black Tree (or Skip List) for sorting keys.
Once Size is full, it is flushed to an SSTable.
🔹 Example:
Imagine a database receiving 100 writes per second. Instead of writing each write to disk, we:

Store them in MemTable (sorted).
Once full (~64MB), we flush it as a new SSTable.
✅ Advantage:

Keeps writes fast (since in-memory writes are fast).
Data remains sorted before flushing.
Data recovery
❌ Downside:

If the system crashes before flushing, recent writes may be lost .

Log-Structured Merge Trees (LSM Trees) :
An LSM Tree is a write-optimized data structure used in databases like Scylladb , Apache Cassandra, etc. It consists of:

-> In-memory MemTables (fast writes).
-> On-disk SSTables (persistent storage).
-> Compaction process (merging and organizing SSTables).

LSM Tree Workflow: How Data Flows

1. Write request arrives → Stored in MemTable (sorted in memory).
2. Commit Log (WAL) records the write → Ensures durability.
3. MemTable fills up (~64MB) → Flushed as Level-0 SSTable (on disk).
4. Multiple SSTables accumulate → Periodically merged into larger SSTables (Compaction).
5. Older SSTables are discarded after merging.

LSM WORKFLOW

Read Operations in LSM Trees
🔎 How does a read work?

Check MemTable (since recent writes are there).
If not found, check Level-0 SSTables (newest SSTables on disk).
If still not found, search deeper levels (merged SSTables).
Use Bloom Filters to quickly determine if a key is present in an SSTable.
🔹 Optimized Lookups:
✅ MemTable first (fastest, in-memory).
✅ Recent SSTables first (newer data is usually read more often).
✅ Bloom Filters avoid unnecessary disk reads.

Compaction — The Heart of LSM Trees
Since LSM Trees continuously generate new SSTables, older data must be merged & deleted periodically.

🔹 Compaction Process:

Merge overlapping SSTables (e.g., Level-0 → Level-1->Level-2).
Discard duplicate or deleted records.
Rebuild indexes and Bloom Filters.
✅ Advantages of Compaction:

Reduces disk space usage.
Optimizes read performance.
Keeps query latency low.

## Cassandra's Write Flow and Guarantees

### Write Flow Sequence
1. **Commit Log First**
   - Write request is FIRST written to the Commit Log (Write-Ahead Log)
   - Commit log is an append-only file on disk
   - Ensures durability through fsync to disk

2. **MemTable Second**
   - After commit log write is acknowledged, data is written to MemTable
   - MemTable is an in-memory structure (skip list or red-black tree)
   - Provides fast access to recently written data

3. **SSTable Creation**
   - When MemTable reaches size threshold (~64MB)
   - Flushed to disk as a new SSTable
   - Commit log is cleared after successful flush

### Atomicity and Durability Guarantees
- **Atomicity**: Each write is atomic - completely succeeds or completely fails
- **Durability**: 
  - Guaranteed through commit log first approach
  - Data survives system crashes
  - Commit log can be replayed to recover data
  - Fsync ensures data is actually written to disk

#### Understanding Fsync and Durability
When data is written to a file, it goes through several layers before reaching the physical disk:
1. **Application Buffer**: Data is first written to the application's memory buffer
2. **OS Buffer**: Then moves to the operating system's buffer/cache
3. **Disk Controller Cache**: May be stored in the disk controller's cache
4. **Physical Disk**: Finally reaches the actual disk storage

The `fsync` system call:
- Forces all buffered data to be written to the physical disk
- Blocks until the data is actually written to disk
- Ensures that the data is permanently stored and won't be lost in a crash
- Is called after writing to the commit log to guarantee durability

Without fsync, data might be lost if the system crashes before it reaches the physical disk, even though it appears to be "written" from the application's perspective.

### Recovery Process
- On system restart, commit log is replayed
- Reconstructs MemTable state
- Ensures no data loss even after crashes

## Cassandra LSM Tree vs ClickHouse MergeTree

### Cassandra LSM Tree
1. **Write Path**
   - Write to commit log first, then MemTable
   - MemTable flushed to SSTables when full
   - Multiple levels of SSTables (L0, L1, etc.)
   - Periodic compaction to merge SSTables

2. **Read Path**
   - Check MemTable first
   - Then check SSTables in order (newest to oldest)
   - Uses Bloom filters to avoid unnecessary disk reads
   - May need to check multiple SSTables for a single read

3. **Use Case**
   - Optimized for high write throughput
   - Good for time-series data
   - Excellent for write-heavy workloads
   - Strong consistency guarantees

### ClickHouse MergeTree
1. **Write Path**
   - Data is first collected in memory until a part size threshold is reached (default 1024 rows)
   - When threshold is reached, data is written to disk as a new part
   - Each part is sorted by primary key
   - Parts are immutable once written
   - Background merges to combine parts

2. **Read Path**
   - Parts are always sorted by primary key
   - Efficient range queries due to sorting
   - Can skip irrelevant parts using primary key
   - Better for analytical queries

3. **Use Case**
   - Optimized for analytical queries
   - Excellent for OLAP workloads
   - Better for read-heavy analytical workloads
   - Column-oriented storage

### Key Differences
1. **Storage Model**
   - Cassandra: Hybrid storage model
     - Column-family (column-oriented) at the table level
     - Row-oriented within each column family
     - Allows for sparse columns and flexible schemas
   - ClickHouse: Pure column-oriented storage
     - Optimized for column-based operations
     - Better compression ratios
     - Efficient for analytical queries

2. **Memory Usage**
   - Cassandra: Uses MemTable for recent writes (typically 64MB)
   - ClickHouse: Uses memory buffer until part size threshold (default 1024 rows)

3. **Write Batching**
   - Cassandra: Flushes when MemTable reaches size limit
   - ClickHouse: Flushes when row count reaches threshold

4. **Query Performance**
   - Cassandra: Better for point lookups and small range queries
   - ClickHouse: Better for large range queries and aggregations

5. **Consistency Model**
   - Cassandra: Eventually consistent by default
   - ClickHouse: Strong consistency

6. **Compaction Strategy**
   - Cassandra: Leveled or Size-tiered compaction
   - ClickHouse: Always merges parts in order of primary key

7. **Use Case Focus**
   - Cassandra: General-purpose NoSQL database
   - ClickHouse: Specialized for analytical queries
