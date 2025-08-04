package companies.roku;

import java.util.Stack;

public class DecodeString {

    public String decodeString(String s) {
        Stack<Character> stack = new Stack<>();
        char[] arr = s.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            var elem = arr[i];
            if (elem == ']') {
                StringBuilder stringBuilder = new StringBuilder();
                while (!stack.isEmpty() && stack.peek() != '[') {
                    stringBuilder.append(stack.pop());
                }
                stack.pop();

                stringBuilder = stringBuilder.reverse();
                int base = 1;
                int num = 0;
                while (!stack.isEmpty() && Character.isDigit(stack.peek())) {
                    char digit = stack.pop();
                    num = num + Character.getNumericValue(digit) * base;
                    base = base * 10;
                }
                String prev = stringBuilder.toString();
                stringBuilder = new StringBuilder();
                for (int j = 0; j < num; j++) {
                    stringBuilder.append(prev);
                }
//                stringBuilder = stringBuilder.repeat(stringBuilder, num);
                char[] strr = stringBuilder.toString().toCharArray();
                for (int k = 0; k < strr.length; k++) {
                    stack.push(strr[k]);
                }
            } else {
                stack.push(elem);
            }
        }
        StringBuilder res = new StringBuilder();
        while (!stack.isEmpty()) {
            res.append(stack.pop());
        }

        return res.reverse().toString();
    }

    public static void main(String[] args) {
        var sol = new DecodeString();
        System.out.println(sol.decodeString("3[a]2[bc]"));
        System.out.println(sol.decodeString("3[a2[c]]"));
        System.out.println(sol.decodeString("2[abc]3[cd]ef"));
    }
}
