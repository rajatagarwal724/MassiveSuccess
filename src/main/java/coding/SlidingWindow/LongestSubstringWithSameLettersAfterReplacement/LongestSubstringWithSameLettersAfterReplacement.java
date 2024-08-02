package coding.SlidingWindow.LongestSubstringWithSameLettersAfterReplacement;

import java.util.HashMap;
import java.util.Map;

public class LongestSubstringWithSameLettersAfterReplacement {

    public int findLength(String str, int k) {
        int maxLength = 0;
        int windowStart = 0, maxRepeatLetterCount = 0;
        Map<Character, Integer> charFreqMap = new HashMap<>();
        char[] arr = str.toCharArray();

        for (int windowEnd = 0; windowEnd < arr.length; windowEnd++) {
            char elem = arr[windowEnd];
            charFreqMap.put(elem, charFreqMap.getOrDefault(elem, 0) + 1);
            maxRepeatLetterCount = Math.max(maxRepeatLetterCount, charFreqMap.get(elem));

            if ((windowEnd - windowStart + 1 - maxRepeatLetterCount) > k) {
                char elemAtStart = arr[windowStart++];
                charFreqMap.put(elemAtStart, charFreqMap.get(elemAtStart) - 1);
            }

            maxLength = Math.max(maxLength, windowEnd - windowStart + 1);
        }

        return maxLength;
    }



    public static void main(String[] args) {
        var sol = new LongestSubstringWithSameLettersAfterReplacement();

        System.out.println(sol.findLength("aabccbb", 2));
        System.out.println(sol.findLength("abbcb", 1));
        System.out.println(sol.findLength("abccde", 1));
    }
}
