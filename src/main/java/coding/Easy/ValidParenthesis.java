package coding.Easy;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class ValidParenthesis {
    public boolean isValid(String s) {
        Stack<Character> parenthesis = new Stack<>();
        Set<Character> openParenthesis = Set.of('(', '{', '[');
        for (Character ch: s.toCharArray()) {

            if (openParenthesis.contains(ch)) {
                parenthesis.push(ch);
            } else {
                if (ch.equals(')') && !parenthesis.isEmpty() && parenthesis.peek().equals('(')) {
                    parenthesis.pop();
                } else if (ch.equals('}') && !parenthesis.isEmpty() && parenthesis.peek().equals('{')) {
                    parenthesis.pop();
                } else if (ch.equals(']') && !parenthesis.isEmpty() && parenthesis.peek().equals('[')) {
                    parenthesis.pop();
                } else {
                    return false;
                }
            }

        }
        return parenthesis.isEmpty();
    }

    public static void main(String[] args) {
        var sol = new ValidParenthesis();

        System.out.println(sol.isValid("(]"));
        System.out.println(sol.isValid("{[]}"));
        System.out.println(sol.isValid("[{]}"));
    }
}
