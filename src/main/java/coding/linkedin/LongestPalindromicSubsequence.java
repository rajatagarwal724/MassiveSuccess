package coding.linkedin;

public class LongestPalindromicSubsequence {
    public int findLPSLength(String st) {
        return findLPSLengthRecursive(st, 0, st.length() - 1);
    }

    public int findLPSLengthDP(String st) {
        Integer[][] dp = new Integer[st.length()][st.length()];
        return findLPSLengthRecursiveDP(st, 0, st.length() - 1, dp);
    }

    private int findLPSLengthRecursiveDP(String st, int startIdx, int endIdx, Integer[][] dp) {
        if (startIdx > endIdx) {
            return 0;
        }

        if (startIdx == endIdx) {
            return 1;
        }

        if (dp[startIdx][endIdx] != null) {
            return dp[startIdx][endIdx];
        }

        if (st.charAt(startIdx) == st.charAt(endIdx)) {
            dp[startIdx][endIdx] = 2 + findLPSLengthRecursiveDP(st, startIdx + 1, endIdx - 1, dp);
        } else {

            int skipFromLeft = findLPSLengthRecursiveDP(st, startIdx + 1, endIdx, dp);
            int skipFromRight = findLPSLengthRecursiveDP(st, startIdx, endIdx - 1, dp);

            dp[startIdx][endIdx] = Math.max(skipFromLeft, skipFromRight);
        }

        return dp[startIdx][endIdx];
    }

    private int findLPSLengthRecursive(String st, int startIdx, int endIdx) {
        if (startIdx > endIdx) {
            return 0;
        }

        // every sequence with one element is a palindrome of length 1
        if (startIdx == endIdx) {
            return 1;
        }

        if (st.charAt(startIdx) == st.charAt(endIdx)) {
            return 2 + findLPSLengthRecursive(st, startIdx + 1, endIdx - 1);
        }

        int skipFromLeft = findLPSLengthRecursive(st, startIdx + 1, endIdx);
        int skipFromRight = findLPSLengthRecursive(st, startIdx, endIdx - 1);

        return Math.max(skipFromLeft, skipFromRight);
    }

    public static void main(String[] args) {
        var sol = new LongestPalindromicSubsequence();
        System.out.println(
                sol.findLPSLength("abdbca")
        );

        System.out.println(
                sol.findLPSLength("cddpd")
        );

        System.out.println(
                sol.findLPSLength("pqr")
        );

        System.out.println(
                sol.findLPSLengthDP("abdbca")
        );

        System.out.println(
                sol.findLPSLengthDP("cddpd")
        );

        System.out.println(
                sol.findLPSLengthDP("pqr")
        );
    }
}
