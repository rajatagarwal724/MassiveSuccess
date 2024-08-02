package coding.BinarySearch;

public class CeilingOfANumber {

    public static int searchCeilingOfANumber(int[] arr, int key) {
        if (key <= arr[0]) {
            return 0;
        }
        if (key == arr[arr.length - 1]) {
            return arr.length - 1;
        }
        if (arr[arr.length - 1] < key) {
            return -1;
        }

        int left = 0, right = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left)/2;

            if (arr[mid] == key) {
                return mid;
            }

            if (arr[mid] < key) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return left;
    }

    public static void main(String[] args) {

    }
}
