package coding.dp.fibonacci;

public class CalculateFibonacci {

    public int calculateFibonacci(int n) {
        int[] fib = new int[n + 1];
        if (n < 2) {
            return n;
        }

        fib[0] = 0;
        fib[1] = 1;

        for (int i = 2; i <= n; i++) {
            fib[i] = fib[i - 1] + fib[i - 2];
        }

        return fib[n];
    }

    public static void main(String[] args) {
        var sol = new CalculateFibonacci();

//        System.out.println(sol.calculateFibonacci(2));
        System.out.println(sol.calculateFibonacci(5));
    }
}
