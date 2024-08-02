package coding.SlidingWindow.PermutationInAString;

import java.util.HashMap;
import java.util.Map;

public class PermutationInAString {

    public boolean findPermutation(String str, String pattern) {
        Map<Character, Integer> patternCharFreqMap = new HashMap<>();
        for (Character ch: pattern.toCharArray()) {
            patternCharFreqMap.put(ch, patternCharFreqMap.getOrDefault(ch, 0) + 1);
        }

        int windowStart = 0, matched = 0;
        char[] arr = str.toCharArray();

        for (int windowEnd = 0; windowEnd < arr.length; windowEnd++) {
            char elemAtRight = arr[windowEnd];

            if (patternCharFreqMap.containsKey(elemAtRight)) {
                patternCharFreqMap.put(elemAtRight, patternCharFreqMap.get(elemAtRight) - 1);
                if (patternCharFreqMap.get(elemAtRight) == 0) {
                    matched++;
                }
             }

            if (matched == patternCharFreqMap.size()) {
                return true;
            }

            if (windowEnd >= (pattern.length() - 1)) {
                char elemAtLeft = arr[windowStart++];
                if (patternCharFreqMap.containsKey(elemAtLeft)) {
                    if (patternCharFreqMap.get(elemAtLeft) == 0) {
                        matched--;
                    }
                    patternCharFreqMap.put(elemAtLeft, patternCharFreqMap.get(elemAtLeft) + 1);
                }
            }
        }

        return false;
    }

    public static void main(String[] args) {
        var sol = new PermutationInAString();
        System.out.println(sol.findPermutation("oidbcaf", "abc"));
        System.out.println(sol.findPermutation("odicf", "dc"));
        System.out.println(sol.findPermutation("bcdxabcdy", "bcdyabcdx"));
        System.out.println(sol.findPermutation("aaacb", "abc"));
    }
}
