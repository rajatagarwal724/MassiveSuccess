package coding.BinarySearch;

import java.util.Arrays;

public class NumberRange {

    public int[] findRange(int[] arr, int key) {
        int[] result = new int[] { -1, -1 };
        result[0] = search(arr, key, false);
        if (result[0] != -1) {
            result[1] = search(arr, key, true);
        }
        return result;
    }

    private int search(int[] arr, int key, boolean findMaxIndex) {
        int keyIndex = -1;
        int left = 0, right = arr.length - 1;
        while (left <= right) {
            int mid = left + (right - left)/2;
            if (arr[mid] < key) {
                left = mid + 1;
            } else if (key < arr[mid]) {
                right = mid - 1;
            } else {
                keyIndex = mid;
                if (findMaxIndex) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }
        return keyIndex;
    }


    public static void main(String[] args) {
        var sol = new NumberRange();
        System.out.println(Arrays.toString(sol.findRange(new int[]{4, 6, 6, 6, 9}, 6)));
        System.out.println(Arrays.toString(sol.findRange(new int[]{1, 3, 8, 10, 15}, 10)));
        System.out.println(Arrays.toString(sol.findRange(new int[]{1, 3, 8, 10, 15}, 12)));
    }
}
