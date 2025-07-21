package coding.linkedin;

public class MaxConsecutiveOnesIII {

    public int longestOnes(int[] nums, int k) {
        int start = 0, maxOnes = 0, res = Integer.MIN_VALUE;

        for (int end = 0; end < nums.length; end++) {
            if (nums[end] == 1) {
                maxOnes++;
            }

            if ((end - start + 1 - maxOnes) > k) {
                if (nums[start] == 1) {
                    maxOnes--;
                }
                start++;
            }

            res = Math.max(res, end - start + 1);
        }

        return res;
    }

    public static void main(String[] args) {
        var sol = new MaxConsecutiveOnesIII();
        System.out.println(sol.longestOnes(new int[] {0, 1, 1, 0, 0, 0, 1, 1, 0, 1, 1}, 2));
        System.out.println(sol.longestOnes(new int[] {0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1}, 3));
        System.out.println(sol.longestOnes(new int[] {1, 0, 0, 1, 1, 0, 1, 1}, 2));
    }
}
