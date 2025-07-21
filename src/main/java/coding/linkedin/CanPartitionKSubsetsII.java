package coding.linkedin;

import java.util.Arrays;

public class CanPartitionKSubsetsII {

    public boolean canPartitionKSubsets(int[] arr, int k) {
        int totalSum = Arrays.stream(arr).sum();

        if (totalSum % k != 0) {
            return false;
        }

        int targetSum = totalSum/k;
        boolean[] taken = new boolean[arr.length];

        Arrays.sort(arr);

        reverse(arr);
        System.out.println(Arrays.toString(arr));
        return backtrack(arr, 0, 0, 0, targetSum, k, taken);
    }

    private void reverse(int[] arr) {
        int i = 0, j = arr.length - 1;

        while (i < j) {
            var temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
            i++;
            j--;
        }
    }

    boolean backtrack(int[] arr, int index, int currSum, int currCount, int targetSum, int totalSubsets, boolean[] taken) {
        if (currCount == totalSubsets) {
            return true;
        }

        if (currSum > targetSum) {
            return false;
        }

        if (currSum == targetSum) {
            return backtrack(arr, 0, 0, currCount + 1, targetSum, totalSubsets, taken);
        }

        for (int i = index; i < arr.length; i++) {
            if (!taken[i]) {
                taken[i] = true;

                if (backtrack(arr, i + 1, currSum + arr[i], currCount, targetSum, totalSubsets, taken)) {
                    return true;
                }

                taken[i] = false;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        var sol = new CanPartitionKSubsetsII();
        System.out.println(
                sol.canPartitionKSubsets(new int[] {4,3,2,3,5,2,1}, 4)
        );
        System.out.println(
                sol.canPartitionKSubsets(new int[] {1,2,3,4}, 3)
        );
    }
}
