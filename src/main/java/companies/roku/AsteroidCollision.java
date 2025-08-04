package companies.roku;

import java.util.Stack;

public class AsteroidCollision {

    public int[] asteroidCollision(int[] asteroids) {
        Stack<Integer> stack = new Stack<>();
        for (int asteroid: asteroids) {
            boolean addAsteroid = true;
            while (!stack.isEmpty() && stack.peek() >= 0 && asteroid < 0) {

                if (Math.abs(stack.peek()) > Math.abs(asteroid)) {
                    addAsteroid = false;
                    break;
                } else if (Math.abs(stack.peek()) == Math.abs(asteroid)) {
                    addAsteroid = false;
                    stack.pop();
                    break;
                } else if (Math.abs(stack.peek()) < Math.abs(asteroid)) {
                    stack.pop();
                    addAsteroid = true;
                }
            }

            if (addAsteroid) {
                stack.push(asteroid);
            }
        }

        System.out.println(stack);
        return null;
    }

    public static void main(String[] args) {
        var sol = new AsteroidCollision();
//        sol.asteroidCollision(new int[] {5, 10, -5});
//        sol.asteroidCollision(new int[] {8, -8});
        sol.asteroidCollision(new int[] {-2,1,1,-1});
    }
}
