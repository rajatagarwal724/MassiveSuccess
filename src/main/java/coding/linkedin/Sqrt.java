package coding.linkedin;

public class Sqrt {

    public int mySqrt(int x) {
        if (x < 2) {
            return x;
        }

        int left = 2, right = x/2;
        long num = x;
        while (left <= right) {
            int mid = left + (right - left)/2;

            long sqrt = (long) mid * mid;

            if (sqrt == num) {
                return mid;
            } else if (sqrt < num) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return right;
    }

    public static void main(String[] args) {
        var sol = new Sqrt();
        System.out.println(sol.mySqrt(2147395600));
    }
}
