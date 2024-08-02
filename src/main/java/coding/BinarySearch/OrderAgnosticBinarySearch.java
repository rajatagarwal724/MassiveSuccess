package coding.BinarySearch;

public class OrderAgnosticBinarySearch {

    public int search(int[] arr, int key) {

        int left = 0, right = arr.length - 1;

        boolean isAscending = arr[left] <= arr[right];

        while (left <= right) {
            int mid = left + (right - left)/2;

            if (arr[mid] == key) {
                return mid;
            }
            if (isAscending) {
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

        return -1; // element not found
    }

    public static void main(String[] args) {
        var sol = new OrderAgnosticBinarySearch();
        System.out.println(sol.search(new int[] {4, 6, 10}, 10));
        System.out.println(sol.search(new int[] {1, 2, 3, 4, 5, 6, 7}, 5));
        System.out.println(sol.search(new int[] {10, 6, 4}, 10));
        System.out.println(sol.search(new int[] {10, 6, 4}, 4));

    }
}
