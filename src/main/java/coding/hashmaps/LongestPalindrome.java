package coding.hashmaps;

import java.util.HashMap;
import java.util.Map;

public class LongestPalindrome {

    public int longestPalindrome(String s) {
        int length = 0;
        boolean isCenterCharAvailable = false;
        Map<Character, Integer> charFreqMap = new HashMap<>();
        for (char ch: s.toCharArray()) {
            charFreqMap.put(ch, charFreqMap.getOrDefault(ch, 0) + 1);
        }
        for (Map.Entry<Character, Integer> entry: charFreqMap.entrySet()) {
            if (entry.getValue() % 2 == 0) {
                length += entry.getValue();
            } else {
                length += (entry.getValue() - 1);
                isCenterCharAvailable = true;
            }
        }

        if (isCenterCharAvailable) {
            length+=1;
        }
        return length;
    }

    public static void main(String[] args) {
        var sol = new LongestPalindrome();
        System.out.println(sol.longestPalindrome("applepie"));
        System.out.println(sol.longestPalindrome("aabbcc"));
        System.out.println(sol.longestPalindrome("bananas"));
    }
}
