package coding.subset;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class ParanthesesString {
    String str;
    int openCount;
    int closeCount;

    public ParanthesesString(String str) {
        this.str = str;
    }

    public ParanthesesString(String str, int openCount, int closeCount) {
        this.str = str;
        this.openCount = openCount;
        this.closeCount = closeCount;
    }
}

public class BalancedParantheses {

    public List<String> generateValidParenthesesRecursive(int num) {
        List<String> result = new ArrayList<>();
        char[] parenthesesStr = new char[2 * num];
        generateValidParenthesesRecursive(parenthesesStr, num,0, 0, 0, result);
        return result;
    }

    private void generateValidParenthesesRecursive(char[] parenthesesStr, int num, int index, int openCount, int closeCount, List<String> result) {
        if (num == openCount && num == closeCount) {
            result.add(String.valueOf(parenthesesStr));
        } else {
            if (openCount < num) {
                parenthesesStr[index] = '(';
                generateValidParenthesesRecursive(parenthesesStr, num, index + 1, openCount + 1, closeCount, result);
            }

            if (closeCount < openCount) {
                parenthesesStr[index] = ')';
                generateValidParenthesesRecursive(parenthesesStr, num, index + 1, openCount, closeCount + 1, result);
            }
        }
    }

    public List<String> generateValidParentheses(int num) {
        List<String> result = new ArrayList<String>();

        Queue<ParanthesesString> queue = new LinkedList<>();
        queue.add(new ParanthesesString(""));

        while (!queue.isEmpty()) {
            var paranthesesStr = queue.poll();
            if (paranthesesStr.openCount == num && paranthesesStr.closeCount == num) {
                result.add(paranthesesStr.str);
            } else {
                if (paranthesesStr.openCount < num) {
                    queue.offer(new ParanthesesString(paranthesesStr.str + "(", paranthesesStr.openCount + 1, paranthesesStr.closeCount));
                }

                if (paranthesesStr.closeCount < paranthesesStr.openCount) {
                    queue.offer(new ParanthesesString(paranthesesStr.str + ")", paranthesesStr.openCount, paranthesesStr.closeCount + 1));
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        var sol = new BalancedParantheses();
        List<String> result = sol.generateValidParentheses(2);
        System.out.println("All combinations of balanced parentheses are: " + result);

        result = sol.generateValidParentheses(3);
        System.out.println("All combinations of balanced parentheses are: " + result);

        result = sol.generateValidParenthesesRecursive(2);
        System.out.println("All combinations of balanced parentheses are: " + result);

        result = sol.generateValidParenthesesRecursive(3);
        System.out.println("All combinations of balanced parentheses are: " + result);
    }
}
