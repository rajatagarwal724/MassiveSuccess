package companies.roku;

public class StringsMultiplication {

    public String multiply(String num1, String num2) {
        if ("0".equals(num1) || "0".equals(num2)) {
            return "0";
        }

        int m = num1.length(), n = num2.length();
        int[] product = new int[m + n];

        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                int d1 = Character.getNumericValue(num1.charAt(i));
                int d2 = Character.getNumericValue(num2.charAt(j));

                int prod = d1 * d2;

                int p1 = i + j;
                int p2 = i + j + 1;

                int sum = prod + product[p2];

                product[p1] += sum/10;
                product[p2] = sum % 10;
            }
        }

        StringBuilder result = new StringBuilder();
        boolean leadingZero = true;

        for (int i = 0; i < product.length; i++) {
            if (product[i] == 0 && leadingZero) {
                continue;
            }
            leadingZero = false;
            result.append(product[i]);
        }

        return result.toString();
    }

    public static void main(String[] args) {
        var sol = new StringsMultiplication();
        System.out.println(sol.multiply("123", "456"));
        System.out.println(sol.multiply("2", "3"));
    }
}
