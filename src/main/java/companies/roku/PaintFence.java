package companies.roku;

import java.util.HashMap;
import java.util.Map;

public class PaintFence {

    public int numWays(int n, int k) {
        Map<Integer, Integer> dp = new HashMap<>();
        return numWays(n, k, dp);
    }

    private int numWays(int n, int k, Map<Integer, Integer> dp) {
        if (n == 1) {
            return k;
        }

        if (n == 2) {
            return k * k;
        }

        if (dp.containsKey(n)) {
            return dp.get(n);
        }

        int waysToPaintDifferently = (k - 1) * numWays(n - 1, k, dp);
        // Only possible when i-2 is painted differently and the number of ways is
        int waysToPaintWithSameColour =  (k - 1) * numWays(n - 2, k);

        dp.put(n, waysToPaintDifferently + waysToPaintWithSameColour);
        return dp.get(n);
    }

    private int numWaysBottom(int n, int k) {
        if (n == 1) {
            return k;
        }

        if (n == 2) {
            return k * k;
        }

        int[] dp = new int[n + 1];
        dp[1] = k;
        dp[2] = k * k;

        for (int i = 3; i <= n; i++) {
            dp[i] = (k - 1) * (dp[i - 1] + dp[i - 2]);
        }

        return dp[dp.length - 1];
    }

    public static void main(String[] args) {
        var sol = new PaintFence();
        System.out.println(sol.numWays(3, 2));
        System.out.println(sol.numWays(1, 1));
        System.out.println(sol.numWays(7, 2));

        System.out.println(sol.numWaysBottom(3, 2));
        System.out.println(sol.numWaysBottom(1, 1));
        System.out.println(sol.numWaysBottom(7, 2));
    }
}
