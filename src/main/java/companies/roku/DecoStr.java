package companies.roku;

import java.util.Stack;

public class DecoStr {

    public String decodeString(String s) {
        Stack<StringBuilder> stringStack = new Stack<>();
        Stack<Integer> countStack = new Stack<>();
        int k = 0;
        StringBuilder currentString = new StringBuilder();
        char[] arr = s.toCharArray();

        for (int i = 0; i < arr.length; i++) {
            char elem = arr[i];

            if (Character.isDigit(elem)) {
                k = k * 10 + Character.getNumericValue(elem);
            } else if ('[' == elem) {
                countStack.push(k);
                stringStack.push(currentString);

                currentString = new StringBuilder();
                k = 0;
            } else if (']' == elem) {
                StringBuilder decodedString = stringStack.pop();
                int count = countStack.pop();
                for (int j = 0; j < count; j++) {
                    decodedString.append(currentString);
                }

                currentString = decodedString;
            } else {
                currentString.append(elem);
            }
        }

        return currentString.toString();
    }

    public static void main(String[] args) {
        var sol = new DecoStr();
        System.out.println(
                sol.decodeString("3[a]2[bc]")
        );

        System.out.println(
                sol.decodeString("3[a2[c]]")
        );

        System.out.println(
                sol.decodeString("2[abc]3[cd]ef")
        );
    }
}
