package companies.roku.repeat;

import java.util.Arrays;

public class CoinChangeII {

    public int change(int amount, int[] coins) {
        int index = 0;
        int currSum = 0;
        int[][] memo = new int[coins.length][amount + 1];
        for (int[] row: memo) {
            Arrays.fill(row, -1);
        }
        return numberOfWays(index, coins, currSum, amount, memo);
    }

    private int numberOfWays(int index, int[] coins, int currSum, int amount, int[][] memo) {
        if (index == coins.length) {
            return 0;
        }

        if (currSum == amount) {
            return 1;
        }

        if (memo[index][currSum] != -1) {
            return memo[index][currSum];
        }
        int ways = 0;
        if (coins[index] > (amount - currSum)) {
            ways = numberOfWays(index + 1, coins, currSum, amount, memo);
        } else {
            int notPicked = numberOfWays(index + 1, coins, currSum, amount, memo);
            int picked = numberOfWays(index, coins, currSum + coins[index], amount, memo);
            ways = picked + notPicked;
        }

        return memo[index][currSum] = ways;
    }

    public static void main(String[] args) {
        var sol = new CoinChangeII();

        System.out.println(
                sol.change(5, new int[] {1, 2, 5})
        );
    }
}
