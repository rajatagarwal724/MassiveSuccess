package coding.linkedin;

import java.util.Arrays;

public class FirstAndLastPositionInSortedArray {

    public int[] searchRange(int[] nums, int target) {
        int leftIndex = binarySearch(nums, target, true);
        if (leftIndex == -1) {
            return new int[] {-1, -1};
        }
        int rightIndex = binarySearch(nums, target, false);

        return new int[] {leftIndex, rightIndex};
    }

    private int binarySearch(int[] nums, int target, boolean searchLeft) {
        int left = 0, right = nums.length - 1;
        int res = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (target < nums[mid]) {
                right = mid - 1;
            } else if (target > nums[mid]) {
                left = mid + 1;
            } else {
                res = mid;
                if (searchLeft) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            }
        }
        return res;
    }

    public static void main(String[] args) {
        var sol = new FirstAndLastPositionInSortedArray();
        System.out.println(
                Arrays.toString(
                        sol.searchRange(
                new int[] {5,7,7,8,8,10}, 8
        )));

        System.out.println(
                Arrays.toString(
                        sol.searchRange(
                                new int[] {5,7,7,8,8,10}, 6
                        )));

        System.out.println(
                Arrays.toString(
                        sol.searchRange(
                                new int[] {}, 0
                        )));
    }
}
