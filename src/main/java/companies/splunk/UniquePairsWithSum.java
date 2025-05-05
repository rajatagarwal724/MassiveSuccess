package companies.splunk;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UniquePairsWithSum {

    private static Set<List<Integer>> findPairs(int[] nums, int z) {
        Set<Integer> seen = new HashSet<>();
        Set<List<Integer>> uniquePairs = new HashSet<>();

        for (int i = 0; i < nums.length; i++) {
            int complement = (z - nums[i]);
            if (seen.contains(complement)) {
                List<Integer> pair = List.of(Math.min(nums[i], complement), Math.max(nums[i], complement));
                uniquePairs.add(pair);
            }
            seen.add(nums[i]);
        }
        return uniquePairs;
    }


    public static void main(String[] args) {
        int[] nums = {1, 3, 2, 4, 5, 6, 3, 7, 8, 2};
        int Z = 5;

        Set<List<Integer>> pairs = findPairs(nums, Z);
        System.out.println(pairs); // Output: [[1, 4], [2, 3]]
    }


}
