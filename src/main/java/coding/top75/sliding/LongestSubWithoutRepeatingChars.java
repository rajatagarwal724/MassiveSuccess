package coding.top75.sliding;

import java.util.HashSet;
import java.util.Set;

public class LongestSubWithoutRepeatingChars {

    public int lengthOfLongestSubstring(String str) {
        int maxLength = 0, start = 0;
        Set<Character> set = new HashSet<>();
        for (int end = 0; end < str.length(); end++) {
            var elem = str.charAt(end);
            while (set.contains(elem)) {
                var startElem = str.charAt(start);
                if (set.contains(startElem)) {
                    set.remove(startElem);
                }
                start++;
            }
            set.add(elem);
            maxLength = Math.max(maxLength, end - start + 1);
        }
        return maxLength;
    }

    public static void main(String[] args) {
        var sol = new LongestSubWithoutRepeatingChars();
        System.out.println(sol.lengthOfLongestSubstring("abcdaef"));
        System.out.println(sol.lengthOfLongestSubstring("aaaaa"));
        System.out.println(sol.lengthOfLongestSubstring("abrkaabcdefghijjxxx"));
    }
}
