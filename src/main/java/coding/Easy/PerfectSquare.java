package coding.Easy;

public class PerfectSquare {

    // Function to check whether a given number is a perfect square
    public boolean isPerfectSquare(int num) {
        if (num < 2) {
            return num == 1;
        }
        long left = 2, right = num/2;
        while (left <= right) {
            long mid = (left + right)/2;

            long guess = mid * mid;

            if (guess == num) {
                return true;
            } else if (guess < num) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        var sol = new PerfectSquare();
        System.out.println(sol.isPerfectSquare(49));
        System.out.println(sol.isPerfectSquare(55));
        System.out.println(sol.isPerfectSquare(0));
    }
}
