package companies.doordash.repeat;

import java.util.Arrays;

/*

O (N Log M)
M the max pile in the array of Piles
Log M comparisons to reduce the search space to 1
O(N) operation to calculate the hoursTaken with Each Binary Search Operation
SO - O(N Log M)
 */
public class KokoEatingBananas {

    public int minEatingSpeed(int[] piles, int h) {
        int left = 1;
        int right = Arrays.stream(piles).max().getAsInt();

        while (left <= right) {
            int mid = left + (right - left)/2;

            int hoursTaken = 0;
            for (int pile: piles) {
                hoursTaken += Math.ceil((double) pile/mid);
            }

            if (hoursTaken <= h) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }


    public static void main(String[] args) {
        var sol = new KokoEatingBananas();
        System.out.println(
                sol.minEatingSpeed(
                new int[] {3,6,7,11}, 8
            )
        );

        System.out.println(
                sol.minEatingSpeed(
                        new int[] {30,11,23,4,20}, 5
                )
        );

        System.out.println(
                sol.minEatingSpeed(
                        new int[] {30,11,23,4,20}, 6
                )
        );
    }
}
