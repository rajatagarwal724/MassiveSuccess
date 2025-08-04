package coding.leetcode;

/**
 * Count Sub-sequences having sum of their elements equal to K
 * 
 * Problem: Given an array and a target sum K, count the number of subsequences
 * whose elements sum to exactly K.
 * 
 * Examples:
 * Input: arr[] = {5, 5, 1}, K = 6
 * Output: 2 (subsequences: [5,1], [5,1])
 * 
 * Input: arr[] = {3, 2, 5, 1, 2, 4}, K = 5
 * Output: 5 (subsequences: [3,2], [5], [1,4], [2,1,2], [3,2])
 */
public class CountSubsequencesWithSumK {
    
    /**
     * Dynamic Programming approach to count subsequences with sum K
     * 
     * Time Complexity: O(n * K) where n is array length
     * Space Complexity: O(n * K) for the DP table
     * 
     * @param arr input array
     * @param k target sum
     * @return count of subsequences with sum k
     */
    public static int countSubsequences(int[] arr, int k) {
        if (arr == null || arr.length == 0 || k < 0) {
            return 0;
        }
        
        int n = arr.length;
        
        // dp[i][j] = number of subsequences using first i elements with sum j
        int[][] dp = new int[n + 1][k + 1];
        
        // Base case: empty subsequence has sum 0
        for (int i = 0; i <= n; i++) {
            dp[i][0] = 1;
        }
        
        // Fill the DP table
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j <= k; j++) {
                // Don't include current element
                dp[i][j] = dp[i - 1][j];
                
                // Include current element if possible
                if (j >= arr[i - 1]) {
                    dp[i][j] += dp[i - 1][j - arr[i - 1]];
                }
            }
        }
        
        return dp[n][k];
    }
    
    /**
     * Space-optimized version using 1D array
     * 
     * Time Complexity: O(n * K)
     * Space Complexity: O(K)
     */
    public static int countSubsequencesOptimized(int[] arr, int k) {
        if (arr == null || arr.length == 0 || k < 0) {
            return 0;
        }
        
        // dp[j] = number of subsequences with sum j
        int[] dp = new int[k + 1];
        dp[0] = 1; // empty subsequence
        
        // Process each element
        for (int num : arr) {
            // Traverse backwards to avoid using updated values
            for (int j = k; j >= num; j--) {
                dp[j] += dp[j - num];
            }
        }
        
        return dp[k];
    }
    
    /**
     * Recursive approach with memoization (alternative solution)
     */
    public static int countSubsequencesRecursive(int[] arr, int k) {
        if (arr == null || arr.length == 0 || k < 0) {
            return 0;
        }
        
        Integer[][] memo = new Integer[arr.length][k + 1];
        return helper(arr, 0, k, memo);
    }
    
    private static int helper(int[] arr, int index, int remainingSum, Integer[][] memo) {
        // Base cases
        if (remainingSum == 0) {
            return 1; // Found a valid subsequence
        }
        if (index >= arr.length || remainingSum < 0) {
            return 0; // No valid subsequence possible
        }
        
        // Check memoization
        if (memo[index][remainingSum] != null) {
            return memo[index][remainingSum];
        }
        
        // Two choices: include or exclude current element
        int exclude = helper(arr, index + 1, remainingSum, memo);
        int include = helper(arr, index + 1, remainingSum - arr[index], memo);
        
        memo[index][remainingSum] = exclude + include;
        return memo[index][remainingSum];
    }
    
    /**
     * Test method with provided examples
     */
    public static void main(String[] args) {
        // Test case 1
        int[] arr1 = {5, 5, 1};
        int k1 = 6;
        System.out.println("Test 1:");
        System.out.println("Array: [5, 5, 1], K = " + k1);
        System.out.println("DP Solution: " + countSubsequences(arr1, k1));
        System.out.println("Optimized Solution: " + countSubsequencesOptimized(arr1, k1));
        System.out.println("Recursive Solution: " + countSubsequencesRecursive(arr1, k1));
        System.out.println("Expected: 2");
        System.out.println();
        
        // Test case 2
        int[] arr2 = {3, 2, 5, 1, 2, 4};
        int k2 = 5;
        System.out.println("Test 2:");
        System.out.println("Array: [3, 2, 5, 1, 2, 4], K = " + k2);
        System.out.println("DP Solution: " + countSubsequences(arr2, k2));
        System.out.println("Optimized Solution: " + countSubsequencesOptimized(arr2, k2));
        System.out.println("Recursive Solution: " + countSubsequencesRecursive(arr2, k2));
        System.out.println("Expected: 5");
        System.out.println("Subsequences: [3,2], [5], [1,4], [2,1,2], [3,2]");
        System.out.println();
        
        // Additional test cases
        int[] arr3 = {1, 2, 3, 4, 5};
        int k3 = 5;
        System.out.println("Test 3:");
        System.out.println("Array: [1, 2, 3, 4, 5], K = " + k3);
        System.out.println("DP Solution: " + countSubsequences(arr3, k3));
        System.out.println("Optimized Solution: " + countSubsequencesOptimized(arr3, k3));
        System.out.println("Recursive Solution: " + countSubsequencesRecursive(arr3, k3));
        System.out.println("Subsequences: [5], [1,4], [2,3]");
        System.out.println();
        
        // Edge case: empty array
        int[] arr4 = {};
        int k4 = 5;
        System.out.println("Test 4 (Edge case - empty array):");
        System.out.println("Array: [], K = " + k4);
        System.out.println("Result: " + countSubsequences(arr4, k4));
        System.out.println("Expected: 0");
        System.out.println();
        
        // Edge case: K = 0
        int[] arr5 = {1, 2, 3};
        int k5 = 0;
        System.out.println("Test 5 (Edge case - K = 0):");
        System.out.println("Array: [1, 2, 3], K = " + k5);
        System.out.println("Result: " + countSubsequences(arr5, k5));
        System.out.println("Expected: 1 (empty subsequence)");
    }
}
