package companies.splunk;

import java.util.Arrays;

public class FindFirstNLast {

    public int[] searchRange(int[] nums, int target) {
        if (target < nums[0] || target > nums[nums.length - 1]) {
            return new int[] {-1, -1};
        }

        int leftIndex = binarySearch(nums, target, true);
        if (leftIndex == -1) {
            return new int[] {-1, -1};
        }
        int rightIndex = binarySearch(nums, target, false);
        return new int[] {leftIndex, rightIndex};
    }

    private int binarySearch(int[] nums, int target, boolean searchLeft) {
        int keyIndex = -1;
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;

            int valueAtMiddle = nums[mid];

            if (valueAtMiddle < target) {
                left = mid + 1;
            } else if (valueAtMiddle > target) {
                right = mid - 1;
            } else {
                keyIndex = mid;
                if (searchLeft) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            }
        }

        return keyIndex;
    }

    public static void main(String[] args) {
        var sol = new FindFirstNLast();
        System.out.println(Arrays.toString(sol.searchRange(new int[]{5, 7, 7, 8, 8, 10}, 8)));
    }
}
