package companies.roku.repeat;

public class BuySellStockWithFee {

    int[] prices;
    int[][] memo;

    public int maxProfit(int[] prices, int fee) {
        this.prices = prices;
        this.memo = new int[prices.length][2];
        return recurse(0, prices, fee, memo, 0);
    }

    private int recurse(int day, int[] prices, int fee, int[][] memo, int holding) {
        if (day == prices.length) {
            return 0;
        }

        if (memo[day][holding] != 0) {
            return memo[day][holding];
        }

        int doNothing = recurse(day + 1, prices, fee, memo, holding);

        int doSomething;
        if (holding > 0) {
            doSomething = prices[day] - fee + recurse(day + 1, prices, fee, memo, 0);
        } else {
            doSomething = -prices[day] + recurse(day + 1, prices, fee, memo, 1);
        }

        return memo[day][holding] = Math.max(doNothing, doSomething);
    }

    public static void main(String[] args) {
        var sol = new BuySellStockWithFee();
        System.out.println(
                sol.maxProfit(new int[] {1,3,2,8,4,9}, 2)
        );

        System.out.println(
                sol.maxProfit(new int[] {1,3,7,5,10,3}, 3)
        );
    }
}
