package coding.BinarySearch;

public class SearchBitonicArray {

    public int search(int[] arr, int key) {
        int pivotIndex = findPivotIndex(arr);
        int keyIndex = binarySearch(arr, 0, pivotIndex, key, true);
        if (keyIndex == -1) {
            keyIndex = binarySearch(arr, pivotIndex + 1, arr.length - 1, key, false);
        }
        return keyIndex;
    }

    private int binarySearch(int[] arr, int left, int right, int key, boolean isIncreasing) {
        while (left <= right) {
            int mid = left + (right - left)/2;
            if (arr[mid] == key) {
                return mid;
            } else {
                if (isIncreasing) {
                    if (arr[mid] < key) {
                        left = mid + 1;
                    } else {
                        right = mid - 1;
                    }
                } else {
                    if (arr[mid] < key) {
                        right = mid - 1;
                    } else {
                        left = mid + 1;
                    }
                }
            }
        }
        return -1;
    }

    private int findPivotIndex(int[] arr) {
        int left = 0, right = arr.length - 1;
        while (left < right) {
            int mid = left + (right - left)/2;
            if (arr[mid] > arr[mid + 1]) {
                right = mid;
            } else if (arr[mid] < arr[mid + 1]) {
                left = mid + 1;
            }
        }
        return left;
    }

    public static void main(String[] args) {
        var sol = new SearchBitonicArray();

        System.out.println(sol.search(new int[] {1, 3, 8, 4, 3}, 4));
        System.out.println(sol.search(new int[] {3, 8, 3, 1}, 8));
        System.out.println(sol.search(new int[] {1, 3, 8, 12}, 12));
        System.out.println(sol.search(new int[] {10, 9, 8}, 10));
    }
}
