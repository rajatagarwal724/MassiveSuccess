package coding.subset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubsetWithDuplicates {

    public static void main(String[] args) {
        var sol = new SubsetWithDuplicates();
        List<List<Integer>> result = sol.findSubsets(new int[] { 1, 3, 3 });
        System.out.println("Here is the list of subsets: " + result);

        result = sol.findSubsets(new int[] { 1, 5, 3, 3 });
        System.out.println("Here is the list of subsets: " + result);
    }

    private List<List<Integer>> findSubsets(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> subsets = new ArrayList<>();
        subsets.add(new ArrayList<>());
        int startIdx = 0;
        int endIdx = 0;
        for (int i = 0; i < nums.length; i++) {
            startIdx = 0;
            if (i > 0 && i < nums.length && nums[i] == nums[i-1]) {
                startIdx = endIdx;
            }
            endIdx = subsets.size();
            for (int index = startIdx; index < endIdx; index++) {
                List<Integer> subset = new ArrayList<>(subsets.get(index));
                subset.add(nums[i]);
                subsets.add(subset);
            }
        }
        return subsets;
    }
}
