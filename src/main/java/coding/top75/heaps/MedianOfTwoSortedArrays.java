package coding.top75.heaps;

public class MedianOfTwoSortedArrays {


    /**
     * Input: [1, 3, 5] and [2, 4, 6]
     * 3
     * 0 1 2 3 4 5
     * 1 2 3 4 5 6
     *
     *
     * Input: [2, 3, 5, 8] and [1, 4, 6, 7, 9]
     * Expected Output: 5
     * Justification: The merged array would be [1, 2, 3, 4, 5, 6, 7, 8, 9]. The median is 5.
     *
     * @param nums1
     * @param nums2
     * @return
     */
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int totalLength = nums1.length + nums2.length;
        int current = -1;
        int last = -1;

        int pointer1 = 0, pointer2 = 0, currIdx = 0;

        for (currIdx = 0; currIdx <= totalLength/2; currIdx++) {
            int elem = -1;
            if (pointer1 == nums1.length) {
                elem = nums2[pointer2++];
            } else if (pointer2 == nums2.length) {
                elem = nums1[pointer1++];
            } else {
                if (nums1[pointer1] <= nums2[pointer2]) {
                    elem = nums1[pointer1++];
                } else {
                    elem = nums2[pointer2++];
                }
            }

            if (elem == -1 || (nums1.length == pointer1 && nums2.length == pointer2)) {
                break;
            }

            last = current;
            current = elem;
        }

        return totalLength % 2 == 0 ? (last + current)/2.0 : current;
    }

    public static void main(String[] args) {
        var sol = new MedianOfTwoSortedArrays();
        System.out.println(sol.findMedianSortedArrays(
                new int[] {1, 3, 5}, new int[] {2, 4, 6}
        ));

        System.out.println(sol.findMedianSortedArrays(
                new int[] {1, 1, 1}, new int[] {2, 2, 2}
        ));

        System.out.println(sol.findMedianSortedArrays(
                new int[] {2, 3, 5, 8}, new int[] {1, 4, 6, 7, 9}
        ));

    }
}
