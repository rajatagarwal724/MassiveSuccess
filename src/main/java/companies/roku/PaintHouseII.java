package companies.roku;

import java.util.Arrays;

public class PaintHouseII {

    public int minCostII(int[][] costs) {
        int[][] dp = new int[costs.length][costs[0].length];
        for (int[] row: dp) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        dp[0] = costs[0].clone();
        int result = Integer.MAX_VALUE;
        for (int house = 1; house < costs.length; house++) {
            int[] prevHouseColorCosts = dp[house - 1].clone();
            int idx = 0;
            while (idx < prevHouseColorCosts.length) {
                for (int colorIdx = 0; colorIdx < prevHouseColorCosts.length; colorIdx++) {
                    if (idx == colorIdx) {
                        continue;
                    }
                    dp[house][idx] = Math.min(dp[house][idx], costs[house][idx] + prevHouseColorCosts[colorIdx]);
                }
                idx++;
            }
        }

//        System.out.println(Arrays.deepToString(dp));

        int[] lastRow = dp[dp.length - 1];
        for (int i = 0; i < lastRow.length; i++) {
            result = Math.min(result, lastRow[i]);
        }

        return result;
    }

    public static void main(String[] args) {
        var sol = new PaintHouseII();
        System.out.println(
                sol.minCostII(
                        new int[][] {
                                {1,5,3},
                                {2,9,4}
                        }
                )
        );

        System.out.println(
                sol.minCostII(
                        new int[][] {
                                {1,3},
                                {2,4}
                        }
                )
        );

        System.out.println(
                sol.minCostII(
                        new int[][] {
                                {10,15,12,14,18,5},
                                {5,12,18,13,15,8},
                                {4,7,4,2,10,18},
                                {20,9,9,19,20,5},
                                {10,15,10,15,16,20},
                                {9,6,11,10,12,11},
                                {7,10,6,12,20,8},
                                {3,4,4,18,10,2}
                        }
                )
        );
    }
}
