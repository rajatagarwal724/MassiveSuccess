package companies.roku;

import java.util.Map;

public class MinimumPathSum {

    public int minPathSum(int[][] grid) {
        int[][] dp = new int[grid.length][grid[0].length];

        dp[0][0] = grid[0][0];
        for (int i = 1; i < grid[0].length; i++) {
            dp[0][i] = dp[0][i - 1] + grid[0][i];
        }

        for (int j = 1; j < grid.length; j++) {
            dp[j][0] = dp[j - 1][0] + grid[j][0];
        }

        for (int i = 1; i < grid.length; i++) {
            for (int j = 1; j < grid[0].length; j++) {
                dp[i][j] = grid[i][j] + Math.min(dp[i - 1][j], dp[i][j - 1]);
            }
        }

        return dp[dp.length - 1][dp[0].length - 1];
    }

    public static void main(String[] args) {
        var sol = new MinimumPathSum();
        System.out.println(
                sol.minPathSum(
                        new int[][]{
                                {1,3,1},
                                {1,5,1},
                                {4,2,1}
                        }
                )
        );

        System.out.println(
                sol.minPathSum(
                        new int[][]{
                                {1,2,3},
                                {4,5,6}
                        }
                )
        );
    }
}
