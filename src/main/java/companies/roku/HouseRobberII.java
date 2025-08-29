package companies.roku;

public class HouseRobberII {

    public int rob(int[] nums) {
        if (nums.length == 1) {
            return nums[0];
        }

        if (nums.length == 2) {
            return Math.max(nums[0], nums[1]);
        }

        int considerFirstHouse = rob(nums, 0, nums.length - 2);
        int considerLastHouse = rob(nums, 1, nums.length - 1);

        return Math.max(considerFirstHouse, considerLastHouse);
    }

    private int rob(int[] nums, int start, int end) {
        if (end == start) {
            return nums[start];
        }
        int[] dp = new int[end + 1];
        dp[start] = nums[start];
        dp[start + 1] = Math.max(nums[start], nums[start + 1]);

        for (int i = start + 2; i <= end; i++) {
            int picked = dp[i - 2] + nums[i];
            int notPicked = Math.max(dp[i - 1], nums[i]);
            dp[i] = Math.max(picked, notPicked);
        }
        return dp[dp.length - 1];
    }

    public static void main(String[] args) {
        var sol = new HouseRobberII();
        System.out.println(sol.rob(new int[] {2,3,2}));
        System.out.println(sol.rob(new int[] {1,2,3,1}));
        System.out.println(sol.rob(new int[] {1,2,3}));
    }
}
