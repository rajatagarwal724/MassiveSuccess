package coding.BinarySearch;

public class BitonicArray {

    public int findMax(int[] arr) {
        int left = 0, right = arr.length - 1;

        while (left < right) {
            int mid = left + (right - left)/2;
            if (arr[mid] < arr[mid + 1]) {
                left = mid + 1;
            } else if (arr[mid] > arr[mid + 1]) {
                right = mid;
            }
        }
        return arr[left];
    }

    public static void main(String[] args) {

    }
}
