package coding.SlidingWindow.FruitsIntoBaskets;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    public int findLength(char[] arr) {
        if (arr.length == 0) {
            return 0;
        }
        Map<Character, Integer> charFreqMap = new HashMap<>();
        int start = 0, end = 0, maxFruits = Integer.MIN_VALUE;

        for (end = 0; end < arr.length; end++) {
            char rightChar = arr[end];
            charFreqMap.put(rightChar, charFreqMap.getOrDefault(rightChar, 0) + 1);

            while (charFreqMap.size() > 2) {
                char leftChar = arr[start];
                charFreqMap.put(leftChar, charFreqMap.get(leftChar) - 1);
                if (charFreqMap.get(leftChar) == 0) {
                    charFreqMap.remove(leftChar);
                }
                start++;
            }

            maxFruits = Math.max(maxFruits, end - start + 1);
        }
        return maxFruits;
    }
}
