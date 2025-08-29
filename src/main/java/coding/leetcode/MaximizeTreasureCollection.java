package coding.leetcode;

import java.util.Arrays;

/**
 * Problem: Given a list of treasures in a row of caves, find the maximum treasure you can collect
 * with the constraint that you cannot visit more than two consecutive caves.
 */
public class MaximizeTreasureCollection {

    /**
     * Solves the problem using dynamic programming.
     *
     * @param treasures An array representing the amount of treasure in each cave.
     * @return The maximum treasure that can be collected.
     */
    public static int maxTreasure(int[] treasures) {
        if (treasures == null || treasures.length == 0) {
            return 0;
        }

        int n = treasures.length;
        if (n == 1) {
            return treasures[0];
        }

        int[] dp = new int[n + 1];
        dp[0] = 0;
        dp[1] = treasures[0];

        if (n >= 2) {
            dp[2] = treasures[0] + treasures[1];
        }

        // Fill dp table using the recurrence relation
        for (int i = 3; i <= n; i++) {
            // Option 1: Skip the current cave (treasures[i-1])
            int skip = dp[i - 1];
            
            // Option 2: Take the current cave's treasure, skip the previous one.
            int takeOne = treasures[i - 1] + dp[i - 2];
            
            // Option 3: Take the current and previous cave's treasure.
            int takeTwo = treasures[i - 1] + treasures[i - 2] + dp[i - 3];
            
            dp[i] = Math.max(skip, Math.max(takeOne, takeTwo));
        }

        return dp[n];
    }

    public static void main(String[] args) {
        // Example Test Cases
        int[] treasures1 = {10, 20, 30, 40, 50};
        System.out.println("Max treasure for {10, 20, 30, 40, 50}: " + maxTreasure(treasures1)); // Expected: 120 (10+20 + 40+50)

        int[] treasures2 = {100, 40, 50, 10, 15, 80, 70};
        System.out.println("Max treasure for {100, 40, 50, 10, 15, 80, 70}: " + maxTreasure(treasures2)); // Expected: 365 (100+40 + 10+15 + 80+70)

        int[] treasures3 = {5, 1, 1, 10, 2, 3};
        System.out.println("Max treasure for {5, 1, 1, 10, 2, 3}: " + maxTreasure(treasures3)); // Expected: 21 (5 + 1+1 + 10 + 2+3)

        int[] treasures4 = {1, 2, 3, 4, 5};
        System.out.println("Max treasure for {1, 2, 3, 4, 5}: " + maxTreasure(treasures4)); // Expected: 12 (1+2 + 4+5)

        int[] treasures5 = {};
        System.out.println("Max treasure for {}: " + maxTreasure(treasures5)); // Expected: 0

        int[] treasures6 = {10};
        System.out.println("Max treasure for {10}: " + maxTreasure(treasures6)); // Expected: 10

        int[] treasures7 = {10, 20};
        System.out.println("Max treasure for {10, 20}: " + maxTreasure(treasures7)); // Expected: 30
        System.out.println("\nTreasures: " + Arrays.toString(treasures6));
        System.out.println("Max collection: " + maxTreasure(treasures6)); // Expected: 10
    }
}
