package companies.salesforce.DSA;

import java.util.ArrayList;
import java.util.List;

public class PerfectPairs {

    public static List<int[]> findPerfectPairs(int[] A) {
        int n = A.length;
        List<int[]> result = new ArrayList<>();

        // Sort the array
        java.util.Arrays.sort(A);

        // Two-pointer approach
        int left = 0, right = n - 1;
        while (left < right) {
            int x = A[left], y = A[right];
            int diff1 = Math.abs(x - y), diff2 = Math.abs(x + y);
            int maxXY = Math.max(Math.abs(x), Math.abs(y));
            int minXY = Math.min(Math.abs(x), Math.abs(y));

            if (diff1 <= minXY && diff2 >= maxXY) {
                result.add(new int[]{x, y});
                left++;
                right--;
            } else if (Math.min(diff1, diff2) > minXY) {
                left++;
            } else {
                right--;
            }
        }

        return result;
    }

    public static void main(String[] args) {
//        int[] A = {2, 3, 4, 5, 6, 7};
        int[] A = {2, -3, 5};
        List<int[]> perfectPairs = findPerfectPairs(A);
        System.out.println(perfectPairs.size());
    }
}
