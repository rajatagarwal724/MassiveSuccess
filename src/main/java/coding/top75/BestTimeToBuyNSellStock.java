package coding.top75;

public class BestTimeToBuyNSellStock {

    public int maxProfit(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;
        for (int i = 0; i < prices.length; i++) {
            minPrice = Math.min(minPrice, prices[i]);
            maxProfit = Math.max(maxProfit, prices[i] - minPrice);
        }
        return maxProfit;
    }

    public static void main(String[] args) {
        var sol = new BestTimeToBuyNSellStock();
        System.out.println(sol.maxProfit(new int[] {3, 2, 6, 5, 0, 3}));
        System.out.println(sol.maxProfit(new int[] {8, 6, 5, 2, 1}));
        System.out.println(sol.maxProfit(new int[] {1, 2}));
    }
}
