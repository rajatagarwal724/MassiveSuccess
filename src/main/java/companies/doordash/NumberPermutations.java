package companies.doordash;

import java.util.ArrayList;
import java.util.List;

public class NumberPermutations {

    public static List<Integer> findPermutations(int number) {
        // Convert number to string to work with individual digits
        String numStr = Integer.toString(number);
        List<String> stringPerms = new ArrayList<>();
        List<Integer> result = new ArrayList<>();

        // Generate all permutations
        generatePermutations("", numStr, stringPerms);

        // Convert string permutations back to integers
        for (String perm : stringPerms) {
            result.add(Integer.parseInt(perm));
        }

        return result;
    }

    private static void generatePermutations(String prefix, String remaining, List<String> result) {
        int n = remaining.length();

        // Base case: if no characters remain, add the complete permutation
        if (n == 0) {
            result.add(prefix);
            return;
        }

        // Try each character as the next in the permutation
        for (int i = 0; i < n; i++) {
            generatePermutations(
                    prefix + remaining.charAt(i),
                    remaining.substring(0, i) + remaining.substring(i + 1),
                    result
            );
        }
    }

    // Example usage
    public static void main(String[] args) {
        int number = 123;
        List<Integer> permutations = findPermutations(number);

        System.out.println("Permutations of " + number + ":");
        for (int perm : permutations) {
            System.out.println(perm);
        }

        System.out.println("Total permutations: " + permutations.size());
    }
}
