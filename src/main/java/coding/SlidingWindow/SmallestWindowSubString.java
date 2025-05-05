package coding.SlidingWindow;

import java.util.HashMap;
import java.util.Map;

public class SmallestWindowSubString {

    public String findSubstring(String str, String pattern) {
        Map<Character, Integer> patternFreqMap = new HashMap<>();
        for (int i = 0; i < pattern.length(); i++) {
            char elem = pattern.charAt(i);
            patternFreqMap.put(elem, patternFreqMap.getOrDefault(elem, 0) + 1);
        }
        String res = null;
        int left = 0, matched = 0;
        int minLength = Integer.MAX_VALUE;

        for (int right = 0; right < str.length(); right++) {
            char rightChar = str.charAt(right);

            if (patternFreqMap.containsKey(rightChar)) {
                patternFreqMap.put(rightChar, patternFreqMap.get(rightChar) - 1);
                if (patternFreqMap.get(rightChar) == 0) {
                    matched++;
                }
            }

            while (matched == patternFreqMap.size()) {
                if (minLength > (right - left + 1)) {
                    minLength = right - left + 1;
                    res = str.substring(left, right + 1);
                }
                minLength = Math.min(minLength, (right - left + 1));
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
        return res == null ? "" : res;
    }

    public static void main(String[] args) {
        var sol = new SmallestWindowSubString();
        System.out.println(sol.findSubstring("aabdec", "abc"));
        System.out.println(sol.findSubstring("aabdec", "abac"));
        System.out.println(sol.findSubstring("abdbca", "abc"));
        System.out.println(sol.findSubstring("adcad", "abc"));
    }
}
