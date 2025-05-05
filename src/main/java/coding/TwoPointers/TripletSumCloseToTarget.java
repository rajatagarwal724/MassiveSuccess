package coding.TwoPointers;

import java.util.Arrays;

public class TripletSumCloseToTarget {

    public int searchTriplet(int[] arr, int targetSum) {
        Arrays.sort(arr);
        int closestToTarget = Integer.MAX_VALUE;

        for (int i = 0; i < arr.length - 2; i++) {
            if (i > 0 && arr[i] == arr[i - 1]) {
                i++;
            }

            int first = arr[i];

            int low = i + 1;
            int high = arr.length - 1;

            while (low < high) {
                int sum = first + arr[low] + arr[high];

                if (targetSum == sum) {
                    return targetSum;
                }
                int targetDiff = targetSum - sum;
                if ((Math.abs(targetDiff) < Math.abs(closestToTarget))
                || (Math.abs(targetDiff) == Math.abs(closestToTarget) && sum < closestToTarget)) {
                    closestToTarget = sum;
                }

                if (sum < targetSum) {
                    low++;
                } else {
                    high--;
                }
            }
        }
        return closestToTarget;
    }

    public static void main(String[] args) {
        var sol = new TripletSumCloseToTarget();
        System.out.println(sol.searchTriplet(new int[]{-1, 0, 2, 3}, 3));
//        System.out.println(sol.searchTriplet(new int[]{-3, -1, 1, 2}, 1));
//        System.out.println(sol.searchTriplet(new int[]{1, 0, 1, 1}, 100));
//        System.out.println(sol.searchTriplet(new int[]{0, 0, 1, 1, 2, 6}, 5));
    }
}
