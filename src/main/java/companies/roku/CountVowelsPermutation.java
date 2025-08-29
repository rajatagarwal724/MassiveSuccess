package companies.roku;

public class CountVowelsPermutation {

    public int countVowelPermutation(int n) {
        int MOD = 1000000007;
        long[] a = new long[n];
        long[] e = new long[n];
        long[] i = new long[n];
        long[] o = new long[n];
        long[] u = new long[n];

        a[0] = 1;
        e[0] = 1;
        i[0] = 1;
        o[0] = 1;
        u[0] = 1;

        for (int idx = 1; idx < n; idx++) {
            a[idx] = e[idx - 1] % MOD;
            e[idx] = (a[idx - 1] + i[idx - 1]) % MOD;
            i[idx] = (a[idx - 1] + e[idx - 1] + o[idx - 1] + u[idx - 1]) % MOD;
            o[idx] = (i[idx - 1] + u[idx - 1]) % MOD;
            u[idx] = a[idx - 1] % MOD;
        }

        return (int) ((a[n - 1] + e[n - 1] + i[n - 1] + o[n - 1] + u[n - 1]) % MOD);
    }

    public static void main(String[] args) {

    }
}
