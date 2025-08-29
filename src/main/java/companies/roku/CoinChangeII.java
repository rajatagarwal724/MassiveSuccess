package companies.roku;

import java.util.Arrays;
import java.util.Map;

public class CoinChangeII {

    public int change(int amount, int[] coins) {
        int[][] memo = new int[coins.length][amount + 1];
        for (int[] row: memo) {
            Arrays.fill(row, -1);
        }

        return numberOfWays(0, amount, coins, memo);
    }

    private int numberOfWays(int index, int remainingAmount, int[] coins, int[][] memo) {
        if (index == coins.length) {
            return 0;
        }

        if (remainingAmount == 0) {
            return 1;
        }

        if (memo[index][remainingAmount] != -1) {
            return memo[index][remainingAmount];
        }

        if (coins[index] > remainingAmount) {
            // can't pick
            memo[index][remainingAmount] = numberOfWays(index + 1, remainingAmount, coins, memo);
        } else {
            int picked = numberOfWays(index, remainingAmount - coins[index], coins, memo);
            int notPicked = numberOfWays(index + 1, remainingAmount, coins, memo);
            memo[index][remainingAmount] = picked + notPicked;
        }
        return memo[index][remainingAmount];
    }

    public static void main(String[] args) {
        var sol = new CoinChangeII();
        System.out.println(sol.change(5, new int[] {1, 2, 5}));
        System.out.println(sol.change(3, new int[] {2}));
        System.out.println(sol.change(10, new int[] {10}));
    }
}
