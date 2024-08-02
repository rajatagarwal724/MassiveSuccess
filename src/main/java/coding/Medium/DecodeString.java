package coding.Medium;

import java.util.LinkedList;
import java.util.Stack;

public class DecodeString {

    public String decodeString(String s) {
//        char[] arr = s.toCharArray();
//        Stack<String> stack = new Stack<>();
//
//        for (int i = 0; i < arr.length; i++) {
//            char elem = arr[i];
//
//            if (Character.isLetterOrDigit(elem)) {
//                stack.push(String.valueOf(elem));
//            } else if (elem == ']') {
//                StringBuilder literal = new StringBuilder();
//                while (!stack.isEmpty()) {
//                    String pop = stack.pop();
//                    if (Character.isLetter(Integer.parseInt(pop))) {
//                        literal.insert(0, pop);
//                    } else {
//                        int num = stack.pop();
//                        literal.append(String.valueOf(literal).repeat(num));
//                    }
//                }
//                stack.push(new Character(literal.toString()));
//            }
//        }


        return "";
    }

    public static void main(String[] args) {

    }
}
