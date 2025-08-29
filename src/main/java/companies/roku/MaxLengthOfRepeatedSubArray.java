package companies.roku;

public class MaxLengthOfRepeatedSubArray {

    public int findLength(int[] nums1, int[] nums2) {
        int[][] dp = new int[nums1.length][nums2.length];
        int ans = 0;
        for(int i = nums1.length - 1; i >= 0; i--) {
            for (int j = nums2.length - 1; j >= 0; j--) {
                if (nums2[j] == nums1[i]) {
                    dp[i][j] = dp[i + 1][j + 1] + 1;
                    ans = Math.max(ans, dp[i][j]);
                }
            }
        }

        return ans;
    }

    public static void main(String[] args) {

    }
}
