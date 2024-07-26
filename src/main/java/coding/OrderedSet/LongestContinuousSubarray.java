package coding.OrderedSet;

import java.util.TreeMap;

public class LongestContinuousSubarray {

    // Function to find the longest subarray with absolute diff less than or equal to limit
    public  int longestSubarray(int[] nums, int limit) {
        int left = 0, right = 0, maxLength = 0;
        TreeMap<Integer, Integer> orderedMap = new TreeMap<>();

        for (right = 0; right < nums.length; right++) {
            int elem = nums[right];

            orderedMap.put(elem, orderedMap.getOrDefault(elem, 0) + 1);

            while (orderedMap.lastKey() - orderedMap.firstKey() > limit && orderedMap.containsKey(nums[left])) {
                orderedMap.put(nums[left], orderedMap.get(nums[left]) - 1);
                if (orderedMap.get(nums[left]) == 0) {
                    orderedMap.remove(nums[left]);
                }
                left++;
            }
            maxLength = Math.max(maxLength, right - left + 1);
        }
        return maxLength;
    }

    public static void main(String[] args) {
        var solution = new LongestContinuousSubarray();

        System.out.println(solution.longestSubarray(new int[] {10, 1, 2, 4, 7}, 5));
        System.out.println(solution.longestSubarray(new int[] {4, 8, 5, 1, 7, 9}, 3));
        System.out.println(solution.longestSubarray(new int[] {3, 3, 3, 3, 3}, 0));
    }
}
