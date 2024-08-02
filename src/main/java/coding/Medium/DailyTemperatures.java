package coding.Medium;

import java.util.Arrays;
import java.util.Stack;

public class DailyTemperatures {

    public int[] dailyTemperatures(int[] temperatures) {
        int[] res = new int[temperatures.length];
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < temperatures.length; i++) {
            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int popIndex = stack.pop();
                res[popIndex] = i - popIndex;
            }
            stack.push(i);
        }
        while (!stack.isEmpty()) {
            res[stack.pop()] = 0;
        }
        return res;
    }

    public static void main(String[] args) {
        var sol = new DailyTemperatures();

        System.out.println(Arrays.toString(sol.dailyTemperatures(new int[]{45, 50, 40, 60, 55})));
        System.out.println(Arrays.toString(sol.dailyTemperatures(new int[]{80, 75, 85, 90, 60})));
        System.out.println(Arrays.toString(sol.dailyTemperatures(new int[]{32, 32, 32, 32, 32})));
    }
}
