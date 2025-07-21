package coding.linkedin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LetterCombinations1 {

    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();

        Map<Character, String> combinations = Map.of(
                '2', "abc",
                '3', "def",
                '4', "ghi",
                '5', "jkl",
                '6', "mno",
                '7', "pqrs",
                '8', "tuv",
                '9', "wxyz"
        );

        var current = new StringBuilder();

        recurse(result, combinations, digits, 0, current);

        return result;
    }

    private void recurse(
            List<String> result,
            Map<Character, String> combinations,
            String digits,
            int index,
            StringBuilder current
    ) {

        if (current.length() == digits.length()) {
            result.add(new String(current));
            return;
        }

        char digit = digits.charAt(index);

        for (int i = 0; i < combinations.getOrDefault(digit, "").length(); i++) {
            char nextChar = combinations.get(digit).charAt(i);
            current.append(nextChar);
            recurse(result, combinations, digits, index + 1, current);
            current.deleteCharAt(current.length() - 1);
        }
    }

    public static void main(String[] args) {
        var sol = new LetterCombinations1();
        System.out.println(
                sol.letterCombinations("23")
        );
        System.out.println(
                sol.letterCombinations("")
        );
        System.out.println(
                sol.letterCombinations("2")
        );
    }
}
