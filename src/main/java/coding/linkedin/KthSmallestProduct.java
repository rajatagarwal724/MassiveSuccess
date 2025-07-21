package coding.linkedin;

import java.util.*;

public class KthSmallestProduct {

    public long kthSmallestProduct(int[] nums1, int[] nums2, long k) {
        long left = -10_000_000_000L, right = 10_000_000_000L;

        while (left < right) {
            long mid = left + (right - left) / 2;
            long count = countLessOrEqual(nums1, nums2, mid);

            if (count >= k) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    private long countLessOrEqual(int[] nums1, int[] nums2, long mid) {
        long count = 0;
        for (int a : nums1) {
            if (a > 0) {
                // Binary search nums2[j] <= mid / a
                int l = 0, r = nums2.length;
                while (l < r) {
                    int m = (l + r) / 2;
                    if ((long)a * nums2[m] <= mid) l = m + 1;
                    else r = m;
                }
                count += l;
            } else if (a < 0) {
                // Binary search nums2[j] >= ceil(mid / a)
                int l = 0, r = nums2.length;
                while (l < r) {
                    int m = (l + r) / 2;
                    if ((long)a * nums2[m] <= mid) r = m;
                    else l = m + 1;
                }
                count += nums2.length - l;
            } else { // a == 0
                if (mid >= 0) count += nums2.length;
            }
        }
        return count;
    }
    
    /**
     * Example usage and test cases
     */
    public static void main(String[] args) {
        KthSmallestProduct solution = new KthSmallestProduct();
        
        // Test Case 1
        int[] nums1 = {2, 5};
        int[] nums2 = {3, 4};
        long k = 2;
        System.out.println("Test 1: " + solution.kthSmallestProduct(nums1, nums2, k)); // Expected: 8
        
        // Test Case 2
//        nums1 = new int[]{-4, -2, 0, 3};
//        nums2 = new int[]{2, 4};
//        k = 6;
//        System.out.println("Test 2: " + solution.kthSmallestProduct(nums1, nums2, k)); // Expected: 0
//
//        // Test Case 3
//        nums1 = new int[]{-2, -1, 0, 1, 2};
//        nums2 = new int[]{-3, -1, 2, 4, 5};
//        k = 3;
//        System.out.println("Test 3: " + solution.kthSmallestProduct(nums1, nums2, k)); // Expected: -6
        
        // Verify with brute force for small cases
//        System.out.println("\nBrute force verification:");
//        nums1 = new int[]{2, 5};
//        nums2 = new int[]{3, 4};
//        k = 2;
//        System.out.println("Brute force result: " + solution.kthSmallestProductBruteForce(nums1, nums2, k));
    }
}
