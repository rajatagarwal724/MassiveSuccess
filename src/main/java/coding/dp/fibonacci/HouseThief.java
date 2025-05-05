package coding.dp.fibonacci;

public class HouseThief {

    public int findMaxSteal(int[] wealth) {
        int n = wealth.length;
        int[] dp = new int[n];
        dp[0] = wealth[0];

        for (int i = 1; i < n; i++) {
            int pick = wealth[i];

            if (i > 1) {
                pick += dp[i - 2];
            }

            int dontPick = dp[i - 1];

            dp[i] = Math.max(pick, dontPick);
        }

        return dp[n - 1];
    }

    public static void main(String[] args) {
        var ht = new HouseThief();
        int[] wealth = {2, 5, 1, 3, 6, 2, 4};
        System.out.println(ht.findMaxSteal(wealth));
        wealth = new int[]{2, 10, 14, 8, 1};
        System.out.println(ht.findMaxSteal(wealth));
    }
}
