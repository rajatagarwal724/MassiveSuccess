# LeetCode 30: Substring with Concatenation of All Words

This repository contains complete solutions for LeetCode problem #30 in both Python and Java with detailed explanations and multiple approaches.

## Files

### Python Solution
- `substring_concatenation.py` - Complete Python solution with two approaches and comprehensive test cases
- `explanation.md` - Detailed explanation of the problem, algorithms, and complexity analysis

### Java Solution  
- `SubstringConcatenation.java` - Complete Java solution with two approaches and comprehensive test cases
- `java_explanation.md` - Java-specific implementation details and analysis

### Documentation
- `README.md` - This file

## Problem Overview

Find all starting indices of substrings in a given string `s` that are formed by concatenating all words from a given array `words` in any order.

**Example**: 
- Input: `s = "barfoothefoobarman"`, `words = ["foo","bar"]`
- Output: `[0,9]` (substrings "barfoo" and "foobar")

## Solution Approaches

### 1. Brute Force (O(n×m×k))
- Check every possible starting position
- Validate each substring by splitting into word-sized chunks

### 2. Sliding Window (O(n×k)) - Optimized
- Only check positions that are multiples of word length apart
- Use sliding window technique to reuse computations
- Significantly faster for large inputs

## Running the Code

### Python
```bash
python substring_concatenation.py
```

### Java
```bash
javac SubstringConcatenation.java
java SubstringConcatenation
```

Both will run all test cases and compare both approaches.

## Key Insights

- All words in the `words` array have the same length
- We need to find concatenations of ALL words (each used exactly once)
- Words can appear in any order in the valid substring
- Sliding window optimization reduces time complexity significantly

## Test Results

### Python Output
```
Test 1: [0, 9] == [0, 9] -> True
Test 2: [] == [] -> True  
Test 3: [6, 9, 12] == [6, 9, 12] -> True
Test 4: [0] == [0] -> True
Test 5: [] == [] -> True

Comparing both approaches:
Test 1: Brute force [0, 9] == Optimized [0, 9] -> True
Test 2: Brute force [] == Optimized [] -> True
Test 3: Brute force [6, 9, 12] == Optimized [6, 9, 12] -> True
```

### Java Output
```
Test 1: [0, 9] == [0, 9] -> true
Test 2: [] == [] -> true
Test 3: [6, 9, 12] == [6, 9, 12] -> true
Test 4: [0] == [0] -> true
Test 5: [] == [] -> true

Comparing both approaches:
Test 1: Brute force [0, 9] == Optimized [0, 9] -> true
Test 2: Brute force [] == Optimized [] -> true
Test 3: Brute force [6, 9, 12] == Optimized [6, 9, 12] -> true
```

Both languages' brute force and optimized approaches produce identical results, confirming correctness.

## Language Comparison

| Feature | Python | Java |
|---------|--------|------|
| **Data Structures** | `Counter`, `defaultdict` | `HashMap<String, Integer>` |
| **String Slicing** | `s[start:end]` | `s.substring(start, end)` |
| **Collections** | `list` | `ArrayList<Integer>` |
| **Null Safety** | `if not s or not words:` | `if (s == null \|\| s.isEmpty())` |
| **HashMap Default** | `defaultdict(int)` | `map.getOrDefault(key, 0)` |
| **Code Style** | More concise, pythonic | More verbose, explicit types |
| **Performance** | Slightly slower due to interpreted nature | Faster execution after compilation |

Both implementations use identical algorithms and achieve the same O(n×k) time complexity with the sliding window approach.