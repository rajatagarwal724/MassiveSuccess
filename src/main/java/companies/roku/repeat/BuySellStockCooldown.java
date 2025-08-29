package companies.roku.repeat;

public class BuySellStockCooldown {

    int[] prices;
    int[][] memo;

    public int maxProfit(int[] prices) {
        this.prices = prices;
        this.memo = new int[prices.length][2];
        return recurse(0, prices, memo, 0);
    }

    private int recurse(int day, int[] prices, int[][] memo, int holding) {
        if (day >= prices.length) {
            return 0;
        }

        if (memo[day][holding] != 0) {
            return memo[day][holding];
        }

        int doNothing = recurse(day + 1, prices, memo, holding);

        int doSomething;

        if (holding > 0) {
            doSomething = prices[day] + recurse(day + 2, prices, memo, 0);
        } else {
            doSomething = -prices[day] + recurse(day + 1, prices, memo, 1);
        }

        return memo[day][holding] = Math.max(doNothing, doSomething);
    }

    public static void main(String[] args) {

    }
}
