package coding.SlidingWindow.PermutationInAString;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    public boolean findPermutation(String str, String pattern) {
        int matched = 0;
        Map<Character, Integer> patternFreqMap = new HashMap<>();
        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);
            patternFreqMap.put(ch, patternFreqMap.getOrDefault(ch, 0) + 1);
        }

        int left = 0;

        for (int right = 0; right < str.length(); right++) {
            char rightChar = str.charAt(right);

            if (patternFreqMap.containsKey(rightChar)) {
                patternFreqMap.put(rightChar, patternFreqMap.get(rightChar) - 1);
                if (patternFreqMap.get(rightChar) == 0) {
                    matched++;
                }
            }

            if (matched == patternFreqMap.size()) {
                return true;
            }

            if (right >= (pattern.length() - 1)) {
                char leftChar = str.charAt(left);
                if (patternFreqMap.containsKey(leftChar)) {
                    if (patternFreqMap.get(leftChar) == 0) {
                        matched--;
                    }
                    patternFreqMap.put(leftChar, patternFreqMap.get(leftChar) + 1);
                }
                left++;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        var sol = new Solution();
//        System.out.println(sol.findPermutation("oidbcaf", "abc"));
        System.out.println(sol.findPermutation("odicf", "dc"));
//        System.out.println(sol.findPermutation("bcdxabcdy", "bcdyabcdx"));
//        System.out.println(sol.findPermutation("aaacb", "abc"));
    }
}
