package companies.roku;

import java.util.HashSet;
import java.util.Set;

public class LongestSubStringWithoutRepeatingChars {

    public int lengthOfLongestSubstring(String s) {
        Set<Character> set = new HashSet<>();
        int start = 0, maxLen = 0;
        char[] arr = s.toCharArray();

        for (int end = 0; end < arr.length; end++) {
            var elem = arr[end];

            while (set.contains(elem)) {
                set.remove(arr[start++]);
            }

            set.add(elem);
            maxLen = Math.max(maxLen, end - start + 1);
        }
        return maxLen;
    }

    public static void main(String[] args) {
        var sol = new LongestSubStringWithoutRepeatingChars();
        System.out.println(sol.lengthOfLongestSubstring("abcabcbb"));
        System.out.println(sol.lengthOfLongestSubstring("bbbbb"));
        System.out.println(sol.lengthOfLongestSubstring("pwwkew"));
    }
}
