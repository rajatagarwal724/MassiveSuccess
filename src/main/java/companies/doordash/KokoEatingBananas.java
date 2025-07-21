package companies.doordash;

import java.util.Arrays;

/**
 * O (NLogM)
 * M is the maximum pile in the list of bananas
 * and we are iterating n elements with each Binary Search
 */
public class KokoEatingBananas {

    public int minEatingSpeed(int[] piles, int h) {
        int left = 1;
        int right = Arrays.stream(piles).max().getAsInt();
        while (left <= right) {
            int mid = left + (right - left)/2;

            int noOfHours = 0;
            for (int pile: piles) {
                noOfHours += Math.ceil((double) pile/ mid);
            }

            if (noOfHours <= h) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    public static void main(String[] args) {
        var sol = new KokoEatingBananas();
        System.out.println(sol.minEatingSpeed(new int[] {3,6,7,11}, 8));
        System.out.println(sol.minEatingSpeed(new int[] {30,11,23,4,20}, 5));
        System.out.println(sol.minEatingSpeed(new int[] {30,11,23,4,20}, 6));
    }
}
