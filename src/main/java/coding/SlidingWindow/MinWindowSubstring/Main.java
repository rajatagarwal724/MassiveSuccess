package coding.SlidingWindow.MinWindowSubstring;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static String MinWindowSubstring(String[] strArr) {
        char[] input = strArr[0].toCharArray();
        char[] pattern = strArr[1].toCharArray();
        int matched = 0, windowStart = 0, minLength = Integer.MAX_VALUE, subStrStart = Integer.MIN_VALUE;
        Map<Character, Integer> charFreqMap = new HashMap<>();

        for (char ch: pattern) {
            charFreqMap.put(ch, charFreqMap.getOrDefault(ch, 0) + 1);
        }

        for (int windowEnd = 0; windowEnd < input.length; windowEnd++) {
            char elemAtRight = input[windowEnd];

            if (charFreqMap.containsKey(elemAtRight)) {
                charFreqMap.put(elemAtRight, charFreqMap.get(elemAtRight) - 1);
                if (charFreqMap.get(elemAtRight) == 0) {
                    matched++;
                }
            }

            while (matched == charFreqMap.size()) {
                if ((windowEnd - windowStart + 1) < minLength) {
                    minLength = Math.min(minLength, (windowEnd - windowStart + 1));
                    subStrStart = windowStart;
                }
                char elemAtStart = input[windowStart++];

                if (charFreqMap.containsKey(elemAtStart)) {
                    if (charFreqMap.get(elemAtStart) == 0) {
                        matched--;
                    }
                    charFreqMap.put(elemAtStart, charFreqMap.get(elemAtStart) + 1);
                }
            }
        }

        return subStrStart == Integer.MIN_VALUE ? "" : strArr[0].substring(subStrStart, subStrStart + minLength);
    }

    public static void main(String[] args) {
        System.out.println(MinWindowSubstring(new String[] {"aabdec", "abc"}));
        System.out.println(MinWindowSubstring(new String[] {"aabdec", "abac"}));
        System.out.println(MinWindowSubstring(new String[] {"abdbca", "abc"}));
        System.out.println(MinWindowSubstring(new String[] {"adcad", "abc"}));
    }
}
