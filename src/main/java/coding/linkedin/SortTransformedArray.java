package coding.linkedin;

import java.util.Arrays;

public class SortTransformedArray {

    /**
     * Applies a quadratic function f(x) = ax² + bx + c to each element in nums
     * and returns the result in sorted order.
     * 
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public int[] sortTransformedArray(int[] nums, int a, int b, int c) {
        int n = nums.length;
        int[] result = new int[n];
        int left = 0;
        int right = n - 1;
        
        int index = a >= 0 ? n - 1 : 0; // Fill from end if a >= 0, from start if a < 0
        
        // Two-pointer approach
        while (left <= right) {
            int leftVal = calculate(nums[left], a, b, c);
            int rightVal = calculate(nums[right], a, b, c);
            
            if (a >= 0) { // Parabola opens upward, larger values at extremes
                if (leftVal >= rightVal) {
                    result[index] = leftVal;
                    left++;
                } else {
                    result[index] = rightVal;
                    right--;
                }
                index--;
            } else { // Parabola opens downward, smaller values at extremes
                if (leftVal <= rightVal) {
                    result[index] = leftVal;
                    left++;
                } else {
                    result[index] = rightVal;
                    right--;
                }
                index++;
            }
        }
        
        return result;
    }
    
    private int calculate(int x, int a, int b, int c) {
        // Calculate f(x) = ax² + bx + c
        return a * x * x + b * x + c;
    }
    
    public static void main(String[] args) {
        SortTransformedArray solution = new SortTransformedArray();
        
        // Test case 1: a > 0
        int[] nums1 = {-4, -2, 2, 4};
        int[] result1 = solution.sortTransformedArray(nums1, 1, 3, 5);
        System.out.println("Example 1: " + Arrays.toString(result1)); // Expected: [3, 9, 15, 33]
        
        // Test case 2: a < 0
        int[] nums2 = {-4, -2, 2, 4};
        int[] result2 = solution.sortTransformedArray(nums2, -1, 3, 5);
        System.out.println("Example 2: " + Arrays.toString(result2)); // Expected: [-23, -5, 1, 7]
    }
}
