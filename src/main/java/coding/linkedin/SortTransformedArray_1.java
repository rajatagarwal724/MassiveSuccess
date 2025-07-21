package coding.linkedin;

import java.util.Arrays;

public class SortTransformedArray_1 {

    public int[] sortTransformedArray(int[] nums, int a, int b, int c) {
        int left = 0, right = nums.length - 1;
        int index = a >= 0 ? right : left;
        int[] res = new int[nums.length];

        while (left <= right) {
            var leftValue = transformed(nums[left], a, b, c);
            var rightValue = transformed(nums[right], a, b, c);

            if (a >= 0) {
                if (leftValue >= rightValue) {
                    res[index] = leftValue;
                    left++;
                } else {
                    res[index] = rightValue;
                    right--;
                }
                index--;
            } else {
                if (leftValue >= rightValue) {
                    res[index] = rightValue;
                    right--;
                } else {
                    res[index] = leftValue;
                    left++;
                }
                index++;
            }
        }
        return res;
    }

    private int transformed(int num, int a, int b, int c) {
        return a * num * num + b * num + c;
    }

    public static void main(String[] args) {
        var sol = new SortTransformedArray_1();
//        System.out.println(
//                Arrays.toString(
//                        sol.sortTransformedArray(new int[] {-4,-2,2,4}, 1, 3, 5)
//                )
//        );

        System.out.println(
                Arrays.toString(
                        sol.sortTransformedArray(new int[] {-4,-2,2,4}, -1, 3, 5)
                )
        );
    }
}
