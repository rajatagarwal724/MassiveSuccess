package companies.roku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubstringWithConcatenation {


    public List<Integer> findSubstringSimple(String s, String[] words) {
        int totalWords = words.length;
        int wordLen = words[0].length();
        int subStrSize = totalWords * wordLen;
        List<Integer> result = new ArrayList<>();

        Map<String, Integer> wordFreqMap = new HashMap<>();
        for (String word: words) {
            wordFreqMap.put(word, wordFreqMap.getOrDefault(word, 0) + 1);
        }

        for (int left = 0; left <= s.length() - subStrSize; left++) {
            if (check(left, s, subStrSize, wordFreqMap, totalWords, wordLen)) {
                result.add(left);
            }
        }

        return result;
    }

    private boolean check(int left, String s, int subStrSize, Map<String, Integer> wordFreqMap, int totalWords, int wordLen) {
        Map<String, Integer> remaining = new HashMap<>(wordFreqMap);
        int wordsUsed = 0;

        for (int right = left; right < left + subStrSize; right+=wordLen) {
            var subString = s.substring(right, right + wordLen);
            if (remaining.getOrDefault(subString, 0) != 0) {
                remaining.put(subString, remaining.get(subString) - 1);
                wordsUsed++;
            } else {
                break;
            }
        }

        return wordsUsed == totalWords;
    }

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

    public List<Integer> findSubstringTry(String s, String[] words) {
        int wordLen = words[0].length();
        int totalWords = words.length;
        int subStrSize = wordLen * totalWords;
        List<Integer> result = new ArrayList<>();
        Map<String, Integer> wordFreqMap = new HashMap<>();
        for (String word: words) {
            wordFreqMap.put(word, wordFreqMap.getOrDefault(word, 0) + 1);
        }

        for (int i = 0; i < wordLen; i++) {
            int left = i;
            int count = 0;
            Map<String, Integer> seen = new HashMap<>();

            for (int right = i; right <= s.length() - wordLen; right+=wordLen) {
                var elem = s.substring(right, right + wordLen);

                if (wordFreqMap.containsKey(elem)) {
                    seen.put(elem, seen.getOrDefault(elem, 0) + 1);
                    count++;

                    while (seen.get(elem) > wordFreqMap.get(elem)) {
                        var leftElem = s.substring(left, left + wordLen);
                        seen.put(leftElem, seen.get(leftElem) - 1);
                        count--;
                        left+=wordLen;
                    }

                    if (count == totalWords) {
                        result.add(left);
                        var leftElem = s.substring(left, left + wordLen);
                        seen.put(leftElem, seen.get(leftElem) - 1);
                        count--;
                        left+=wordLen;
                    }
                } else {
                    seen.clear();
                    count = 0;
                    left = right + wordLen;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        var sol = new SubstringWithConcatenation();
        System.out.println(sol.findSubstring("barfoothefoobarman", new String[]{"foo","bar"}));
        System.out.println(sol.findSubstringSimple("barfoothefoobarman", new String[]{"foo","bar"}));
        System.out.println(sol.findSubstringTry("barfoofoobarthefoobarman", new String[]{"bar","foo","the"}));
    }
}
