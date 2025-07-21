package coding.linkedin;

import java.util.HashSet;
import java.util.Set;

public class LongestSubstringWithoutRepeatingCharacters {

    public int lengthOfLongestSubstring(String str) {
        int maxLength = 0;
        int start = 0;
        Set<Character> set = new HashSet<>();
        char[] arr = str.toCharArray();

        for (int end = 0; end < arr.length; end++) {
            var elem = arr[end];

            while (set.contains(elem)) {
                set.remove(arr[start]);
                start++;
            }
            set.add(elem);
            maxLength = Math.max(maxLength, end - start + 1);
        }
        return maxLength;
    }


    public static void main(String[] args) {
        var sol = new LongestSubstringWithoutRepeatingCharacters();

        System.out.println(
                sol.lengthOfLongestSubstring("abcdaef")
        );

        System.out.println(
                sol.lengthOfLongestSubstring("aaaa")
        );

        System.out.println(
                sol.lengthOfLongestSubstring("abrkaabcdefghijjxxx")
        );

    }
}
