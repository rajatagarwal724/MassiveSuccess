package companies.roku;

import java.util.Stack;

public class BackspaceStringCompare {

    public boolean backspaceCompare(String s, String t) {
        char[] sArr = s.toCharArray();
//        System.out.println(findString(sArr));

        char[] tArr = t.toCharArray();
//        System.out.println(findString(tArr));

        int i = sArr.length - 1, j = tArr.length - 1;

        while (i >= 0 || j >= 0) {
            int nextSIdx = modifyString(sArr, i);
            int nextTIdx = modifyString(tArr, j);

            if (nextSIdx == -1 && nextTIdx == -1) {
                return true;
            }

            if (nextSIdx == -1 || nextTIdx == -1) {
                return false;
            }

            if (sArr[nextSIdx] != tArr[nextTIdx]) {
                return false;
            }

            i = nextSIdx - 1;
            j = nextTIdx - 1;
        }

        return true;
    }

    private static int modifyString(char[] str, int index) {
        int count = 0;
        for (int i = index; index >= 0; index--) {
            var elem = str[index];
            if (elem == '#') {
                count++;
            } else {
                if (count > 0) {
                    return i - count - 1;
                }
                return i;
            }
        }
        return -1;
    }

    private static String findString(char[] sArr) {
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < sArr.length; i++) {
            var elem = sArr[i];
            if (elem == '#') {
                if (!stack.isEmpty()) {
                    stack.pop();
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
        var sol = new BackspaceStringCompare();
//        System.out.println(sol.backspaceCompare("ab#c", "ad#c"));
        System.out.println(sol.backspaceCompare("ab##", "c#d#"));
//        System.out.println(sol.backspaceCompare("a#c", "b"));
    }
}
