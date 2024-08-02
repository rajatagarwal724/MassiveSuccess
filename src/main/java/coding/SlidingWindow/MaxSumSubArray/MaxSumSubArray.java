package coding.SlidingWindow.MaxSumSubArray;

public class MaxSumSubArray {

    public int findMaxSumSubArray(int k, int[] arr) {
        int windowStart = 0, windowEnd = 0, windowSum = 0, result = Integer.MIN_VALUE;

        for (windowEnd = 0; windowEnd < arr.length; windowEnd++) {
            windowSum += arr[windowEnd];

            if ((windowEnd - windowStart + 1) >= k) {
                result = Math.max(windowSum, result);
                windowSum -= arr[windowStart++];
            }
        }

        return result;
    }

    public static void main(String[] args) {
        var sol = new MaxSumSubArray();
        System.out.println(sol.findMaxSumSubArray(3, new int[] {2, 1, 5, 1, 3, 2}));
        System.out.println(sol.findMaxSumSubArray(2, new int[] {2, 3, 4, 1, 5}));
    }
}
