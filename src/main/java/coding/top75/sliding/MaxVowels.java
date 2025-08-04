package coding.top75.sliding;

import java.util.Set;

public class MaxVowels {

    public int maxVowels(String s, int k) {
        int res = Integer.MIN_VALUE;
        int maxVowels = 0;
        int start = 0;
        Set<Character> vowels = Set.of('a','e','i','o','u');
        for (int end = 0; end < s.toLowerCase().length(); end++) {
            char elem = s.charAt(end);
            if (vowels.contains(elem)) {
                maxVowels++;
            }
            res = Math.max(res, maxVowels);

            if (end >= k - 1) {
                if (vowels.contains(s.charAt(start))) {
                    maxVowels--;
                }
                start++;
            }
        }
        return res;
    }

    public static void main(String[] args) {
        var sol = new MaxVowels();
        System.out.println(sol.maxVowels("azerdii", 4));
        System.out.println(sol.maxVowels("abcde", 2));
        System.out.println(sol.maxVowels("zaeixoyuxyz", 7));
    }
}
