"""
LeetCode 30: Substring with Concatenation of All Words

Problem Description:
You are given a string s and an array of strings words. All the strings of words are of the same length.
A concatenated substring in s is a substring that contains all the strings of any permutation of words concatenated.

Return the starting indices of all the concatenated substrings in s.

Examples:
1. s = "barfoothefoobarman", words = ["foo","bar"]
   Output: [0,9]
   
2. s = "barfoofoobarthefoobarman", words = ["bar","foo","the"]
   Output: [6,9,12]

Time Complexity: O(n * m * k) where n = len(s), m = len(words), k = len(words[0])
Space Complexity: O(m * k)
"""

from collections import Counter, defaultdict
from typing import List


class Solution:
    def findSubstring_bruteforce(self, s: str, words: List[str]) -> List[int]:
        """
        Approach 1: Brute Force with HashMap
        
        For each possible starting position, check if the substring of length
        (len(words) * len(words[0])) is a valid concatenation.
        
        Time: O(n * m * k) where n=len(s), m=len(words), k=len(words[0])
        Space: O(m * k)
        """
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
                
                # If word not in original list or appears too many times
                if seen[word] > word_freq[word]:
                    break
                    
                j += 1
            
            # If we successfully processed all words
            if j == word_count:
                result.append(i)
        
        return result
    
    def findSubstring_sliding_window(self, s: str, words: List[str]) -> List[int]:
        """
        Approach 2: Optimized Sliding Window
        
        Instead of checking every position, we use sliding window technique.
        We only need to check positions that are multiples of word_len apart.
        
        Time: O(n * k) where n=len(s), k=len(words[0])
        Space: O(m * k) where m=len(words)
        """
        if not s or not words:
            return []
        
        word_len = len(words[0])
        word_count = len(words)
        total_len = word_len * word_count
        result = []
        
        # Count frequency of each word
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
    
    def findSubstring(self, s: str, words: List[str]) -> List[int]:
        """
        Main solution using the optimized sliding window approach
        """
        return self.findSubstring_sliding_window(s, words)


def test_solution():
    """Test cases for the solution"""
    solution = Solution()
    
    # Test case 1
    s1 = "barfoothefoobarman"
    words1 = ["foo", "bar"]
    expected1 = [0, 9]
    result1 = solution.findSubstring(s1, words1)
    print(f"Test 1: {result1} == {expected1} -> {sorted(result1) == sorted(expected1)}")
    
    # Test case 2
    s2 = "wordgoodgoodgoodbestword"
    words2 = ["word", "good", "best", "word"]
    expected2 = []
    result2 = solution.findSubstring(s2, words2)
    print(f"Test 2: {result2} == {expected2} -> {result2 == expected2}")
    
    # Test case 3
    s3 = "barfoofoobarthefoobarman"
    words3 = ["bar", "foo", "the"]
    expected3 = [6, 9, 12]
    result3 = solution.findSubstring(s3, words3)
    print(f"Test 3: {result3} == {expected3} -> {sorted(result3) == sorted(expected3)}")
    
    # Test case 4: Edge case with repeated words
    s4 = "goodgoodbestword"
    words4 = ["word", "good", "best", "good"]
    expected4 = [0]  # "goodgoodbestword" = "good" + "good" + "best" + "word"
    result4 = solution.findSubstring(s4, words4)
    print(f"Test 4: {result4} == {expected4} -> {result4 == expected4}")
    
    # Test case 5: No valid concatenation
    s5 = "wordgoodgoodgoodbestword"
    words5 = ["word", "good", "best", "xyz"]
    expected5 = []
    result5 = solution.findSubstring(s5, words5)
    print(f"Test 5: {result5} == {expected5} -> {result5 == expected5}")
    
    # Compare brute force and optimized solutions
    print("\nComparing both approaches:")
    for i, (s, words) in enumerate([(s1, words1), (s2, words2), (s3, words3)], 1):
        brute_result = solution.findSubstring_bruteforce(s, words)
        optimized_result = solution.findSubstring_sliding_window(s, words)
        print(f"Test {i}: Brute force {sorted(brute_result)} == Optimized {sorted(optimized_result)} -> {sorted(brute_result) == sorted(optimized_result)}")


if __name__ == "__main__":
    test_solution() 