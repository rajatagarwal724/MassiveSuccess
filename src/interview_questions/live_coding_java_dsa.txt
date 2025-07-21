# Java DSA String Manipulation Problems - Live Coding Preparation

## Overview
This section covers common string manipulation problems that may appear in a live coding round. For each problem type, I've included example problems, solutions with explanations, optimization techniques, and time/space complexity analysis.

## Common String Manipulation Problem Types

### 1. Character Replacement/Substitution

**Example Problem:** Replace all vowels in a string with '*'

```java
/**
 * Solution: Replace all vowels in a string with '*'
 * Time Complexity: O(n) where n is the length of the input string
 * Space Complexity: O(n) for the result string
 */
public String replaceVowels(String input) {
    if (input == null || input.isEmpty()) {
        return input;
    }
    
    StringBuilder result = new StringBuilder();
    for (char c : input.toCharArray()) {
        if ("aeiouAEIOU".indexOf(c) != -1) {
            result.append('*');
        } else {
            result.append(c);
        }
    }
    
    return result.toString();
}

// More optimal solution using regex
public String replaceVowelsWithRegex(String input) {
    if (input == null || input.isEmpty()) {
        return input;
    }
    return input.replaceAll("[aeiouAEIOU]", "*");
}
```

**Key techniques:**
- String traversal with character checking
- Using StringBuilder for efficient string manipulation
- Alternative regex approach

### 2. String Reversal

**Example Problem:** Reverse words in a string while preserving the word order

```java
/**
 * Solution: Reverse individual words in a sentence while maintaining order
 * Time Complexity: O(n) where n is the length of the input string
 * Space Complexity: O(n) for the result string
 */
public String reverseWords(String sentence) {
    if (sentence == null || sentence.isEmpty()) {
        return sentence;
    }
    
    String[] words = sentence.split(" ");
    StringBuilder result = new StringBuilder();
    
    for (int i = 0; i < words.length; i++) {
        StringBuilder wordReversed = new StringBuilder(words[i]).reverse();
        result.append(wordReversed);
        
        if (i < words.length - 1) {
            result.append(" ");
        }
    }
    
    return result.toString();
}
```

**Key techniques:**
- String splitting and joining
- Using StringBuilder's reverse() method
- Handling edge cases properly

### 3. Palindrome Checking

**Example Problem:** Check if a string is a palindrome (ignoring spaces, punctuation, and case)

```java
/**
 * Solution: Check if a string is a palindrome (ignoring non-alphanumeric characters)
 * Time Complexity: O(n) where n is the length of the input string
 * Space Complexity: O(1) as we only use two pointers
 */
public boolean isPalindrome(String input) {
    if (input == null || input.isEmpty()) {
        return true;
    }
    
    int left = 0;
    int right = input.length() - 1;
    
    while (left < right) {
        // Skip non-alphanumeric characters from left
        while (left < right && !Character.isLetterOrDigit(input.charAt(left))) {
            left++;
        }
        
        // Skip non-alphanumeric characters from right
        while (left < right && !Character.isLetterOrDigit(input.charAt(right))) {
            right--;
        }
        
        // Compare characters (case-insensitive)
        if (Character.toLowerCase(input.charAt(left)) != Character.toLowerCase(input.charAt(right))) {
            return false;
        }
        
        left++;
        right--;
    }
    
    return true;
}
```

**Key techniques:**
- Two-pointer approach
- Character-by-character comparison
- Handling special characters and case sensitivity

### 4. Anagram Detection

**Example Problem:** Check if two strings are anagrams of each other

```java
/**
 * Solution: Check if two strings are anagrams
 * Time Complexity: O(n) where n is the length of the input strings
 * Space Complexity: O(k) where k is the size of the character set (usually 128 ASCII or 26 alphabet)
 */
public boolean areAnagrams(String str1, String str2) {
    if (str1 == null || str2 == null || str1.length() != str2.length()) {
        return false;
    }
    
    int[] charCount = new int[26]; // Assuming only lowercase letters
    
    for (char c : str1.toLowerCase().toCharArray()) {
        if (Character.isLetter(c)) {
            charCount[c - 'a']++;
        }
    }
    
    for (char c : str2.toLowerCase().toCharArray()) {
        if (Character.isLetter(c)) {
            charCount[c - 'a']--;
            if (charCount[c - 'a'] < 0) {
                return false;
            }
        }
    }
    
    return true;
}

// Alternative approach using sorting
public boolean areAnagramsWithSorting(String str1, String str2) {
    if (str1 == null || str2 == null || str1.length() != str2.length()) {
        return false;
    }
    
    char[] chars1 = str1.toLowerCase().toCharArray();
    char[] chars2 = str2.toLowerCase().toCharArray();
    
    Arrays.sort(chars1);
    Arrays.sort(chars2);
    
    return Arrays.equals(chars1, chars2);
}
```

**Key techniques:**
- Character counting with arrays
- Alternative sorting-based approach
- Early termination when possible

### 5. Substring Search and Manipulation

**Example Problem:** Find all occurrences of a pattern in a string

```java
/**
 * Solution: Find all occurrences of a pattern in a string
 * Time Complexity: O(n*m) where n is the length of the text and m is the length of the pattern
 * Space Complexity: O(k) where k is the number of occurrences
 */
public List<Integer> findAllOccurrences(String text, String pattern) {
    List<Integer> positions = new ArrayList<>();
    if (text == null || pattern == null || pattern.length() > text.length()) {
        return positions;
    }
    
    for (int i = 0; i <= text.length() - pattern.length(); i++) {
        boolean found = true;
        for (int j = 0; j < pattern.length(); j++) {
            if (text.charAt(i + j) != pattern.charAt(j)) {
                found = false;
                break;
            }
        }
        
        if (found) {
            positions.add(i);
        }
    }
    
    return positions;
}

// Using Java's built-in methods
public List<Integer> findAllOccurrencesBuiltIn(String text, String pattern) {
    List<Integer> positions = new ArrayList<>();
    if (text == null || pattern == null || pattern.length() > text.length()) {
        return positions;
    }
    
    int index = text.indexOf(pattern);
    while (index != -1) {
        positions.add(index);
        index = text.indexOf(pattern, index + 1);
    }
    
    return positions;
}
```

**Key techniques:**
- Sliding window approach
- Using built-in string methods
- Handling edge cases

### 6. String Compression

**Example Problem:** Compress string "aabcccccaaa" to "a2b1c5a3"

```java
/**
 * Solution: Basic string compression
 * Time Complexity: O(n) where n is the length of the input string
 * Space Complexity: O(n) in worst case
 */
public String compressString(String input) {
    if (input == null || input.length() <= 2) {
        return input;
    }
    
    StringBuilder compressed = new StringBuilder();
    char currentChar = input.charAt(0);
    int count = 1;
    
    for (int i = 1; i < input.length(); i++) {
        if (input.charAt(i) == currentChar) {
            count++;
        } else {
            compressed.append(currentChar).append(count);
            currentChar = input.charAt(i);
            count = 1;
        }
    }
    
    // Don't forget to append the last set
    compressed.append(currentChar).append(count);
    
    // Return the original string if compression doesn't make it smaller
    return compressed.length() < input.length() ? compressed.toString() : input;
}
```

**Key techniques:**
- Character counting
- StringBuilder for efficiency
- Boundary condition checking

## Advanced String Manipulation Problems

### 1. Longest Palindromic Substring

```java
/**
 * Solution: Find the longest palindromic substring
 * Time Complexity: O(n²) where n is the length of the input string
 * Space Complexity: O(1)
 */
public String longestPalindromicSubstring(String s) {
    if (s == null || s.length() < 2) {
        return s;
    }
    
    int start = 0, maxLength = 1;
    
    for (int i = 0; i < s.length(); i++) {
        // Check for odd length palindromes
        int len1 = expandAroundCenter(s, i, i);
        // Check for even length palindromes
        int len2 = expandAroundCenter(s, i, i + 1);
        
        int len = Math.max(len1, len2);
        if (len > maxLength) {
            maxLength = len;
            start = i - (len - 1) / 2;
        }
    }
    
    return s.substring(start, start + maxLength);
}

private int expandAroundCenter(String s, int left, int right) {
    while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
        left--;
        right++;
    }
    return right - left - 1;
}
```

### 2. String Pattern Matching (KMP Algorithm)

```java
/**
 * Solution: KMP pattern matching algorithm
 * Time Complexity: O(n + m) where n is the length of the text and m is the length of the pattern
 * Space Complexity: O(m) for the prefix table
 */
public List<Integer> kmpSearch(String text, String pattern) {
    List<Integer> positions = new ArrayList<>();
    if (pattern == null || text == null || pattern.length() > text.length()) {
        return positions;
    }
    
    int[] lps = computeLPSArray(pattern);
    int i = 0; // index for text
    int j = 0; // index for pattern
    
    while (i < text.length()) {
        if (pattern.charAt(j) == text.charAt(i)) {
            i++;
            j++;
        }
        
        if (j == pattern.length()) {
            positions.add(i - j);
            j = lps[j - 1];
        } else if (i < text.length() && pattern.charAt(j) != text.charAt(i)) {
            if (j != 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
    }
    
    return positions;
}

private int[] computeLPSArray(String pattern) {
    int[] lps = new int[pattern.length()];
    int len = 0;
    int i = 1;
    
    while (i < pattern.length()) {
        if (pattern.charAt(i) == pattern.charAt(len)) {
            len++;
            lps[i] = len;
            i++;
        } else {
            if (len != 0) {
                len = lps[len - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }
    }
    
    return lps;
}
```

### 3. Longest Common Subsequence

```java
/**
 * Solution: Find the longest common subsequence between two strings
 * Time Complexity: O(m*n) where m and n are the lengths of the two strings
 * Space Complexity: O(m*n)
 */
public String longestCommonSubsequence(String text1, String text2) {
    int m = text1.length();
    int n = text2.length();
    
    int[][] dp = new int[m + 1][n + 1];
    
    // Build DP table
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                dp[i][j] = dp[i - 1][j - 1] + 1;
            } else {
                dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
    }
    
    // Reconstruct the LCS
    StringBuilder lcs = new StringBuilder();
    int i = m, j = n;
    
    while (i > 0 && j > 0) {
        if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
            lcs.append(text1.charAt(i - 1));
            i--;
            j--;
        } else if (dp[i - 1][j] > dp[i][j - 1]) {
            i--;
        } else {
            j--;
        }
    }
    
    return lcs.reverse().toString();
}
```

## Tips for String Manipulation Interviews

1. **Always handle edge cases first:**
   - Empty strings
   - Null inputs
   - Single-character strings

2. **Be conscious of efficiency:**
   - String concatenation in loops is inefficient; use StringBuilder
   - Be aware of when to use arrays vs. maps for character counting

3. **Know your String APIs:**
   - Methods like `substring()`, `indexOf()`, `lastIndexOf()`, `split()`
   - Character methods like `Character.isLetterOrDigit()`, `Character.toLowerCase()`

4. **Understand when to use specialized algorithms:**
   - When to use KMP vs. Naive pattern matching
   - When to use dynamic programming for string problems

5. **Talk through your approach:**
   - Discuss your thought process when deciding on data structures
   - Explain your strategy for string traversal (forward, backward, two-pointer)
   - Calculate and mention time/space complexity

By practicing these problems and understanding the underlying techniques, you'll be well-prepared for string manipulation challenges in live coding interviews.
