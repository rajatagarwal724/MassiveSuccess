package companies.doordash;

import java.util.*;

public class SlidingWindowMedian {
    
    public double[] medianSlidingWindow(int[] nums, int k) {
        double[] result = new double[nums.length - k + 1];
        
        // Max heap for smaller half (left side)
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> Integer.compare(b, a));
        // Min heap for larger half (right side)
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        
        for (int i = 0; i < nums.length; i++) {
            // Add new element
            addNumber(nums[i], maxHeap, minHeap);
            
            // Remove element going out of window
            if (i >= k) {
                removeNumber(nums[i - k], maxHeap, minHeap);
            }
            
            // Calculate median when window is full
            if (i >= k - 1) {
                result[i - k + 1] = getMedian(maxHeap, minHeap);
            }
        }
        
        return result;
    }
    
    private void addNumber(int num, PriorityQueue<Integer> maxHeap, PriorityQueue<Integer> minHeap) {
        // Always add to maxHeap first
        maxHeap.offer(num);
        
        // Move the largest from maxHeap to minHeap
        minHeap.offer(maxHeap.poll());
        
        // Balance the heaps: maxHeap should have same size or one more element
        if (maxHeap.size() < minHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }
    
    private void removeNumber(int num, PriorityQueue<Integer> maxHeap, PriorityQueue<Integer> minHeap) {
        if (maxHeap.contains(num)) {
            maxHeap.remove(num);
        } else {
            minHeap.remove(num);
        }
        
        // Rebalance after removal
        balance(maxHeap, minHeap);
    }
    
    private void balance(PriorityQueue<Integer> maxHeap, PriorityQueue<Integer> minHeap) {
        if (maxHeap.size() > minHeap.size() + 1) {
            minHeap.offer(maxHeap.poll());
        } else if (minHeap.size() > maxHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }
    
    private double getMedian(PriorityQueue<Integer> maxHeap, PriorityQueue<Integer> minHeap) {
        if (maxHeap.size() == minHeap.size()) {
            // Even number of elements - average of two middle elements
            return ((long) maxHeap.peek() + (long) minHeap.peek()) / 2.0;
        } else {
            // Odd number of elements - middle element is in maxHeap
            return maxHeap.peek();
        }
    }
    
    public static void main(String[] args) {
        SlidingWindowMedian solution = new SlidingWindowMedian();
        
        // Test case 1
        int[] nums1 = {1, 3, -1, -3, 5, 3, 6, 7};
        int k1 = 3;
        double[] result1 = solution.medianSlidingWindow(nums1, k1);
        System.out.println("Input: " + Arrays.toString(nums1) + ", k=" + k1);
        System.out.println("Output: " + Arrays.toString(result1));
        System.out.println("Expected: [1.0, -1.0, -1.0, 3.0, 5.0, 6.0]");
        System.out.println();
        
        // Test case 2
        int[] nums2 = {1, 2, 3, 4, 2, 3, 1, 4, 2};
        int k2 = 3;
        double[] result2 = solution.medianSlidingWindow(nums2, k2);
        System.out.println("Input: " + Arrays.toString(nums2) + ", k=" + k2);
        System.out.println("Output: " + Arrays.toString(result2));
        System.out.println();
        
        // Test case 3 - Edge case with even k
        int[] nums3 = {1, 4, 2, 3};
        int k3 = 4;
        double[] result3 = solution.medianSlidingWindow(nums3, k3);
        System.out.println("Input: " + Arrays.toString(nums3) + ", k=" + k3);
        System.out.println("Output: " + Arrays.toString(result3));
        System.out.println("Expected: [2.5]");
    }
} 