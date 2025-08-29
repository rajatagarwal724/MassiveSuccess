package companies.roku;

public class BestTimeToBuyAndSellCooldown {
    int[] prices;
    int[][] memo;
    public int maxProfit(int[] prices) {
        this.prices = prices;
        this.memo = new int[prices.length][2];
        return dp(0, 0);
    }

    private int dp(int day, int holding) {
        if (day >= prices.length) {
            return 0;
        }

        if(memo[day][holding] != 0) {
            return memo[day][holding];
        }

        int doNothing = dp(day + 1, holding);

        int doSomething;

        if (holding == 1) {
            doSomething = prices[day] + dp(day + 2, 0);
        } else {
            doSomething = -prices[day] + dp(day + 1, 1);
        }

        memo[day][holding] = Math.max(doNothing, doSomething);
        return memo[day][holding];
    }

    public static void main(String[] args) {
        var sol = new BestTimeToBuyAndSellCooldown();
        System.out.println(sol.maxProfit(new int[] {1,2,3,0,2}));
        System.out.println(sol.maxProfit(new int[] {1}));
    }
}
