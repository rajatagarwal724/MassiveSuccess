package coding.top75;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LongestConsecutiveSequence {

    public int longestConsecutive(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        
        int longestSequence = 0;
        Set<Integer> allNums = Arrays.stream(nums).boxed().collect(Collectors.toSet());

        for (int num: nums) {
            if (!allNums.contains(num + 1)) {
                int numberToCheck = num;
                int occurrence = 0;
                while (allNums.contains(numberToCheck)) {
                    occurrence++;
                    numberToCheck = numberToCheck - 1;
                    longestSequence = Math.max(longestSequence, occurrence);
                }
            }
        }
        return longestSequence;
    }

    public static void main(String[] args) {
        var sol = new LongestConsecutiveSequence();
        System.out.println("Test 1: " + Arrays.toString(new int[] {10, 11, 14, 12, 13}) + 
            " -> " + sol.longestConsecutive(new int[] {10, 11, 14, 12, 13}));
        System.out.println("Test 2: " + Arrays.toString(new int[] {3, 6, 4, 100, 101, 102}) + 
            " -> " + sol.longestConsecutive(new int[] {3, 6, 4, 100, 101, 102}));
        System.out.println("Test 3: " + Arrays.toString(new int[] {4, 3, 6, 2, 5, 8, 4, 7, 0, 1}) + 
            " -> " + sol.longestConsecutive(new int[] {4, 3, 6, 2, 5, 8, 4, 7, 0, 1}));
        System.out.println("Test 4: " + Arrays.toString(new int[] {7, 8, 10, 11, 15}) + 
            " -> " + sol.longestConsecutive(new int[] {7, 8, 10, 11, 15}));
        // Test edge cases
        System.out.println("Test 5 (empty): " + Arrays.toString(new int[] {}) + 
            " -> " + sol.longestConsecutive(new int[] {}));
        System.out.println("Test 6 (null): null -> " + sol.longestConsecutive(null));
    }
}
