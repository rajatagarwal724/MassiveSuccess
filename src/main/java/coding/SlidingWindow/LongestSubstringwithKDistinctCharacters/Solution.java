package coding.SlidingWindow.LongestSubstringwithKDistinctCharacters;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    public int findLength(String str, int k) {
        char[] ch = str.toCharArray();
        int maxLength = Integer.MIN_VALUE;
        Map<Character, Integer> charFreqMap = new HashMap<>();
        int start = 0;
        for (int end = 0; end < ch.length; end++) {
            char elem = ch[end];
            charFreqMap.put(elem, charFreqMap.getOrDefault(elem, 0) + 1);
            while (charFreqMap.size() > k) {
                char charAtStart = ch[start];
                charFreqMap.put(charAtStart, charFreqMap.getOrDefault(charAtStart, 0) - 1);
                if (charFreqMap.get(charAtStart) <= 0) {
                    charFreqMap.remove(charAtStart);
                }
                start++;
            }
            maxLength = Integer.max(maxLength, end - start + 1);
        }
        if (maxLength == Integer.MIN_VALUE) {
            return 0;
        }
        return maxLength;
    }

    public static void main(String[] args) {
        var sol = new Solution();
        System.out.println(sol.findLength("araaci", 2));
    }
}
