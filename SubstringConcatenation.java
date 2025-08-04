import java.util.*;

/**
 * LeetCode 30: Substring with Concatenation of All Words
 * 
 * Problem Description:
 * You are given a string s and an array of strings words. All the strings of words are of the same length.
 * A concatenated substring in s is a substring that contains all the strings of any permutation of words concatenated.
 * 
 * Return the starting indices of all the concatenated substrings in s.
 * 
 * Examples:
 * 1. s = "barfoothefoobarman", words = ["foo","bar"]
 *    Output: [0,9]
 *    
 * 2. s = "barfoofoobarthefoobarman", words = ["bar","foo","the"]
 *    Output: [6,9,12]
 * 
 * Time Complexity: O(n * m * k) where n = s.length(), m = words.length, k = words[0].length()
 * Space Complexity: O(m * k)
 */
public class SubstringConcatenation {
    
    /**
     * Approach 1: Brute Force with HashMap
     * 
     * For each possible starting position, check if the substring of length
     * (words.length * words[0].length()) is a valid concatenation.
     * 
     * Time: O(n * m * k) where n=s.length(), m=words.length, k=words[0].length()
     * Space: O(m * k)
     */
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
                
                // If word not in original list or appears too many times
                if (seen.get(word) > wordFreq.getOrDefault(word, 0)) {
                    break;
                }
                j++;
            }
            
            // If we successfully processed all words
            if (j == wordCount) {
                result.add(i);
            }
        }
        
        return result;
    }
    
    /**
     * Approach 2: Optimized Sliding Window
     * 
     * Instead of checking every position, we use sliding window technique.
     * We only need to check positions that are multiples of wordLen apart.
     * 
     * Time: O(n * k) where n=s.length(), k=words[0].length()
     * Space: O(m * k) where m=words.length
     */
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
        
        // We only need to start from positions 0, 1, 2, ..., wordLen-1
        for (int i = 0; i < wordLen; i++) {
            int left = i;
            int right = i;
            int currCount = 0;
            Map<String, Integer> currFreq = new HashMap<>();
            
            while (right + wordLen <= s.length()) {
                // Get the word at right pointer
                String word = s.substring(right, right + wordLen);
                right += wordLen;
                
                if (wordFreq.containsKey(word)) {
                    currFreq.put(word, currFreq.getOrDefault(word, 0) + 1);
                    currCount++;
                    
                    // If we have too many of this word, shrink window from left
                    while (currFreq.get(word) > wordFreq.get(word)) {
                        String leftWord = s.substring(left, left + wordLen);
                        currFreq.put(leftWord, currFreq.get(leftWord) - 1);
                        currCount--;
                        left += wordLen;
                    }
                    
                    // If we have exactly the right number of words
                    if (currCount == wordCount) {
                        result.add(left);
                        
                        // Move left pointer to find next potential match
                        String leftWord = s.substring(left, left + wordLen);
                        currFreq.put(leftWord, currFreq.get(leftWord) - 1);
                        currCount--;
                        left += wordLen;
                    }
                } else {
                    // Reset everything if we encounter a word not in our list
                    currFreq.clear();
                    currCount = 0;
                    left = right;
                }
            }
        }
        
        return result;
    }
    
    /**
     * Main solution using the optimized sliding window approach
     */
    public List<Integer> findSubstring(String s, String[] words) {
        return findSubstringSlidingWindow(s, words);
    }
    
    /**
     * Test cases for the solution
     */
    public static void testSolution() {
        SubstringConcatenation solution = new SubstringConcatenation();
        
        // Test case 1
        String s1 = "barfoothefoobarman";
        String[] words1 = {"foo", "bar"};
        List<Integer> expected1 = Arrays.asList(0, 9);
        List<Integer> result1 = solution.findSubstring(s1, words1);
        System.out.printf("Test 1: %s == %s -> %b%n", 
            result1, expected1, compareLists(result1, expected1));
        
        // Test case 2
        String s2 = "wordgoodgoodgoodbestword";
        String[] words2 = {"word", "good", "best", "word"};
        List<Integer> expected2 = Arrays.asList();
        List<Integer> result2 = solution.findSubstring(s2, words2);
        System.out.printf("Test 2: %s == %s -> %b%n", 
            result2, expected2, result2.equals(expected2));
        
        // Test case 3
        String s3 = "barfoofoobarthefoobarman";
        String[] words3 = {"bar", "foo", "the"};
        List<Integer> expected3 = Arrays.asList(6, 9, 12);
        List<Integer> result3 = solution.findSubstring(s3, words3);
        System.out.printf("Test 3: %s == %s -> %b%n", 
            result3, expected3, compareLists(result3, expected3));
        
        // Test case 4: Edge case with repeated words
        String s4 = "goodgoodbestword";
        String[] words4 = {"word", "good", "best", "good"};
        List<Integer> expected4 = Arrays.asList(0); // "goodgoodbestword" = "good" + "good" + "best" + "word"
        List<Integer> result4 = solution.findSubstring(s4, words4);
        System.out.printf("Test 4: %s == %s -> %b%n", 
            result4, expected4, result4.equals(expected4));
        
        // Test case 5: No valid concatenation
        String s5 = "wordgoodgoodgoodbestword";
        String[] words5 = {"word", "good", "best", "xyz"};
        List<Integer> expected5 = Arrays.asList();
        List<Integer> result5 = solution.findSubstring(s5, words5);
        System.out.printf("Test 5: %s == %s -> %b%n", 
            result5, expected5, result5.equals(expected5));
        
        // Compare brute force and optimized solutions
        System.out.println("\nComparing both approaches:");
        String[][] testCases = {{s1, s2, s3}, {null, null, null}};
        String[][][] wordCases = {{words1, words2, words3}};
        
        for (int i = 0; i < 3; i++) {
            String s = new String[]{s1, s2, s3}[i];
            String[] words = new String[][]{words1, words2, words3}[i];
            
            List<Integer> bruteResult = solution.findSubstringBruteForce(s, words);
            List<Integer> optimizedResult = solution.findSubstringSlidingWindow(s, words);
            
            Collections.sort(bruteResult);
            Collections.sort(optimizedResult);
            
            System.out.printf("Test %d: Brute force %s == Optimized %s -> %b%n", 
                i + 1, bruteResult, optimizedResult, bruteResult.equals(optimizedResult));
        }
    }
    
    /**
     * Helper method to compare two lists regardless of order
     */
    private static boolean compareLists(List<Integer> list1, List<Integer> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        List<Integer> sorted1 = new ArrayList<>(list1);
        List<Integer> sorted2 = new ArrayList<>(list2);
        Collections.sort(sorted1);
        Collections.sort(sorted2);
        return sorted1.equals(sorted2);
    }
    
    /**
     * Main method to run the tests
     */
    public static void main(String[] args) {
        System.out.println("LeetCode 30: Substring with Concatenation of All Words - Java Solution");
        System.out.println("==================================================================");
        testSolution();
    }
} 