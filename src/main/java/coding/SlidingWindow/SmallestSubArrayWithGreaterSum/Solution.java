package coding.SlidingWindow.SmallestSubArrayWithGreaterSum;

public class Solution {

    public int findMinSubArray(int S, int[] arr) {
        int end = 0, start = 0, res = Integer.MAX_VALUE, sum = 0;

        for (end = 0; end < arr.length; end++) {
            sum += arr[end];

            while (sum >= S && (end - start + 1) >= 0) {
                res = Math.min(res, end - start + 1);
                sum -= arr[start];
                start++;
            }
        }

        if (res == Integer.MAX_VALUE) {
            return 0;
        }

        return res;
    }

        public static void main(String[] args) {

    }
}
