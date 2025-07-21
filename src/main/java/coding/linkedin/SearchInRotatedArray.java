package coding.linkedin;

public class SearchInRotatedArray {
    public int search(int[] arr, int key) {
        int left = 0, right = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left)/2;

            var midElem = arr[mid];
            if (midElem == key) {
                return mid;
            }

            if (arr[left] <= arr[mid]) {
                if (arr[left] <= key && key< arr[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else {
                if (arr[mid] < key && key <= arr[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        var sol = new SearchInRotatedArray();

        System.out.println(
                sol.search(new int[] {10, 15, 1, 3, 8}, 15)
        );
        System.out.println(
                sol.search(new int[] {4, 5, 7, 9, 10, -1, 2}, 10)
        );
    }
}
