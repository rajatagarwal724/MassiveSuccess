package companies.roku;

import java.util.Arrays;
import java.util.Map;

public class Candy {

    public int candy(int[] ratings) {
        int[] candies = new int[ratings.length];

        Arrays.fill(candies, 1);;

        for (int i = 1; i < ratings.length; i++) {
            if (ratings[i] > ratings[i - 1]) {
                candies[i] = candies[i - 1] + 1;
            }
        }

        for (int i = ratings.length - 2; i >= 0; i--) {
            if (ratings[i] > ratings[i + 1]) {
                candies[i] = Math.max(candies[i], candies[i] + 1);
            }
        }

        int sum = Arrays.stream(candies).sum();

        return sum;
    }

    public static void main(String[] args) {
        var sol = new Candy();
        System.out.println(sol.candy(new int[] {1,0,2}));
        System.out.println(sol.candy(new int[] {1,2,2}));
    }
}
