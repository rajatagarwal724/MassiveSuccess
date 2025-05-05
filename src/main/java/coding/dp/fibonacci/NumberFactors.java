package coding.dp.fibonacci;

import coding.topk.FrequencyStack.Solution;

public class NumberFactors {
    public int countWays(int n) {

        if (n < 3) {
            return 1;
        }
        if (n == 3) {
            return 2;
        }

        int[] dp = new int[n + 1];
        dp[0] = 1;
        dp[1] = 1;
        dp[2] = 1;
        dp[3] = 2;

        for (int i = 4; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 3] + dp[i - 4];
        }

        return dp[n];
    }

    public static void main(String[] args) {
        var sol = new NumberFactors();
        System.out.println(sol.countWays(4));
        System.out.println(sol.countWays(5));
    }
}
