package coding.SlidingWindow.LongestSubstringwithKDistinctCharacters;

import java.util.HashMap;
import java.util.Map;

public class LongestSubstringWithAtMostKDistinctChars {

    public int lengthOfLongestSubstringKDistinct(String s, int k) {
        char[] arr = s.toCharArray();
        Map<Character, Integer> charFreqMap = new HashMap<>();
        int maxLength = 0, windowStart = 0;

        for (int windowEnd = 0; windowEnd < arr.length; windowEnd++) {
            char elem = arr[windowEnd];
            charFreqMap.put(elem, charFreqMap.getOrDefault(elem, 0) + 1);

            while (charFreqMap.size() > k) {
                char elemAtWindowStart = arr[windowStart];
                charFreqMap.put(elemAtWindowStart, charFreqMap.get(elemAtWindowStart) - 1);
                if (charFreqMap.get(elemAtWindowStart) == 0) {
                    charFreqMap.remove(elemAtWindowStart);
                }
                windowStart++;
            }

            maxLength = Math.max(maxLength, windowEnd - windowStart + 1);
        }
        return maxLength;
    }

    public static void main(String[] args) {
        var sol = new LongestSubstringKDistinctChars();
        System.out.println(sol.findLength("eceba", 2));
        System.out.println(sol.findLength("aa", 1));
    }
}
