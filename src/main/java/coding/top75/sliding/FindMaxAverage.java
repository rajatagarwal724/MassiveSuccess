package coding.top75.sliding;

public class FindMaxAverage {

    public double findMaxAverage(int[] nums, int k) {
        double res = Integer.MIN_VALUE;
        int start = 0;
        int sum = 0;
        for(int end = 0; end < nums.length; end++) {
            sum += nums[end];
            if(end >= (k - 1)) {
                double avg = (double) sum/k;
                res = Math.max(res, avg);
                sum-=nums[start++];
            }
        }
        return res;
    }

    public static void main(String[] args) {
        var sol = new FindMaxAverage();
        System.out.println(sol.findMaxAverage(new int[] {-1, -2, -3, -4, -5}, 2));
    }
}
