So I have given 5 rounds of interviews with LinkedIn for the position of
Senior Software Engineer.

Now just 1 round remains ie Technical Communication where in the focus would be explained below:-

Technical Communication (1 hour):
This module will focus on technical communication. This will be a deep dive into projects
you worked on, technologies used, and architectural decisions. Make sure you're able to
speak of an interesting/challenging project you worked on and be able to explain the
challenges faced, lessons learned, and technical details. They'll be looking for excitement or
interest in the projects you worked on because they want to work alongside engineers who
are passionate about what they do.
Start at a high level explaining the project (why was it needed, what was it used for, who
would use it, etc.), then explain what your team did, then what you were responsible for.
Make sure you have a good understanding of the decisions that were made and a holistic
understanding of the work. Think about how you would go about helping onboard a new
employee to your existing team and environment and how you would go about helping
them understand the ins and outs of your group and how it integrates with your engineering
organization, how do you work with product, and how does it all fit together? Think about
how you might do a quick whiteboard illustration around this to help them better understand
as well.

Now I want to focus on one of the project I recently did though I might not as of now 100 percent
correct about it but let's prepare

Current System
So there is 1 ETL Service which is a single node service which on a quartz schedule runs every 10-15 mins
and exports the data from MySQL Databases which has Metadata DB and Accounts DB (Multiple Shards, each shard has subsets of accounts to handle) and persist it to Aws S3 bucket, from there it is being picked up

by GoodData a 3rd Party BI provider then loads the data into their VerticaDB Datawarehouse after structuring the data and then for all our customers to visualize and download the reports


Mysql DBs ---->  ETL Job(Single Node Service) ----> Aws S3 Bucket -----> GoodData owned VerticaDB ----> GoodData Visualization

Now there were a lot of problems
1) This Single Node service can't cater to increasing number of customers and data, it's already running n 24GB Memory Limits and keeps giving OOM issues, there were lot issues maintaining this service as it's not testable, for writing the files to S3 we need to create a buffered writer in the service to first write a 
in memory file and then to S3 etc

2) The easiest solution could have been to scale this single node service to multiple service but then 
distributing the accounts to multiple services would have been another problem. Probably we could have
used Apache Helix for distributed Coordination to do that [We have to discuss on this, so that in case of any cross questioning from the Interviewer we could answer]

## Scaling the Service: A Deep Dive

Here, we'll explore how to scale the single-node ETL service to a multi-node architecture to handle the load distribution for the 300 accounts.

### 1. Using Apache Helix

Apache Helix is a generic cluster management framework used for the automatic management of partitioned, replicated, and distributed resources hosted on a cluster of nodes. It's a powerful choice for this use case, especially if you anticipate more complex coordination needs in the future.

#### How it Works for This Use Case:

Helix helps manage the assignment of "partitions" (in this case, accounts) to "participants" (your service nodes).

1.  **Setup:**
    *   **ZooKeeper:** Helix uses Apache ZooKeeper for coordination and maintaining cluster state. You'll need a running ZooKeeper ensemble.
    *   **Cluster Setup:** You define a cluster in ZooKeeper.
    *   **Resources:** You define a "resource," which is the entity you want to manage. Here, the resource could be named `AccountDataFetcher`.
    *   **Partitions:** This resource is divided into partitions. The most straightforward approach is to have one partition per account, so you'd have 300 partitions (e.g., `account_1`, `account_2`, ..., `account_300`).
    *   **Participants (Nodes):** Each instance of your service acts as a "participant" in the Helix cluster. When a service instance starts, it connects to ZooKeeper and joins the cluster.
    *   **State Model:** You define a state model. A common one is `OnlineOffline` or `MasterSlave`. For this use case, `OnlineOffline` is sufficient. It defines the states a partition can be in on a node (e.g., `ONLINE`, `OFFLINE`, `DROPPED`). A partition being `ONLINE` on a node means that node is responsible for it.

2.  **Distribution of Load:**
    *   Helix's **Controller** (a separate process, or embedded in a participant) observes the cluster state.
    *   When nodes join or leave the cluster, the Controller automatically rebalances the partitions across the available `ONLINE` nodes.
    *   Helix tries to distribute the partitions as evenly as possible. If you have 3 nodes and 300 accounts (partitions), each node will be assigned responsibility for approximately 100 accounts.
    *   Your service code on each node will implement a **State Model Listener**. This listener gets notified by Helix when it should take ownership of a partition (transition to `ONLINE`) or release it (transition to `OFFLINE`).

3.  **Application Logic:**
    *   When your service instance receives an `onBecomeOnlineFromOffline` callback for `account_123`, it knows it is now responsible for fetching data for account 123. It can then start its data fetching logic for that account.
    *   If that node fails, the Controller will detect it (via loss of the ZooKeeper session) and re-assign `account_123` to another healthy node in the cluster. That new node will get the `ONLINE` transition callback and take over the work.

#### Advantages of Apache Helix:
*   **Automatic Rebalancing:** Handles node addition/removal automatically. No manual intervention is needed to redistribute the load.
*   **Fault Tolerance:** Built-in failure detection and automatic failover are core features.
*   **Proven & Battle-Tested:** Used by large-scale systems like LinkedIn, Uber, and Pinterest.
*   **Extensible:** While simple for this use case, it can handle much more complex scenarios (e.g., leader-standby for partitions, different state models).

#### Disadvantages of Apache Helix:
*   **Operational Overhead:** Requires setting up and maintaining a ZooKeeper cluster, which adds complexity.
*   **Steep Learning Curve:** The concepts (State Models, Rebalancers, etc.) can be complex for a simple use case.
*   **"Heavy" for a Simple Problem:** For just distributing 300 static accounts, Helix might be overkill.

---

### 2. Alternative Solutions

For this specific problem, simpler solutions might be more appropriate.

#### a) Static Partitioning / Hashing

This is a simple, stateless approach.

*   **How it Works:**
    1.  Each node knows the total number of nodes (`N`) in the cluster.
    2.  When a task for an account comes in, the node uses a consistent hashing function on the account ID to determine which node is responsible. For example, `node_index = hash(account_id) % N`.
    3.  The node then either processes the request (if it's the responsible node) or forwards it to the correct node.
*   **Advantages:**
    *   **Extremely Simple:** No external coordination service (like ZooKeeper) is needed.
    *   **Stateless:** Nodes don't need to maintain complex state about partition ownership.
*   **Disadvantages:**
    *   **No Automatic Rebalancing:** If a node is added or removed, `N` changes, and many accounts will be re-mapped. This can cause a "thundering herd" problem on your database. Consistent hashing mitigates this, but it's still a significant shift.
    *   **No Failure Detection:** You need a separate mechanism (e.g., a load balancer with health checks) to detect and route around failed nodes.

> **Note on Kubernetes:** It is technically possible to implement this in Kubernetes. You would use a `StatefulSet` to give each pod a stable index (e.g., `pod-0`, `pod-1`) and use the Kubernetes API to allow pods to discover the total replica count. However, this approach is brittle. When you scale the `StatefulSet` up or down, the replica count changes, causing a massive and disruptive reshuffle of account ownership due to the `account_id % replica_count` logic. This highlights why this method is generally avoided in dynamic, auto-scaling environments.

### 3. In-Depth Look: AWS Step Functions

AWS Step Functions is a serverless workflow orchestration service that is a very strong, cloud-native alternative to Apache Helix for this problem.

#### How it Works for This Use Case:

You would define a state machine that orchestrates the entire Map-Reduce process:

1.  **Start State:** The workflow is initiated, perhaps on a schedule, with an input payload containing the list of 300 account IDs: `{"accounts": ["id_1", "id_2", ..., "id_300"]}`.

2.  **Map State (The "Map" Phase):**
    *   You use a `Map` state to iterate over the `$.accounts` array from the input.
    *   The `Map` state runs a sub-workflow for each account ID **in parallel** (up to a configurable concurrency limit).
    *   Each sub-workflow invokes a task to do the actual work. For a long-running data fetch, this would typically be an **AWS Fargate** or **ECS task**, but could also be a Lambda function if the task fits within its execution limits.
    *   This task fetches the data for its assigned account, creates a partial CSV file, and saves it to a unique location in an S3 bucket (e.g., `s3://my-bucket/processing/{execution_id}/{account_id}.csv`).

3.  **Aggregation State (The "Reduce" Phase):**
    *   The `Map` state automatically waits for all 300 of its parallel executions to complete successfully.
    *   Once finished, the workflow transitions to a final state. This state invokes a single, larger Fargate/ECS task.
    *   This "reduce" task lists all the partial CSV files in the S3 processing directory, downloads them, aggregates them into one final CSV file, and uploads it to the final destination.

4.  **End State:** The workflow finishes.

#### Visual Workflow Example:

```
[Start] -> [Run Parallel Fetch Jobs (Map State)] -> [Aggregate Results (Reduce Task)] -> [End]
```

#### Advantages of AWS Step Functions:
*   **Fully Managed:** No need to set up, manage, or scale ZooKeeper, Helix controllers, or the nodes themselves. This is a huge operational win.
*   **Visual Workflow:** You can visualize the state machine in the AWS console, making it easy to understand, debug, and monitor executions.
*   **Built-in Error Handling & Retries:** You can define sophisticated retry logic and catchers for different error types directly in the state machine definition.
*   **Seamless AWS Integration:** Natively integrates with dozens of AWS services (Lambda, Fargate, S3, etc.).

#### Disadvantages of AWS Step Functions:
*   **Vendor Lock-in:** The solution is tightly coupled to the AWS ecosystem.
*   **Cost:** While serverless, costs are based on the number of state transitions. For extremely high-frequency or complex workflows, this could become more expensive than a self-hosted solution (though for a job running every 15 minutes, it's likely very cost-effective).
*   **Configuration as Code:** Workflows are defined in Amazon States Language (a JSON-based format), which can be verbose.

---

#### b) Message Queue with Competing Consumers

This pattern uses a message queue (like RabbitMQ, Kafka, or AWS SQS) to distribute the work.

*   **How it Works:**
    1.  A separate "producer" process periodically enqueues tasks into a queue. Each task is a message containing an `account_id` to be processed.
    2.  Your service nodes act as "consumers." They all listen to the same queue.
    3.  The message queue ensures that each message is delivered to only one consumer (the competing consumer pattern).
    4.  When a node receives a message for `account_123`, it fetches the data.
*   **Advantages:**
    *   **Excellent Decoupling:** The producer of tasks is decoupled from the consumers (your service nodes).
    *   **Natural Load Balancing:** The queue naturally distributes tasks among available consumers. If a consumer is busy, the queue gives the next task to a free one.
    *   **Elasticity:** You can easily add or remove consumer nodes, and the system adapts automatically.
    *   **Buffering & Retries:** Queues provide a buffer if there's a spike in tasks and often have built-in retry mechanisms.
*   **Disadvantages:**
    *   **Operational Overhead:** You need to manage a message queue system (though managed services like SQS make this easier).
    *   **Potential for Duplicates:** Depending on the queue's delivery guarantees (e.g., "at-least-once"), your application logic might need to be idempotent to handle potential duplicate processing of the same account.
    *   **No Guaranteed Partitioning:** You don't have a guarantee that the same node will always process the same account, which might be a problem if you need to maintain some state/cache for that account on the node. For Kafka, you can use `account_id` as the partition key to ensure the same consumer processes messages for the same account.

---

### 3. Comparison and Recommendation

| Feature                 | Apache Helix                               | Static Hashing                             | Message Queue                              |
| ----------------------- | ------------------------------------------ | ------------------------------------------ | ------------------------------------------ |
| **Coordination**        | Centralized (ZooKeeper)                    | Decentralized (None)                       | Centralized (Broker)                       |
| **Rebalancing**         | Automatic & Smooth                         | Manual / Disruptive                        | Automatic & Elastic                        |
| **Fault Tolerance**     | Built-in, automatic failover               | None (requires external system)            | High (queue handles consumer failure)      |
| **Complexity**          | High                                       | Low                                        | Medium                                     |
| **Stateful Processing** | Excellent support                          | Difficult                                  | Possible with sticky partitioning (Kafka)  |
| **Best For**            | Complex distributed systems, stateful services, long-running tasks. | Simple, stateless services where node changes are infrequent. | Decoupled, event-driven, stateless tasks. |

#### Recommendation for Your Use Case (Updated for Long-Running Tasks & Aggregation)

The new requirements—that tasks are long-running and require a final aggregation step (creating a single CSV)—significantly favor a solution with built-in workflow orchestration capabilities. A simple message queue becomes less suitable.

**The problem now looks like a classic Map-Reduce workflow:**
1.  **Map Phase:** Process each of the 300 accounts in parallel on multiple nodes.
2.  **Reduce Phase:** After all accounts are processed, aggregate the results into a single file.

Here are the updated recommendations:

*   **Top Recommendation: Apache Helix**
    Helix is extremely well-suited for this. You can use its **Task Framework** to define a workflow.
    *   The workflow would have one "map" job with 300 tasks (one per account).
    *   Helix distributes these tasks across the nodes and manages their lifecycle, which is ideal for long-running processes.
    *   You can define a final "reduce" job that is configured to run only after the map job (all 300 tasks) is complete.
    *   This provides a single, cohesive framework for distribution, fault-tolerance, and orchestration of the entire process.

*   **Strong Cloud-Native Alternative: AWS Step Functions**
    If you prefer a managed cloud service over hosting ZooKeeper/Helix, AWS Step Functions is an excellent choice.
    *   You can define a `Map` state in your Step Function workflow that iterates through the 300 account IDs.
    *   For each account, it can invoke a long-running task (e.g., on AWS Fargate or ECS).
    *   The Step Function will naturally wait for all 300 parallel tasks to complete.
    *   Once they are done, it automatically transitions to a final "reduce" state, which invokes a single task to create the final CSV from the partial results stored in S3.

*   **Revised View on Message Queues:**
    Using a simple message queue is now more complex. You would need to build the orchestration logic yourself: manage long-running task timeouts, implement a mechanism (like a distributed counter in Redis/DynamoDB) to track the completion of all 300 tasks, and have a separate system to trigger the final aggregation job. This adds significant complexity that Helix or Step Functions handle out-of-the-box.

**Final Verdict for the Interview:** For this specific, stateful, multi-stage problem, **Apache Helix** is the technically robust answer that directly addresses the challenges of distributed coordination. Mentioning **AWS Step Functions** as a modern, managed alternative would also demonstrate strong practical knowledge.

3) As you know Analytics/Reporting is the most downstream service, so in case of issues with the upstream
data we have to do backfills from time to time and doing that with the existing export service required 
constant monitoring of resources and SRE support but that's our company's issue.
For we had a shadow instance using which we used to do Backfill.

4) We didn't own our data that means all our operational databases archive the data prior to 4 months or it depends on the data retention policies with the customer.

5) 3rd Party company GoodData wanted us to move on from this solution to GD Cloud/GD UI where in the data
would be owned by us and the UI/Visualization party would be theirs

Now what is what we wanted eventually:-
1) First to handle the growing scale of sending the data to GoodData S3 bucket
2) Ease of doing Backfills
3) Data Ownership: Own our data both for reporting and internal analytics
4) Compliance: CCPA/GDPR compliant data masking so that we could retain the data forever for our
internal analytics purpose, campaign performance, agent performance, etc.
5) Move to GD UI from legacy GD, which means to handle all the requests from the GD UI and also handle
the requests for Customer reports
6) Real Time Analytics - to have a separate visualization for the Customer's Supervisor who can have
a look at the agents activity working for them as it is a contact center product
7) For Future purpose in case if we get the CDC approvals from the higher ups to move from
Pull based model to Push based. So we could get rid of data export Spark Job from MySQL to Iceberg to getting the data from Kafka events to Clickhouse as I think with Iceberg it will create small file problems.


here is update diagram

1st Pipeline to support Legacy Good Data

Mysql DBs ---> Glue Spark Job ---> Iceberg S3 Base Tables ---> Glue Spark Job     ----> GoodData S3 Bucket
                                                                export data for
                                                                Legacy GoodData


2nd Pipeline to structure the data as going forward we will store the data in our Data Lake and Warehouses


Iceberg S3 Base Tables ---> Glue Spark Job to create Structured Data ----> Iceberg S3 Tables 
                                                                            for Structured Data


3rd Pipeline         

Iceberg S3 Tables ---->   Glue Spark Job to            -------->  Iceberg S3 Tables Masked Data 
Structured Data           Mask the Data for Internal
                          Analytics



4rth Pipeline to enable GD UI

Iceberg S3 Tables ---> Glue Spark Job to put data to Clickhouse(90 Days) -----> Grpc Streamer ----> GD UI
Structured Data


5th Real Time Streaming

Kafka ---> Flink ----> ClickhouseDB ----> Grpc Streamer ---> Our Own UI Page


6th Download Reports for Customers
Till now all the download reports requests were handled by GoodData

Airflow Jobs to schedule Reports Delivery
On Trigger call Glue API to trigger a Spark Job 
Glue Job first checks if it can access the client's resources which is being provided by Airflow job as parameter
which will query Iceberg Structured Data Tables
to get the required data to a S3 Bucket then based on customers preference send it to
either of these 3
1) S3 bucket customer's
1) SFTP bucket customer's
1) FTP bucket customer's



7th If we get the required approvals for CDC

Kafka --> Kafka Consumer ---> Clickhouse Base Tables ---> Spark Job ---> Iceberg Tables
                                                                         with Structured
                                                                         Data
                         ---> Clickhouse Tables
                             with Structured Data
                             for GD UI     


More improvements to add support for Slowly CHanging Dimensions 

So this is the whole idea I have not implemented this end to end but need to pitch myself as the architect and developer of this solution so we need to prepare Meticulously. So Let's discuss more in the details and prepare the required points  


LEGACY PIPELINE
---------------
[MySQL DBs]
     |
     v
[Glue Spark Job: Export]
     |
     v
[Iceberg S3 Base Tables]
     |
     v
[Glue Spark Job: Export for Legacy GD]
     |
     v
[GoodData S3 Bucket]
     |
     v
[GoodData VerticaDB]
     |
     v
[GoodData Visualization]


MODERN PIPELINE
---------------
[Iceberg S3 Base Tables]
     |
     v
[Glue Spark Job: Structure Data]
     |
     v
[Iceberg S3 Structured Tables]
     |
     v
[Glue Spark Job: Mask Data]
     |
     v
[Iceberg S3 Masked Tables]


GD UI PIPELINE
--------------
[Iceberg S3 Structured Tables]
     |
     v
[Glue Spark Job: To Clickhouse (90 days)]
     |
     v
[ClickhouseDB]
     |
     v
[gRPC Streamer]
     |
     v
[GD UI]


REAL-TIME PIPELINE
------------------
[Kafka]
     |
     v
[Flink]
     |
     v
[ClickhouseDB]
     |
     v
[gRPC Streamer]
     |
     v
[Internal UI]


REPORTS PIPELINE
----------------
[Airflow Jobs]
     |
     v
[Glue API]
     |
     v
[Glue Spark Job: Query Structured Data]
     |
     v
[S3 Bucket]
     |
     v
[SFTP/FTP/Customer S3]


FUTURE CDC PIPELINE
-------------------
[Kafka]
     |
     v
[Kafka Consumer]
     |
     v
[Clickhouse Base Tables]
     |
     v
[Spark Job]
     |
     v
[Iceberg Structured Tables]
     |
     v
[Clickhouse Structured Tables]
     |
     v
[GD UI]

1. Let's also consider talking about Glue Spot Instances for Report Delivery
2. How could a Single Node Service be scaled in case we had to
3. Also how to schedule the different jobs using Airflow as an orchestrator
4. For Report delivery, what would be a better approach whether to schedule 2 Jobs
    First to create the report 
    Second then to send it to the customer in case if the report generation takes time
    OR
    Do both in the same Job ?




Status Quo
Data Source ---> Data Lake ---> Data Warehouse
Problems
1. Data Warehouse is Expensive
2. Proprietary Components (Vendor Lock In, Tool Lock Out)
3. Data Drifts additional copies of Data, Data Drift from marts/cubes/extracts

Data Lakehouse Architecture
Data Source ---> Data Lake (Object Storage) ---> Open Table Format ----> Open Engines (Spark, Dremio)
                                                Apache Iceberg

Benefits
1) Data Lakes are inexpensive
2) Apache Iceberg enables Data Warehouse Performance and features on the data lake.
3) Open formats let you use the tools of today 

Components of Data Lakehouse

1) Lakehouse Engines - Spark, Athena, Presto. That supports running reading and writing to Iceberg at scale
2) Catalog - To Track Iceberg Tables. Project Nessie, Aws Glue or Hive
3) Table Format - Organize the data where it lives into tables using Apache Iceberg.
4) File Format - Store in Apache Parquet Files (Open Columnar Format)
5) Storage Layer - Use Cloud Object Storage


Iceberg 
Cleaning Up Metadata Files
write.metadata.delete-after-commit.enabled: true
write.metadata.previous-versions-max: 50

Columns Metrics Tracking
If a Table has many Columns tracking the metrics for all Columns can get very expensive to your writes. 
We can manage metrics and turn them off for Columns where they may not be relevant to your query patterns.

write.metadata.metrics.default: none
write.metadata.metrics.column.col1: counts
write.metadata.metrics.column.col5: full

CTAS - create table as statement
create table prod.db.sample
using Iceberg
partitioned by(bucket(16, id), days(ts), truncate(last_name, 2))
as select ...


ICEBERG SNAPSHOT EXPIRATION FOR MASKED DATA TABLES
==================================================

Understanding Snapshot Expiration and Data File Retention
---------------------------------------------------------

Key Question: When running Iceberg's expiring snapshots procedure, will it delete actual data files or just previous snapshots?

Answer: Iceberg's snapshot expiration procedure primarily removes metadata snapshots and will only delete actual data files when they are no longer referenced by any remaining snapshots.

How Snapshot Expiration Works:
------------------------------

1. Removes Snapshot Metadata:
   - The procedure removes old snapshot entries from the table metadata
   - Makes them unavailable for time travel queries
   - Removes references to data files from expired snapshots

2. Deletes Unreferenced Data Files ONLY when:
   - They are not referenced by any remaining snapshot
   - They are not needed for time travel or rollback operations
   - They are no longer part of the current table state

Data Preservation Guarantees:
-----------------------------

According to Iceberg documentation:
"Data files are not deleted until they are no longer referenced by a snapshot that may be used for time travel or rollback. Regularly expiring snapshots deletes unused data files."

This means:
- Masked data tables will retain all data files that are still referenced by any non-expired snapshot
- Data files are only deleted when they become completely orphaned (not referenced by any snapshot)
- The procedure is designed to be safe and won't accidentally delete data that might still be needed

Configuration for Masked Data Tables (Store Forever):
----------------------------------------------------

For our use case where we want to retain masked data forever:

1. Table Properties Configuration:
   ```
   history.expire.min-snapshots-to-keep=1000  // High number to keep substantial history
   history.expire.max-snapshot-age-ms=31536000000  // 1 year in milliseconds (can be longer)
   history.expire.max-ref-age-ms=94608000000  // 3 years for branches and tags
   ```

2. Important Notes:
   - min-snapshots-to-keep takes PRECEDENCE over age-based expiration
   - A snapshot older than max-snapshot-age-ms CAN be deleted but may NOT NECESSARILY be deleted
   - Setting high min-snapshots-to-keep ensures we always keep recent snapshots

3. Alternative Approaches for Permanent Retention:
   - Use Iceberg's branching and tagging features to create permanent references
   - Create named branches for critical data states
   - Use tags to mark important snapshots that should never be expired

Example Commands for Snapshot Management:
-----------------------------------------

1. Manual Snapshot Expiration (Spark):
   ```sql
   CALL system.expire_snapshots(table => 'masked_data_tables.customer_data')
   
   -- Or with custom timestamp
   CALL system.expire_snapshots(
       table => 'masked_data_tables.customer_data',
       older_than => TIMESTAMP '2023-01-01 00:00:00.000'
   )
   ```

2. Java API Approach:
   ```java
   // Load table
   Table table = catalog.loadTable(TableIdentifier.of("masked_data", "customer_data"));
   
   // Expire using table settings (recommended)
   table.expireSnapshots().commit();
   
   // Or with custom expiration threshold
   table.expireSnapshots()
       .expireOlderThan(System.currentTimeMillis() - RETENTION_PERIOD_MS)
       .commit();
   ```

3. Creating Permanent References:
   ```sql
   -- Create a branch for permanent retention
   ALTER TABLE masked_data_tables.customer_data CREATE BRANCH important_data_v1
   
   -- Create a tag for a specific point in time
   ALTER TABLE masked_data_tables.customer_data CREATE TAG quarterly_snapshot_q4_2024
   ```

Best Practices for Our Masked Data Pipeline:
--------------------------------------------

1. Pipeline-Level Configuration:
   - Set different retention policies for different table types:
     * Base Tables: Standard retention (30-90 days)
     * Structured Tables: Extended retention (6 months)
     * Masked Tables: Very long retention (2+ years) or permanent branches

2. Monitoring and Alerting:
   - Monitor table metadata size growth
   - Set up alerts if snapshot count exceeds thresholds
   - Track storage costs for different retention policies

3. Automated Maintenance Strategy:
   ```
   Daily Jobs:
   - Expire snapshots for base tables (keep last 30 days)
   - Clean up orphan files
   
   Weekly Jobs:
   - Expire snapshots for structured tables (keep last 6 months)
   - Compact small files
   
   Monthly Jobs:
   - Review masked table snapshots (manual process)
   - Create quarterly tags for compliance
   ```

4. Compliance Considerations:
   - GDPR/CCPA require ability to delete personal data
   - Use branching strategy to isolate data that might need deletion
   - Maintain audit trail of what data was masked and when

Example Configuration for Our Use Case:
--------------------------------------

```sql
-- For Masked Data Tables (permanent retention)
ALTER TABLE iceberg_masked_tables.customer_data SET TBLPROPERTIES (
    'history.expire.min-snapshots-to-keep'='2000',
    'history.expire.max-snapshot-age-ms'='63072000000',  -- 2 years
    'history.expire.max-ref-age-ms'='157680000000'       -- 5 years for refs
);

-- For Structured Tables (extended retention)
ALTER TABLE iceberg_structured_tables.customer_data SET TBLPROPERTIES (
    'history.expire.min-snapshots-to-keep'='100',
    'history.expire.max-snapshot-age-ms'='15552000000',  -- 6 months
    'history.expire.max-ref-age-ms'='31536000000'        -- 1 year for refs
);

-- For Base Tables (standard retention)
ALTER TABLE iceberg_base_tables.customer_data SET TBLPROPERTIES (
    'history.expire.min-snapshots-to-keep'='50',
    'history.expire.max-snapshot-age-ms'='2592000000',   -- 30 days
    'history.expire.max-ref-age-ms'='7776000000'         -- 90 days for refs
);
```

Interview Talking Points:
------------------------

1. Data Safety: "I designed the retention strategy with data safety as the primary concern. Iceberg's snapshot expiration is inherently safe - it only removes data files when they're completely orphaned."

2. Compliance: "For our masked data tables, we implemented a tiered retention strategy where compliance-critical data has permanent retention through branching, while operational data has reasonable cleanup cycles."

3. Cost Optimization: "We balanced storage costs with business needs by having different retention policies for different data tiers - base tables clean up quickly, while masked data for analytics is retained much longer."

4. Operational Excellence: "The solution includes automated monitoring of metadata growth and storage costs, with manual review processes for the most critical data."

5. Future-Proofing: "Using Iceberg's branching and tagging features gives us flexibility to adapt our retention strategy as business requirements evolve."


### 1. MySQL to Iceberg Export Jobs

**Watermark Storage Strategy**:
```sql
-- Dedicated watermark table in MySQL
CREATE TABLE export_watermarks (
    table_name VARCHAR(255) PRIMARY KEY,
    last_processed_timestamp TIMESTAMP,
    last_processed_id BIGINT,
    batch_id VARCHAR(100),
    job_status ENUM('RUNNING', 'COMPLETED', 'FAILED'),
    account_id VARCHAR(255),
    num_records_processed INT,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

### 2. Airflow Integration for Orchestration

**DAG with Watermark Management**:
```python
from airflow import DAG
from airflow.providers.amazon.aws.operators.glue import GlueJobOperator
from airflow.operators.python_operator import PythonOperator

def check_watermark_status(**context):
    """Check if previous job completed successfully"""
    table_name = context['params']['table_name']
    status = get_watermark_status(table_name)
    
    if status == 'RUNNING':
        # Previous job still running, skip this iteration
        raise AirflowSkipException("Previous job still running")
    elif status == 'FAILED':
        # Send alert and potentially reset watermark
        send_alert(f"Export job failed for {table_name}")
        
    return status

dag = DAG(
    'mysql_to_iceberg_export',
    schedule_interval='*/15 * * * *',  # Every 15 minutes
    catchup=False
)

# Check watermark before starting
watermark_check = PythonOperator(
    task_id='check_watermark_status',
    python_callable=check_watermark_status,
    params={'table_name': 'customer_data'},
    dag=dag
)

# Run export job
export_job = GlueJobOperator(
    task_id='export_customer_data',
    job_name='mysql-to-iceberg-export',
    script_args={
        '--table_name': 'customer_data',
        '--watermark_table': 'export_watermarks'
    },
    dag=dag
)

watermark_check >> export_job
```

Monitoring and Alerting for Watermarks:
### 1. Watermark Lag Monitoring
# CloudWatch metrics for watermark lag
def publish_watermark_metrics():
    """
    Monitor watermark lag and publish metrics
    """
    for table in MONITORED_TABLES:
        watermark = get_current_watermark(table)
        current_time = datetime.now()
        
        # Calculate lag in minutes
        lag_minutes = (current_time - watermark.last_processed_timestamp).total_seconds() / 60
        
        # Publish to CloudWatch
        cloudwatch.put_metric_data(
            Namespace='DataPipeline/Watermarks',
            MetricData=[
                {
                    'MetricName': 'WatermarkLag',
                    'Dimensions': [
                        {'Name': 'TableName', 'Value': table}
                    ],
                    'Value': lag_minutes,
                    'Unit': 'Count'
                }
            ]
        )
        
        # Alert if lag exceeds threshold
        if lag_minutes > MAX_LAG_MINUTES:
            send_alert(f"Watermark lag for {table}: {lag_minutes} minutes")
```
### 2. Data Quality Checks

```python
def validate_watermark_progress():
    """
    Validate that watermarks are progressing as expected
    """
    for table in MONITORED_TABLES:
        current_watermark = get_current_watermark(table)
        previous_watermark = get_previous_watermark(table)
        
        # Check for backward movement (data corruption)
        if current_watermark.last_processed_timestamp < previous_watermark.last_processed_timestamp:
            send_critical_alert(f"Watermark moved backward for {table}")
        
        # Check for stale watermarks
        if (datetime.now() - current_watermark.updated_at).total_seconds() > STALE_THRESHOLD:
            send_alert(f"Stale watermark detected for {table}")
```
Configuration Examples for Different Environments:
-------------------------------------------------

### Development Environment
```yaml
watermark_config:
  batch_size: 1000
  checkpoint_interval: 30s
  max_lag_tolerance: 5m
  storage: "local_db"
```

### Production Environment
```yaml
watermark_config:
  batch_size: 100000
  checkpoint_interval: 5m
  max_lag_tolerance: 15m
  storage: "rds_cluster"
  backup_frequency: "1h"
  retention_period: "30d"
```

Interview Talking Points - Watermarking:
---------------------------------------

1. **Reliability**: "I implemented watermarking to ensure exactly-once processing and enable safe recovery from failures. Each job tracks its progress persistently, so we can resume from the exact point of failure."

2. **Scalability**: "The watermarking strategy scales horizontally - each shard maintains its own watermark, and we coordinate them for global consistency when needed."

3. **Data Consistency**: "By using watermarks, we guarantee that related data across multiple tables is processed together, maintaining referential integrity in our analytics pipeline."

4. **Operational Excellence**: "The system includes comprehensive monitoring of watermark lag and automatic alerting when processing falls behind, enabling proactive intervention."

5. **Cost Optimization**: "Watermarking enables efficient incremental processing, reducing compute costs by only processing new or changed data rather than full table scans."

6. **Business Continuity**: "The watermarking mechanism supports both regular incremental loads and on-demand backfills without conflicts, ensuring business operations can continue during maintenance windows."

**Tuning for High Concurrency**
When dealing with high concurrency, it's crucial to tune the properties related to commit retries and wait times to ensure
that transactions can successfully commit even when conflicts arise. 

commit.retry.num-retries
commit.retry.min-wait-ms
commit.retry.max-wait-ms 

can help avoid missing rows and improve the overall reliability of concurrent writes.

write.merge.isolation-level: snapshot/ serializable

```
org.apache.iceberg.exceptions.ValidationException: 
Found conflicting files that can contain records matching true: 
[warehouse/iceberg-concurrent-write-isolation-test/test_db/test_table/data/00000-17-848de3f7-0d53-4d0d-bc1a-0edcb0a5b5a7-0-00001.parquet]
```

Addictional Note: Transaction Type

It's crucial to note that in the context of concurrent transactions on Iceberg tables, the behavior observed with snapshot and serializable isolation levels is intrinsic to the type of transaction being executed. Specifically, the MERGE operation, which is used in the demonstration, exhibits different outcomes under these isolation levels due to its nature of potentially modifying existing data. In contrast, an INSERT operation, such as INSERT INTO {table} (id, user, date) VALUES ('id_new_{id}', 'user4', '20240327'), does not inherently cause a conflict because Iceberg handles INSERT operations by simply adding a new metadata file. This distinction highlights the importance of understanding the specific characteristics of the operations being performed when configuring isolation levels in Iceberg, ensuring that the chosen level aligns with the desired behavior and performance characteristics of the data processing tasks at hand.

## Database Concurrency Problems: Dirty Reads, Non-Repeatable Reads, and Phantom Reads

### 1. Dirty Reads
**Definition**: A dirty read occurs when a transaction reads data that has been modified by another transaction that hasn't been committed yet.

**Example Scenario**:
```sql
-- Transaction A
BEGIN;
UPDATE accounts SET balance = 1000 WHERE id = 1;
-- Transaction A hasn't committed yet

-- Transaction B (running concurrently)
BEGIN;
SELECT balance FROM accounts WHERE id = 1;  -- Reads 1000 (uncommitted data)
COMMIT;

-- Transaction A
ROLLBACK;  -- Oops! The change is rolled back
-- Transaction B saw data that never actually existed!
```

**Problem**: Transaction B read "dirty" data (uncommitted changes) that was later rolled back. This leads to inconsistent application state.

**Real-world Impact**: 
- In our analytics pipeline, if a Spark job reads uncommitted customer data during an account update, reports could show incorrect balances
- Financial calculations based on dirty reads could lead to compliance issues

### 2. Non-Repeatable Reads
**Definition**: A non-repeatable read occurs when a transaction reads the same row twice and gets different values because another transaction modified and committed the data between the reads.

**Example Scenario**:
```sql
-- Transaction A
BEGIN;
SELECT balance FROM accounts WHERE id = 1;  -- Returns 500

-- Transaction B (runs and commits)
BEGIN;
UPDATE accounts SET balance = 1000 WHERE id = 1;
COMMIT;

-- Transaction A continues
SELECT balance FROM accounts WHERE id = 1;  -- Returns 1000 (different value!)
COMMIT;
```

**Problem**: The same query in Transaction A returned different results within the same transaction, violating consistency expectations.

**Real-world Impact**:
- In report generation, if we read customer metrics at the beginning and end of processing, we might get inconsistent aggregations
- Audit trails become unreliable when the same data query returns different results

### 3. Phantom Reads
**Definition**: A phantom read occurs when a transaction re-executes a query and finds additional rows that satisfy the search criteria, inserted by another committed transaction.

**Example Scenario**:
```sql
-- Transaction A
BEGIN;
SELECT COUNT(*) FROM orders WHERE status = 'PENDING';  -- Returns 5

-- Transaction B (runs and commits)
BEGIN;
INSERT INTO orders (id, status) VALUES (100, 'PENDING');
COMMIT;

-- Transaction A continues
SELECT COUNT(*) FROM orders WHERE status = 'PENDING';  -- Returns 6 (phantom row!)
COMMIT;
```

**Problem**: New rows "appeared" (like phantoms) that weren't there in the first query, even though Transaction A didn't insert them.

**Real-world Impact**:
- In our ETL jobs, if we count records at the start of processing and again at the end, phantom reads could make us think we processed more records than we actually did
- Pagination in reports could skip or duplicate results

## Isolation Levels and Problem Prevention

| Isolation Level | Dirty Reads | Non-Repeatable Reads | Phantom Reads |
|----------------|-------------|---------------------|---------------|
| **Read Uncommitted** | ❌ Allows | ❌ Allows | ❌ Allows |
| **Read Committed** | ✅ Prevents | ❌ Allows | ❌ Allows |
| **Repeatable Read** | ✅ Prevents | ✅ Prevents | ❌ Allows |
| **Serializable** | ✅ Prevents | ✅ Prevents | ✅ Prevents |

### How This Applies to Our Iceberg Pipeline

**1. For Analytics Jobs**:
```python
# Reading data for report generation
spark.conf.set("spark.sql.adaptive.enabled", "true")
spark.conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

# Use snapshot isolation to prevent dirty reads
df = spark.read.option("snapshot-id", snapshot_id).table("iceberg_tables.customer_data")
```

**2. For Real-time Streaming**:
```python
# Flink job reading from Iceberg
# Use consistent snapshot reads to avoid phantom reads in windowed aggregations
TableEnvironment.create(settings).executeSql("""
    CREATE TABLE iceberg_source (
        customer_id STRING,
        event_time TIMESTAMP,
        amount DECIMAL(10,2)
    ) WITH (
        'connector' = 'iceberg',
        'catalog-name' = 'glue_catalog',
        'warehouse' = 's3://our-datalake/',
        'table-name' = 'events.customer_transactions'
    )
""")
```

**3. For Concurrent ETL Jobs**:
```sql
-- Using proper isolation in our export jobs
SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;

BEGIN;
-- Read watermark
SELECT last_processed_id FROM export_watermarks WHERE table_name = 'customers';

-- Export data based on watermark
INSERT INTO iceberg_tables.customers 
SELECT * FROM mysql_customers WHERE id > ?;

-- Update watermark
UPDATE export_watermarks SET last_processed_id = ? WHERE table_name = 'customers';
COMMIT;
```

## Interview Talking Points

**1. Problem Understanding**: "These concurrency issues are fundamental to any multi-user system. In our analytics pipeline, understanding them helps us choose the right isolation levels for different types of jobs."

**2. Practical Application**: "For our real-time dashboard, we use snapshot isolation to ensure users see consistent data even while ETL jobs are updating the underlying tables."

**3. Performance Trade-offs**: "Higher isolation levels prevent these problems but can impact performance. We balance consistency needs with throughput requirements - using Read Committed for high-frequency operations and Serializable for critical financial calculations."

**4. Iceberg Advantages**: "Iceberg's snapshot isolation naturally prevents dirty reads, and its copy-on-write mechanism helps with consistency without blocking readers during writes."

---

## Apache Spark: Interview Questions for Batch Processing

Given that your project heavily relies on Spark for batch ETL, here are some likely questions and effective talking points for your interview.

### Category 1: Core Concepts

**Q1: Can you explain the difference between an RDD, a DataFrame, and a Dataset? When would you use each?**

*   **RDD (Resilient Distributed Dataset):** The original, low-level abstraction. It's a distributed collection of objects.
    *   **Use When:** You need fine-grained control over data placement and transformations, or when dealing with unstructured data. It's less common now for structured data processing.
*   **DataFrame:** A distributed collection of data organized into named columns, conceptually similar to a table in a relational database.
    *   **Use When:** Working with structured or semi-structured data in Python, R, or Scala. This is the most common API. It leverages the **Catalyst Optimizer** for significant performance gains.
*   **Dataset:** A strongly-typed version of a DataFrame, available only in Scala and Java. It provides compile-time type safety.
    *   **Use When:** You are working in Scala/Java and data integrity is critical. It offers the best of both worlds: the performance of the Catalyst Optimizer and the type safety of RDDs.

**Talking Point:** "In our project, we primarily used **DataFrames** because we were processing structured data from MySQL. This allowed us to leverage the **Catalyst Optimizer** and Tungsten execution engine, which dramatically improved performance over using low-level RDDs."

**Q2: What is Lazy Evaluation in Spark? Why is it important?**

*   **Lazy Evaluation** means that Spark doesn't execute transformations immediately. It builds up a Directed Acyclic Graph (DAG) of transformations.
*   Execution is only triggered when an **action** (like `count()`, `collect()`, `save()`) is called.
*   **Importance:** This allows the Catalyst Optimizer to analyze the entire DAG and create the most efficient physical execution plan. It can reorder operations, combine transformations (pipelining), and optimize data access.

**Talking Point:** "Lazy evaluation was key for our complex ETL jobs. It allowed Spark to pipeline multiple `filter` and `select` operations together, minimizing the number of passes over the data and significantly reducing I/O."

---

### Category 2: Performance Optimization

**Q3: Your project moved a lot of data. What is "shuffling" in Spark and how can it hurt performance? How did you minimize it?**

*   **Shuffling** is the process of redistributing data across partitions. It's triggered by wide transformations like `groupByKey`, `reduceByKey`, `join`, and `repartition`.
*   **Performance Impact:** Shuffling is very expensive. It involves disk I/O, data serialization, and network I/O as data is moved between executors.
*   **Minimizing Shuffling:**
    1.  **Use `reduceByKey` over `groupByKey`:** `reduceByKey` performs partial aggregation on each partition *before* shuffling, reducing the amount of data sent over the network.
    2.  **Use Broadcast Joins:** When joining a large DataFrame with a small one, you can `broadcast` the small DataFrame to every executor. This avoids shuffling the large DataFrame entirely.
    3.  **Correct Partitioning:** Partition your data effectively using `partitionBy()` when writing to disk. When you read it back, operations on the partitioned columns (like filters or joins) can avoid shuffling.

**Talking Point:** "We faced significant shuffling costs during our data structuring jobs. We optimized this by using `reduceByKey` for aggregations and, critically, by using **broadcast joins** when enriching our main dataset with smaller metadata tables. We also partitioned our Iceberg tables by date, which allowed downstream jobs to filter by date without triggering a full table scan and shuffle."

**Q4: What is the difference between `cache()` and `persist()`? When did you use them in your project?**

*   `cache()` is a shortcut for `persist(StorageLevel.MEMORY_ONLY)`. It stores the DataFrame/RDD in memory on the executors.
*   `persist()` allows you to specify a `StorageLevel`, giving you more control (e.g., `MEMORY_AND_DISK`, `MEMORY_ONLY_SER`, `DISK_ONLY`).
*   **When to Use:** You use them when you need to access the same DataFrame multiple times in your job. By caching it, you avoid re-computing its entire lineage from the source.

**Talking Point:** "In our pipeline, after the initial data cleansing and structuring step, we had a 'base' DataFrame that was used by both the GoodData export job and the ClickHouse export job. We used `persist(StorageLevel.MEMORY_AND_DISK)` on this DataFrame. This was a key optimization because it prevented us from re-reading and re-processing the raw data from S3 twice. We chose `MEMORY_AND_DISK` to handle cases where the DataFrame was too large to fit entirely in memory, preventing OOM errors."

**Q5: How do you handle Data Skew in a Spark job?**

*   **Data Skew** is when data is unevenly distributed across partitions. One or a few partitions are much larger than the others, causing a few tasks to run much longer and creating a bottleneck.
*   **Handling Skew:**
    1.  **Identify:** Look for tasks in the Spark UI that are taking much longer than the median task time.
    2.  **Salting:** Add a random "salt" key to the skewed keys. This breaks up the skewed partition into multiple smaller partitions. For example, instead of joining on `user_id`, you join on `concat(user_id, '_', floor(rand() * N))`. You must add the same salt to the corresponding keys in the other DataFrame.
    3.  **Isolate Skewed Keys:** You can process the skewed keys separately from the rest of the data and then `union` the results back together.

**Talking Point:** "We noticed data skew for a few very large accounts. Our solution was to implement **salting**. We identified the skewed account IDs, appended a random suffix to them to spread their data across more partitions, and then performed the join. This balanced the workload across the executors and resolved the bottleneck."

---

### Category 3: Project-Specific Scenarios

**Q6: Your service had OOM (Out of Memory) issues. How would you diagnose and solve OOM errors in a Spark application?**

*   **Diagnosis (Spark UI):**
    *   Check the **Executors** tab in the Spark UI. Look for failed executors.
    *   Examine the `stderr` logs for the failed executor. It will usually contain the `java.lang.OutOfMemoryError` message.
    *   Analyze the stage that failed. Is it a wide transformation with a massive shuffle? Is it a `collect()` action bringing too much data to the driver?
*   **Solutions:**
    1.  **Increase Executor Memory:** The simplest solution is to increase `spark.executor.memory`.
    2.  **Tune Memory Overhead:** Increase `spark.executor.memoryOverhead` if the OOM is happening in non-heap memory (e.g., from Python processes).
    3.  **Reduce Partition Size:** Use `repartition()` or `coalesce()` to create more, smaller partitions. This reduces the amount of data processed by a single task at once.
    4.  **Check for `collect()`:** Ensure you are not using `.collect()` on a large DataFrame, which tries to pull all data to the driver node.
    5.  **Broadcast Joins:** If the OOM happens during a join, check if one DataFrame is small enough to be broadcast.

**Q7: You moved from a single-node service to a distributed Spark job. How does Spark achieve fault tolerance?**

*   Spark's fault tolerance comes from its core abstraction, the **RDD**, and its concept of **lineage**.
*   **Lineage:** Spark maintains a DAG (Directed Acyclic Graph) of all the transformations that were used to create an RDD from the source data. This graph is the RDD's "lineage."
*   **Re-computation:** If a partition of an RDD is lost (e.g., because an executor node fails), Spark can use the lineage to re-compute just that lost partition from the original source data on another available node.
*   **Checkpoints:** For very long lineages, re-computation can be slow. You can periodically **checkpoint** an RDD, which saves it to a reliable distributed file system (like HDFS/S3). This truncates the lineage graph, so if a failure occurs after a checkpoint, Spark can recover from the checkpoint instead of the original source.

**Talking Point:** "Spark's fault tolerance was crucial for our long-running ETL jobs. We relied on its ability to re-compute lost partitions using the lineage graph. For our most complex jobs with many stages, we implemented **checkpointing** to S3 after the main data structuring phase. This saved us significant recovery time during failures by preventing Spark from having to re-run the entire job from the very beginning."

---

### Understanding the Spark Execution Model & UI

This is a critical area for any data engineering interview. Being able to explain how a Spark application is executed and how you use the UI to debug it demonstrates deep practical knowledge.

#### The Hierarchy: Job -> Stage -> Task

1.  **Application:** Your entire Spark program, from the start of the `SparkContext` to its end.

2.  **Job:** One **action** triggers one Job. Actions are operations that return a result to the driver or write to storage (e.g., `count()`, `collect()`, `save()`). A single Spark application can have multiple jobs that run sequentially or in parallel (if you use multiple threads).

3.  **Stage:** Jobs are broken down into Stages. A new stage is created at every **shuffle boundary**. Transformations are grouped into the same stage if they can be done in memory on a single executor (these are called *narrow transformations*, like `map` or `filter`). A *wide transformation* (like `groupByKey` or `join`) requires a shuffle and thus creates a new stage boundary.

4.  **Task:** A Stage is a collection of parallel tasks. A Task is the smallest unit of execution. One task is launched for each partition of the data. If a stage is processing a DataFrame with 200 partitions, that stage will consist of 200 tasks.

5.  **Executor & Driver:** The **Driver** is the process where your `main()` method runs. It plans the execution. **Executors** are the processes on the worker nodes that run the actual tasks.

#### Analyzing the Spark UI for Bottlenecks

**Q: How do you use the Spark UI to find a performance bottleneck in your job?**

**A:** "My process is to start high-level and drill down:

1.  **Jobs Tab:** First, I look at the Jobs tab to see the overall status. If a job is taking much longer than expected, I click on it to see its stages.

2.  **Stages Tab:** This is where I spend most of my time. I look for the longest-running stage, as that's the primary bottleneck. I check the **DAG Visualization** to understand what operations are in that stage. I also look at the summary metrics:
    *   **Shuffle Read/Write:** A very large shuffle write indicates that a wide transformation is moving a huge amount of data, which is a major red flag.
    *   **Input Size:** If the input size is massive, I might need to reconsider my filtering strategy or increase the number of partitions.

3.  **Tasks View (within the Stage page):** Once I've identified the bottleneck stage, I drill into its tasks to find the root cause.
    *   **Event Timeline:** This is the best tool. I enable the timeline to get a visual gantt chart of all tasks. I'm looking for two things:
        *   **Gaps:** Gaps between tasks mean the driver is busy or there's a lack of resources.
        *   **Long Bars:** A few tasks that are much longer than the others is a classic sign of **data skew**.
    *   **Summary Metrics Table:** I sort the tasks by duration. If the **Max** duration is significantly higher than the **Median** or **75th percentile**, that confirms data skew. I also check:
        *   **Shuffle Read/Write Size:** If a few tasks are reading or writing much more shuffle data than others, that's another sign of skew.
        *   **GC Time:** High Garbage Collection (GC) time indicates the task is under memory pressure. It might be trying to process a partition that's too large, or the executor memory might be too low.

4.  **Executors Tab:** If I suspect a memory issue, I check the Executors tab. I can see the memory usage, GC time per executor, and check the `stderr` logs for any OutOfMemoryErrors.

By following this top-down approach from Job to Stage to Task, I can systematically pinpoint whether the bottleneck is due to a large shuffle, data skew, or memory pressure, and then apply the appropriate optimization like salting, broadcasting, or repartitioning."

**Q8: In your project, you needed to read data once and sink it to multiple destinations (e.g., Iceberg and ClickHouse). How do you do this efficiently in Spark?**

*   **The Inefficient Approach:** A common mistake is to simply read a DataFrame and then call a `write` action on it for each destination. Due to Spark's lazy evaluation, this will cause Spark to go back to the original source and re-read and re-compute the entire DataFrame for every single write operation. This is incredibly wasteful.

*   **The Efficient Solution: `cache()` or `persist()`**
    The correct pattern is to read the source data, perform any common transformations, and then explicitly cache the resulting DataFrame in memory before writing it to the various destinations.

    ```python
    # 1. Read from the source once
    source_df = spark.read.format("jdbc").option(...).load()

    # 2. Apply common transformations
    transformed_df = source_df.filter("status = 'active'").select("id", "name", "event_date")

    # 3. Cache the result in memory!
    transformed_df.cache()

    # 4. Now, write to multiple destinations.
    # The first write will trigger the computation and fill the cache.
    transformed_df.write.format("iceberg").mode("overwrite").save("path/to/iceberg_table")

    # The second write will read from the cache, not the original source.
    transformed_df.write.format("jdbc").option("url", "jdbc:clickhouse:...").save()
    ```

**Talking Point:** "In our architecture, we had a base structured DataFrame that needed to be written to both our main Iceberg table and our ClickHouse analytics database. To avoid re-reading from our source S3 data twice, we used `transformed_df.cache()`. This materialized the DataFrame in the executors' memory after the first write to Iceberg. The subsequent write to ClickHouse was then able to read directly from this in-memory cache, which cut the execution time for the second half of the job by more than half and reduced the load on our source systems."

**Q9: What if the DataFrame you want to cache is too large to fit in memory? Can you cache to disk?**

**A:** Yes, absolutely. This is a critical consideration for large-scale jobs, and Spark provides two main strategies for this.

#### Strategy 1: Use `persist()` with `StorageLevel.MEMORY_AND_DISK`

*   **`cache()` vs. `persist()`:** It's important to know that `cache()` is just a convenient alias for `persist(StorageLevel.MEMORY_ONLY)`.
*   **Spilling to Disk:** The `persist()` method is more flexible and allows you to choose different storage levels. The most common one for large datasets is `StorageLevel.MEMORY_AND_DISK`.
    *   **How it works:** Spark will first try to store the DataFrame partitions in memory. If an executor runs out of memory, it will "spill" the remaining partitions to its local disk.
    *   **Benefit:** This prevents OutOfMemory errors while still providing fast access for the partitions that *do* fit in memory. Reading from local disk is slower than RAM, but it's almost always much faster than re-computing the entire DataFrame from the original source.

#### Strategy 2: Checkpointing (The Most Robust Method)

If the DataFrame is so massive that even spilling to disk is problematic, or if you need better fault tolerance, the best practice is to **checkpoint** the DataFrame.

*   **How it works:** Instead of using `cache()` or `persist()`, you physically write the intermediate DataFrame to a reliable distributed file system like S3 or HDFS. Then, your subsequent steps read from this new, materialized location.

    ```python
    # Instead of caching...
    # transformed_df.persist(StorageLevel.MEMORY_AND_DISK)

    # ...write it to a reliable location.
    checkpoint_path = "s3://my-bucket/checkpoints/my-job/run-123"
    transformed_df.write.mode("overwrite").parquet(checkpoint_path)

    # Downstream jobs read from the checkpointed location
    iceberg_df = spark.read.parquet(checkpoint_path)
    iceberg_df.write.format("iceberg").save(...)

    clickhouse_df = spark.read.parquet(checkpoint_path)
    clickhouse_df.write.format("jdbc").save(...)
    ```

*   **Key Advantages of Checkpointing:**
    *   **Better Fault Tolerance:** Data on S3/HDFS is safe. If the Spark job fails, you can restart it from the checkpoint. Data cached with `persist()` is stored on executor-local disks and is lost if the executor fails, forcing Spark to recompute it.
    *   **Decoupling Jobs:** You can have completely separate Spark applications read from the checkpointed location, fully decoupling the production of the data from its consumption.

**Talking Point:** "For most of our multi-sink jobs, we used `persist(StorageLevel.MEMORY_AND_DISK)`. This gave us the performance benefit of in-memory caching while gracefully handling larger-than-memory datasets by spilling to disk. However, for our most critical and largest intermediate DataFrame, which was the result of a very complex transformation, we chose to checkpoint it to S3. This was a trade-off: the write to S3 was slower than a simple `persist`, but it gave us rock-solid fault tolerance and allowed us to decouple the complex data generation job from the simpler downstream export jobs."

---

## Window Functions: SQL vs. Spark

**Q: Can you explain what a window function is and show how to use one in both SQL and PySpark?**

**A:** Absolutely. Window functions are a powerful feature for performing calculations across a set of rows that are related to the current row. Unlike a `GROUP BY` aggregation, which collapses rows into a single output row, a window function returns a value for **every single row**.

They are defined using the `OVER()` clause, which has three main parts:
*   **`PARTITION BY`**: Divides the rows into partitions (the "window"). The function is applied independently to each partition.
*   **`ORDER BY`**: Orders the rows within each partition. This is crucial for ranking and sequence functions.
*   **Frame Clause (`ROWS BETWEEN ...`)**: Specifies the subset of rows within the partition to use (e.g., a running total).

--- 

**Example Problem: Find the top 3 highest-paid employees in each department.**

Let's assume we have an `employees` table with `name`, `department`, and `salary`.

#### SQL Implementation

In SQL, you define the window directly in the `OVER` clause.

```sql
WITH RankedEmployees AS (
    SELECT
        name,
        department,
        salary,
        ROW_NUMBER() OVER(PARTITION BY department ORDER BY salary DESC) as rank
    FROM employees
)
SELECT
    name,
    department,
    salary
FROM RankedEmployees
WHERE rank <= 3;
```

#### PySpark Implementation

In PySpark, the concept is identical, but the syntax is programmatic. You define a `WindowSpec` and then use it with a function like `row_number()`.

```python
from pyspark.sql.window import Window
from pyspark.sql.functions import row_number

# Assume 'employees_df' is our DataFrame

# 1. Define the Window Specification
windowSpec  = Window.partitionBy("department").orderBy(employees_df['salary'].desc())

# 2. Apply the window function to the DataFrame
ranked_df = employees_df.withColumn("rank", row_number().over(windowSpec))

# 3. Filter for the top 3
top_3_df = ranked_df.filter(ranked_df['rank'] <= 3)

top_3_df.select("name", "department", "salary").show()
```

**Talking Point:** "Window functions are essential for advanced analytics. In our project, we used them to calculate user session data. By partitioning by `user_id` and ordering by `event_timestamp`, we could use the `LAG` function to calculate the time difference between consecutive events for each user, allowing us to identify session boundaries without having to do expensive, complex joins."

---

## Orchestration with Airflow: Interview Questions

**Q1: How would you schedule a complex workflow: an ingestion job runs every 20 minutes for the first 20 hours of the day, then a 1-hour data compaction job runs, and then the ingestion resumes?**

**A:** This is a classic operational scenario that requires more than a simple cron schedule. The best practice in Airflow is to use a **Two-DAG Pattern** to separate the concerns of ingestion and maintenance. This is cleaner and more maintainable than creating one massive, complex DAG.

Here’s the design:

#### DAG 1: `continuous_ingestion_dag.py`

This DAG is responsible only for ingestion. It runs every 20 minutes but has an internal gatekeeper to check if it's allowed to run.

*   **Schedule:** `*/20 * * * *` (Every 20 minutes)
*   **Logic:** A `BranchPythonOperator` checks the time. If it's within the 20-hour operational window, it runs the Spark job. Otherwise, it does nothing.

```python
from airflow.operators.python import BranchPythonOperator
from airflow.operators.dummy import DummyOperator
from datetime import datetime

def _check_time_window():
    # Run from hour 0 to 19 (the first 20 hours of the day)
    if datetime.now().hour < 20:
        return 'run_ingestion_spark_job'
    else:
        return 'skip_ingestion'

with DAG(...) as dag:
    check_time = BranchPythonOperator(
        task_id='check_time_window',
        python_callable=_check_time_window
    )

    run_ingestion = SparkSubmitOperator(task_id='run_ingestion_spark_job', ...)
    skip_ingestion = DummyOperator(task_id='skip_ingestion')

    check_time >> [run_ingestion, skip_ingestion]
```

#### DAG 2: `daily_compaction_dag.py`

This DAG's only job is to run the daily compaction. It's simple and self-contained.

*   **Schedule:** `0 20 * * *` (Once a day at 8 PM UTC)
*   **Logic:** It has one primary task: run the Spark compaction job for the Iceberg table.

```python
with DAG(
    dag_id='daily_iceberg_compaction',
    schedule_interval='0 20 * * *', # Runs at 8 PM UTC
    ...
) as dag:
    run_compaction = SparkSubmitOperator(
        task_id='run_iceberg_compaction',
        application='s3://path/to/compaction_job.py',
        ...
    )
```

### How It Works Together

1.  **Ingestion:** The `continuous_ingestion_dag` runs every 20 minutes. From 00:00 to 19:59 UTC, the time check passes, and the ingestion job is executed.
2.  **Pause:** At 20:00 UTC, the time check in the ingestion DAG starts failing. All runs from 20:00 to 23:59 will follow the 'skip' path, effectively pausing ingestion.
3.  **Compaction:** At exactly 20:00 UTC, the `daily_compaction_dag` is triggered by its own schedule and runs the maintenance job.
4.  **Resumption:** At 00:00 UTC the next day, the time check in the ingestion DAG starts passing again, and the 20-minute ingestion flow resumes automatically.

**Talking Point:** "For this requirement, I would architect a two-DAG solution in Airflow. One DAG would handle the 20-minute ingestion, with a Python branch operator to enforce the 20-hour operational window. A second, simpler DAG would be scheduled to run the compaction job daily during the maintenance window. This separates concerns, makes the system easier to monitor and maintain, and avoids creating a single, overly complex DAG that is hard to debug."

**Q2: That daily compaction pattern leads to query performance degrading all day. How would you evolve this design for more consistent performance, and where would you add a daily data masking job?**

**A:** That's a critical point. The daily maintenance window is a good starting point, but for better analytics performance, a **concurrent, more frequent compaction strategy** is the superior architecture. This involves decoupling the DAGs and letting them run independently.

Here is the evolved three-DAG design:

#### DAG 1: `continuous_ingestion_dag.py` (Simplified)

We remove the time-based branching logic. The ingestion job now runs around the clock.

*   **Schedule:** `*/20 * * * *` (Every 20 minutes, 24/7)
*   **Logic:** A single `SparkSubmitOperator` to run the ingestion job.

#### DAG 2: `frequent_compaction_dag.py` (Re-scheduled)

This DAG now runs more frequently to keep the number of small files low throughout the day.

*   **Schedule:** `0 */4 * * *` (Every 4 hours)
*   **Concurrency:** This DAG will run at the same time as the ingestion DAG. This is safe because Iceberg uses optimistic locking. If both jobs try to commit a change to the table at the exact same time, one will fail with a `CommitFailedException`. Airflow's retry mechanism can handle this gracefully, ensuring both jobs eventually succeed.

#### DAG 3: `daily_masking_job_dag.py` (New)

This is a new, self-contained DAG for the security task.

*   **Schedule:** `0 1 * * *` (Once a day at 1 AM UTC)
*   **Logic:** It runs the Spark job to produce the masked dataset. By scheduling it after the midnight compaction run (which happens at 00:00), we ensure it reads from a recently optimized table.

### Architectural Trade-offs

*   **Pro:** This design provides **consistent, predictable query performance** for users because the table is always in a relatively optimized state.
*   **Con:** It introduces more operational complexity. You rely on Iceberg's locking and Airflow's retries to manage concurrency, which requires more careful monitoring.

**Talking Point:** "While a daily maintenance window is simpler, I would advocate for a concurrent architecture to ensure consistent query performance for our analytics users. We would have our ingestion DAG run 24/7. A separate compaction DAG would run every four hours, relying on Iceberg's optimistic locking to safely manage concurrent writes. Finally, a third daily DAG would handle data masking, scheduled to run shortly after a compaction cycle to ensure it reads from an optimized data source. This pattern provides a much better user experience at the cost of some added operational complexity."

---

**Q3: Can an Airflow DAG trigger an AWS Glue job via an API call?**

**A:** Yes, and this is a very common requirement. There are two excellent patterns for this, a direct approach and an indirect (decoupled) approach.

#### Pattern 1: The Direct Approach (Using `AWSGlueJobOperator`)

This is the simplest and most common method. The official AWS provider for Airflow includes an operator that handles the interaction with the Glue API for you.

*   **How it works:** The operator uses the AWS connection configured in Airflow to make a `start_job_run` API call to AWS Glue directly.
*   **Best for:** Most standard use cases where the Airflow environment has direct IAM permissions to access Glue.

```python
from airflow.providers.amazon.aws.operators.glue import AWSGlueJobOperator

with DAG(...) as dag:
    trigger_glue_job = AWSGlueJobOperator(
        task_id='trigger_daily_glue_job',
        job_name='my-daily-etl-job',
        script_location='s3://my-bucket/glue-scripts/my_script.py',
        aws_conn_id='aws_default',
        region_name='us-east-1'
    )
```

#### Pattern 2: The Indirect/Decoupled Approach (API Gateway -> Lambda -> Glue)

This pattern is used when you want to abstract the interaction or when Airflow shouldn't have direct permissions to Glue.

*   **How it works:**
    1.  You create a **Lambda function** that contains the logic to start the Glue job (using the `boto3` library).
    2.  You expose this Lambda function via an **API Gateway** endpoint.
    3.  In Airflow, you use the `SimpleHttpOperator` to make a POST request to this API endpoint.
*   **Best for:** Scenarios where you want to provide a generic interface for triggering jobs, for security reasons to limit Airflow's direct permissions, or to integrate with other non-Airflow systems.

**Airflow DAG Code:**
```python
from airflow.providers.http.operators.http import SimpleHttpOperator

with DAG(...) as dag:
    trigger_glue_via_api = SimpleHttpOperator(
        task_id='trigger_glue_via_api_gateway',
        http_conn_id='my_api_gateway_conn', # Connection configured in Airflow UI
        endpoint='v1/start-job',
        method='POST',
        data='{"job_name": "my-daily-etl-job"}',
        headers={"Content-Type": "application/json"}
    )
```

**Talking Point:** "Yes, you can trigger Glue jobs from Airflow. For most cases, I'd use the standard `AWSGlueJobOperator` as it's the most direct and maintainable approach. However, if the requirement was to provide a generic job-triggering endpoint for multiple services or to strictly limit Airflow's IAM permissions, I would design a decoupled solution using an API Gateway to trigger a Lambda function, which in turn starts the Glue job. The Airflow DAG would then simply call this API using the `SimpleHttpOperator`."

---

## Apache Iceberg: Interview Questions

**Q1: Your project used Iceberg. What is the "small file problem" and how does Iceberg help solve it? What is the default target file size for compaction?**

*   **The Small File Problem:** In data lakes, frequent small writes (e.g., from streaming jobs or incremental updates) create a large number of small files. This is inefficient for query engines like Spark because the overhead of opening, reading, and closing thousands of files can dominate the actual data processing time.

*   **Iceberg's Solution (Compaction):** Iceberg provides procedures to run compaction jobs. These jobs read the small data files in your table and rewrite them into a smaller number of larger, more optimal files. This is a background maintenance operation.

*   **Default Target File Size:** The default target size for these new files is **512 MB**. This is controlled by the table property `write.target.file.size.bytes`.

**Talking Point:** "In our project, we ingested data every 15 minutes, which led to a lot of small Parquet files being created in our Iceberg table. To maintain query performance for our downstream analytics, we scheduled a daily compaction job. This job would rewrite the small files into larger files targeting the default size of 512 MB, which is optimal for S3 and significantly improved our Spark read performance."

---

## ClickHouse: Interview Questions

**Q1: Your project used ClickHouse for analytics. Can you explain how its core storage engine, `MergeTree`, works?**

*   **Core Concept (LSM-Tree like):** The `MergeTree` engine family is conceptually similar to a Log-Structured Merge-Tree (LSM-Tree). Data is stored in sorted, immutable files on disk called **"parts"**.

*   **How Writes Work:**
    1.  When you `INSERT` data into a `MergeTree` table, ClickHouse writes that data into a new part (or multiple parts).
    2.  Each part is internally sorted by the table's **Primary Key**.
    3.  This makes writes extremely fast, as they are mostly sequential appends.

*   **The Primary Key is a Sorting Key:** This is the most critical concept. Unlike in traditional relational databases, the `PRIMARY KEY` in ClickHouse is **not a uniqueness constraint**. It defines how data is sorted within each part. The `ORDER BY` clause in the `CREATE TABLE` statement sets this key.

*   **How Reads Work (The Power of the Primary Key):**
    1.  When you run a query with a `WHERE` clause on the primary key columns, ClickHouse can use its sparse primary index to very quickly identify which blocks of data it needs to read from disk.
    2.  Because the data is sorted, it can perform extremely fast range scans, skipping over massive amounts of irrelevant data.

*   **Background Merges:** Just like Iceberg, `MergeTree` tables can accumulate many small parts from frequent inserts. ClickHouse has a background process that continuously **merges** smaller parts into larger, more efficient ones. This is crucial for maintaining query performance over time.

**Talking Point:** "We chose ClickHouse because our queries were primarily analytical, filtering and aggregating on specific time ranges and dimensions. We designed our `MergeTree` table with `ORDER BY (event_date, account_id)`. This meant our data was physically sorted on disk by date and account. When our users queried for a specific account's data within a date range, ClickHouse could instantly seek to the relevant data blocks, making our dashboards incredibly fast. The background merge process was also key, as it automatically optimized the table structure without any manual intervention from our side."


**Q2: Can you explain the relationship between Parts, Granules, and the Primary Index in ClickHouse?**

This question dives deeper into the `MergeTree` architecture. Here's how they fit together:

1.  **Part:** As discussed, a Part is a chunk of data on disk, sorted by the primary key. A table is a collection of parts.

2.  **Granule:** This is the smallest unit of data that ClickHouse reads from disk. Each Part is internally divided into **Granules**. By default, a granule contains **8,192 rows**. This size is controlled by the `index_granularity` table setting.

3.  **Sparse Primary Index:** This is the secret to ClickHouse's speed. Unlike a traditional B-Tree index that might have an entry for every row, ClickHouse's index is **sparse**.
    *   It does **not** index every row.
    *   Instead, it stores an index entry (a "mark") for the **first row of each granule**.

**How it All Works Together (The Read Path):**

Let's say you have a table with `ORDER BY (EventDate)` and an `index_granularity` of 8192. The `primary.idx` file contains the `EventDate` for row 0, row 8192, row 16384, and so on.

When you run `WHERE EventDate = '2023-10-26'`, ClickHouse does this:

1.  **Scan the Index:** It performs a quick binary search over the tiny `primary.idx` file in memory.
2.  **Find Relevant Granules:** It finds the range of "marks" that could contain `'2023-10-26'`. For example, it might find that the data must lie between the mark for `'2023-10-25'` and the mark for `'2023-10-27'`.
3.  **Read Only What's Necessary:** ClickHouse now knows exactly which granules (blocks of 8,192 rows) it needs to load from disk to satisfy the query. It completely skips reading all other granules.

**Talking Point:** "The sparse index is a brilliant trade-off. By only indexing every 8,192 rows, the index file is small enough to fit in RAM, making the initial scan incredibly fast. This allows ClickHouse to narrow down a search over billions of rows to just a few megabytes of data that need to be read from disk. This architecture of parts, granules, and marks is what enables its sub-second query performance on massive datasets."

**Q3: Can you draw a diagram of this? And does ClickHouse have a memtable for recent data?**

**A:** Absolutely. Here’s a diagram illustrating the sparse index, and let's clarify the memtable question.

#### Diagram of a Part, Granules, and the Sparse Index

```
A Single Data Part on Disk (e.g., part_202310_1_1_0)
[ Granule 1 (8192 rows) | Granule 2 (8192 rows) | Granule 3 (8192 rows) | ... ]
  ^
  |  (Primary Key Value: '2023-10-01')
  |
primary.idx File (tiny, in memory)
[ Mark 1: value='2023-10-01' ] -----> Points to the start of Granule 1
[ Mark 2: value='2023-10-03' ] -----> Points to the start of Granule 2
[ Mark 3: value='2023-10-05' ] -----> Points to the start of Granule 3
...
```

**Query Example:** `WHERE date = '2023-10-04'`
1.  **Scan `primary.idx`:** ClickHouse quickly scans the marks in memory and sees that `'2023-10-04'` falls between the values of Mark 2 (`'2023-10-03'`) and Mark 3 (`'2023-10-05'`).
2.  **Identify Granule:** It concludes the data, if it exists, *must* be in Granule 2.
3.  **Read from Disk:** It seeks directly to the start of Granule 2 and reads only that 8,192-row block, ignoring all other granules in the part.

#### Does ClickHouse Have a Memtable?

No, not in the traditional sense of a queryable, in-memory write buffer.

*   **How Writes Work:** When you `INSERT` data, it is buffered in memory very briefly and then written directly to the filesystem as a new, immutable data part. It does not stay in a memtable to serve reads.
*   **The Role of the OS Page Cache:** ClickHouse achieves the *effect* of a memtable by relying on the operating system's page cache. When a new part is written, the OS keeps that file in its RAM cache. Any immediate queries for that recent data are served directly from RAM by the OS, which is extremely fast.

**Talking Point:** "ClickHouse makes a deliberate design choice to not implement its own memtable. It offloads that responsibility to the OS page cache, which is highly optimized for caching file data. This simplifies the database's architecture while still providing excellent read performance for recent data, as newly created parts are almost always hot in the page cache."

**Q4: How do you handle duplicate data or updates in ClickHouse? Let's discuss `ReplacingMergeTree` and `VersionedCollapsingMergeTree`.**

**A:** This is a key challenge, as the base `MergeTree` engine doesn't enforce uniqueness. For this, you use specialized `MergeTree` engines.

#### `ReplacingMergeTree` (for simple de-duplication)

*   **Purpose:** To automatically remove duplicate rows that have the same sorting key (`ORDER BY` clause).
*   **Mechanism:** During a background merge, if multiple rows within the merging parts have the same sorting key, `ReplacingMergeTree` keeps only the **last one** it encounters. You can optionally specify a version column to ensure it keeps the newest row, otherwise the choice is non-deterministic.
*   **The `FINAL` Keyword:** The de-duplication only happens *during merges*. This means that at any given time, the table might still contain duplicates across different data parts. To get a fully de-duplicated result in a query, you must use the `FINAL` modifier (e.g., `SELECT ... FROM my_table FINAL`). Be aware that `FINAL` can be slow as it forces a merge-on-the-fly during the query.

**Use Case:** Perfect for event streams where you might receive duplicate events and only care about keeping the latest one.

```sql
CREATE TABLE my_events (
    event_id UInt64,
    event_time DateTime,
    data String
)
ENGINE = ReplacingMergeTree(event_time) -- Uses event_time as the version
ORDER BY (event_id);
```

#### `VersionedCollapsingMergeTree` (for updates and deletes)

*   **Purpose:** A more advanced engine designed to handle state changes, like tracking updates and deletes, which is essential for implementing Slowly Changing Dimensions (SCD).
*   **Mechanism:** It requires two specific columns:
    1.  `Sign`: An `Int8` column. `1` represents a new or updated state, and `-1` represents a "deleted" state.
    2.  `Version`: A numeric column that tracks the version of a row.
    During a merge, for a given primary key, the engine collapses all rows, keeping only the row with the latest `Version` if its `Sign` is `1`. If the latest version has a `Sign` of `-1`, all rows for that key are removed.
*   **The `FINAL` Keyword:** Like `ReplacingMergeTree`, the collapsing is not guaranteed to be complete at all times. You often need to write queries that account for this, either by using `FINAL` or by using specific aggregations like `sum(Sign)` and `having(sum(Sign) > 0)`.

**Use Case:** Ideal for replicating changes from a transactional database. If a user's address changes, you insert a `Sign=-1` row for the old address and a `Sign=1` row for the new one, both with an updated version.

**Talking Point:** "In our project, we used `ReplacingMergeTree` for our raw event tables to handle potential duplicate events sent by our source systems. For our dimension tables that mirrored customer data from MySQL, we considered `VersionedCollapsingMergeTree` to implement SCD Type 2, allowing us to track the history of changes to customer attributes over time."

---

## System Design: The gRPC Streamer Service

**Q: Your diagrams mention a "gRPC Streamer." What is that, and why is it used?**

**A:** The gRPC Streamer is a custom **Backend-for-Frontend (BFF)** service we designed to efficiently serve large analytical datasets from ClickHouse to our web UIs. It's a critical component for ensuring a responsive user experience.

Let's break down the name:

1.  **gRPC:** This is a high-performance RPC framework that uses a binary data format (Protocol Buffers) and runs on HTTP/2. We chose it over a traditional REST/JSON API for two main reasons:
    *   **Performance:** The binary protocol is much smaller and faster to parse than text-based JSON, reducing network latency.
    *   **Streaming Support:** gRPC has first-class support for server-side streaming, which is the core of this service's design.

2.  **Streamer:** This describes the primary function of the service. When a UI requests a large dataset (e.g., 1 million rows), a traditional API would fetch all the data, load it into memory, and send it in one massive, blocking response. Our streamer service does this instead:
    *   The UI makes a single gRPC call to the streamer.
    *   The streamer queries ClickHouse.
    *   As soon as the streamer receives the first chunk of rows from the database, it immediately sends it to the UI over the open gRPC stream.
    *   It continues to send chunks as they arrive from the database until the result set is complete.

#### Architectural Flow

`[Web UI] <--gRPC Stream--> [gRPC Streamer Service] <--SQL Query--> [ClickHouse]`

**Benefits of this approach:**
*   **Low Latency to First Byte:** The UI can begin rendering data almost instantly, instead of waiting for the full query to finish.
*   **Low Client-Side Memory:** The browser never needs to load the entire dataset into memory, preventing crashes and sluggishness.
*   **Network Efficiency:** The binary protocol saves bandwidth.

**Talking Point:** "To ensure our analytics UI was fast and responsive even with large datasets, we built a dedicated 'gRPC Streamer' service. It acts as a Backend-for-Frontend, querying ClickHouse and using gRPC's server-side streaming capabilities to pipe results back to the client as they become available. This non-blocking, streaming approach was critical for providing a good user experience and preventing the browser from freezing when handling millions of rows."

---

## System Design: Scheduled Report Delivery Service

**Q: How would you design a service that generates reports and delivers them to customer S3 buckets on custom schedules?**

**A:** The key is to decouple the system into two main workflows: **Generation** and **Delivery**. This makes the architecture scalable, secure, and resilient.

Here’s a component-by-component breakdown:

1.  **Report Generation Workflow (Internal):**
    *   **Trigger:** An Airflow DAG scheduled to run once daily (e.g., `0 2 * * *`).
    *   **Compute:** The DAG triggers a Spark job that reads from our main Iceberg data warehouse.
    *   **Storage:** The Spark job generates the report files (e.g., CSV or Parquet) and saves them to a secure, internal S3 bucket with a well-defined key structure like `s3://our-reports/pending-delivery/dt=YYYY-MM-DD/customer_id=.../report.csv`.

2.  **Report Delivery Workflow (Customer-Facing):**
    *   **Configuration:** A DynamoDB table stores each customer's delivery preferences: `(customer_id, delivery_schedule_cron, destination_s3_path, customer_iam_role_arn)`.
    *   **Scheduler:** We use **Amazon EventBridge Scheduler**. When a customer configures their schedule, our application creates a corresponding schedule in EventBridge. This is a highly scalable, serverless approach that avoids polling.
    *   **Executor:** The EventBridge schedule's target is a **Lambda function**. The payload contains the `customer_id`.
    *   **Security & Delivery:** The Lambda function:
        1.  Fetches the delivery configuration from DynamoDB.
        2.  Constructs the source S3 path for today's report.
        3.  **Assumes the cross-account IAM role** (`customer_iam_role_arn`) provided by the customer. This gives our Lambda temporary, limited permissions to write to their bucket.
        4.  Performs an `s3:CopyObject` from our internal bucket to their destination bucket.
    *   **Auditing:** The Lambda logs the outcome (success/failure) to CloudWatch and writes a record to an audit table for tracking and troubleshooting.

#### Architectural Diagram (Flow)

```
[Airflow DAG (Daily)] -> [Spark Job] -> [Saves to s3://our-reports]

[EventBridge Scheduler (Customer Cron)] --triggers--> [Delivery Lambda]
                                                         |
                                                         v
                                          [1. Reads from DynamoDB Config]
                                          [2. Assumes Customer IAM Role]
                                          [3. Copies from s3://our-reports to s3://customer-bucket]
                                          [4. Writes to Audit Log]
```

**Talking Point:** "I would design a decoupled system. A daily Airflow DAG would handle report generation, placing files in an internal S3 bucket. For delivery, I'd use Amazon EventBridge Scheduler to trigger a Lambda function based on each customer's unique schedule stored in DynamoDB. The Lambda would then assume a cross-account IAM role provided by the customer to securely copy the report from our bucket to theirs. This architecture is serverless, scalable, and follows security best practices for cross-account access."

**Q2: How would you optimize the cost of this report generation? Can you use spot instances?**

**A:** Yes, and this is a critical optimization. The report generation Spark job is likely to be the most expensive part of this entire workflow. Using spot instances via AWS Glue's **Flex execution class** is the best way to manage this cost.

However, it comes with a crucial trade-off: **cost vs. reliability**.

*   **Benefit:** Glue's Flex execution can reduce job costs by up to 90%. For a large-scale daily job, this is a massive operational saving.
*   **Risk:** Spot instances can be reclaimed by AWS at any time. This means the job can be interrupted, leading to longer and unpredictable run times.

This is why the choice of architectural pattern is so important:

*   **The "Generate Daily" (Batch) Pattern is PERFECT for Spot:** This job is non-urgent and runs in a large, off-peak window. If it gets interrupted and takes an extra hour to complete, it doesn't impact the business. We get huge cost savings with minimal risk to the SLA.

*   **The "Just-in-Time" (On-Demand) Pattern is TERRIBLE for Spot:** This job has a tight, customer-facing SLA (e.g., 'deliver in 15 minutes'). The risk of a spot interruption causing a missed deadline is unacceptable. These jobs **must** run on standard, on-demand instances to guarantee performance.

**Talking Point:** "To optimize the cost of our report generation, I would absolutely use AWS Glue's Flex execution to leverage spot instances. This is a perfect fit for our daily batch generation model, as the workload is fault-tolerant and not time-sensitive, allowing us to absorb potential interruptions in exchange for significant cost savings. However, I would explicitly advise against using spot for any 'just-in-time' or on-demand report generation, as the risk of missing a tight SLA would outweigh the cost benefits."
