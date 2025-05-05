package coding.SlidingWindow.AllAnagrams;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    public List<Integer> findStringAnagrams(String str, String pattern) {
        List<Integer> resultIndices = new ArrayList<Integer>();
        Map<Character, Integer> patternFreqMap = new HashMap<>();

        for (int i = 0; i < pattern.length(); i++) {
            char elem = pattern.charAt(i);
            patternFreqMap.put(elem, patternFreqMap.getOrDefault(elem, 0) + 1);
        }
        int left = 0, matched = 0;

        for (int right = 0; right < str.length(); right++) {
            char rightChar = str.charAt(right);

            if (patternFreqMap.containsKey(rightChar)) {
                patternFreqMap.put(rightChar, patternFreqMap.get(rightChar) - 1);
                if (patternFreqMap.get(rightChar) == 0) {
                    matched++;
                }
            }

            if (matched == patternFreqMap.size()) {
                resultIndices.add(left);
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

        return resultIndices;
    }

    public static void main(String[] args) {
        var sol = new Solution();
        System.out.println(StringUtils.join(sol.findStringAnagrams("ppqp", "pq")));
        System.out.println(StringUtils.join(sol.findStringAnagrams("abbcabc", "abc")));
    }
}
