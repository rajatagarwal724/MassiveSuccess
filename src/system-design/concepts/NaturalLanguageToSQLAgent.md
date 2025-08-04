# System Design: Natural Language to SQL AI Agent

**Author:** Cascade
**Version:** 1.0
**Date:** 2025-07-25

---

## 1. Introduction & Goals

This document outlines the system design for an AI-powered agent capable of translating natural language queries into executable SQL. The primary goal is to empower non-technical users to perform complex data analysis on a data warehouse without writing any code, effectively democratizing data access.

### 1.1. Goals
- **Accessibility:** Allow users to query data using plain English.
- **Accuracy:** Generate correct and semantically valid SQL queries.
- **Security:** Prevent malicious or harmful queries (e.g., SQL injection, accidental data deletion).
- **Performance:** Execute queries efficiently and return results in a timely manner.
- **Scalability:** Handle a large number of users and a complex, evolving data warehouse schema.

### 1.2. Non-Goals
- This system is not designed to perform database administration tasks (e.g., creating tables, managing users).
- It will not handle real-time transactional (OLTP) workloads; it is designed for analytical (OLAP) queries.

---

## 2. Requirements

### 2.1. Functional Requirements
- **Natural Language Input:** Users must be able to submit queries in plain text.
- **SQL Generation:** The system must generate syntactically correct SQL (for a specific dialect like PostgreSQL, Snowflake SQL, etc.).
- **Query Execution:** The system must execute the generated query against the target data warehouse.
- **Result Visualization:** Display results in a user-friendly format (e.g., tables, charts, natural language summaries).
- **Clarification Dialog:** If a query is ambiguous, the agent should ask clarifying questions.
- **Schema Awareness:** The agent must understand the tables, columns, and relationships in the data warehouse.

### 2.2. Non-Functional Requirements
- **Low Latency:** Simple queries should return results within seconds.
- **High Availability:** The system should be highly available and fault-tolerant.
- **Data Security:** Sensitive data must be protected, and user access controls must be enforced.
- **Auditability:** All queries and user interactions should be logged for security and debugging.

---

## 3. High-Level Architecture

The system is designed as a microservices architecture, where each component is responsible for a specific part of the request lifecycle. This promotes separation of concerns, independent scaling, and maintainability.

The data flows as follows:
1.  The **User** submits a natural language query (e.g., "What were our sales in California last month?") through the **Natural Language Interface** (e.g., a web app or a chat bot).
2.  The query is sent to the **Core LLM Engine**. 
3.  The LLM Engine first queries the **Metadata Service** to fetch the relevant database schema information (tables, columns, keys) that might be related to the user's query.
4.  The LLM Engine combines the user's query with the retrieved schema information into a carefully crafted prompt and generates the corresponding SQL query.
5.  The generated SQL is passed to the **Query Validation & Security Service**. This service checks for syntax errors, enforces security rules (like preventing `DROP TABLE`), and validates that the query adheres to governance policies (e.g., the user is allowed to see sales data).
6.  Once validated, the SQL query is handed to the **Query Execution Service**, which runs it against the target **Data Warehouse**.
7.  The raw results are returned to the **Result Presentation & Caching Layer**. This layer formats the data into a user-friendly format (table, chart, etc.) and caches the result to serve identical future requests instantly.
8.  Finally, the formatted result is sent back to the user via the interface.

### System Diagram

```
+-----------------------+
|         User          |
+-----------+-----------+
            |
            v
+-----------------------+
| Natural Language      |
| Interface (Frontend)  |
+-----------+-----------+
            | (1. NL Query)
            v
+-----------------------+
|   Core LLM Engine     | --(3. Get Schema)--> +--------------------+
| (Text-to-SQL Gen)     |                      |  Metadata Service  |
+-----------+-----------+ <-- (4. Return Schema) -- |   & Schema Store   |
            |                                      +--------------------+
            | (5. Generated SQL)
            v
+-----------------------+
| Query Validation &    |
|   Security Service    |
+-----------+-----------+
            | (6. Validated SQL)
            v
+-----------------------+
| Query Execution       | --(7. Execute Query)--> +--------------------+
|       Service         |                         |   Data Warehouse   |
+-----------+-----------+ <-- (8. Raw Data) ------- +--------------------+
            | (9. Formatted Result)
            v
+-----------------------+
| Result Presentation   |
|  & Caching Layer      |
+-----------+-----------+
            |
            v
+-----------------------+
|         User          |
+-----------------------+
```

---

## 4. Detailed Component Design

### 4.1. Natural Language Interface (Frontend/Chatbot)

- **Responsibility:** Provide a clean and intuitive interface for users to input their queries. It should also handle the rendering of results, including tables, charts, and text summaries. For ambiguous queries, this interface will manage the clarification dialogue with the user.
- **Technology Stack:**
  - **Frontend Framework:** React or Vue.js for a responsive web application.
  - **API Communication:** REST or GraphQL to communicate with the backend services.
  - **Real-time:** WebSockets for features like streaming results or real-time clarification dialogues.
- **Key Considerations:**
  - **User Experience:** The interface must be simple. An autocomplete feature suggesting common questions or column names can be very helpful.
  - **State Management:** Needs to manage the state of conversations, including previous questions and clarifications.

### 4.2. Metadata Service & Schema Store (DataHub Integration)

- **Responsibility:** This service leverages **DataHub** as the primary metadata management platform. DataHub serves as the source of truth for schema, business context, data lineage, and governance policies. Our service acts as an intelligent interface between the LLM and DataHub's rich metadata.
- **Technology Stack:**
  - **DataHub:** The core metadata platform providing automated schema discovery, business glossary, and data governance
  - **DataHub APIs:** GraphQL and REST APIs to query metadata, relationships, and business context
  - **Caching Layer:** Redis for caching frequently accessed DataHub responses to reduce API latency
  - **Search Integration:** Elasticsearch (DataHub's built-in search) for semantic metadata discovery
- **Key Advantages with DataHub:**
  - **Automated Discovery:** DataHub automatically crawls data sources and maintains up-to-date schema information
  - **Rich Business Context:** Business glossary, column descriptions, and data steward annotations provide semantic understanding
  - **Data Lineage:** Understanding how data flows between tables helps the LLM make better join decisions
  - **Usage Analytics:** DataHub tracks which tables/columns are frequently used together, improving RAG retrieval
  - **Governance Integration:** Built-in access controls and data classification ensure secure query generation

### 4.3. Core LLM Engine (Text-to-SQL Generation)

- **Responsibility:** This is the core of the AI agent. It receives the user's natural language query and the relevant schema from the Metadata Service. It then constructs a detailed prompt for a Large Language Model (LLM) to generate the SQL query.
- **Technology Stack:**
  - **LLM Provider:** OpenAI (GPT-4), Anthropic (Claude 3), or a fine-tuned open-source model like Llama 3.
  - **Framework:** LangChain or a similar framework to manage the interaction with the LLM, including prompt engineering and chaining requests.
  - **Programming Language:** Python is the de-facto standard for AI/ML workloads.
- **Key Considerations:**
  - **Prompt Engineering:** This is critical for accuracy. The prompt must include the user query, relevant schema details, and few-shot examples of similar text-to-SQL conversions to guide the model.
  - **Tokenization and Context Window:** The schema information and query must fit within the LLM's context window. Techniques like schema pruning (only providing the most relevant tables/columns) are essential.
  - **Model Selection:** Choosing the right model is a trade-off between performance, cost, and accuracy. Fine-tuning a smaller model on the specific company's data can yield better results and lower costs than a generic, larger model.
#### 4.3.1. The Role of Retrieval-Augmented Generation (RAG)

The entire query generation process is fundamentally a **Retrieval-Augmented Generation (RAG)** pipeline. Instead of naively asking the LLM to generate SQL from a question, we first retrieve relevant context to ground the model and improve accuracy.

- **Retrieve:** The first step is always to retrieve context. In its basic form, this is the schema from the Metadata Service. In its advanced form, this involves using vector search.
- **Augment:** The retrieved context is then used to augment the user's original query, creating a rich, detailed prompt for the LLM.
- **Generate:** Only with this augmented prompt does the LLM generate the final SQL query.

**Advanced RAG Techniques:**
1.  **Vector-Based Retrieval:** We can convert all schema information (table/column names, descriptions) and even business glossary terms into vector embeddings and store them in a Vector DB (e.g., Pinecone, Weaviate). When a user asks a question, we search for the most semantically similar items to retrieve, which is far more powerful than simple keyword matching.
2.  **Few-Shot Example Retrieval:** A library of high-quality (NL Query, SQL) pairs can be vectorized. The system can retrieve the most relevant examples and include them in the prompt (few-shot learning), significantly improving the LLM's accuracy for complex queries.
3.  **DataHub Knowledge Graph Retrieval:** Leverage DataHub's knowledge graph to retrieve not just schema information, but also business relationships, data lineage, and usage patterns. This provides much richer context than traditional metadata stores.
4.  **Unstructured Data Retrieval:** The RAG pipeline can be extended to retrieve information from unstructured sources like company wikis or documentation to resolve business-specific jargon or complex metric definitions.

#### 4.3.2. Token Management & Context Optimization

One of the biggest challenges in enterprise text-to-SQL systems is managing token limits when dealing with massive schemas. Here are the key strategies:

**1. Intelligent Schema Pruning**
- Use vector similarity search to identify only schema elements relevant to the user's query
- Score tables and columns based on semantic similarity to the question
- Include only high-scoring elements in the context, dramatically reducing token usage

**2. Hierarchical Context Building**
- Priority 1: Tables/columns directly mentioned in the query
- Priority 2: Tables with foreign key relationships to Priority 1 tables
- Priority 3: Tables with similar business context (using DataHub's knowledge graph)
- Priority 4: Frequently co-used tables (from usage analytics)

**3. Dynamic Context Budgeting**
- Simple queries (single table): 500-1000 tokens for schema
- Complex queries (joins, aggregations): 2000-4000 tokens
- Adjust based on query complexity and available context window

**4. Schema Summarization**
- Convert verbose CREATE TABLE statements to condensed formats
- Focus on essential information: table names, key columns, relationships, data types
- Use abbreviated column descriptions instead of full business glossary entries

**5. Multi-Step RAG Pipeline**
- Step 1: Use a smaller model to identify relevant tables from table names only
- Step 2: Retrieve detailed schema for identified tables
- Step 3: Generate SQL with focused, relevant context
- This approach can reduce token usage by 70-80% while maintaining accuracy

**6. Context Compression**
- Use specialized compression models to summarize large schema contexts
- Maintain essential information while reducing token count
- Particularly useful for legacy systems with hundreds of tables

### 4.4. Query Validation & Security Service

- **Responsibility:** This service acts as a critical gatekeeper. It inspects the LLM-generated SQL to ensure it's safe and compliant before execution. Its primary jobs are to prevent malicious attacks and enforce data governance rules.
- **Technology Stack:**
  - **SQL Parsing Library:** A robust SQL parser (e.g., `sqlparse` in Python) to deconstruct the query into its abstract syntax tree (AST).
  - **Rule Engine:** A simple, custom-built rule engine or an off-the-shelf one like Drools to enforce security policies.
- **Key Validations:**
  - **Syntax Check:** Ensure the SQL is valid for the target dialect.
  - **Denylist Operations:** Block destructive commands like `DROP`, `DELETE`, `UPDATE`, `GRANT`, etc.
  - **Query Complexity Analysis:** Reject queries that are too complex (e.g., too many joins) to prevent resource exhaustion on the data warehouse.
  - **RBAC Enforcement:** Check against a user's permissions to ensure they are not trying to access tables or columns they are not authorized to see.

### 4.5. Query Execution Service

- **Responsibility:** This service manages the connection to the data warehouse and executes the validated SQL queries. It's designed to handle long-running queries asynchronously without blocking the entire system.
- **Technology Stack:**
  - **Database Connectors:** Standard database drivers (e.g., JDBC, ODBC) for the target data warehouse (Snowflake, BigQuery, Redshift, etc.).
  - **Task Queue:** A message queue like RabbitMQ or Kafka, coupled with workers (e.g., Celery in Python), to manage a queue of queries to be executed. This allows the system to handle many concurrent requests and long-running jobs gracefully.
- **Key Considerations:**
  - **Connection Pooling:** Maintain a pool of database connections to reduce the overhead of establishing a new connection for every query.
  - **Asynchronous Execution:** For queries expected to take a long time, the service should immediately return a `task_id` to the user. The frontend can then poll for the result using this ID.
  - **Resource Management:** Implement timeouts and resource limits to kill queries that run for too long or consume too many resources.

### 4.6. Result Presentation & Caching Layer

- **Responsibility:** Once the Query Execution Service returns the raw data, this layer transforms it into a useful format for the user. It also implements a caching strategy to avoid re-running expensive queries.
- **Technology Stack:**
  - **API Framework:** A standard backend framework like FastAPI (Python), Express.js (Node.js), or Spring Boot (Java).
  - **Caching Store:** Redis or Memcached is ideal for caching query results.
  - **Data Visualization:** The backend can either send raw data for a frontend library (like D3.js or Chart.js) to render, or generate charts on the server side.
- **Key Considerations:**
  - **Cache Key Strategy:** The cache key should be a hash of the canonical SQL query. A simple change in the natural language prompt that results in the same SQL should hit the cache.
  - **Cache Invalidation:** Decide on a TTL (Time-To-Live) for the cache. For data that changes frequently, the TTL should be short. For historical data, it can be much longer.
  - **Natural Language Summaries:** A secondary call to the LLM can be made here to summarize the tabular results in plain English (e.g., "Sales increased by 15% in Q2 compared to Q1.").

---

## 5. Data Models & Schema

The Metadata Service requires a robust schema to store not just the technical details of the data warehouse, but also the semantic business context.

### Metadata DB Schema

**Table: `tables`**
- `table_id` (PK)
- `table_name`
- `schema_name`
- `description` (Business-friendly description of the table)
- `last_updated`

**Table: `columns`**
- `column_id` (PK)
- `table_id` (FK to `tables`)
- `column_name`
- `data_type`
- `description` (Business-friendly description)
- `is_pii` (Boolean, marks Personally Identifiable Information)
- `tags` (e.g., "financial", "user_metric")

**Table: `relationships`**
- `relationship_id` (PK)
- `primary_table_id` (FK to `tables`)
- `primary_column_id` (FK to `columns`)
- `foreign_table_id` (FK to `tables`)
- `foreign_column_id` (FK to `columns`)
- `relationship_type` (e.g., 'one-to-many')

**Table: `business_glossary`**
- `term_id` (PK)
- `term_name` (e.g., "ARR", "Active User")
- `definition` (The business definition of the term)
- `formula` (Optional: how the metric is calculated in pseudo-code)

---

## 6. Scalability and Performance

### 6.1. Scaling the LLM Service
- **Load Balancing:** Use a load balancer to distribute requests across multiple instances of the Core LLM Engine.
- **Model Optimization:** For very high-volume use cases, consider using smaller, fine-tuned models which are faster and cheaper than large, general-purpose models. Techniques like quantization and knowledge distillation can be applied.
- **Batching:** If multiple requests arrive simultaneously, they can be batched into a single call to the LLM provider, reducing API overhead.

### 6.2. Caching Strategies
- **Metadata Cache:** The Metadata Service should heavily cache schema information in memory (e.g., Redis) to provide near-instant access to the LLM Engine.
- **Result Cache:** The Result Presentation Layer will cache the results of executed queries. The cache key should be a hash of the final, canonical SQL query. This ensures that different natural language questions that produce the same SQL query will benefit from the cache.
- **Semantic Cache:** A more advanced technique where we cache the *intent* of the user's question. If a new question is semantically similar to a cached one, we can serve the cached result, potentially after a quick validation with the LLM.

### 6.3. Asynchronous Query Execution
- The use of a task queue (like Celery with RabbitMQ/Redis) is fundamental to the system's scalability and user experience. When a query is submitted, it's placed on the queue. A pool of worker processes picks up tasks from the queue and executes them. This prevents the main application thread from blocking and allows the system to handle many concurrent users and long-running queries without timing out.

---

## 7. Security and Governance

### 7.1. Preventing SQL Injection
- While the LLM generates the SQL, it's not immune to generating unsafe code if prompted maliciously. The **Query Validation Service** is the primary defense. By parsing the SQL into an AST and using a denylist of commands (`DROP`, `UPDATE`, etc.) and patterns (e.g., comment characters `--`), we can effectively block injection attempts.

### 7.2. Role-Based Access Control (RBAC)
- The system must integrate with the company's existing identity provider (e.g., Okta, Active Directory).
- Before executing a query, the Validation Service must check the user's roles and permissions against the tables and columns referenced in the SQL AST. If the query attempts to access a restricted resource, it must be rejected.

### 7.3. Data Masking and Anonymization
- For columns marked as PII (Personally Identifiable Information) in our metadata store, the system can apply automatic data masking at the presentation layer. For example, a user in the 'analyst' role might see a credit card number as `****-****-****-1234`, while a 'finance_admin' might see the full number.

### 7.4. Auditing and Logging
- Every action must be logged. This includes the original natural language query, the generated SQL, the user who made the request, and the result that was returned. These logs are critical for security audits, debugging failed queries, and creating datasets for fine-tuning the LLM in the future.

---

## 8. Key Challenges & Mitigations

### 8.1. Ambiguity in Natural Language
- **Challenge:** A user might ask for "top customers." This is ambiguous. Does it mean by revenue, order count, or tenure?
- **Mitigation:** Implement a clarification mechanism. The LLM can be prompted to recognize ambiguity. If detected, the agent will respond to the user with clarifying questions, like "Should I define 'top customers' by total spending or by number of orders?"

### 8.2. Handling Complex Schemas
- **Challenge:** Enterprise data warehouses can have thousands of tables. Including the entire schema in an LLM prompt is not feasible due to context window limits and cost.
- **Mitigation:** This is a primary use case for an advanced **RAG pipeline** combined with intelligent **token management**. We use vector-based retrieval to perform a semantic search over the schema metadata, followed by hierarchical context building and dynamic token budgeting. This allows us to identify and retrieve only the most relevant tables and columns for the user's specific query. The highly-focused, pruned schema is then passed to the LLM, making the process efficient, cost-effective, and more accurate while staying within token limits.

### 8.3. LLM Hallucination
- **Challenge:** The LLM might generate a syntactically correct but semantically nonsensical query, or invent columns that don't exist.
- **Mitigation:** 
  1. **Grounding:** The prompt must strongly "ground" the model by providing the exact schema and instructing it to only use the provided columns.
  2. **Validation:** The Query Validation Service can check if all tables and columns in the generated SQL actually exist in the schema.
  3. **User Feedback Loop:** Include a feature for users to flag incorrect queries. This feedback is invaluable for fine-tuning the model and improving its accuracy over time.

### 8.4. Cost Management (LLM API & Warehouse Costs)
- **Challenge:** Both LLM API calls and data warehouse query execution can be expensive.
- **Mitigation:**
  - **Aggressive Caching:** The result cache is the first line of defense against costs.
  - **Query Complexity Limits:** The Validation Service should reject overly complex queries that would be expensive to run.
  - **Model Tiering:** Use cheaper models for simple tasks (like identifying relevant schema) and reserve the most expensive, powerful models for the final SQL generation step.
  - **Budgeting and Alerts:** Implement monitoring on both API and warehouse usage with alerts for when costs exceed a certain threshold.

---

## 8.5. DataHub Integration Benefits

**DataHub as the Metadata Backbone:**
Integrating DataHub transforms our system from a simple text-to-SQL tool into an intelligent data discovery platform:

- **Semantic Understanding:** DataHub's business glossary and rich metadata enable the LLM to understand business context, not just technical schema
- **Automatic Schema Evolution:** As data warehouse schemas change, DataHub automatically updates our system without manual intervention
- **Data Discovery:** Users can ask questions like "What data do we have about customers?" and get comprehensive answers based on DataHub's catalog
- **Quality Awareness:** The system can warn users about data quality issues or suggest fresher alternatives based on DataHub's data quality metrics
- **Collaborative Intelligence:** DataHub's social features (comments, ratings, documentation) provide crowdsourced intelligence that improves query generation

**Implementation Approach:**
1. **DataHub as Primary Metadata Source:** Replace custom metadata ingestion with DataHub's automated discovery
2. **Enhanced RAG Pipeline:** Use DataHub's search APIs and knowledge graph for more intelligent metadata retrieval
3. **Governance Integration:** Leverage DataHub's access controls and data classification for secure query generation
4. **Feedback Loop:** Use query patterns and user feedback to enrich DataHub's metadata and improve future recommendations

---

## 9. Future Considerations

- **Support for Multiple Database Dialects:** Abstract the Query Execution and Validation services so that new SQL dialects (e.g., T-SQL, PL/SQL) can be added as plugins.
- **Proactive Insights and Suggestions:** Instead of just answering questions, the agent could analyze query patterns and proactively suggest interesting insights or related questions the user might want to ask.
- **Integration with BI Tools:** Allow the agent to be embedded within existing BI tools like Tableau or Power BI, enabling users to build dashboards using natural language.
- **Self-Healing Schema:** The agent could detect schema drift (e.g., a column was renamed) and suggest updates to the Metadata Store, or even attempt to fix queries automatically.
