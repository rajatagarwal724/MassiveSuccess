package companies.tekion;

public class CountSubsequencesWithSumK {

    public static int countSubsequencesRecursive(int[] arr, int k) {
        if (null == arr || arr.length == 0 || k < 0) {
            return 0;
        }
        Integer[][] memo = new Integer[arr.length][k + 1];
        return countSubsequencesRecursive(arr, 0, k, k, memo);
    }

    private static int countSubsequencesRecursive(int[] arr, int index, int remainingSum, int targetSum, Integer[][] memo) {
        if (remainingSum == 0) {
            return 1;
        }

        if (index >= arr.length || remainingSum < 0) {
            return 0;
        }

        if (null != memo[index][remainingSum]) {
            return memo[index][remainingSum];
        }

        int exclude = countSubsequencesRecursive(arr, index + 1, remainingSum, targetSum, memo);
        int include = countSubsequencesRecursive(arr, index + 1, remainingSum - arr[index], targetSum, memo);
        memo[index][remainingSum] = exclude + include;
        return memo[index][remainingSum];
    }

    public static void main(String[] args) {
        var sol = new CountSubsequencesWithSumK();
        System.out.println(countSubsequencesRecursive(new int[] {1, 2, 2, 3}, 3));
        System.out.println(countSubsequencesRecursive(new int[] {5,5,1}, 6));
        System.out.println(countSubsequencesRecursive(new int[] {3, 2, 5, 1, 2, 4}, 5));
    }
}
