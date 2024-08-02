package coding.SlidingWindow.LongestSubstringwithKDistinctCharacters;

import java.util.HashMap;
import java.util.Map;

public class LongestSubstringKDistinctChars {

    public int findLength(String str, int k) {
        int maxLength = 0;
        Map<Character, Integer> charFreqMap = new HashMap<>();
        int windowStart = 0;
        char[] chars = str.toCharArray();

        for (int windowEnd = 0; windowEnd < chars.length; windowEnd++) {
            char elem = chars[windowEnd];

            charFreqMap.put(elem, charFreqMap.getOrDefault(elem, 0) + 1);

            while (charFreqMap.size() > k) {
                char windowStartElem = chars[windowStart++];
                if (charFreqMap.containsKey(windowStartElem)) {
                    charFreqMap.put(windowStartElem, charFreqMap.get(windowStartElem) - 1);
                    if (charFreqMap.get(windowStartElem) == 0) {
                        charFreqMap.remove(windowStartElem);
                    }
                }
            }
            maxLength = Math.max(maxLength, windowEnd - windowStart + 1);
        }

        return maxLength;
    }

    public static void main(String[] args) {
        var sol = new LongestSubstringKDistinctChars();
        System.out.println(sol.findLength("araaci", 2));
        System.out.println(sol.findLength("araaci", 1));
        System.out.println(sol.findLength("cbbebi", 3));
    }
}
