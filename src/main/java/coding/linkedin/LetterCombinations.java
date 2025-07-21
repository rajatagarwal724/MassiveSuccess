package coding.linkedin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LetterCombinations {

    /**
     * Returns all possible letter combinations that the phone number digits could represent
     * Time Complexity: O(4^n) where n is the number of digits (at most 4 letters per digit)
     * Space Complexity: O(n) for recursion stack + O(4^n) for output
     */
    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        // Handle empty input
        if (digits == null || digits.isEmpty()) {
            return result;
        }
        
        // Map each digit to its corresponding letters
        Map<Character, String> phoneMap = new HashMap<>();
        phoneMap.put('2', "abc");
        phoneMap.put('3', "def");
        phoneMap.put('4', "ghi");
        phoneMap.put('5', "jkl");
        phoneMap.put('6', "mno");
        phoneMap.put('7', "pqrs");
        phoneMap.put('8', "tuv");
        phoneMap.put('9', "wxyz");
        
        // Use backtracking to find all combinations
        backtrack(result, phoneMap, digits, 0, new StringBuilder());
        return result;
    }
    
    private void backtrack(List<String> result, Map<Character, String> phoneMap, String digits, 
                          int index, StringBuilder current) {
        // Base case: If we've processed all digits, add the current combination
        if (index == digits.length()) {
            result.add(current.toString());
            return;
        }
        
        // Get the current digit and its corresponding letters
        char digit = digits.charAt(index);
        String letters = phoneMap.get(digit);
        
        // Try each letter for the current digit
        for (char letter : letters.toCharArray()) {
            // Add the letter to our current combination
            current.append(letter);
            
            // Recurse to the next digit
            backtrack(result, phoneMap, digits, index + 1, current);
            
            // Backtrack (remove the letter to try the next one)
            current.deleteCharAt(current.length() - 1);
        }
    }

    public static void main(String[] args) {
        LetterCombinations sol = new LetterCombinations();
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
