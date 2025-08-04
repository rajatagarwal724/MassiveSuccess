package coding.top75.sliding;

public class LongestSubArrayOfOnesAfterDeletingOneZero {

    public int longestSubarray(int[] nums) {
        int start = 0, zeroCount = 0, res = Integer.MIN_VALUE;

        for (int end = 0; end < nums.length; end++) {
            int elem = nums[end];

            if (elem == 0) {
                zeroCount++;
            }

            while (zeroCount > 1) {
                if (nums[start] == 0) {
                    zeroCount--;
                }
                start++;
            }

            res = Math.max(res, end - start);
        }

        return res;
    }

    public static void main(String[] args) {
        var sol = new LongestSubArrayOfOnesAfterDeletingOneZero();
        System.out.println(sol.longestSubarray(new int[] {1, 1, 0, 0, 1, 1}));
        System.out.println(sol.longestSubarray(new int[] {1, 1, 0, 1, 1, 1}));
        System.out.println(sol.longestSubarray(new int[] {1, 0, 1, 1, 0, 1}));
    }
}
