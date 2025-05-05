package coding.top150;

public class MinSubArrayLen {

    public int minSubArrayLen(int target, int[] nums) {
        int start = 0, end = 0;
        int minLen = Integer.MAX_VALUE;
        int sum = 0;
        for(end = 0; end < nums.length; end++) {
            sum += nums[end];
            while (sum >= target) {
                minLen = Math.min(minLen, end - start + 1);
                sum -= nums[start++];
            }
        }
        if (minLen == Integer.MAX_VALUE) {
            return 0;
        }
        return minLen;
    }

    public static void main(String[] args) {
        var sol = new MinSubArrayLen();
        System.out.println(sol.minSubArrayLen(7, new int[] {2,3,1,2,4,3}));
        System.out.println(sol.minSubArrayLen(4, new int[] {1,4,4}));
        System.out.println(sol.minSubArrayLen(11, new int[] {1,1,1,1,1,1,1,1}));
    }
}
