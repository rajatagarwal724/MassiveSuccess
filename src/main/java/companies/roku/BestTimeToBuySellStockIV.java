package companies.roku;

public class BestTimeToBuySellStockIV {

    private int[] prices;
    private int[][][] memo;

    public int maxProfit(int k, int[] prices) {
        this.prices = prices;
        this.memo = new int[prices.length][k + 1][2];
        return dp(0, k, 0);
    }

    private int dp(int day, int remainingTxs, int holding) {
        if (remainingTxs == 0 || day == prices.length) {
            return 0;
        }

        if (memo[day][remainingTxs][holding] != 0) {
            return memo[day][remainingTxs][holding];
        }

        int doNothing = dp(day + 1, remainingTxs, holding);

        int doSomething;

        if (holding == 0) {
            // Buy
            doSomething = -prices[day] + dp(day + 1, remainingTxs, 1);
        } else {
            doSomething = prices[day] + dp(day + 1, remainingTxs - 1, 0);
        }

        memo[day][remainingTxs][holding] = Math.max(doNothing, doSomething);
        return memo[day][remainingTxs][holding];
    }


    public static void main(String[] args) {
        var sol = new BestTimeToBuySellStockIV();
        System.out.println(sol.maxProfit(2, new int[] {2, 4, 1}));
        System.out.println(sol.maxProfit(2, new int[] {3,2,6,5,0,3}));
    }
}
