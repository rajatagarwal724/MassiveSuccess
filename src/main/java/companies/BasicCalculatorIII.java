package companies;

import java.util.Stack;

/**
 * LeetCode 772: Basic Calculator III
 * 
 * Implement a basic calculator to evaluate a simple expression string.
 * The expression string contains only non-negative integers, '+', '-', '*', '/' operators,
 * and open '(' and closing ')' parentheses.
 * 
 * The integer division should truncate toward zero.
 * You may assume that the given expression is always valid.
 * All intermediate results will be in the range of [-2^31, 2^31 - 1].
 * 
 * Note: You are not allowed to use any built-in function which evaluates strings as 
 * mathematical expressions, such as eval().
 * 
 * Example 1:
 * Input: s = "1+1"
 * Output: 2
 * 
 * Example 2:
 * Input: s = "6-4/2"
 * Output: 4
 * 
 * Example 3:
 * Input: s = "2*(5+5*2)/3+(6/2+8)"
 * Output: 21
 * 
 * Time Complexity: O(n) where n is the length of the string
 * Space Complexity: O(n) for the recursive call stack in worst case
 */
public class BasicCalculatorIII {
    
    /**
     * Approach 1: Recursive Descent Parser (MAIN SOLUTION)
     * 
     * This is the most intuitive approach that mirrors how we naturally parse math expressions.
     * It follows proper operator precedence:
     * 1. Parentheses (highest precedence)
     * 2. Multiplication and Division
     * 3. Addition and Subtraction (lowest precedence)
     */
    private int index = 0;
    
    public int calculate(String s) {
        index = 0;
        return parseExpression(s);
    }
    
    /**
     * Parse addition and subtraction (lowest precedence)
     */
    private int parseExpression(String s) {
        int result = parseTerm(s);
        
        while (index < s.length()) {
            skipWhitespace(s);
            if (index >= s.length()) break;
            
            char op = s.charAt(index);
            if (op == '+' || op == '-') {
                index++; // skip operator
                int nextTerm = parseTerm(s);
                result = (op == '+') ? result + nextTerm : result - nextTerm;
            } else {
                break;
            }
        }
        
        return result;
    }
    
    /**
     * Parse multiplication and division (higher precedence)
     */
    private int parseTerm(String s) {
        int result = parseFactor(s);
        
        while (index < s.length()) {
            skipWhitespace(s);
            if (index >= s.length()) break;
            
            char op = s.charAt(index);
            if (op == '*' || op == '/') {
                index++; // skip operator
                int nextFactor = parseFactor(s);
                result = (op == '*') ? result * nextFactor : result / nextFactor;
            } else {
                break;
            }
        }
        
        return result;
    }
    
    /**
     * Parse numbers and parentheses (highest precedence)
     */
    private int parseFactor(String s) {
        skipWhitespace(s);
        
        if (index >= s.length()) {
            return 0;
        }
        
        char c = s.charAt(index);
        
        // Handle parentheses
        if (c == '(') {
            index++; // skip '('
            int result = parseExpression(s);
            skipWhitespace(s);
            if (index < s.length() && s.charAt(index) == ')') {
                index++; // skip ')'
            }
            return result;
        }
        
        // Handle negative numbers
        if (c == '-') {
            index++; // skip '-'
            return -parseFactor(s);
        }
        
        // Handle positive numbers (including implicit positive)
        if (c == '+') {
            index++; // skip '+'
            return parseFactor(s);
        }
        
        // Parse number
        int num = 0;
        while (index < s.length() && Character.isDigit(s.charAt(index))) {
            num = num * 10 + (s.charAt(index) - '0');
            index++;
        }
        
        return num;
    }
    
    /**
     * Skip whitespace characters
     */
    private void skipWhitespace(String s) {
        while (index < s.length() && s.charAt(index) == ' ') {
            index++;
        }
    }
    
    /**
     * Approach 2: Stack-based Solution with Recursion
     * 
     * This approach uses a stack to handle operator precedence and recursion for parentheses.
     * It's more efficient for expressions with many nested parentheses.
     */
    public int calculateStack(String s) {
        return evaluateHelper(s, 0)[0];
    }
    
    private int[] evaluateHelper(String s, int start) {
        Stack<Integer> stack = new Stack<>();
        int num = 0;
        char operation = '+';
        int i = start;
        
        while (i < s.length()) {
            char c = s.charAt(i);
            
            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            } else if (c == '(') {
                // Recursively evaluate expression inside parentheses
                int[] result = evaluateHelper(s, i + 1);
                num = result[0];
                i = result[1];
            } else if (c == ')') {
                // End of current parentheses group
                break;
            }
            
            // Process operation when we encounter an operator or reach end
            if (c == '+' || c == '-' || c == '*' || c == '/' || c == ')' || i == s.length() - 1) {
                if (operation == '+') {
                    stack.push(num);
                } else if (operation == '-') {
                    stack.push(-num);
                } else if (operation == '*') {
                    stack.push(stack.pop() * num);
                } else if (operation == '/') {
                    stack.push(stack.pop() / num);
                }
                
                if (c == ')') {
                    break;
                }
                
                operation = c;
                num = 0;
            }
            
            i++;
        }
        
        // Sum all values in the stack
        int result = 0;
        while (!stack.isEmpty()) {
            result += stack.pop();
        }
        
        return new int[]{result, i};
    }
    
    /**
     * Approach 3: Simple Iterative Solution
     * 
     * This approach handles parentheses by finding matching pairs and recursively evaluating substrings.
     * It's easier to understand but less efficient for deeply nested expressions.
     */
    public int calculateIterative(String s) {
        Stack<Integer> stack = new Stack<>();
        int num = 0;
        char operation = '+';
        
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            
            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            } else if (c == '(') {
                // Find matching closing parenthesis
                int count = 1;
                int j = i + 1;
                while (count > 0) {
                    if (s.charAt(j) == '(') count++;
                    else if (s.charAt(j) == ')') count--;
                    j++;
                }
                // Recursively evaluate the substring inside parentheses
                num = calculateIterative(s.substring(i + 1, j - 1));
                i = j - 1; // Move index to closing parenthesis
            }
            
            // Process operation
            if (c == '+' || c == '-' || c == '*' || c == '/' || i == s.length() - 1) {
                if (operation == '+') {
                    stack.push(num);
                } else if (operation == '-') {
                    stack.push(-num);
                } else if (operation == '*') {
                    stack.push(stack.pop() * num);
                } else if (operation == '/') {
                    stack.push(stack.pop() / num);
                }
                
                operation = c;
                num = 0;
            }
        }
        
        // Sum all values in the stack
        int result = 0;
        while (!stack.isEmpty()) {
            result += stack.pop();
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        BasicCalculatorIII calculator = new BasicCalculatorIII();
        
        System.out.println("=== Basic Calculator III Tests ===");
        
        // Test 1: Basic addition
        System.out.println("Test 1: '1+1' = " + calculator.calculate("1+1"));
        
        // Test 2: Division with precedence
        System.out.println("Test 2: '6-4/2' = " + calculator.calculate("6-4/2"));
        
        // Test 3: Complex expression with parentheses
        System.out.println("Test 3: '2*(5+5*2)/3+(6/2+8)' = " + calculator.calculate("2*(5+5*2)/3+(6/2+8)"));
        
        // Test 4: Expression with spaces
        System.out.println("Test 4: ' 2-1 + 2 ' = " + calculator.calculate(" 2-1 + 2 "));
        
        // Test 5: Nested parentheses
        System.out.println("Test 5: '(2+6*3+5-(3*14/7+2)*5)+3' = " + calculator.calculate("(2+6*3+5-(3*14/7+2)*5)+3"));
        
        // Test 6: Division truncation
        System.out.println("Test 6: '14/3*2' = " + calculator.calculate("14/3*2"));
        
        // Test 7: Single number
        System.out.println("Test 7: '42' = " + calculator.calculate("42"));
        
        // Test 8: All operations
        System.out.println("Test 8: '1*2-3/4+5*6-7*8+9/10' = " + calculator.calculate("1*2-3/4+5*6-7*8+9/10"));
        
        // Test alternative implementations
        System.out.println("\n=== Testing Alternative Implementations ===");
        String testExpr = "2*(5+5*2)/3+(6/2+8)";
        
        // Reset index for recursive descent parser
        calculator.index = 0;
        System.out.println("Recursive Descent: '" + testExpr + "' = " + calculator.calculate(testExpr));
        
        System.out.println("Stack-based: '" + testExpr + "' = " + calculator.calculateStack(testExpr));
        System.out.println("Iterative: '" + testExpr + "' = " + calculator.calculateIterative(testExpr));
        
        // Additional test cases
        System.out.println("\n=== Additional Test Cases ===");
        
        // Reset index
        calculator.index = 0;
        System.out.println("'1-1+1' = " + calculator.calculate("1-1+1"));
        
        calculator.index = 0;
        System.out.println("'1-(5)' = " + calculator.calculate("1-(5)"));
        
        calculator.index = 0;
        System.out.println("'(1)' = " + calculator.calculate("(1)"));
        
        calculator.index = 0;
        System.out.println("'2*3+4' = " + calculator.calculate("2*3+4"));
        
        calculator.index = 0;
        System.out.println("'2+3*4' = " + calculator.calculate("2+3*4"));
        
        // Edge cases
        System.out.println("\n=== Edge Cases ===");
        
        calculator.index = 0;
        System.out.println("'0' = " + calculator.calculate("0"));
        
        calculator.index = 0;
        System.out.println("'1000000' = " + calculator.calculate("1000000"));
        
        calculator.index = 0;
        System.out.println("'(((1)))' = " + calculator.calculate("(((1)))"));
        
        calculator.index = 0;
        System.out.println("'1+2*3+4*5' = " + calculator.calculate("1+2*3+4*5"));
    }
} 