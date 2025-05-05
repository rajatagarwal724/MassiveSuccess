package coding.SlidingWindow.LongestSubstringWithSameLettersAfterReplacement;

import java.util.HashMap;
import java.util.Map;

public class Solution {

    public int findLength(String str, int k) {
        int maxLength = Integer.MIN_VALUE;
        Map<Character, Integer> charFreqMap = new HashMap<>();
        int maxFreqChar = 0;
        int start = 0, end = 0;

        for (end = 0; end < str.length(); end++) {
            char rightChar = str.charAt(end);
            charFreqMap.put(rightChar, charFreqMap.getOrDefault(rightChar, 0) + 1);
            maxFreqChar = Math.max(maxFreqChar, charFreqMap.get(rightChar));

            if ((end - start + 1 - maxFreqChar) > k) {
                char leftChar = str.charAt(start);
                charFreqMap.put(leftChar, charFreqMap.getOrDefault(leftChar, 0) - 1);
                if (charFreqMap.get(leftChar) <= 0) {
                    charFreqMap.remove(leftChar);
                }
                start++;
            }
            maxLength = Math.max(maxLength, end - start + 1);
        }
        return maxLength;
    }

    public static void main(String[] args) {
        var sol = new Solution();
        System.out.println(sol.findLength("aabccbb", 2));
        System.out.println(sol.findLength("abbcb", 1));
        System.out.println(sol.findLength("abccde", 1));
    }
}
