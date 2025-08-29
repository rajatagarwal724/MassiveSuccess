package companies.roku;

public class DecodeWays {
    public int numDecodings(String s) {
        if (s.length() == 0) {
            return 1;
        }

        int[] dp = new int[s.length() + 1];
        dp[0] = 1;

        dp[1] = s.charAt(0) == '0' ? 0 : 1;

        for (int i = 2; i <= s.length(); i++) {
            char elem = s.charAt(i - 1);

            if (elem != '0') {
                dp[i] = dp[i - 1];
            }

            int value = Integer.valueOf(s.substring(i - 2, i));
            if (value >= 10 && value <= 26) {
                dp[i] += dp[i - 2];
            }
        }

        return dp[dp.length - 1];
    }

    public static void main(String[] args) {
        var sol = new DecodeWays();
        System.out.println(
                sol.numDecodings("12")
        );

        System.out.println(
                sol.numDecodings("226")
        );

        System.out.println(
                sol.numDecodings("06")
        );
    }
}
