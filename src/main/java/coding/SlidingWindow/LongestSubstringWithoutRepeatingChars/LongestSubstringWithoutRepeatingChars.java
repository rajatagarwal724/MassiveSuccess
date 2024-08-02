package coding.SlidingWindow.LongestSubstringWithoutRepeatingChars;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LongestSubstringWithoutRepeatingChars {

    public int lengthOfLongestSubstring(String s) {
        char[] arr = s.toCharArray();
        Map<Character, Integer> charFreqMap = new HashMap<>();
        int maxLength = 0, windowStart = 0;

        for (int windowEnd = 0; windowEnd < arr.length; windowEnd++) {
            char elem = arr[windowEnd];
            charFreqMap.put(elem, charFreqMap.getOrDefault(elem, 0) + 1);

            while (charFreqMap.get(elem) > 1) {
                char elemAtStart = arr[windowStart];
                if (charFreqMap.containsKey(elemAtStart)) {
                    charFreqMap.put(elemAtStart, charFreqMap.get(elemAtStart) - 1);
                    if (charFreqMap.get(elemAtStart) == 0) {
                        charFreqMap.remove(elemAtStart);
                    }
                }
                windowStart++;
            }
            maxLength = Math.max(maxLength, windowEnd - windowStart + 1);
        }

        return maxLength;
    }

    public static void main(String[] args) {
        var sol = new LongestSubstringWithoutRepeatingChars();

        System.out.println(sol.lengthOfLongestSubstring("abcabcbb"));
        System.out.println(sol.lengthOfLongestSubstring("bbbbb"));
        System.out.println(sol.lengthOfLongestSubstring("pwwkew"));
    }
}
