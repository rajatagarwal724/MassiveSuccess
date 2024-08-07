package coding.BinarySearch;

public class MinimumDifferenceElement {

    public static int searchMinDiffElement(int[] arr, int key) {

        if (key < arr[0]) {
            return arr[0];
        }
        if (key > arr[arr.length - 1]) {
            return arr[arr.length - 1];
        }

        int left = 0, right = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left)/2;

            if (key == arr[mid]) {
                return arr[mid];
            } else if (key < arr[mid]) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        if ((arr[left] - key) < (key - arr[right])) {
            return arr[left];
        }
        return arr[right];
    }

    public static void main(String[] args) {
        System.out.println(searchMinDiffElement(new int[] {4, 6, 10}, 7));
        System.out.println(searchMinDiffElement(new int[] {4, 6, 10}, 4));
        System.out.println(searchMinDiffElement(new int[] {1, 3, 8, 10, 15}, 12));
        System.out.println(searchMinDiffElement(new int[] {4, 6, 10}, 17));
    }
}
