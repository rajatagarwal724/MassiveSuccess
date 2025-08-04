package coding.top75.sliding;

public class MinimumSizeSubarraySum {

    public int minSubArrayLen(int target, int[] nums) {
        int start = 0, minLen = Integer.MAX_VALUE, sum = 0;
        for (int end = 0; end < nums.length; end++) {
            var elem = nums[end];
            sum+=elem;
            while (sum >= target) {
                minLen = Math.min(minLen, end - start + 1);
                sum-=nums[start++];
            }
        }
        return minLen == Integer.MAX_VALUE ? 0: minLen;
    }

    public static void main(String[] args) {
        var sol = new MinimumSizeSubarraySum();
        System.out.println(sol.minSubArrayLen(15, new int[] {1, 2, 3, 4, 5, 6, 7, 8}));
        System.out.println(sol.minSubArrayLen(11, new int[] {2, 1, 5, 2, 8}));
        System.out.println(sol.minSubArrayLen(8, new int[] {2, 1, 5, 2, 3}));
    }
}
