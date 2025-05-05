package coding.BinarySearch;

public class SearchInRotatedArray {

    public int search(int[] arr, int key) {
        int left = 0, right = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left)/2;

            if (arr[mid] == key) {
                return mid;
            } else {
                if (arr[left] <= arr[mid]) {
                    if (key > arr[mid]) {
                        left = mid + 1;
                    } else {
                        right = mid - 1;
                    }
                } else {
                    if (key > arr[mid]) {
                        right = mid - 1;
                    } else {
                        left = mid + 1;
                    }
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        var solution = new SearchInRotatedArray();
        System.out.println(solution.search(new int[] {10, 15, 1, 3, 8}, 15));
        System.out.println(solution.search(new int[] {4, 5, 7, 9, 10, -1, 2}, 10));
    }
}
