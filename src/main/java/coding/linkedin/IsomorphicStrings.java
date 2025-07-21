package coding.linkedin;

import java.util.HashMap;
import java.util.Map;

public class IsomorphicStrings {

    public boolean isIsomorphic(String str, String t) {
        Map<Character, Character> srcTargetMap = new HashMap<>();
        Map<Character, Character> targetSrcMap = new HashMap<>();

        if (str.length() != t.length()) {
            return false;
        }

        for (int i = 0; i < str.length(); i++) {
            var firstChar = str.charAt(i);
            var secondChar = t.charAt(i);

            if (srcTargetMap.containsKey(firstChar) && srcTargetMap.get(firstChar) != secondChar) {
                return false;
            }

            if (targetSrcMap.containsKey(secondChar) && targetSrcMap.get(secondChar) != firstChar) {
                return false;
            }

            srcTargetMap.put(firstChar, secondChar);
            targetSrcMap.put(secondChar, firstChar);
        }

        return true;
    }

    public static void main(String[] args) {
        var sol = new IsomorphicStrings();
        System.out.println(sol.isIsomorphic("abb", "cdd"));
        System.out.println(sol.isIsomorphic("cbcrt", "abaxv"));
        System.out.println(sol.isIsomorphic("abcd", "bbcd"));
    }
}
