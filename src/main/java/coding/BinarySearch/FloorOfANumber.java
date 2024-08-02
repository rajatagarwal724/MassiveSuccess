package coding.BinarySearch;

public class FloorOfANumber {

    public int searchFloorOfANumber(int[] arr, int key) {
        if (key < arr[0]) {
            return -1;
        }

        if (key > arr[arr.length - 1]) {
            return arr.length - 1;
        }

        int left = 0, right = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left)/2;

            if (arr[mid] == key) {
                return mid;
            } else if (arr[mid] < key) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return right;
    }

    public static void main(String[] args) {
        var sol = new FloorOfANumber();
        System.out.println(sol.searchFloorOfANumber(new int[] {4, 6, 10}, 6));
        System.out.println(sol.searchFloorOfANumber(new int[] {1, 3, 8, 10, 15}, 12));
        System.out.println(sol.searchFloorOfANumber(new int[] {4, 6, 10}, 17));
        System.out.println(sol.searchFloorOfANumber(new int[] {4, 6, 10}, -1));
    }
}
