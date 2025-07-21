package coding.linkedin;

import java.util.Stack;

public class LongestValidParentheses {
    public int longestValidParentheses(String s) {
        if (null == s || s.isBlank()) {
            return 0;
        }

        char[] arr = s.toCharArray();
        int maxLen = Integer.MIN_VALUE;
        for (int i = 0; i < s.length() - 1; i++) {
            for (int j = i + 1; j < s.length(); j += 1) {
                if (isValid(arr, i, j)) {
                    maxLen = Integer.max(maxLen, j - i + 1);
                }
            }
        }

        return maxLen;
    }

    private boolean isValid(char[] arr, int left, int right) {
        Stack<Character> stack = new Stack<>();
        for (int i = left; i <= right; i++) {
            var elem = arr[i];
            if (elem == '(') {
                stack.push('(');
            } else if (!stack.isEmpty() && stack.peek() == '('){
                stack.pop();
            } else {
                return false;
            }
        }

        return stack.isEmpty();
    }

    public int longestValidParentheses_1(String s) {
        char[] arr = s.toCharArray();
        int left = 0, right = 0;
        int maxLen = 0;

        for (int i = 0; i < arr.length; i++) {
            var elem = arr[i];

            if (elem == '(') {
                left++;
            } else {
                right++;
            }

            if (left == right) {
                maxLen = Math.max(maxLen, 2 * right);
            }

            if (right > left) {
                left = 0;
                right = 0;
            }
        }

        left = 0;
        right = 0;

        for (int i = arr.length - 1; i >= 0; i--) {
            var elem = arr[i];

            if (elem == '(') {
                left++;
            } else {
                right++;
            }

            if (left == right) {
                maxLen = Math.max(maxLen, 2 * left);
            }

            if (left > right) {
                left = right = 0;
            }
        }

        return maxLen;
    }

    public static void main(String[] args) {
        var sol = new LongestValidParentheses();
//        System.out.println(
//                sol.longestValidParentheses("(()")
//        );
//
//        System.out.println(
//                sol.longestValidParentheses(")()())")
//        );
//
//        System.out.println(
//                sol.longestValidParentheses("")
//        );
//
//        System.out.println(
//                sol.longestValidParentheses_1("(()")
//        );
//
//        System.out.println(
//                sol.longestValidParentheses_1(")()())")
//        );
//
//        System.out.println(
//                sol.longestValidParentheses_1("")
//        );

        System.out.println(
                sol.longestValidParentheses_1(")()())")
        );
    }
}
