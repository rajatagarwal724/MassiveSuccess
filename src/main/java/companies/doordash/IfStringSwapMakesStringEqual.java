package companies.doordash;

import java.util.Arrays;

public class IfStringSwapMakesStringEqual {

    public boolean areAlmostEqual(String s1, String s2) {
        if (null == s1 && null == s2) {
            return true;
        }

        if (null == s1 || null == s2) {
            return false;
        }

        if (s1.equals(s2)) {
            return true;
        }

        if (s1.length() != s2.length()) {
            return false;
        }

        char[] s1FreqArr = new char[26];
        char[] s2FreqArr = new char[26];
        int numDiff = 0;

        for (int i = 0; i < s1.length(); i++) {
            char s1Char = s1.charAt(i);
            char s2Char = s2.charAt(i);

            if (s1Char != s2Char) {
                numDiff++;
            }

            if (numDiff > 2) {
                return false;
            }

            s1FreqArr[s1Char - 'a']++;
            s2FreqArr[s2Char - 'a']++;
        }

        return Arrays.equals(s1FreqArr, s2FreqArr);
    }

    public static void main(String[] args) {
        var sol = new IfStringSwapMakesStringEqual();
        System.out.println(
                sol.areAlmostEqual(
                        "bank", "kanb"
                )
        );

        System.out.println(
                sol.areAlmostEqual(
                        "attack", "defend"
                )
        );

        System.out.println(
                sol.areAlmostEqual(
                        "kelb", "kelb"
                )
        );
    }
}
