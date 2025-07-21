package coding.linkedin;

public class SearchInSortedRotatedArrayWithDuplicates {

    public int search(int[] arr, int key) {
        int start = 0, end = arr.length - 1;

        while (start <= end) {
            int mid = start + (end - start)/2;

            if (arr[mid] == key) {
                return mid;
            }

            if (arr[start] == arr[mid] && arr[mid] == arr[end]) {
                ++start;
                --end;
            } else {
                if (arr[start] <= arr[mid]) {
                    if (arr[start] <= key && key < arr[mid]) {
                        end = mid - 1;
                    } else {
                        start = mid + 1;
                    }
                } else {
                    if (arr[mid] < key && key <= arr[end]) {
                        start = mid + 1;
                    } else {
                        end = mid - 1;
                    }
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        var sol = new SearchInSortedRotatedArrayWithDuplicates();

        System.out.println(
                sol.search(new int[] {3, 7, 3, 3, 3}, 7)
        );
    }
}


