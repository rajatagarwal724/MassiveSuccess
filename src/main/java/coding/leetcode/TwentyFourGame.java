package coding.leetcode;

import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode Problem: 24 Game
 * URL: https://leetcode.com/problems/24-game/
 *
 * Problem Statement:
 * You are given an integer array cards of length 4. You have four cards, each containing a number from 1 to 9.
 * You need to judge whether these four cards can form the number 24 through the operations: +, -, *, /.
 * The division operator / represents real division, not integer division.
 * For example, 8 / (3 - 8/3) = 8 / (1/3) = 24.
 * You should return true if you can get 24 and false otherwise.
 */
public class TwentyFourGame {

    // Epsilon for floating-point comparison
    private static final double EPSILON = 1e-6;

    public boolean judgePoint24(int[] cards) {
        List<Double> list = new ArrayList<>();
        for (int card : cards) {
            list.add((double) card);
        }
        return solve(list);
    }

    private boolean solve(List<Double> nums) {
        // Base case: If only one number is left, check if it's 24.
        if (nums.size() == 1) {
            return Math.abs(nums.get(0) - 24.0) < EPSILON;
        }

        // Recursive step: Pick any two numbers and perform all operations.
        for (int i = 0; i < nums.size(); i++) {
            for (int j = 0; j < nums.size(); j++) {
                // Ensure we are picking two different numbers
                if (i == j) {
                    continue;
                }

                List<Double> nextRound = new ArrayList<>();
                // Add all other numbers to the list for the next recursive call
                for (int k = 0; k < nums.size(); k++) {
                    if (k != i && k != j) {
                        nextRound.add(nums.get(k));
                    }
                }

                double a = nums.get(i);
                double b = nums.get(j);

                // Perform all 6 possible operations (a+b, a-b, b-a, a*b, a/b, b/a)
                // Note: a+b and a*b are commutative, so we only need to check them once.
                // We check all combinations of i and j, so we don't need to check b+a and b*a separately.
                List<Double> results = new ArrayList<>();
                results.add(a + b);
                results.add(a - b);
                results.add(a * b);
                if (Math.abs(b) > EPSILON) {
                    results.add(a / b);
                }

                for (double res : results) {
                    List<Double> nextRoundWithResult = new ArrayList<>(nextRound);
                    nextRoundWithResult.add(res);
                    if (solve(nextRoundWithResult)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static void main(String[] args) {
        TwentyFourGame solution = new TwentyFourGame();

        // Example 1
        int[] cards1 = {4, 1, 8, 7};
        System.out.println("Input: [4, 1, 8, 7]");
        System.out.println("Output: " + solution.judgePoint24(cards1)); // Expected: true (8 * (7 - 4) + 1 = 25, (8-1)*(4-7/7)) -> (8-1)*(4-1)=21, (8/4+1)*7=21, (8-4)*7-1=27, 8/(1-7/4) -> 8/(-3/4)=-32/3, (7-1)*(4) = 24

        // Example 2
        int[] cards2 = {1, 2, 1, 2};
        System.out.println("\nInput: [1, 2, 1, 2]");
        System.out.println("Output: " + solution.judgePoint24(cards2)); // Expected: false

        // Example 3
        int[] cards3 = {1, 1, 1, 1};
        System.out.println("\nInput: [1, 1, 1, 1]");
        System.out.println("Output: " + solution.judgePoint24(cards3)); // Expected: false

        // Example 4
        int[] cards4 = {3, 3, 8, 8};
        System.out.println("\nInput: [3, 3, 8, 8]");
        System.out.println("Output: " + solution.judgePoint24(cards4)); // Expected: true (8 / (3 - 8/3)) = 24
    }
}
