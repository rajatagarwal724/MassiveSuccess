# Apache Spark - Comprehensive Interview Preparation Guide

## Table of Contents
1. [Introduction to Apache Spark](#introduction-to-apache-spark)
2. [Core Concepts](#core-concepts)
3. [Architecture](#architecture)
4. [RDD (Resilient Distributed Datasets)](#rdd-resilient-distributed-datasets)
5. [DataFrames and Datasets](#dataframes-and-datasets)
6. [Spark SQL](#spark-sql)
7. [Spark Streaming](#spark-streaming)
8. [MLlib (Machine Learning)](#mllib-machine-learning)
9. [GraphX](#graphx)
10. [Performance Optimization](#performance-optimization)
11. [Processing Large Files (100GB+)](#processing-large-files-100gb)
12. [Memory Management](#memory-management)
13. [Troubleshooting Common Issues](#troubleshooting-common-issues)
14. [Interview Questions](#interview-questions)
15. [Best Practices](#best-practices)

---

## Introduction to Apache Spark

Apache Spark is an open-source, distributed computing system designed for fast processing of large datasets across clusters of computers. It provides high-level APIs in Java, Scala, Python, and R.

### Key Features:
- **Speed**: Up to 100x faster than Hadoop MapReduce
- **Ease of Use**: Simple APIs in multiple languages
- **Generality**: Combines SQL, streaming, and complex analytics
- **Runs Everywhere**: Hadoop, Apache Mesos, Kubernetes, standalone, or cloud

### Use Cases:
- ETL (Extract, Transform, Load)
- Real-time analytics
- Machine learning
- Graph processing
- Interactive analytics

---

## Core Concepts

### 1. Driver Program
- Main program that creates SparkContext
- Coordinates the execution of tasks
- Maintains information about the Spark application

### 2. Cluster Manager
- External service for acquiring resources (YARN, Mesos, Kubernetes)
- Allocates resources across applications

### 3. Executors
- Worker processes that run tasks
- Store data for caching
- Report results back to driver

### 4. Tasks
- Units of work sent to executors
- Operate on data partitions

---

## Architecture

```
Driver Program
├── SparkContext
├── DAG Scheduler
├── Task Scheduler
└── Cluster Manager
    ├── Worker Node 1
    │   ├── Executor
    │   ├── Cache
    │   └── Tasks
    ├── Worker Node 2
    │   ├── Executor
    │   ├── Cache
    │   └── Tasks
    └── Worker Node N
        ├── Executor
        ├── Cache
        └── Tasks
```

### Execution Flow:
1. Driver creates SparkContext
2. SparkContext connects to cluster manager
3. Cluster manager allocates resources
4. Driver sends tasks to executors
5. Executors run tasks and return results

---

## RDD (Resilient Distributed Datasets)

RDDs are the fundamental data structure of Spark - immutable, distributed collections of objects.

### Key Properties:
- **Immutable**: Cannot be changed after creation
- **Distributed**: Partitioned across cluster nodes
- **Fault-tolerant**: Can be rebuilt using lineage
- **Lazy Evaluation**: Computed only when actions are called

### Creating RDDs:
```python
# From collections
rdd = spark.sparkContext.parallelize([1, 2, 3, 4, 5])

# From files
rdd = spark.sparkContext.textFile("hdfs://path/to/file")

# From other RDDs
filtered_rdd = rdd.filter(lambda x: x > 2)
```

### Transformations (Lazy):
```python
# map - applies function to each element
mapped_rdd = rdd.map(lambda x: x * 2)

# filter - returns elements matching predicate
filtered_rdd = rdd.filter(lambda x: x > 10)

# flatMap - maps each element to multiple elements
flat_rdd = rdd.flatMap(lambda x: x.split(" "))

# union - combines two RDDs
union_rdd = rdd1.union(rdd2)

# join - joins two pair RDDs
joined_rdd = rdd1.join(rdd2)
```

### Actions (Eager):
```python
# collect - returns all elements to driver
result = rdd.collect()

# count - returns number of elements
count = rdd.count()

# first - returns first element
first = rdd.first()

# take - returns first n elements
sample = rdd.take(10)

# reduce - aggregates elements
sum_result = rdd.reduce(lambda a, b: a + b)
```

---

## DataFrames and Datasets

DataFrames are distributed collections of data organized into named columns, similar to tables in relational databases.

### Creating DataFrames:
```python
from pyspark.sql import SparkSession

spark = SparkSession.builder.appName("Example").getOrCreate()

# From RDD
df = spark.createDataFrame(rdd, schema)

# From files
df = spark.read.json("path/to/file.json")
df = spark.read.csv("path/to/file.csv", header=True, inferSchema=True)
df = spark.read.parquet("path/to/file.parquet")

# From database
df = spark.read.format("jdbc") \
    .option("url", "jdbc:postgresql://localhost/test") \
    .option("dbtable", "schema.tablename") \
    .option("user", "username") \
    .option("password", "password") \
    .load()
```

### DataFrame Operations:
```python
# Select columns
df.select("name", "age").show()

# Filter rows
df.filter(df.age > 21).show()

# Group by
df.groupBy("department").count().show()

# Join
df1.join(df2, df1.id == df2.id, "inner").show()

# Aggregations
from pyspark.sql.functions import avg, max, min
df.agg(avg("age"), max("salary"), min("age")).show()
```

---

## Spark SQL

Spark SQL allows you to execute SQL queries on DataFrames and Datasets.

### Usage:
```python
# Register DataFrame as temporary view
df.createOrReplaceTempView("employees")

# Execute SQL queries
result = spark.sql("""
    SELECT department, AVG(salary) as avg_salary
    FROM employees
    WHERE age > 25
    GROUP BY department
    ORDER BY avg_salary DESC
""")

result.show()
```

### Catalog API:
```python
# List databases
spark.catalog.listDatabases()

# List tables
spark.catalog.listTables()

# Cache table
spark.catalog.cacheTable("employees")
```

---

## Spark Streaming

Spark Streaming enables scalable, high-throughput, fault-tolerant stream processing.

### Structured Streaming:
```python
from pyspark.sql.functions import explode, split

# Read from Kafka
df = spark \
    .readStream \
    .format("kafka") \
    .option("kafka.bootstrap.servers", "localhost:9092") \
    .option("subscribe", "topic") \
    .load()

# Process stream
words = df.select(
    explode(split(df.value.cast("string"), " ")).alias("word")
)

wordCounts = words.groupBy("word").count()

# Write to console
query = wordCounts \
    .writeStream \
    .outputMode("complete") \
    .format("console") \
    .start()

query.awaitTermination()
```

### Window Operations:
```python
from pyspark.sql.functions import window

windowed_counts = df \
    .groupBy(
        window(df.timestamp, "10 minutes", "5 minutes"),
        df.word
    ) \
    .count()
```

---

## MLlib (Machine Learning)

Spark's machine learning library provides algorithms and utilities.

### Example - Linear Regression:
```python
from pyspark.ml.regression import LinearRegression
from pyspark.ml.feature import VectorAssembler

# Prepare features
assembler = VectorAssembler(
    inputCols=["feature1", "feature2"],
    outputCol="features"
)
train_data = assembler.transform(df)

# Train model
lr = LinearRegression(featuresCol="features", labelCol="label")
model = lr.fit(train_data)

# Make predictions
predictions = model.transform(test_data)
```

### Pipeline Example:
```python
from pyspark.ml import Pipeline
from pyspark.ml.feature import StringIndexer, VectorAssembler
from pyspark.ml.classification import RandomForestClassifier

# Define stages
indexer = StringIndexer(inputCol="category", outputCol="categoryIndex")
assembler = VectorAssembler(inputCols=["feature1", "feature2"], outputCol="features")
rf = RandomForestClassifier(featuresCol="features", labelCol="label")

# Create pipeline
pipeline = Pipeline(stages=[indexer, assembler, rf])

# Train model
model = pipeline.fit(train_data)
```

---

## GraphX

GraphX is Spark's API for graph and graph-parallel computation.

### Creating Graphs:
```python
from pyspark.sql.functions import col

# Create vertices and edges DataFrames
vertices = spark.createDataFrame([
    ("1", "Alice", 28),
    ("2", "Bob", 27)
], ["id", "name", "age"])

edges = spark.createDataFrame([
    ("1", "2", "friend")
], ["src", "dst", "relationship"])

# Create graph
from graphframes import GraphFrame
graph = GraphFrame(vertices, edges)
```

---

## Processing Large Files (100GB+)

### 1. File Format Selection:
```python
# Parquet - Best for analytics (columnar format)
df.write.mode("overwrite").parquet("path/to/parquet")
df = spark.read.parquet("path/to/parquet")

# Delta Lake - ACID transactions
df.write.format("delta").mode("overwrite").save("path/to/delta")
df = spark.read.format("delta").load("path/to/delta")
```

### 2. Partitioning Strategy:
```python
# Partition by column for better query performance
df.write.partitionBy("year", "month").parquet("path/to/partitioned")

# Coalesce to reduce small files
df.coalesce(100).write.parquet("path/to/output")

# Repartition for even distribution
df.repartition(200, "column").write.parquet("path/to/output")
```

### 3. Reading Large Files:
```python
# Read with schema to avoid inference
from pyspark.sql.types import StructType, StructField, StringType, IntegerType

schema = StructType([
    StructField("id", IntegerType(), True),
    StructField("name", StringType(), True),
    StructField("value", StringType(), True)
])

df = spark.read.schema(schema).csv("path/to/large/file.csv")
```

### 4. Processing Strategies:
```python
# Incremental processing
def process_partition(partition_path):
    df = spark.read.parquet(partition_path)
    # Process data
    processed_df = df.groupBy("category").agg(sum("amount"))
    return processed_df

# Process partitions separately
partitions = ["2023/01", "2023/02", "2023/03"]
results = []
for partition in partitions:
    result = process_partition(f"data/{partition}")
    results.append(result)

# Union results
final_result = results[0]
for result in results[1:]:
    final_result = final_result.union(result)
```

### 5. Memory-Efficient Processing:
```python
# Use persist with appropriate storage level
from pyspark import StorageLevel

df.persist(StorageLevel.MEMORY_AND_DISK_SER)

# Process in chunks
def process_in_chunks(df, chunk_size=1000000):
    df.createOrReplaceTempView("temp_table")
    
    total_count = df.count()
    num_chunks = (total_count // chunk_size) + 1
    
    for i in range(num_chunks):
        offset = i * chunk_size
        chunk_df = spark.sql(f"""
            SELECT * FROM temp_table
            LIMIT {chunk_size} OFFSET {offset}
        """)
        
        # Process chunk
        yield process_chunk(chunk_df)
```

### 6. Optimized Configurations for Large Files:
```python
spark.conf.set("spark.sql.adaptive.enabled", "true")
spark.conf.set("spark.sql.adaptive.coalescePartitions.enabled", "true")
spark.conf.set("spark.sql.adaptive.skewJoin.enabled", "true")
spark.conf.set("spark.sql.files.maxPartitionBytes", "134217728")  # 128MB
spark.conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
```

---

## Memory Management

### Memory Structure:
- **Reserved Memory**: 300MB for system
- **User Memory**: For user data structures (40% by default)
- **Spark Memory**: For caching and execution (60% by default)
  - Storage Memory: For caching RDDs/DataFrames
  - Execution Memory: For shuffles, joins, aggregations

### Configuration:
```python
# Memory fractions
spark.conf.set("spark.memory.fraction", "0.8")  # Spark memory vs user memory
spark.conf.set("spark.memory.storageFraction", "0.5")  # Storage vs execution

# Executor memory
spark.conf.set("spark.executor.memory", "4g")
spark.conf.set("spark.executor.memoryOffHeap.enabled", "true")
spark.conf.set("spark.executor.memoryOffHeap.size", "2g")
```

### Caching Strategies:
```python
# Cache levels
MEMORY_ONLY          # Fast, memory only
MEMORY_AND_DISK      # Spill to disk when memory full
MEMORY_ONLY_SER      # Serialized in memory
DISK_ONLY           # Only on disk
MEMORY_AND_DISK_2   # Replicated twice

# Usage
df.cache()  # Default: MEMORY_AND_DISK
df.persist(StorageLevel.MEMORY_ONLY)
df.unpersist()  # Remove from cache
```

---

## Performance Optimization

### 1. Data Skew Handling:
```python
# Detect skew
df.groupBy("key").count().orderBy(col("count").desc()).show()

# Salting technique
from pyspark.sql.functions import rand, concat, lit

salted_df = df.withColumn("salted_key", concat(col("key"), lit("_"), (rand() * 100).cast("int")))
```

### 2. Join Optimizations:
```python
# Broadcast join for small tables
from pyspark.sql.functions import broadcast
result = large_df.join(broadcast(small_df), "key")

# Bucket join
df1.write.bucketBy(10, "key").saveAsTable("bucketed_table1")
df2.write.bucketBy(10, "key").saveAsTable("bucketed_table2")
```

### 3. Predicate Pushdown:
```python
# Filter early
df = spark.read.parquet("data")
filtered_df = df.filter(col("date") >= "2023-01-01")  # Pushed to storage
```

### 4. Column Pruning:
```python
# Select only needed columns
df.select("id", "name", "amount").show()  # Only these columns read
```

---

## Troubleshooting Common Issues

### 1. OutOfMemory Errors:
```python
# Solutions:
# - Increase executor memory
# - Reduce partition size
# - Use serialized storage
# - Tune garbage collection

spark.conf.set("spark.executor.memory", "8g")
spark.conf.set("spark.sql.files.maxPartitionBytes", "67108864")  # 64MB
```

### 2. Data Skew:
```python
# Identify skewed partitions
def analyze_partitions(df):
    return df.mapPartitionsWithIndex(
        lambda idx, partition: [(idx, sum(1 for _ in partition))]
    ).collect()

# Repartition by multiple columns
df.repartition("col1", "col2")
```

### 3. Shuffle Performance:
```python
# Optimize shuffle
spark.conf.set("spark.sql.shuffle.partitions", "400")
spark.conf.set("spark.sql.adaptive.shuffle.targetPostShuffleInputSize", "67108864")
```

---

## Interview Questions

### Basic Questions:
1. **What is Apache Spark and how does it differ from Hadoop MapReduce?**
   - Spark: In-memory processing, DAG execution, multi-language support
   - Hadoop: Disk-based, two-stage execution, Java-centric

2. **Explain RDD lineage and fault tolerance.**
   - RDD lineage tracks transformations to rebuild lost partitions
   - Fault tolerance through recomputation using lineage graph

3. **What are transformations vs actions in Spark?**
   - Transformations: Lazy, create new RDDs (map, filter, join)
   - Actions: Eager, trigger computation (collect, count, save)

### Intermediate Questions:
4. **How does Spark handle memory management?**
   - Unified memory manager with storage and execution regions
   - Dynamic borrowing between regions based on demand

5. **Explain Spark's catalyst optimizer.**
   - Rule-based optimization engine for Spark SQL
   - Performs predicate pushdown, column pruning, constant folding

6. **What is data skew and how do you handle it?**
   - Uneven distribution of data across partitions
   - Solutions: Salting, broadcasting, repartitioning

### Advanced Questions:
7. **How would you process a 1TB dataset efficiently?**
   - Partition data appropriately
   - Use columnar formats (Parquet)
   - Enable adaptive query execution
   - Optimize cluster resources

8. **Explain different join strategies in Spark.**
   - Broadcast Hash Join: Small table broadcast
   - Sort Merge Join: Large tables sorted and merged
   - Shuffle Hash Join: Hash-based with shuffle

9. **How do you optimize Spark applications?**
   - Data format optimization
   - Proper partitioning
   - Caching strategy
   - Resource tuning
   - Code optimization

---

## Best Practices

### 1. Development Best Practices:
```python
# Use DataFrame API over RDD when possible
# DataFrames benefit from Catalyst optimizer

# Avoid collect() on large datasets
# Use take() or limit() instead

# Cache wisely
# Cache only when data is accessed multiple times

# Use appropriate file formats
# Parquet for analytics, Avro for streaming
```

### 2. Performance Best Practices:
```python
# Partition pruning
df.filter(col("partition_col") == "value")

# Column pruning
df.select("needed_col1", "needed_col2")

# Predicate pushdown
df.where("condition").select("columns")
```

### 3. Resource Management:
```python
# Right-size your cluster
# Balance between cost and performance

# Dynamic allocation
spark.conf.set("spark.dynamicAllocation.enabled", "true")
spark.conf.set("spark.dynamicAllocation.minExecutors", "1")
spark.conf.set("spark.dynamicAllocation.maxExecutors", "20")
```

### 4. Monitoring and Debugging:
```python
# Use Spark UI for monitoring
# Monitor stages, tasks, and storage

# Enable history server
spark.conf.set("spark.eventLog.enabled", "true")
spark.conf.set("spark.eventLog.dir", "hdfs://path/to/logs")

# Use explain() for query plans
df.explain(True)
```

---

## Advanced Configuration for 100GB+ Files

### 1. Cluster Configuration:
```bash
# Driver configuration
--driver-memory 8g
--driver-cores 4
--driver-max-result-size 4g

# Executor configuration
--executor-memory 16g
--executor-cores 5
--num-executors 20

# Network timeout for large transfers
--conf spark.network.timeout=800s
--conf spark.sql.broadcastTimeout=1200
```

### 2. Adaptive Query Execution:
```python
# Enable AQE for large datasets
spark.conf.set("spark.sql.adaptive.enabled", "true")
spark.conf.set("spark.sql.adaptive.coalescePartitions.enabled", "true")
spark.conf.set("spark.sql.adaptive.advisoryPartitionSizeInBytes", "256MB")
spark.conf.set("spark.sql.adaptive.skewJoin.enabled", "true")
```

### 3. Storage Optimizations:
```python
# Delta Lake for large datasets
from delta.tables import DeltaTable

# Write with optimization
df.write \
  .format("delta") \
  .option("delta.autoOptimize.optimizeWrite", "true") \
  .option("delta.autoOptimize.autoCompact", "true") \
  .save("path/to/delta")

# Z-ordering for better query performance
from delta.tables import DeltaTable
deltaTable = DeltaTable.forPath(spark, "path/to/delta")
deltaTable.optimize().executeZOrderBy("frequently_queried_column")
```

This comprehensive guide covers all aspects of Apache Spark that are commonly asked in interviews, with special focus on handling large datasets. Practice these concepts and code examples to ace your interview!
