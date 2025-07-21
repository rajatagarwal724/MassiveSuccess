package companies;

import java.util.Stack;

/**
 * LeetCode 224: Basic Calculator
 * 
 * Given a string s representing a valid expression, implement a basic calculator to evaluate it.
 * The expression string may contain:
 * - Non-negative integers
 * - '+', '-' operators
 * - '(' and ')' parentheses
 * - ' ' spaces
 * 
 * Time Complexity: O(n) where n is the length of the string
 * Space Complexity: O(n) for the stack in worst case (nested parentheses)
 */
public class BasicCalculator {

    /**
     * Stack-based approach to handle parentheses
     * 
     * Algorithm:
     * 1. Use a stack to store intermediate results when encountering '('
     * 2. When we see '(', push current result and sign to stack
     * 3. When we see ')', pop from stack and combine with current result
     * 4. Process numbers and operators normally
     */
    public int calculate(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        
        Stack<Integer> stack = new Stack<>();
        int result = 0;
        int number = 0;
        int sign = 1; // 1 for positive, -1 for negative
        
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            
            if (Character.isDigit(c)) {
                // Build multi-digit number
                number = number * 10 + (c - '0');
            } else if (c == '+') {
                // Add previous number to result
                result += sign * number;
                number = 0;
                sign = 1;
            } else if (c == '-') {
                // Add previous number to result
                result += sign * number;
                number = 0;
                sign = -1;
            } else if (c == '(') {
                // Push current result and sign to stack
                stack.push(result);
                stack.push(sign);
                // Reset for new sub-expression
                result = 0;
                sign = 1;
            } else if (c == ')') {
                // Complete current sub-expression
                result += sign * number;
                number = 0;
                
                // Pop sign and previous result from stack
                result *= stack.pop(); // This is the sign before '('
                result += stack.pop(); // This is the result before '('
            }
            // Skip spaces
        }
        
        // Don't forget the last number
        result += sign * number;
        
        return result;
    }

    /**
     * Alternative recursive approach
     * Uses a global index to track position in string
     */
    private int index = 0;
    
    public int calculateRecursive(String s) {
        index = 0;
        return helper(s);
    }
    
    private int helper(String s) {
        int result = 0;
        int number = 0;
        int sign = 1;
        
        while (index < s.length()) {
            char c = s.charAt(index);
            
            if (Character.isDigit(c)) {
                number = number * 10 + (c - '0');
            } else if (c == '+') {
                result += sign * number;
                number = 0;
                sign = 1;
            } else if (c == '-') {
                result += sign * number;
                number = 0;
                sign = -1;
            } else if (c == '(') {
                index++; // Skip '('
                int subResult = helper(s);
                result += sign * subResult;
                number = 0;
            } else if (c == ')') {
                result += sign * number;
                return result;
            }
            index++;
        }
        
        return result + sign * number;
    }

    public static void main(String[] args) {
        BasicCalculator calculator = new BasicCalculator();
        
        // Test cases
        System.out.println("=== Basic Calculator I Tests ===");
        
        // Test 1: "1 + 1" = 2
        System.out.println("1 + 1 = " + calculator.calculate("1 + 1"));
        
        // Test 2: " 2-1 + 2 " = 3
        System.out.println(" 2-1 + 2  = " + calculator.calculate(" 2-1 + 2 "));
        
        // Test 3: "(1+(4+5+2)-3)+(6+8)" = 23
        System.out.println("(1+(4+5+2)-3)+(6+8) = " + calculator.calculate("(1+(4+5+2)-3)+(6+8)"));
        
        // Test 4: "2147483647" = 2147483647
        System.out.println("2147483647 = " + calculator.calculate("2147483647"));
        
        // Test 5: "- (3 + (4 + 5))" = -12
        System.out.println("- (3 + (4 + 5)) = " + calculator.calculate("- (3 + (4 + 5))"));
        
        // Test recursive approach
        System.out.println("\n=== Recursive Approach Tests ===");
        System.out.println("1 + 1 = " + calculator.calculateRecursive("1 + 1"));
        System.out.println("(1+(4+5+2)-3)+(6+8) = " + calculator.calculateRecursive("(1+(4+5+2)-3)+(6+8)"));
    }
} 