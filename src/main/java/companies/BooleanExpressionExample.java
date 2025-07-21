package companies;

/**
 * Example demonstrating boolean expression evaluation:
 * (!Character.isDigit(currentChar) && !Character.isWhitespace(currentChar) || i == length - 1)
 */
public class BooleanExpressionExample {
    
    public static void evaluateExpression(String input) {
        System.out.println("Evaluating string: \"" + input + "\"");
        System.out.println("Index | Char | isDigit | isWhitespace | !isDigit | !isWhitespace | (!isDigit && !isWhitespace) | i==length-1 | Final Result");
        System.out.println("------|------|---------|--------------|----------|---------------|---------------------------|-----------|-------------");
        
        int length = input.length();
        
        for (int i = 0; i < length; i++) {
            char currentChar = input.charAt(i);
            
            // Individual conditions
            boolean isDigit = Character.isDigit(currentChar);
            boolean isWhitespace = Character.isWhitespace(currentChar);
            boolean notDigit = !isDigit;
            boolean notWhitespace = !isWhitespace;
            boolean bothNot = notDigit && notWhitespace;
            boolean isLast = (i == length - 1);
            
            // Final result
            boolean finalResult = (!Character.isDigit(currentChar) && !Character.isWhitespace(currentChar) || i == length - 1);
            
            System.out.printf("%5d | %4c | %7s | %12s | %8s | %13s | %25s | %9s | %11s%n",
                i, currentChar, isDigit, isWhitespace, notDigit, notWhitespace, bothNot, isLast, finalResult);
        }
        System.out.println();
    }
    
    public static void demonstrateWithCalculatorLogic(String expression) {
        System.out.println("=== Calculator Logic Demonstration ===");
        System.out.println("Expression: \"" + expression + "\"");
        System.out.println("Processing when condition is true (operator or end of string):");
        
        int length = expression.length();
        int currentNumber = 0;
        
        for (int i = 0; i < length; i++) {
            char currentChar = expression.charAt(i);
            
            if (Character.isDigit(currentChar)) {
                currentNumber = currentNumber * 10 + (currentChar - '0');
            }
            
            // The condition we're analyzing
            if (!Character.isDigit(currentChar) && !Character.isWhitespace(currentChar) || i == length - 1) {
                System.out.println("  At index " + i + " (char: '" + currentChar + "'): Processing number " + currentNumber);
                currentNumber = 0; // Reset for next number
            }
        }
        System.out.println();
    }
    
    public static void main(String[] args) {
        // Example 1: Simple expression
        evaluateExpression("12+34");
        
        // Example 2: Expression with spaces
        evaluateExpression("5 - 3");
        
        // Example 3: Expression with parentheses
        evaluateExpression("(2*3)");
        
        // Example 4: Single digit
        evaluateExpression("7");
        
        // Demonstrate practical usage
        demonstrateWithCalculatorLogic("12+34*5");
        demonstrateWithCalculatorLogic("1 - 2 + 3");
    }
} 