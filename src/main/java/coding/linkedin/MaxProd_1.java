package coding.linkedin;

public class MaxProd_1 {

    public int maxProduct(int[] nums) {
        int maxProduct = nums[0];
        int minProduct = nums[0];
        int result = maxProduct;

        for(int i = 1; i < nums.length; i++) {
            int curr = nums[i];
            int temp = Math.max(
                    curr,
                    Math.max(curr * maxProduct, curr * minProduct)
            );

            minProduct = Math.min(
                    curr,
                    Math.min(curr * maxProduct, curr * minProduct)
            );

            maxProduct = temp;

            result = Math.max(result, maxProduct);
        }

        // ToDo: Write Your Code Here.
        return result;
    }

    public static void main(String[] args) {
        var sol = new MaxProd_1();
//        System.out.println(sol.maxProduct());
    }
}
