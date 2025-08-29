package companies.roku;

public class PaintHouseI {

    public int minCostOpt(int[][] costs) {
        int prevRed = costs[0][0], prevBlue = costs[0][1], prevGreen = costs[0][2];

        for(int house = 1; house < costs.length; house++) {
            int red = costs[house][0] + Math.min(prevBlue, prevGreen);
            int blue = costs[house][1] + Math.min(prevRed, prevGreen);
            int green = costs[house][2] + Math.min(prevRed, prevBlue);

            prevRed = red;
            prevBlue = blue;
            prevGreen = green;
        }

        return Math.min(prevRed, Math.min(prevBlue, prevGreen));
    }

    public int minCost(int[][] costs) {
        int[][] dp = new int[costs.length][costs[0].length];

        dp[0][0] = costs[0][0];
        dp[0][1] = costs[0][1];
        dp[0][2] = costs[0][2];

        for (int house = 1; house < costs.length; house++) {
            dp[house][0] = costs[house][0] + Math.min(dp[house - 1][1], dp[house - 1][2]);
            dp[house][1] = costs[house][1] + Math.min(dp[house - 1][0], dp[house - 1][2]);
            dp[house][2] = costs[house][2] + Math.min(dp[house - 1][0], dp[house - 1][1]);
        }
        int n = dp.length - 1;

        return Math.min(dp[n][0], Math.min(dp[n][1], dp[n][2]));
    }

    public static void main(String[] args) {
        var sol = new PaintHouseI();
        System.out.println(
                sol.minCost(new int[][]{
                        {17,2,17},
                        {16,16,5},
                        {14,3,19}
                })
        );

        System.out.println(
                sol.minCost(new int[][]{
                        {7,6,2}
                })
        );

        System.out.println(
                sol.minCostOpt(new int[][]{
                        {17,2,17},
                        {16,16,5},
                        {14,3,19}
                })
        );

        System.out.println(
                sol.minCostOpt(new int[][]{
                        {7,6,2}
                })
        );
    }
}
