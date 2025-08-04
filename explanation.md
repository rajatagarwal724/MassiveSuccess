# LeetCode 30: Substring with Concatenation of All Words

## Problem Statement

You are given:
- A string `s`  
- An array of strings `words` where all strings have the same length

**Goal**: Find all starting indices of substrings in `s` that are formed by concatenating all words from `words` in any order (each word used exactly once).

## Examples

### Example 1
```
Input: s = "barfoothefoobarman", words = ["foo","bar"]
Output: [0,9]

Explanation:
- Length of each word: 3
- Number of words: 2  
- Total concatenation length: 6

Starting at index 0: "barfoo" = "bar" + "foo" ✓
Starting at index 9: "foobar" = "foo" + "bar" ✓
```

### Example 2
```
Input: s = "barfoofoobarthefoobarman", words = ["bar","foo","the"]  
Output: [6,9,12]

Explanation:
- Length of each word: 3
- Number of words: 3
- Total concatenation length: 9

Starting at index 6: "foobarthe" = "foo" + "bar" + "the" ✓
Starting at index 9: "barthefoo" = "bar" + "the" + "foo" ✓  
Starting at index 12: "thefoobar" = "the" + "foo" + "bar" ✓
```

## Algorithm Approaches

### Approach 1: Brute Force with HashMap

**Algorithm**:
1. For each possible starting position in `s`
2. Extract substring of length `len(words) * len(words[0])`
3. Split this substring into chunks of `len(words[0])`
4. Check if these chunks form exactly the words in `words`

**Time Complexity**: O(n × m × k)
- n = length of string s
- m = number of words  
- k = length of each word

**Space Complexity**: O(m × k)

```python
def findSubstring_bruteforce(self, s: str, words: List[str]) -> List[int]:
    if not s or not words:
        return []
    
    word_len = len(words[0])
    word_count = len(words)
    total_len = word_len * word_count
    result = []
    
    # Count frequency of each word
    word_freq = Counter(words)
    
    # Try each possible starting position
    for i in range(len(s) - total_len + 1):
        seen = defaultdict(int)
        j = 0
        
        # Check each word-sized chunk
        while j < word_count:
            word = s[i + j * word_len:i + (j + 1) * word_len]
            seen[word] += 1
            
            if seen[word] > word_freq[word]:
                break
                
            j += 1
        
        if j == word_count:
            result.append(i)
    
    return result
```

### Approach 2: Optimized Sliding Window

**Key Insight**: We don't need to check every position. We only need to check positions that differ by `word_len`.

**Algorithm**:
1. For each offset `i` in `[0, word_len-1]`
2. Use sliding window starting from position `i`
3. Move window by `word_len` each time
4. Maintain current word frequencies in the window

**Time Complexity**: O(n × k) where n = len(s), k = len(words[0])
**Space Complexity**: O(m × k) where m = len(words)

```python
def findSubstring_sliding_window(self, s: str, words: List[str]) -> List[int]:
    if not s or not words:
        return []
    
    word_len = len(words[0])
    word_count = len(words)
    result = []
    word_freq = Counter(words)
    
    # We only need to start from positions 0, 1, 2, ..., word_len-1
    for i in range(word_len):
        left = i
        right = i
        curr_count = 0
        curr_freq = defaultdict(int)
        
        while right + word_len <= len(s):
            # Get the word at right pointer
            word = s[right:right + word_len]
            right += word_len
            
            if word in word_freq:
                curr_freq[word] += 1
                curr_count += 1
                
                # If we have too many of this word, shrink window from left
                while curr_freq[word] > word_freq[word]:
                    left_word = s[left:left + word_len]
                    curr_freq[left_word] -= 1
                    curr_count -= 1
                    left += word_len
                
                # If we have exactly the right number of words
                if curr_count == word_count:
                    result.append(left)
                    
                    # Move left pointer to find next potential match
                    left_word = s[left:left + word_len]
                    curr_freq[left_word] -= 1
                    curr_count -= 1
                    left += word_len
            
            else:
                # Reset everything if we encounter a word not in our list
                curr_freq.clear()
                curr_count = 0
                left = right
    
    return result
```

## Step-by-Step Example

Let's trace through Example 1: `s = "barfoothefoobarman"`, `words = ["foo","bar"]`

**Setup**:
- word_len = 3
- word_count = 2  
- word_freq = {"foo": 1, "bar": 1}

**Sliding Window with offset 0 (positions 0, 3, 6, 9, 12, 15)**:

1. **Position 0**: word = "bar"
   - curr_freq = {"bar": 1}, curr_count = 1
   - Need 2 words total

2. **Position 3**: word = "foo"  
   - curr_freq = {"bar": 1, "foo": 1}, curr_count = 2
   - curr_count == word_count, so add left=0 to result: **[0]**
   - Remove left word "bar": curr_freq = {"foo": 1}, curr_count = 1, left = 3

3. **Position 6**: word = "the"
   - "the" not in word_freq, reset everything: left = 9

4. **Position 9**: word = "foo"
   - curr_freq = {"foo": 1}, curr_count = 1

5. **Position 12**: word = "bar"
   - curr_freq = {"foo": 1, "bar": 1}, curr_count = 2  
   - curr_count == word_count, so add left=9 to result: **[0, 9]**

**Final Result**: [0, 9]

## Edge Cases

1. **Empty inputs**: Return empty list
2. **Repeated words in `words`**: Use frequency counting
3. **No valid concatenations**: Return empty list
4. **String shorter than total length**: Return empty list

## Why Sliding Window is More Efficient

- **Brute Force**: Checks every possible starting position
- **Sliding Window**: Only checks positions that are multiples of word_len apart
- **Reuse**: Sliding window reuses previous computations instead of starting fresh each time

The sliding window approach reduces the time complexity from O(n × m × k) to O(n × k), which is a significant improvement when the number of words (m) is large. 