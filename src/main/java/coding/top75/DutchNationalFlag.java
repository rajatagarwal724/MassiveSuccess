package coding.top75;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class DutchNationalFlag {

    public int[] sort(int[] arr) {

        int low = 0, high = arr.length - 1;
        int mid = 0;

        while (mid <= high) {
            if (arr[mid] == 0) {
                int temp = arr[mid];
                arr[mid] = arr[low];
                arr[low] = temp;
                low++;
                mid++;
            } else if (arr[mid] == 2) {
                int temp = arr[high];
                arr[high] = arr[mid];
                arr[mid] = temp;
                high--;
            } else {
                mid++;
            }
        }
        return arr;
    }

    public static void main(String[] args) {
        var sol = new DutchNationalFlag();
        System.out.println("Sorted array: " + Arrays.toString(sol.sort(new int[] {1, 0, 2, 1, 0})));
        System.out.println("Sorted array: " + Arrays.toString(sol.sort(new int[] {2, 2, 0, 1, 2, 0})));
    }

}
