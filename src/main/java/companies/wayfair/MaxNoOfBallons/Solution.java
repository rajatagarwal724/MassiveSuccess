package companies.wayfair.MaxNoOfBallons;

import java.util.HashMap;
import java.util.Map;

/**
 * https://leetcode.com/problems/maximum-number-of-balloons/description/
 *
 * Given a string text, you want to use the characters of text to form as many instances of the word "balloon" as possible.
 *
 * You can use each character in text at most once. Return the maximum number of instances that can be formed.
 *
 *
 *
 * Example 1:
 * Input: text = "nlaebolko"
 * Output: 1
 *
 * Example 2:
 * Input: text = "loonbalxballpoon"
 * Output: 2
 * Example 3:
 *
 * Input: text = "leetcode"
 * Output: 0
 *
 *
 * Constraints:
 *
 * 1 <= text.length <= 104
 * text consists of lower case English letters only.
 *
 *
 *
 */
public class Solution {
    public int maxNumberOfBalloons(String text) {
        Map<Character, Integer> charFreqMap = new HashMap<>();
        for (char ch: text.toCharArray()) {
            charFreqMap.put(ch, charFreqMap.getOrDefault(ch, 0) + 1);
        }

        int result = Integer.MAX_VALUE;

        result = Math.min(result, charFreqMap.getOrDefault('b', 0));
        result = Math.min(result, charFreqMap.getOrDefault('a', 0));
        result = Math.min(result, charFreqMap.getOrDefault('l', 0) / 2);
        result = Math.min(result, charFreqMap.getOrDefault('o', 0) / 2);
        result = Math.min(result, charFreqMap.getOrDefault('n', 0));

        return result;
    }
}
