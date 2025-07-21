package coding.linkedin;

import java.util.Stack;

public class ValidParanthesis {

    public boolean isValid(String s) {
        char[] arr = s.toCharArray();
        Stack<Character> stack = new Stack<>();

        for (int i = 0; i < arr.length; i++) {
            var elem = arr[i];

            if (elem == '(' || elem == '{' || elem == '[') {
                stack.push(elem);
            } else if (!stack.isEmpty()) {
                if (
                        (elem == ')' && stack.peek() == '(')
                        ||
                                (elem == '}' && stack.peek() == '{')
                        ||
                                (elem == ']' && stack.peek() == '[')
                ) {
                    stack.pop();
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return stack.isEmpty();
    }

    public static void main(String[] args) {
        var sol = new ValidParanthesis();
        System.out.println(sol.isValid("()"));
        System.out.println(sol.isValid("()[]{}"));
        System.out.println(sol.isValid("(]"));
        System.out.println(sol.isValid("([])"));
    }
}
