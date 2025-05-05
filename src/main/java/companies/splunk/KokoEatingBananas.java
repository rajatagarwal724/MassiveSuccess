package companies.splunk;

import java.util.Arrays;

public class KokoEatingBananas {

    public int minEatingSpeed(int[] piles, int h) {
        int left = 1;
        int right = Arrays.stream(piles).max().getAsInt();

        while (left < right) {
            int middle = left + (right - left)/2;
            int hourSpent = 0;
            for (int i = 0; i < piles.length; i++) {
                hourSpent += Math.ceil((double) piles[i]/ middle);
            }
            if (hourSpent <= h) {
                right = middle;
            } else {
                left = middle + 1;
            }
        }
        return right;
    }

    public static void main(String[] args) {
        var sol = new KokoEatingBananas();
        System.out.println(sol.minEatingSpeed(new int[] {3,6,7,11}, 8));
        System.out.println(sol.minEatingSpeed(new int[] {30,11,23,4,20}, 5));
        System.out.println(sol.minEatingSpeed(new int[] {30,11,23,4,20}, 6));
    }
}
