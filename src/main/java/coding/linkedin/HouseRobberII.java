package coding.linkedin;

public class HouseRobberII {

    /**
     * Solves the House Robber II problem where houses are arranged in a circle
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public int rob(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        
        if (nums.length == 1) {
            return nums[0];
        }
        
        if (nums.length == 2) {
            return Math.max(nums[0], nums[1]);
        }
        
        // Rob houses from 0 to n-2 (exclude the last house)
        int rob1 = robHouses(nums, 0, nums.length - 2);
        
        // Rob houses from 1 to n-1 (exclude the first house)
        int rob2 = robHouses(nums, 1, nums.length - 1);
        
        // Return the maximum amount
        return Math.max(rob1, rob2);
    }

    /**
     * Helper method to solve the classic House Robber problem for a segment of houses
     * @param nums array of house values
     * @param start starting index (inclusive)
     * @param end ending index (inclusive)
     * @return maximum amount that can be robbed
     */
    private int robHouses(int[] nums, int start, int end) {
        int prev1 = 0; // max money if we rob up to two houses back
        int prev2 = 0; // max money if we rob up to one house back
        
        // Iterate through the segment of houses
        for (int i = start; i <= end; i++) {
            // Current max is either:
            // 1. Skip current house and take prev2
            // 2. Rob current house and add to prev1
            int temp = Math.max(prev2, prev1 + nums[i]);
            
            // Update previous values
            prev1 = prev2;
            prev2 = temp;
        }
        
        return prev2; // This contains the maximum amount
    }

    public static void main(String[] args) {
        var sol = new HouseRobberII();
        System.out.println(
                sol.rob(new int[] {4, 2, 3, 1}) // Expected: 6 (4 + 3 = 7 or 2 + 4 = 6)
        );
        System.out.println(
                sol.rob(new int[] {5, 1, 2, 5}) // Expected: 7 (5 + 2 = 7)
        );
        System.out.println(
                sol.rob(new int[] {1, 2, 3, 4, 5}) // Expected: 9 (1 + 3 + 5 = 9 or 2 + 4 = 6)
        );
    }
}
