package coding.subset;

import java.util.ArrayList;
import java.util.List;

public class PermutationsByChangingCase {
    public List<String> findLetterCaseStringPermutations(String str) {
        List<String> permutations = new ArrayList<>();
        if (null == str) {
            return permutations;
        }
        permutations.add(str);
        for (int strIndex = 0; strIndex < str.length(); strIndex++) {
            if (!Character.isLetter(str.charAt(strIndex))) {
                continue;
            }
            int n = permutations.size();
            for (int index = 0; index < n; index++) {
                char[] permutation = permutations.get(index).toCharArray();
                if (Character.isLowerCase(permutation[strIndex])) {
                    permutation[strIndex] = Character.toUpperCase(permutation[strIndex]);
                } else {
                    permutation[strIndex] = Character.toLowerCase(permutation[strIndex]);
                }
                permutations.add(new String(permutation));
            }
        }
        return permutations;
    }

    public static void main(String[] args) {
        var sol = new PermutationsByChangingCase();
        List<String> result =
                sol.findLetterCaseStringPermutations("ad52");
        System.out.println(" String permutations are: " + result);

        result = sol.findLetterCaseStringPermutations("ab7c");
        System.out.println(" String permutations are: " + result);
    }
}
