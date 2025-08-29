package companies.roku.repeat;

public class CircularArrayMaxKadaneSum {
    public int maxSubarraySumCircular(int[] nums) {
        int maxSum = nums[0];
        int maxSumSoFar = nums[0];

        int minSum = nums[0];
        int minSumSoFar = nums[0];

        int totalSum = nums[0];

        for (int i = 1; i < nums.length; i++) {
            int num = nums[i];

            maxSumSoFar = Math.max(num, maxSumSoFar + num);
            maxSum = Math.max(maxSum, maxSumSoFar);

            minSumSoFar = Math.min(num, minSumSoFar + num);
            minSum = Math.min(minSum, minSumSoFar);

            totalSum += num;
        }

        if (totalSum == minSum) {
            return maxSum;
        }

        return Math.max(maxSum, totalSum - minSum);
    }

    public static void main(String[] args) {
        var sol = new CircularArrayMaxKadaneSum();
        System.out.println(
                sol.maxSubarraySumCircular(new int[] {1,-2,3,-2})
        );

        System.out.println(
                sol.maxSubarraySumCircular(new int[] {5,-3, 5})
        );

        System.out.println(
                sol.maxSubarraySumCircular(new int[] {-3,-2,-3})
        );
    }
}
