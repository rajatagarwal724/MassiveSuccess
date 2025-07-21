package companies.doordash.repeat;

import java.util.Stack;

public class BasicCalculator {

    public int calculate(String s) {
        int num = 0;
        int result = 0;
        int sign = 1;
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < s.length(); i++) {
            char elem = s.charAt(i);

            if (Character.isDigit(elem)) {
                num = num * 10 + (elem - '0');
            } else if (elem == '+') {
                result += sign * num;
                sign = 1;
                num = 0;
            } else if (elem == '-') {
                result += sign * num;
                sign = -1;
                num = 0;
            } else if (elem == '(') {
                stack.push(result);
                stack.push(sign);

                result = 0;
                sign = 1;
            } else if (elem == ')') {
                result += sign * num;

                result *= stack.pop();
                result += stack.pop();

                num = 0;
            }
        }

        return result + (sign * num);
    }

    public static void main(String[] args) {
        var sol = new BasicCalculator();
        System.out.println(
                sol.calculate("1 + 1")
        );

        System.out.println(
                sol.calculate(" 2-1 + 2 ")
        );

        System.out.println(
                sol.calculate("(1+(4+5+2)-3)+(6+8)")
        );
    }
}
