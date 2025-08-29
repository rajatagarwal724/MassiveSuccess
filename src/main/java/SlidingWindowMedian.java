import java.util.*;

public class SlidingWindowMedian {
    private TreeMap<Integer, Integer> lo, hi; // TreeMaps to simulate multiset
    private int loSize, hiSize; // Track sizes for efficiency
    
    public SlidingWindowMedian() {
        lo = new TreeMap<>(); // stores smaller half
        hi = new TreeMap<>(); // stores larger half
        loSize = 0;
        hiSize = 0;
    }
    
    public double[] medianSlidingWindow(int[] nums, int k) {
        List<Double> medians = new ArrayList<>();
        
        for (int i = 0; i < nums.length; i++) {
            // Remove outgoing element
            if (i >= k) {
                int outgoing = nums[i - k];
                if (outgoing <= getLoMax()) {
                    removeFromLo(outgoing);
                } else {
                    removeFromHi(outgoing);
                }
            }
            
            // Insert incoming element
            addToLo(nums[i]);
            
            // Balance the sets
            int maxLo = getLoMax();
            removeFromLo(maxLo);
            addToHi(maxLo);
            
            if (loSize < hiSize) {
                int minHi = getHiMin();
                removeFromHi(minHi);
                addToLo(minHi);
            }
            
            // Get median
            if (i >= k - 1) {
                if ((k & 1) == 1) { // k is odd
                    medians.add((double) getLoMax());
                } else { // k is even
                    medians.add(((double) getLoMax() + (double) getHiMin()) * 0.5);
                }
            }
        }
        
        return medians.stream().mapToDouble(Double::doubleValue).toArray();
    }
    
    private void addToLo(int num) {
        lo.put(num, lo.getOrDefault(num, 0) + 1);
        loSize++;
    }
    
    private void addToHi(int num) {
        hi.put(num, hi.getOrDefault(num, 0) + 1);
        hiSize++;
    }
    
    private void removeFromLo(int num) {
        int count = lo.get(num);
        if (count == 1) {
            lo.remove(num);
        } else {
            lo.put(num, count - 1);
        }
        loSize--;
    }
    
    private void removeFromHi(int num) {
        int count = hi.get(num);
        if (count == 1) {
            hi.remove(num);
        } else {
            hi.put(num, count - 1);
        }
        hiSize--;
    }
    
    private int getLoMax() {
        return lo.lastKey();
    }
    
    private int getHiMin() {
        return hi.firstKey();
    }
    
    // Main method for testing
    public static void main(String[] args) {
        SlidingWindowMedian solution = new SlidingWindowMedian();
        
        // Test case 1
        int[] nums1 = {1, 3, -1, -3, 5, 3, 6, 7};
        int k1 = 3;
        System.out.println("Test case 1:");
        System.out.println("Input: nums = " + Arrays.toString(nums1) + ", k = " + k1);
        System.out.println("Output: " + Arrays.toString(solution.medianSlidingWindow(nums1, k1)));
        
        // Test case 2
        int[] nums2 = {1, 2, 3, 4, 2, 3, 1, 4, 2};
        int k2 = 3;
        System.out.println("\nTest case 2:");
        System.out.println("Input: nums = " + Arrays.toString(nums2) + ", k = " + k2);
        System.out.println("Output: " + Arrays.toString(solution.medianSlidingWindow(nums2, k2)));
        
        // Test case 3 - even window size
        int[] nums3 = {1, 4, 2, 3};
        int k3 = 4;
        System.out.println("\nTest case 3:");
        System.out.println("Input: nums = " + Arrays.toString(nums3) + ", k = " + k3);
        System.out.println("Output: " + Arrays.toString(solution.medianSlidingWindow(nums3, k3)));
    }
} 