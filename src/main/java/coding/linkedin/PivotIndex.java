package coding.linkedin;

import java.util.Arrays;

public class PivotIndex {

    public int pivotIndex(int[] nums) {
        int sum = Arrays.stream(nums).sum();
        int leftSum = 0;

        for (int i = 0; i < nums.length; i++) {
            int rightSum = sum - leftSum - nums[i];
            if (leftSum == rightSum) {
                return i;
            }
            leftSum+= nums[i];
        }
        return -1;
    }

    public static void main(String[] args) {
        var sol = new PivotIndex();
        System.out.println(
                sol.pivotIndex(new int[] {1, 2, 3, 4, 3, 2, 1})
        );
        System.out.println(
                sol.pivotIndex(new int[] {2, 1, 3})
        );
        System.out.println(
                sol.pivotIndex(new int[] {1, 100, 50, -51, 1, 1})
        );
    }
}
