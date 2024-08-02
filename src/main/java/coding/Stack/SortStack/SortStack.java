package coding.Stack.SortStack;

import coding.SlidingWindow.LongestContiguousSubarrayWith1.Solution;

import java.util.Stack;

public class SortStack {

    public static Stack<Integer> sortStack(Stack<Integer> input) {
        Stack<Integer> tmpStack = new Stack<Integer>();

        while (!input.isEmpty()) {
            int top = input.pop();

            if (tmpStack.isEmpty() || top >= tmpStack.peek()) {
                tmpStack.push(top);
            } else {
                while (!tmpStack.isEmpty() && top < tmpStack.peek()) {
                    input.push(tmpStack.pop());
                }
                tmpStack.push(top);
            }
        }

        return tmpStack;
    }

    private static Stack<Integer> getStack(int[] nums) {
        Stack<Integer> stack = new Stack<>();

        for (int num: nums) {
            stack.push(num);
        }
        return stack;
    }

    public static void main(String[] args) {
        System.out.println(sortStack(getStack(new int[]{34, 3, 31, 98, 92, 23})));
    }
}
