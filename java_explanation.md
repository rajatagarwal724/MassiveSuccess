# LeetCode 30: Substring with Concatenation of All Words - Java Implementation

## Overview

This document provides a comprehensive explanation of the Java solution for LeetCode problem #30, including two different approaches with detailed analysis.

## Problem Recap

**Input**: 
- `String s` - the main string to search in
- `String[] words` - array of words to concatenate (all same length)

**Output**: 
- `List<Integer>` - starting indices of valid concatenated substrings

## Java-Specific Implementation Details

### Key Data Structures Used

1. **`HashMap<String, Integer>`** - For counting word frequencies
2. **`ArrayList<Integer>`** - For storing results
3. **`String.substring(start, end)`** - For efficient string slicing

### Approach 1: Brute Force Implementation

```java
public List<Integer> findSubstringBruteForce(String s, String[] words) {
    List<Integer> result = new ArrayList<>();
    if (s == null || s.isEmpty() || words == null || words.length == 0) {
        return result;
    }
    
    int wordLen = words[0].length();
    int wordCount = words.length;
    int totalLen = wordLen * wordCount;
    
    // Count frequency of each word
    Map<String, Integer> wordFreq = new HashMap<>();
    for (String word : words) {
        wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
    }
    
    // Try each possible starting position
    for (int i = 0; i <= s.length() - totalLen; i++) {
        Map<String, Integer> seen = new HashMap<>();
        int j = 0;
        
        // Check each word-sized chunk
        while (j < wordCount) {
            String word = s.substring(i + j * wordLen, i + (j + 1) * wordLen);
            seen.put(word, seen.getOrDefault(word, 0) + 1);
            
            // Early termination if word frequency exceeded
            if (seen.get(word) > wordFreq.getOrDefault(word, 0)) {
                break;
            }
            j++;
        }
        
        // Valid concatenation found
        if (j == wordCount) {
            result.add(i);
        }
    }
    
    return result;
}
```

**Time Complexity**: O(n × m × k)
- `n` = length of string s
- `m` = number of words  
- `k` = length of each word

**Space Complexity**: O(m × k)

### Approach 2: Optimized Sliding Window

```java
public List<Integer> findSubstringSlidingWindow(String s, String[] words) {
    List<Integer> result = new ArrayList<>();
    if (s == null || s.isEmpty() || words == null || words.length == 0) {
        return result;
    }
    
    int wordLen = words[0].length();
    int wordCount = words.length;
    
    // Count frequency of each word
    Map<String, Integer> wordFreq = new HashMap<>();
    for (String word : words) {
        wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
    }
    
    // Only check positions 0, 1, 2, ..., wordLen-1
    for (int i = 0; i < wordLen; i++) {
        int left = i;
        int right = i;
        int currCount = 0;
        Map<String, Integer> currFreq = new HashMap<>();
        
        while (right + wordLen <= s.length()) {
            String word = s.substring(right, right + wordLen);
            right += wordLen;
            
            if (wordFreq.containsKey(word)) {
                currFreq.put(word, currFreq.getOrDefault(word, 0) + 1);
                currCount++;
                
                // Shrink window if too many occurrences
                while (currFreq.get(word) > wordFreq.get(word)) {
                    String leftWord = s.substring(left, left + wordLen);
                    currFreq.put(leftWord, currFreq.get(leftWord) - 1);
                    currCount--;
                    left += wordLen;
                }
                
                // Valid concatenation found
                if (currCount == wordCount) {
                    result.add(left);
                    
                    // Slide window forward
                    String leftWord = s.substring(left, left + wordLen);
                    currFreq.put(leftWord, currFreq.get(leftWord) - 1);
                    currCount--;
                    left += wordLen;
                }
            } else {
                // Reset on invalid word
                currFreq.clear();
                currCount = 0;
                left = right;
            }
        }
    }
    
    return result;
}
```

**Time Complexity**: O(n × k)
**Space Complexity**: O(m × k)

## Step-by-Step Example Walkthrough

Let's trace through the sliding window approach with:
- `s = "barfoothefoobarman"`
- `words = ["foo", "bar"]`

**Setup**:
- `wordLen = 3`
- `wordCount = 2`
- `wordFreq = {"foo": 1, "bar": 1}`

**Iteration with offset i = 0** (checking positions 0, 3, 6, 9, 12, 15):

| Step | Position | Word  | Action | currFreq | currCount | Result |
|------|----------|-------|--------|----------|-----------|---------|
| 1 | 0 | "bar" | Add to window | {"bar": 1} | 1 | - |
| 2 | 3 | "foo" | Add to window | {"bar": 1, "foo": 1} | 2 | Add 0 to result |
| 3 | 3 | - | Slide window | {"foo": 1} | 1 | left = 3 |
| 4 | 6 | "the" | Reset (invalid) | {} | 0 | left = 9 |
| 5 | 9 | "foo" | Add to window | {"foo": 1} | 1 | - |
| 6 | 12 | "bar" | Add to window | {"foo": 1, "bar": 1} | 2 | Add 9 to result |

**Final Result**: `[0, 9]`

## Java-Specific Optimizations

### 1. HashMap Operations
```java
// Efficient frequency counting
wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);

// Safe access with default value
if (seen.get(word) > wordFreq.getOrDefault(word, 0))
```

### 2. String Substring Operations
```java
// Efficient substring extraction (O(k) where k is substring length)
String word = s.substring(right, right + wordLen);
```

### 3. Early Termination
```java
// Break early if invalid word found
if (seen.get(word) > wordFreq.getOrDefault(word, 0)) {
    break;
}
```

## Memory Management in Java

- **HashMap resizing**: HashMaps automatically resize when load factor exceeds 0.75
- **String immutability**: `substring()` operations create new String objects
- **ArrayList growth**: ArrayList doubles in size when capacity is exceeded

## Edge Cases Handled

1. **Null/Empty inputs**:
   ```java
   if (s == null || s.isEmpty() || words == null || words.length == 0) {
       return result;
   }
   ```

2. **Repeated words**:
   ```java
   // Use frequency counting instead of Set
   Map<String, Integer> wordFreq = new HashMap<>();
   ```

3. **Boundary conditions**:
   ```java
   for (int i = 0; i <= s.length() - totalLen; i++)
   while (right + wordLen <= s.length())
   ```

## Performance Comparison

| Approach | Time | Space | Best Use Case |
|----------|------|-------|---------------|
| Brute Force | O(n×m×k) | O(m×k) | Small inputs, simple to understand |
| Sliding Window | O(n×k) | O(m×k) | Large inputs, optimal performance |

## Testing Strategy

The solution includes comprehensive test cases:

```java
// Standard cases
testCase("barfoothefoobarman", ["foo", "bar"], [0, 9]);

// Edge cases  
testCase("wordgoodgoodgoodbestword", ["word", "good", "best", "word"], []);

// Repeated words
testCase("goodgoodbestword", ["word", "good", "best", "good"], [0]);
```

## Conclusion

The Java implementation provides:
- **Clean, readable code** with proper error handling
- **Two approaches** for different performance requirements  
- **Comprehensive testing** to ensure correctness
- **Optimal time complexity** O(n×k) with sliding window approach

The sliding window technique reduces the complexity significantly compared to brute force, making it suitable for large inputs while maintaining code clarity. 