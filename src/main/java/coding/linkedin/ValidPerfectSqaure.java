package coding.linkedin;

public class ValidPerfectSqaure {

    public boolean isPerfectSquare(int num) {
        if (num < 2) return num == 1;

        int left = 2, right = num / 2;

        while (left <= right) {
            int mid = left + (right - left)/2;

            int guess = mid * mid;
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
        var sol = new ValidPerfectSqaure();
        System.out.println(sol.isPerfectSquare(49));
        System.out.println(sol.isPerfectSquare(55));
        System.out.println(sol.isPerfectSquare(0));
    }
}
