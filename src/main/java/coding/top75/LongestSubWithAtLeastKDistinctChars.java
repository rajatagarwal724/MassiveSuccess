package coding.top75;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LongestSubWithAtLeastKDistinctChars {

    public int longestSubstring(String s, int k) {
        if (s.length() < k) {
            return 0;
        }
        return longestSubstringHelper(s, k);
    }

    private int longestSubstringHelper(String s, int k) {
        if (s.length() < k) {
            return 0;
        }

        Map<Character, Integer> charFreqMap = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            var elem = s.charAt(i);
            charFreqMap.put(elem, charFreqMap.getOrDefault(elem, 0) + 1);
        }

        for (Map.Entry<Character, Integer> entry: charFreqMap.entrySet()) {
            if (entry.getValue() < k) {
                var splitChar = entry.getKey();
                String[] subStrs = s.split(splitChar.toString());
                int maxLen = 0;
                for (String sub: subStrs) {
                    maxLen = Math.max(maxLen, longestSubstringHelper(sub, k));
                }
                return maxLen;
            }
        }
        return s.length();
    }

    public static void main(String[] args) {

    }
}
