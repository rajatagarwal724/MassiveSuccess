package companies.roku;

public class ClimbStairs {

    public int climbStairs(int n) {
        if (n == 1) {
            return 1;
        }

        if (n == 2) {
            return 2;
        }

        return climbStairs(n - 1) + climbStairs(n - 2);
    }

    public int climbStairsB(int n) {
        if (n == 1) {
            return 1;
        }

        if (n == 2) {
            return 2;
        }
        int[] dp = new int[n + 1];
        for (int i = 3; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }

    public static void main(String[] args) {
        var sol = new ClimbStairs();
        System.out.println(sol.climbStairs(3));
        System.out.println(sol.climbStairsB(3));
        System.out.println(sol.climbStairs(4));
        System.out.println(sol.climbStairs(5));
    }
}
