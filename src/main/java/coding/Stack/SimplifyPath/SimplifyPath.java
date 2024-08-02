package coding.Stack.SimplifyPath;

import java.util.Stack;

public class SimplifyPath {

    public String simplifyPath(String path) {
        String[] arr = path.split("/");
        Stack<String> stack = new Stack<>();

        for (String input: arr) {
            if (input.equals("") || input.equals(".")) {
                continue;
            } else if (input.equals("..")) {
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else {
                stack.push(input);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();

        for (String dir: stack) {
            stringBuilder.append("/");
            stringBuilder.append(dir);
        }

        return stringBuilder.isEmpty() ? "/" : stringBuilder.toString();
    }

    public static void main(String[] args) {
        var sol = new SimplifyPath();
        System.out.println(sol.simplifyPath("/home//foo/"));
        System.out.println(sol.simplifyPath("/../"));
        System.out.println(sol.simplifyPath("/a//b////c/d//././/.."));
    }
}
