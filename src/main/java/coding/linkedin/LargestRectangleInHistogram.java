package coding.linkedin;

import java.util.Stack;

public class LargestRectangleInHistogram {

    public int largestRectangleArea(int[] heights) {
        int maxArea = 0;
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i <= heights.length; i++) {
            int currentHeight = i == heights.length ? 0 : heights[i];

            while (!stack.isEmpty() && currentHeight < heights[stack.peek()]) {
                int height = heights[stack.pop()];
                int width = stack.isEmpty() ? i : (i - stack.peek() - 1);
                maxArea = Math.max(maxArea, height * width);
            }

            stack.push(i);
        }
        return maxArea;
    }

    public static void main(String[] args) {
        var sol = new LargestRectangleInHistogram();
        System.out.println(sol.largestRectangleArea(new int[] {2,1,5,6,2,3}));
//        System.out.println(sol.largestRectangleArea(new int[] {4, 2, 3, 2}));
//        System.out.println(sol.largestRectangleArea(new int[] {1,3,4,5,2}));
//        System.out.println(sol.largestRectangleArea(new int[] {6,2,5,4,5,1,6}));
    }
}
