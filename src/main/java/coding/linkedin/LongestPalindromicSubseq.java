package coding.linkedin;

public class LongestPalindromicSubseq {
    public int longestPalindromeSubseq(String s) {
        int[][] memo = new int[s.length()][s.length()];
        return lps(s, 0, s.length() - 1, memo);
    }

    private int lps(String s, int startIdx, int endIdx, int[][] memo) {
        if (memo[startIdx][endIdx] != 0) {
            return memo[startIdx][endIdx];
        }

        if (startIdx > endIdx) {
            return 0;
        }

        if (startIdx == endIdx) {
            return 1;
        }

        if (s.charAt(startIdx) == s.charAt(endIdx)) {
            memo[startIdx][endIdx] = 2 + lps(s, startIdx + 1, endIdx - 1, memo);
            return memo[startIdx][endIdx];
        }

        int skipLeft = lps(s, startIdx + 1, endIdx, memo);
        int skipRight = lps(s, startIdx, endIdx - 1, memo);

        memo[startIdx][endIdx] = Math.max(skipLeft, skipRight);
        return memo[startIdx][endIdx];
    }

    public static void main(String[] args) {
        var sol = new LongestPalindromicSubseq();
        System.out.println(
                sol.longestPalindromeSubseq(
                "bbbab"
        ));

        System.out.println(
                sol.longestPalindromeSubseq(
                        "cbbd"
                ));
    }
}
