public class FairArrayAfterRemoval {
    
    /**
     * Solution: For each index, remove it and check if the resulting array is fair.
     * Key insight: After removing an element, the indices of remaining elements shift.
     * 
     * Time Complexity: O(n²) where n is the length of nums
     * Space Complexity: O(n) for creating subarrays
     */
    public int waysToMakeFair(int[] nums) {
        int count = 0;
        
        for (int i = 0; i < nums.length; i++) {
            if (isFairAfterRemoval(nums, i)) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Check if array becomes fair after removing element at given index
     */
    private boolean isFairAfterRemoval(int[] nums, int removeIndex) {
        int evenSum = 0;
        int oddSum = 0;
        
        for (int i = 0; i < nums.length; i++) {
            if (i == removeIndex) continue; // Skip the removed element
            
            // After removal, elements shift left, so we need to recalculate indices
            int newIndex = i > removeIndex ? i - 1 : i;
            
            if (newIndex % 2 == 0) {
                evenSum += nums[i];
            } else {
                oddSum += nums[i];
            }
        }
        
        return evenSum == oddSum;
    }
    
    /**
     * Optimized Solution: O(n) time complexity
     * 
     * Key insight: Instead of recalculating sums for each removal,
     * we can precompute prefix sums and calculate the result efficiently.
     */
    public int waysToMakeFairOptimized(int[] nums) {
        int n = nums.length;
        if (n == 1) return 0; // Can't make fair with 1 element
        
        // Precompute prefix sums for even and odd indices
        int[] evenPrefix = new int[n + 1];
        int[] oddPrefix = new int[n + 1];
        
        for (int i = 0; i < n; i++) {
            if (i % 2 == 0) {
                evenPrefix[i + 1] = evenPrefix[i] + nums[i];
                oddPrefix[i + 1] = oddPrefix[i];
            } else {
                oddPrefix[i + 1] = oddPrefix[i] + nums[i];
                evenPrefix[i + 1] = evenPrefix[i];
            }
        }
        
        int count = 0;
        
        for (int i = 0; i < n; i++) {
            // Calculate sums after removing element at index i
            int evenSum = evenPrefix[i] + (oddPrefix[n] - oddPrefix[i + 1]);
            int oddSum = oddPrefix[i] + (evenPrefix[n] - evenPrefix[i + 1]);
            
            if (evenSum == oddSum) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Most Optimized Solution: O(n) time, O(1) space
     * 
     * We can calculate the result in a single pass by tracking
     * the difference between even and odd sums.
     */
    public int waysToMakeFairMostOptimized(int[] nums) {
        int n = nums.length;
        if (n == 1) return 0;
        
        // Calculate total even and odd sums
        int totalEven = 0, totalOdd = 0;
        for (int i = 0; i < n; i++) {
            if (i % 2 == 0) {
                totalEven += nums[i];
            } else {
                totalOdd += nums[i];
            }
        }
        
        int count = 0;
        int evenSum = 0, oddSum = 0;
        
        for (int i = 0; i < n; i++) {
            // After removing element at index i:
            // - Elements before i keep their original positions
            // - Elements after i shift left by 1 position
            
            int remainingEven, remainingOdd;
            
            if (i % 2 == 0) {
                // Removing an even-indexed element
                remainingEven = evenSum + (totalOdd - oddSum);
                remainingOdd = oddSum + (totalEven - evenSum - nums[i]);
            } else {
                // Removing an odd-indexed element
                remainingEven = evenSum + (totalOdd - oddSum - nums[i]);
                remainingOdd = oddSum + (totalEven - evenSum);
            }
            
            if (remainingEven == remainingOdd) {
                count++;
            }
            
            // Update running sums
            if (i % 2 == 0) {
                evenSum += nums[i];
            } else {
                oddSum += nums[i];
            }
        }
        
        return count;
    }
    
    // Test cases
    public static void main(String[] args) {
        FairArrayAfterRemoval solution = new FairArrayAfterRemoval();
        
        // Test case 1: [2,1,6,4] -> Expected: 1
        int[] nums1 = {2, 1, 6, 4};
        System.out.println("Test 1: " + solution.waysToMakeFair(nums1)); // Should print 1
        System.out.println("Test 1 (Optimized): " + solution.waysToMakeFairOptimized(nums1));
        System.out.println("Test 1 (Most Optimized): " + solution.waysToMakeFairMostOptimized(nums1));
        
        // Test case 2: [1,1,1] -> Expected: 3
        int[] nums2 = {1, 1, 1};
        System.out.println("Test 2: " + solution.waysToMakeFair(nums2)); // Should print 3
        System.out.println("Test 2 (Optimized): " + solution.waysToMakeFairOptimized(nums2));
        System.out.println("Test 2 (Most Optimized): " + solution.waysToMakeFairMostOptimized(nums2));
        
        // Test case 3: [1,2,3] -> Expected: 0
        int[] nums3 = {1, 2, 3};
        System.out.println("Test 3: " + solution.waysToMakeFair(nums3)); // Should print 0
        System.out.println("Test 3 (Optimized): " + solution.waysToMakeFairOptimized(nums3));
        System.out.println("Test 3 (Most Optimized): " + solution.waysToMakeFairMostOptimized(nums3));
        
        // Additional test case: [6,1,7,4,1] -> Let's see what happens
        int[] nums4 = {6, 1, 7, 4, 1};
        System.out.println("Test 4: " + solution.waysToMakeFair(nums4));
        System.out.println("Test 4 (Optimized): " + solution.waysToMakeFairOptimized(nums4));
        System.out.println("Test 4 (Most Optimized): " + solution.waysToMakeFairMostOptimized(nums4));
    }
} 