package companies.roku.repeat;

public class BuySellStockIV {

    int[] prices;
    int[][][] memo;

    public int maxProfit(int k, int[] prices) {
        this.prices = prices;
        this.memo = new int[prices.length][k + 1][2];
        return recurse(0, prices, memo, k, 0);
    }

    private int recurse(int day, int[] prices, int[][][] memo, int remainingTxs, int holding) {
        if (day == prices.length || remainingTxs == 0) {
            return 0;
        }

        if (memo[day][remainingTxs][holding] != 0) {
            return memo[day][remainingTxs][holding];
        }

        int doNothing = recurse(day + 1, prices, memo, remainingTxs, holding);
        int doSomething;

        if (holding > 0) {
            doSomething = prices[day] + recurse(day + 1, prices, memo, remainingTxs - 1, 0);
        } else {
            doSomething = -prices[day] + recurse(day + 1, prices, memo, remainingTxs, 1);
        }

        return memo[day][remainingTxs][holding] = Math.max(doNothing, doSomething);
    }

    public static void main(String[] args) {

    }
}
