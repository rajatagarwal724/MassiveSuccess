package coding.linkedin;

public class MaxProductSubArray {

    public int maxProduct(int[] nums) {
        int max_so_far = nums[0];
        int min_so_far = nums[0];
        int result = max_so_far;

        for (int i = 1; i < nums.length; i++) {
            int curr_num = nums[i];

            int temp_max = Math.max(
                    curr_num,
                    Math.max(max_so_far * curr_num, min_so_far * curr_num)
            );

            min_so_far = Math.min(
                    curr_num,
                    Math.min(max_so_far * curr_num, min_so_far * curr_num)
            );

            max_so_far = temp_max;

            result = Math.max(result, max_so_far);
        }

        return result;
    }


    public static void main(String[] args) {
        var solution = new MaxProductSubArray();

        System.out.println(solution.maxProduct(new int[] {2,3,-2,4}));
//        System.out.println(solution.maxProduct(new int[] {-2,0,-1}));
    }
}
