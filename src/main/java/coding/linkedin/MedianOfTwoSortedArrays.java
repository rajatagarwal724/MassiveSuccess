package coding.linkedin;

import java.util.ArrayList;

public class MedianOfTwoSortedArrays {

    // Function to find the median of two sorted arrays
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int n1 = 0, n2 = 0;

        int totalLength = nums1.length + nums2.length;

        boolean isOdd = totalLength % 2 != 0;

        var medianIndices = new ArrayList<>();
        medianIndices.add(totalLength/2);

        if (!isOdd) {
            medianIndices.add((totalLength/2) - 1);
        }

        var last = 0;
        var secondLast = 0;
        var current = 0;
        while (current <= (totalLength/2)) {

        }

        return 0;
    }

    public static void main(String[] args) {
        var sol = new MedianOfTwoSortedArrays();
        System.out.println(
                sol.findMedianSortedArrays(
                        new int[] {2, 3, 5, 8},
                        new int[] {1, 4, 6, 7, 9}
                )
        );
    }
}
