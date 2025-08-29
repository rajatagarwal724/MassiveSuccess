package coding.leetcode;

import java.util.Stack;

/**
 * LeetCode Problem: https://leetcode.com/problems/largest-rectangle-in-histogram/
 *
 * Given an array of integers heights representing the histogram's bar height where the width of each bar is 1,
 * return the area of the largest rectangle in the histogram.
 */
public class LargestRectangleInHistogram {

    /**
     * Calculates the largest rectangular area in a histogram using a monotonic stack.
     *
     * The core idea is to find, for each bar, the largest rectangle that can be formed with that bar as the smallest height.
     * This requires finding the first bar to the left and the first bar to the right that are shorter than the current bar.
     *
     * A monotonic (increasing) stack helps find these boundaries efficiently in a single pass.
     * The stack stores indices of bars with increasing heights.
     *
     * Algorithm:
     * 1. Iterate through the histogram bars. A virtual bar of height 0 is considered at the end to process all remaining bars in the stack.
     * 2. If the current bar is taller than or equal to the bar at the stack's top, push its index onto the stack.
     * 3. If the current bar is shorter, it means the bar at the stack's top cannot extend further right. We pop it and calculate its area.
     *    - Height `h`: The height of the popped bar.
     *    - Right boundary `r`: The current index `i`.
     *    - Left boundary `l`: The index of the new stack top.
     *    - Width `w`: `r - l - 1`.
     * 4. Keep track of the maximum area found.
     *
     * Time Complexity: O(n) because each index is pushed and popped exactly once.
     * Space Complexity: O(n) for the stack in the worst case (a strictly increasing histogram).
     *
     * @param heights An array of integers representing the histogram.
     * @return The area of the largest rectangle.
     */
    public int largestRectangleArea(int[] heights) {
        if (heights == null || heights.length == 0) {
            return 0;
        }

        Stack<Integer> stack = new Stack<>();
        int maxArea = 0;
        int n = heights.length;

        for (int i = 0; i <= n; i++) {
            // Use a virtual bar of height 0 at the end to pop all remaining bars from the stack
            int currentHeight = (i == n) ? 0 : heights[i];

            while (!stack.isEmpty() && currentHeight < heights[stack.peek()]) {
                int h = heights[stack.pop()];
                int w = stack.isEmpty() ? i : i - stack.peek() - 1;
                maxArea = Math.max(maxArea, h * w);
            }
            stack.push(i);
        }

        return maxArea;
    }

    public static void main(String[] args) {
        LargestRectangleInHistogram solution = new LargestRectangleInHistogram();

        // Test cases
        int[][] testCases = {
            {2, 1, 5, 6, 2, 3},   // Expected: 10
            {2, 4},             // Expected: 4
            {1},                // Expected: 1
            {0, 9},             // Expected: 9
            {6, 2, 5, 4, 5, 1, 6} // Expected: 12
        };

        int[] expectedResults = {10, 4, 1, 9, 12};

        for (int i = 0; i < testCases.length; i++) {
            int result = solution.largestRectangleArea(testCases[i]);
            System.out.printf("Test Case %d: %s%n", i + 1, java.util.Arrays.toString(testCases[i]));
            System.out.printf("Expected: %d, Got: %d%n", expectedResults[i], result);
            System.out.println("Result: " + (expectedResults[i] == result ? "PASS" : "FAIL"));
            System.out.println("-------------------------------------");
        }
    }
}
