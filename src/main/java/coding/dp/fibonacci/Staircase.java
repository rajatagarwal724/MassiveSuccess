package coding.dp.fibonacci;

public class Staircase {

    public int countWays(int n) {
        if (n == 0) {
            return 1;
        }
        if (n < 3) {
            return n;
        }
        if (n == 3) {
            return 4;
        }
        int[] dp = new int[n + 1];
        dp[0] = 1;
        dp[1] = 1;
        dp[2] = 2;
        dp[3] = 4;

        for (int i = 3; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2] + dp[i - 2];
        }

        return dp[n];
    }

    public int countWays_1(int n) {
        if (n == 0) {
            return 1;
        }
        if (n < 3) {
            return n;
        }

        int n1 = 1, n2 = 1, n3 = 2;

        for (int i = 3; i <= n; i++) {
            int temp = n1 + n2 + n3;
            n1 = n2;
            n2 = n3;
            n3 = temp;
        }

        return n3;
    }

    public static void main(String[] args) {
        var sol = new Staircase();
        System.out.println(sol.countWays_1(4));
    }
}
