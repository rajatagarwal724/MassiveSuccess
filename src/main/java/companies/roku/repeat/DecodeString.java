package companies.roku.repeat;

import java.util.Stack;

public class DecodeString {

    public String decodeString(String s) {
        Stack<Integer> countStack = new Stack<>();
        Stack<StringBuilder> stringStack = new Stack<>();

        int k = 0;
        StringBuilder currentString = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            char elem = s.charAt(i);

            if (Character.isDigit(elem)) {
                k = k * 10 + Character.getNumericValue(elem);
            } else if (elem == '[') {
                countStack.push(k);
                stringStack.push(currentString);

                k = 0;
                currentString = new StringBuilder();
            } else if (elem == ']') {
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
        var sol = new DecodeString();
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
