package companies.doordash;

public class MinimumStepsToMakeAnagrams {

    public int minSteps(String s, String t) {
        int[] freqArr = new int[26];
        int ans = 0;

        for (int i = 0; i < s.length(); i++) {
            char sChar = s.charAt(i);
            char tChar = t.charAt(i);

            freqArr[tChar - 'a']++;
            freqArr[sChar - 'a']--;
        }

        for (int i = 0; i < 26; i++) {
            ans += Math.max(0, freqArr[i]);
        }

        return ans;
    }

    public static void main(String[] args) {
        var sol = new MinimumStepsToMakeAnagrams();
//        System.out.println(
//                sol.minSteps("bab", "aba")
//        );
        System.out.println(
                sol.minSteps("leetcode", "practice")
        );
//        System.out.println(
//                sol.minSteps("anagram", "mangaar")
//        );
    }
}
