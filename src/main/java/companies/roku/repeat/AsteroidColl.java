package companies.roku.repeat;

import java.util.Arrays;
import java.util.Stack;

public class AsteroidColl {

    public int[] asteroidCollision(int[] asteroids) {
        Stack<Integer> stack = new Stack<>();
        for(int asteroid : asteroids) {
            boolean addFlag = true;
            while (!stack.isEmpty() && stack.peek() > 0 && asteroid < 0) {

                if (Math.abs(stack.peek()) == Math.abs(asteroid)) {
                    stack.pop();
                    addFlag = false;
                    break;
                } else if(Math.abs(stack.peek()) > Math.abs(asteroid)) {
                    addFlag = false;
                    break;
                } else if(Math.abs(stack.peek()) < Math.abs(asteroid)) {
                    stack.pop();
                    addFlag = true;
                }
            }

            if(addFlag) {
                stack.push(asteroid);
            }
        }

        int[] res = new int[stack.size()];
        for(int i = res.length - 1; i >= 0; i--) {
            res[i] = stack.pop();
        }

        return res;
    }

    public static void main(String[] args) {
        var sol = new AsteroidColl();
        System.out.println(
                Arrays.toString(sol.asteroidCollision(new int[] {10, 2, -5}))
        );
    }
}
