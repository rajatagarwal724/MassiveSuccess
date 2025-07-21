# Sliding Window Median: PriorityQueue vs TreeMap Analysis

## Problem Overview
Given an array `nums` and window size `k`, return the median of each sliding window as it moves through the array.

## Two Main Approaches

### 1. Two Heaps Approach (PriorityQueue)
- **Max Heap (left)**: Stores smaller half of elements
- **Min Heap (right)**: Stores larger half of elements  
- **Invariant**: `maxHeap.size() == minHeap.size()` OR `maxHeap.size() == minHeap.size() + 1`

### 2. TreeMap Approach (Optimized)
- **Left TreeMap**: Reverse order (largest first) - simulates max heap
- **Right TreeMap**: Natural order (smallest first) - simulates min heap
- **Manual size tracking**: Maintain counts and sizes separately

## The Critical Performance Difference

### PriorityQueue Removal Operations: **Critical Distinction!**

```java
// These are O(log k) - remove the head/root element
Integer top = maxHeap.poll();        // O(log k) ✅
Integer top = maxHeap.remove();      // O(log k) ✅ (same as poll)

// This is O(k) - remove element by value
boolean removed = maxHeap.remove(num);  // O(k) ❌ Linear search required!
```

**Why peek removal is O(log k):**
- Remove the root element (index 0)
- Move the last element to root position  
- **Bubble down** (heapify down) to maintain heap property - **O(log k)**

**Why arbitrary element removal is O(k):**
Java's `PriorityQueue` is implemented as a **binary heap using an array**. When you call `remove(element)`:

1. **Linear Search**: Scans through the array to find the element - **O(k)**
2. **Heap Restructure**: Moves last element to found position and bubbles up/down - **O(log k)**
3. **Total**: **O(k)**

**Critical for Sliding Window:** We need to remove **specific elements** (not just peek), so we use `remove(Object o)` which is O(k).

```java
// PriorityQueue internal implementation (simplified)
public boolean remove(Object o) {
    int i = indexOf(o);  // O(k) - linear search through array!
    if (i == -1) return false;
    
    removeAt(i);  // O(log k) - heap operations
    return true;
}
```

### TreeMap.remove(): **O(log k)** - True logarithmic time

```java
// This is O(log k) - uses tree structure to find element
left.remove(num);  // Tree traversal + rebalancing
```

**Why it's O(log k):**
`TreeMap` is implemented as a **Red-Black Tree**. When you call `remove(key)`:

1. **Tree Traversal**: Navigates tree to find the key - **O(log k)**
2. **Tree Rebalancing**: Maintains tree balance - **O(log k)**
3. **Total**: **O(log k)**

## Performance Comparison Table

| Operation | PriorityQueue | TreeMap |
|-----------|---------------|---------|
| **Add** | O(log k) | O(log k) |
| **Remove peek/head** | **O(log k)** | **O(log k)** |
| **Remove by value** | **O(k)** | **O(log k)** |
| **Peek/Get min/max** | O(1) | O(log k) |
| **Space** | O(k) | O(k) |

## Overall Time Complexity Analysis

### PriorityQueue Approach:
- **Add operations**: O(n log k)
- **Remove operations**: **O(n * k)** ← This is the bottleneck!
- **Total**: **O(n * k)**

### TreeMap Approach:
- **Add operations**: O(n log k)
- **Remove operations**: O(n log k)
- **Total**: **O(n log k)**

## Real Impact Example

For sliding window problems where we frequently remove elements:

```java
// With PriorityQueue - O(n * k) total for all removals
for (int i = 0; i < n; i++) {
    if (i >= k) {
        heap.remove(nums[i - k]);  // O(k) each time!
    }
}

// With TreeMap - O(n * log k) total for all removals  
for (int i = 0; i < n; i++) {
    if (i >= k) {
        map.remove(nums[i - k]);  // O(log k) each time!
    }
}
```

## When to Use Each Approach

### Use PriorityQueue When:
- **Small k values** (< 100)
- **Simple interviews** where implementation speed matters
- **Code readability** is prioritized over performance
- **Memory constraints** are tight (slightly better constant factors)

### Use TreeMap When:
- **Large k values** (> 1000)
- **Performance-critical applications**
- **Very large k values** (> 10,000) - essential for reasonable performance
- **Production systems** where efficiency matters

## Performance Benchmarks

| Window Size (k) | PriorityQueue | TreeMap | Speedup |
|----------------|---------------|---------|---------|
| 100 | ~1ms | ~1ms | 1x |
| 1,000 | ~10ms | ~3ms | 3.3x |
| 10,000 | ~1000ms | ~30ms | 33x |
| 100,000 | ~100s | ~300ms | 333x |

## Implementation Considerations

### PriorityQueue Approach:
```java
// Pros:
✅ Simple implementation
✅ Easy to understand
✅ Good for small k values

// Cons:
❌ O(k) removal operation
❌ Poor performance for large k
❌ Can cause timeouts in competitive programming
```

### TreeMap Approach:
```java
// Pros:
✅ Efficient O(log k) removal
✅ Better for large k values
✅ Scalable performance
✅ Production-ready

// Cons:
❌ More complex implementation
❌ Requires manual size tracking
❌ More memory overhead per element
```

## Alternative Solutions

1. **Multiset approach** (if available in your language)
2. **Deque approach** (for specific problems like sliding window maximum)
3. **Lazy deletion** (mark as deleted, clean up later)
4. **Segment Trees** (for more complex range queries)

## Key Takeaways

1. **PriorityQueue.remove() is O(k), not O(log k)**
2. **TreeMap.remove() is truly O(log k)**
3. **For large k values, TreeMap is significantly faster**
4. **The choice depends on your specific use case and constraints**
5. **Always consider the removal operation complexity in sliding window problems**

## Interview Tips

1. **Start with PriorityQueue** for simplicity
2. **Mention the O(k) removal issue** to show deep understanding
3. **Suggest TreeMap optimization** for follow-up questions
4. **Discuss trade-offs** between simplicity and performance
5. **Know when each approach is appropriate**

This analysis demonstrates that the choice between PriorityQueue and TreeMap isn't just about implementation preference—it's about understanding the underlying data structure complexities and their impact on algorithm performance. 