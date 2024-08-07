package coding.SlidingWindow.AllAnagrams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllAnagrams {

    public List<Integer> findStringAnagrams(String str, String pattern) {
        List<Integer> resultIndices = new ArrayList<>();
        int windowStart = 0, matched = 0;
        Map<Character, Integer> patternFreqMap = new HashMap<>();
        for (char ch: pattern.toCharArray()) {
            patternFreqMap.put(ch, patternFreqMap.getOrDefault(ch, 0) + 1);
        }
        char[] arr = str.toCharArray();

        for (int windowEnd = 0; windowEnd < arr.length; windowEnd++) {
            char rightChar = arr[windowEnd];

            if (patternFreqMap.containsKey(rightChar)) {
                patternFreqMap.put(rightChar, patternFreqMap.get(rightChar) - 1);
                if (patternFreqMap.get(rightChar) == 0) {
                    matched++;
                }
            }

            if (matched == pattern.length()) {
                resultIndices.add(windowStart);
            }

            if(windowEnd >= (pattern.length() - 1)) {
                char leftChar = arr[windowStart++];
                if (patternFreqMap.containsKey(leftChar)) {
                    if (patternFreqMap.get(leftChar) == 0) {
                        matched--;
                    }
                    patternFreqMap.put(leftChar, patternFreqMap.get(leftChar) + 1);
                }
            }
        }
        return resultIndices;
    }

    public static void main(String[] args) {
        var sol = new AllAnagrams();
        sol.findStringAnagrams("ppqp", "pq").forEach(System.out::println);
        System.out.println("#################");

        sol.findStringAnagrams("abbcabc", "abc").forEach(System.out::println);
        System.out.println("#################");
    }
}
