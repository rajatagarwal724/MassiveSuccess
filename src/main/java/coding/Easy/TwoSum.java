package coding.Easy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TwoSum {

    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> indexMap = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int elem = nums[i];
            if (indexMap.containsKey(target - elem)) {
                return new int[] {indexMap.get(target - elem), i};
            }
            indexMap.put(elem, i);
        }
        return new int[]{-1, -1};
    }

    public static void main(String[] args) {
        var sol = new TwoSum();
        System.out.println(
                Arrays.toString(sol.twoSum(new int[] {3, 2, 4}, 6))
        );

        System.out.println(
                Arrays.toString(sol.twoSum(new int[] {-1, -2, -3, -4, -5}, -8))
        );

        System.out.println(
                Arrays.toString(sol.twoSum(new int[] {10, 15, 20, 25, 30}, 45))
        );
    }
}
