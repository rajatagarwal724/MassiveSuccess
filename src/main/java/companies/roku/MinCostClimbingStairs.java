package companies.roku;

public class MinCostClimbingStairs {
    public int minCostClimbingStairs(int[] cost) {
        int[] dp = new int[cost.length + 1];
        // Cost to Reach Step 0 and 1 is
        dp[0] = 0;
        dp[1] = 0;
        for (int i = 2; i <= cost.length; i++) {
            int takeOneSteps = dp[i - 1] + cost[i - 1];
            int takeTwoSteps = dp[i - 2] + cost[i - 2];
            dp[i] = Math.min(takeOneSteps, takeTwoSteps);
        }
        return dp[dp.length - 1];
    }

    public static void main(String[] args) {
        var sol = new MinCostClimbingStairs();
        System.out.println(sol.minCostClimbingStairs(new int[] {10,15,20}));
        System.out.println(sol.minCostClimbingStairs(new int[] {1,100,1,1,1,100,1,1,100,1}));
    }
}
