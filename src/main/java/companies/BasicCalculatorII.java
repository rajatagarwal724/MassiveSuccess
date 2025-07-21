package companies;

import java.util.Stack;

public class BasicCalculatorII {

    public int calculate(String s) {
        if (null == s || s.isBlank()) {
            return 0;
        }
        int result = 0;
        int currNum = 0;
        char operation = '+';
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < s.length(); i++) {
            char currChar = s.charAt(i);

            if (Character.isDigit(currChar)) {
                currNum = (currNum * 10) + (currChar - '0');
            }

            if (!Character.isDigit(currChar) || i == (s.length() - 1)) {
                if (operation == '+') {
                    stack.push(currNum);
                }

                if (operation == '-') {
                    stack.push(-currNum);
                }

                if (operation == '*') {
                    stack.push(stack.pop() * currNum);
                }

                if (operation == '/') {
                    stack.push(stack.pop() / currNum);
                }

                currNum = 0;
                operation = currChar;
            }
        }

        while (!stack.isEmpty()) {
            result += stack.pop();
        }

        return result;
    }

    public int calculate_(String s) {
        if (null == s || s.isBlank()) {
            return 0;
        }

        int result = 0, currNum = 0, n = s.length() - 1;
        char operation = '+';
        Stack<Integer> stack = new Stack<>();

        for(int i = 0; i < n; i++) {
            char currChar = s.charAt(i);

            if (Character.isDigit(currChar)) {
                currNum = (currNum * 10) + (currChar - '0');
            }

            if (!Character.isDigit(currChar) && !Character.isWhitespace(currChar) || i == n -1) {
                if(operation == '+') {
                    stack.push(currNum);
                }

                if(operation == '-') {
                    stack.push(-currNum);
                }

                if(operation == '*') {
                    stack.push(stack.pop() * currNum);
                }

                if(operation == '/') {
                    stack.push(stack.pop() / currNum);
                }

                currNum = 0;
                operation = currChar;
            }
        }

        while (!stack.isEmpty()) {
            result += stack.pop();
        }

        return result;
    }

    public static void main(String[] args) {
        var sol = new BasicCalculatorII();
        System.out.println(
                sol.calculate("22-3*5")
        );

        System.out.println(
                sol.calculate_("3+2*2")
        );
    }
}
