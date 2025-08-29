package coding.leetcode;

import java.util.Arrays;

/**
 * LeetCode Problem: https://leetcode.com/problems/multiply-strings/
 *
 * Given two non-negative integers num1 and num2 represented as strings, return the product of num1 and num2, also represented as a string.
 *
 * Note: You must not use any built-in BigInteger library or convert the inputs to integers directly.
 */
public class MultiplyStrings {

    /**
     * Multiplies two numbers represented as strings.
     *
     * The method simulates manual multiplication. The product of two numbers with lengths
     * m and n can have at most m + n digits. We use an integer array `product` of size m + n
     * to store the intermediate results.
     *
     * The core idea is that the product of the i-th digit of num1 and the j-th digit of num2
     * contributes to the (i + j)-th and (i + j + 1)-th positions in the final result.
     *
     * For num1[i] and num2[j]:
     * - The multiplication is `(num1.charAt(i) - '0') * (num2.charAt(j) - '0')`.
     * - This product contributes to `product[i + j + 1]` (the current digit) and `product[i + j]` (the carry).
     *
     * After iterating through all digit pairs, the `product` array holds the result in reverse order of calculation,
     * but correct positional order. The final step is to convert this array into a string, handling leading zeros.
     *
     * @param num1 The first number as a string.
     * @param num2 The second number as a string.
     * @return The product of num1 and num2 as a string.
     */
    public String multiply(String num1, String num2) {
        // Handle edge case where one of the numbers is "0"
        if ("0".equals(num1) || "0".equals(num2)) {
            return "0";
        }

        int m = num1.length();
        int n = num2.length();
        int[] product = new int[m + n];

        // Reverse iteration to simplify position calculation (units, tens, etc.)
        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                int d1 = num1.charAt(i) - '0';
                int d2 = num2.charAt(j) - '0';
                int mul = d1 * d2;

                // p1 is the position for the carry, p2 is for the current digit
                int p1 = i + j;
                int p2 = i + j + 1;

                // Add the new product to the existing value at product[p2]
                int sum = mul + product[p2];

                // Update the carry and the current digit
                product[p1] += sum / 10; // Add carry to the more significant position
                product[p2] = sum % 10;  // Set the current digit
            }
        }

        // Convert the product array to a string, skipping leading zeros
        StringBuilder sb = new StringBuilder();
        boolean leadingZero = true;
        for (int digit : product) {
            if (digit == 0 && leadingZero) {
                continue;
            }
            leadingZero = false;
            sb.append(digit);
        }

        // If the result is empty (e.g., product was all zeros), return "0"
        return sb.length() == 0 ? "0" : sb.toString();
    }

    public static void main(String[] args) {
        MultiplyStrings solution = new MultiplyStrings();

        // Test cases
        String[][] testCases = {
//            {"2", "3", "6"},
            {"123", "456", "56088"}
//                ,
//            {"999", "999", "998001"},
//            {"0", "12345", "0"},
//            {"12345", "0", "0"},
//            {"1", "1", "1"},
//            {"9133", "0", "0"}
        };

        for (int i = 0; i < testCases.length; i++) {
            String num1 = testCases[i][0];
            String num2 = testCases[i][1];
            String expected = testCases[i][2];
            String result = solution.multiply(num1, num2);

            System.out.printf("Test Case %d: %s * %s%n", i + 1, num1, num2);
            System.out.printf("Expected: %s, Got: %s%n", expected, result);
            System.out.println("Result: " + (expected.equals(result) ? "PASS" : "FAIL"));
            System.out.println("-------------------------------------");
        }
    }
}
