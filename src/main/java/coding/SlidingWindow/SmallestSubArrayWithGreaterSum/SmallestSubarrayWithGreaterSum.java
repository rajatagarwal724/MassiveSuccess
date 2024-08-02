package coding.SlidingWindow.SmallestSubArrayWithGreaterSum;

public class SmallestSubarrayWithGreaterSum {

    public int findMinSubArray(int S, int[] arr) {
        int res = Integer.MAX_VALUE, windowStart = 0, windowSum = 0;
        for (int windowEnd = 0; windowEnd < arr.length; windowEnd++) {
            windowSum += arr[windowEnd];

            while (windowSum >= S && windowStart <= windowEnd) {
                res = Math.min(res, windowEnd - windowStart + 1);
                windowSum -= arr[windowStart++];
            }
        }

        if (res == Integer.MAX_VALUE) {
            return 0;
        }

        return res;
    }

    public static void main(String[] args) {
        var sol = new SmallestSubarrayWithGreaterSum();
        System.out.println(sol.findMinSubArray(7, new int[] {2, 1, 5, 2, 3, 2}));
        System.out.println(sol.findMinSubArray(7, new int[] {2, 1, 5, 2, 8}));
        System.out.println(sol.findMinSubArray(8, new int[] {3, 4, 1, 1, 6}));
    }
}
