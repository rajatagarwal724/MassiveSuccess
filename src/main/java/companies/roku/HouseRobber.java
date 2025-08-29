package companies.roku;

public class HouseRobber {

    public int rob(int[] nums) {
        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        dp[1] = Math.max(dp[0], dp[1]);

        for (int i = 2; i < nums.length; i++) {
            int picked = dp[i - 2] + nums[i];
            int notPicked = Math.max(dp[i-1], nums[i]);
            dp[i] = Math.max(picked, notPicked);
        }

        return dp[nums.length - 1];
    }


    public static void main(String[] args) {
        var sol = new HouseRobber();
        System.out.println(sol.rob(new int[]{1,2,3,1}));
        System.out.println(sol.rob(new int[]{2,7,9,3,1}));
    }
}

