# Facts and Dimensions in Data Engineering

In data engineering, **facts** and **dimensions** are the fundamental building blocks of a dimensional model, which is the standard design paradigm for data warehouses. They work together to give business data meaning and make it easy to analyze.

Here’s a simple breakdown:

### Facts

**What they are:** Facts are the **quantitative measurements** of a business event. They are always numeric and represent what you are trying to measure or analyze.

*   **Think:** "What happened?" or "How much/many?"
*   **Examples:**
    *   `sales_amount`
    *   `quantity_sold`
    *   `profit_margin`
    *   `clicks`
    *   `login_count`
*   **Key Property:** Facts are **aggregatable**. You can perform mathematical operations on them, like `SUM()`, `COUNT()`, `AVG()`, etc. For example, you can calculate the "total sales amount" or the "average number of clicks."

A **Fact Table** is the central table in a dimensional model (like a star schema) and contains these numeric facts.

### Dimensions

**What they are:** Dimensions are the **descriptive, contextual attributes** that give meaning to the facts. They describe the "who, what, where, when, why" behind a business event.

*   **Think:** "Who did it?", "What was involved?", "Where did it happen?", "When did it happen?"
*   **Examples:**
    *   **Product Dimension:** `product_name`, `category`, `brand`
    *   **Customer Dimension:** `customer_name`, `age_group`, `city`
    *   **Time Dimension:** `date`, `month`, `year`, `day_of_week`
    *   **Store Dimension:** `store_name`, `region`, `country`
*   **Key Property:** Dimensions are used to **filter, group, and label** the facts. You use them in the `GROUP BY`, `WHERE`, and `JOIN` clauses of a SQL query.

A **Dimension Table** contains these descriptive attributes.

---

### Analogy: A Retail Sale

Imagine a single transaction at a store:
*A customer named **John Doe** bought **2 cartons of milk** for **$6.00** at the **Seattle** store on **August 12, 2025**.*

*   **Facts (The Measurements):**
    *   `quantity_sold`: 2
    *   `total_price`: 6.00
*   **Dimensions (The Context):**
    *   **Customer:** John Doe
    *   **Product:** Milk
    *   **Store:** Seattle
    *   **Time:** August 12, 2025

With this model, you can ask complex business questions by combining facts and dimensions:
*   "What were the **total sales** (`Fact`) for the **Seattle store** (`Dimension`) in **August** (`Dimension`)?"
*   "Show me the **average quantity sold** (`Fact`) per **product category** (`Dimension`)."

### Summary Table

| Feature | Fact | Dimension |
| :--- | :--- | :--- |
| **Purpose** | Measures a business process | Provides context to a measurement |
| **Data Type** | Numeric (integer, decimal) | Text, date, boolean, categorical |
| **Nature** | Quantitative, transactional | Qualitative, descriptive, master data |
| **Function** | What you aggregate (SUM, AVG) | What you filter or group by |
| **Example** | `sales_amount`, `units_sold` | `product_name`, `customer_city` |
| **Table Type**| Fact Table (center of schema) | Dimension Table (surrounds fact table)|
