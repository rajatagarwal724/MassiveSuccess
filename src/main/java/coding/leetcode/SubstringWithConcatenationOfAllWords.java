package coding.leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LeetCode Problem: Substring with Concatenation of All Words
 * URL: https://leetcode.com/problems/substring-with-concatenation-of-all-words/
 *
 * Problem Statement:
 * You are given a string s and an array of strings words. All the strings of words are of the same length.
 * A concatenated substring in s is a substring that contains all the strings of any permutation of words concatenated.
 * For example, if words = ["ab","cd","ef"], then "abcdef", "abefcd", "cdabef", "cdefab", "efabcd", and "efcdab" are all concatenated strings.
 * A concatenated substring is a substring of s that is a concatenation of each word in words exactly once and without any intervening characters.
 * Return the starting indices of all the concatenated substrings in s. You can return the answer in any order.
 */
public class SubstringWithConcatenationOfAllWords {

    public List<Integer> findSubstring(String s, String[] words) {
        List<Integer> result = new ArrayList<>();
        if (s == null || s.length() == 0 || words == null || words.length == 0) {
            return result;
        }

        int wordLen = words[0].length();
        int numWords = words.length;
        int totalLen = wordLen * numWords;

        if (s.length() < totalLen) {
            return result;
        }

        // 1. Create a frequency map of the words.
        Map<String, Integer> wordCount = new HashMap<>();
        for (String word : words) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }

        // 2. Slide a window across the string `s`.
        // The outer loop iterates `wordLen` times. This is an optimization.
        // It ensures we check all possible starting points of a concatenated substring.
        // For example, if wordLen is 3, we start checks at index 0, 1, and 2.
        // Any substring starting at index 3, 4, 5 etc., will be covered by these initial checks.
        for (int i = 0; i < wordLen; i++) {
            int left = i;
            int count = 0;
            Map<String, Integer> seenWords = new HashMap<>();

            // The inner loop moves the window of size `totalLen` across the string.
            for (int j = i; j <= s.length() - wordLen; j += wordLen) {
                String currentWord = s.substring(j, j + wordLen);

                if (wordCount.containsKey(currentWord)) {
                    // Word is part of the required words.
                    seenWords.put(currentWord, seenWords.getOrDefault(currentWord, 0) + 1);
                    count++;

                    // If we have seen a word more times than it appears in `words`,
                    // we must shrink the window from the left until it's valid again.
                    while (seenWords.get(currentWord) > wordCount.get(currentWord)) {
                        String leftWord = s.substring(left, left + wordLen);
                        seenWords.put(leftWord, seenWords.get(leftWord) - 1);
                        count--;
                        left += wordLen;
                    }

                    // 3. If the window is valid, add the starting index to the result.
                    if (count == numWords) {
                        result.add(left);
                        // Move the window forward by one word length to find the next potential match.
                        String leftWord = s.substring(left, left + wordLen);
                        seenWords.put(leftWord, seenWords.get(leftWord) - 1);
                        count--;
                        left += wordLen;
                    }
                } else {
                    // Word is not in our list, so this window is invalid. Reset everything.
                    seenWords.clear();
                    count = 0;
                    left = j + wordLen;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        SubstringWithConcatenationOfAllWords solution = new SubstringWithConcatenationOfAllWords();

        // Example 1
        String s1 = "barfoothefoobarman";
        String[] words1 = {"foo", "bar"};
        System.out.println("Input: s = \"" + s1 + "\", words = [\"foo\",\"bar\"]");
        System.out.println("Output: " + solution.findSubstring(s1, words1)); // Expected: [0, 9]

        // Example 2
        String s2 = "wordgoodgoodgoodbestword";
        String[] words2 = {"word", "good", "best", "word"};
        System.out.println("\nInput: s = \"" + s2 + "\", words = [\"word\",\"good\",\"best\",\"word\"]");
        System.out.println("Output: " + solution.findSubstring(s2, words2)); // Expected: []

        // Example 3
        String s3 = "barfoofoobarthefoobarman";
        String[] words3 = {"bar", "foo", "the"};
        System.out.println("\nInput: s = \"" + s3 + "\", words = [\"bar\",\"foo\",\"the\"]");
        System.out.println("Output: " + solution.findSubstring(s3, words3)); // Expected: [6, 9, 12]

        // Example 4: Words with duplicates
        String s4 = "wordgoodgoodgoodbestword";
        String[] words4 = {"word","good","best","good"};
        System.out.println("\nInput: s = \"" + s4 + "\", words = [\"word\",\"good\",\"best\",\"good\"]");
        System.out.println("Output: " + solution.findSubstring(s4, words4)); // Expected: [8]
    }
}
