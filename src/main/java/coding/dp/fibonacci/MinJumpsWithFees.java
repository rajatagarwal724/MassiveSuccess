package coding.dp.fibonacci;

public class MinJumpsWithFees {

    public int findMinFee(int[] fee) {
        int[] dp = new int[fee.length];
        dp[0] = fee[0];
        dp[1] = fee[0];
        dp[2] = fee[0];

        for (int i = 3; i < fee.length; i++) {
            dp[i] = Integer.MAX_VALUE;
        }

        for (int start = 1; start < fee.length - 1; start++) {
            for (int end = start + 1; end <= (start + 3) && end < fee.length; end++) {
                dp[end] = Math.min(dp[end], dp[start] + fee[start]);
            }
        }

        return dp[fee.length - 1];
    }

    public static void main(String[] args) {
        var sc = new MinJumpsWithFees();
        int[] fee = { 1, 2, 5, 2, 1, 2 };
        System.out.println(sc.findMinFee(fee));
        fee = new int[] { 2, 3, 4, 5 };
        System.out.println(sc.findMinFee(fee));
    }
}
