package companies.doordash;

import java.util.*;

public class SlidingWindowMedianOptimized {
    
    public double[] medianSlidingWindow(int[] nums, int k) {
        double[] result = new double[nums.length - k + 1];
        
        // TreeMap to maintain sorted order with counts
        TreeMap<Integer, Integer> left = new TreeMap<>(Collections.reverseOrder()); // Max heap equivalent
        TreeMap<Integer, Integer> right = new TreeMap<>(); // Min heap equivalent
        
        int leftSize = 0, rightSize = 0;
        
        for (int i = 0; i < nums.length; i++) {
            // Add new element
            int[] sizes = addNumber(nums[i], left, right, leftSize, rightSize);
            leftSize = sizes[0];
            rightSize = sizes[1];
            
            // Remove element going out of window
            if (i >= k) {
                int[] newSizes = removeNumber(nums[i - k], left, right, leftSize, rightSize);
                leftSize = newSizes[0];
                rightSize = newSizes[1];
            }
            
            // Calculate median when window is full
            if (i >= k - 1) {
                result[i - k + 1] = getMedian(left, right, leftSize, rightSize);
            }
        }
        
        return result;
    }
    
    private int[] addNumber(int num, TreeMap<Integer, Integer> left, TreeMap<Integer, Integer> right, 
                           int leftSize, int rightSize) {
        // Decide which side to add the number
        if (leftSize == 0 || num <= left.firstKey()) {
            left.put(num, left.getOrDefault(num, 0) + 1);
            leftSize++;
        } else {
            right.put(num, right.getOrDefault(num, 0) + 1);
            rightSize++;
        }
        
        // Balance the two sides
        return balance(left, right, leftSize, rightSize);
    }
    
    private int[] removeNumber(int num, TreeMap<Integer, Integer> left, TreeMap<Integer, Integer> right,
                              int leftSize, int rightSize) {
        if (left.containsKey(num)) {
            left.put(num, left.get(num) - 1);
            if (left.get(num) == 0) {
                left.remove(num);
            }
            leftSize--;
        } else {
            right.put(num, right.get(num) - 1);
            if (right.get(num) == 0) {
                right.remove(num);
            }
            rightSize--;
        }
        
        // Balance after removal
        return balance(left, right, leftSize, rightSize);
    }
    
    private int[] balance(TreeMap<Integer, Integer> left, TreeMap<Integer, Integer> right,
                         int leftSize, int rightSize) {
        // Left side should have same size or one more element
        if (leftSize > rightSize + 1) {
            // Move largest from left to right
            int moveNum = left.firstKey();
            left.put(moveNum, left.get(moveNum) - 1);
            if (left.get(moveNum) == 0) {
                left.remove(moveNum);
            }
            right.put(moveNum, right.getOrDefault(moveNum, 0) + 1);
            leftSize--;
            rightSize++;
        } else if (rightSize > leftSize) {
            // Move smallest from right to left
            int moveNum = right.firstKey();
            right.put(moveNum, right.get(moveNum) - 1);
            if (right.get(moveNum) == 0) {
                right.remove(moveNum);
            }
            left.put(moveNum, left.getOrDefault(moveNum, 0) + 1);
            rightSize--;
            leftSize++;
        }
        
        return new int[]{leftSize, rightSize};
    }
    
    private double getMedian(TreeMap<Integer, Integer> left, TreeMap<Integer, Integer> right,
                            int leftSize, int rightSize) {
        if (leftSize == rightSize) {
            // Even number of elements
            return ((long) left.firstKey() + (long) right.firstKey()) / 2.0;
        } else {
            // Odd number of elements - median is in left side
            return left.firstKey();
        }
    }
    
    public static void main(String[] args) {
        SlidingWindowMedianOptimized solution = new SlidingWindowMedianOptimized();
        
        // Test case 1
        int[] nums1 = {1, 3, -1, -3, 5, 3, 6, 7};
        int k1 = 3;
        double[] result1 = solution.medianSlidingWindow(nums1, k1);
        System.out.println("Input: " + Arrays.toString(nums1) + ", k=" + k1);
        System.out.println("Output: " + Arrays.toString(result1));
        System.out.println("Expected: [1.0, -1.0, -1.0, 3.0, 5.0, 6.0]");
        System.out.println();
        
        // Test case 2 - Large numbers to test overflow protection
        int[] nums2 = {2147483647, 2147483647};
        int k2 = 2;
        double[] result2 = solution.medianSlidingWindow(nums2, k2);
        System.out.println("Input: " + Arrays.toString(nums2) + ", k=" + k2);
        System.out.println("Output: " + Arrays.toString(result2));
        System.out.println("Expected: [2147483647.0]");
    }
} 