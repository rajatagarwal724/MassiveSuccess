package coding.top75;

public class MinWindowSort {

    public static int sort(int[] arr) {
        int low = 0, high = arr.length - 1;

        while (low < high && arr[low] <= arr[low + 1]) {
            low++;
        }

        if (low == high) {
            return 0;
        }

        while (low < high && arr[high] >= arr[high - 1]) {
            high--;
        }

        int minValue = arr[low], maxValue = arr[high];

        for (int index = low; index <= high; index++) {
            minValue = Math.min(minValue, arr[index]);
            maxValue = Math.max(maxValue, arr[index]);
        }

        for (int i = low - 1; i >= 0; i--) {
            if (arr[i] > minValue) {
                low = i;
            }
        }

        for (int i = high + 1; i < arr.length; i++) {
            if (arr[i] < maxValue) {
                high = i;
            }
        }

        return (high - low + 1);
    }

    public static void main(String[] args) {
        System.out.println(MinWindowSort.sort(new int[] {1, 2, 5, 3, 7, 10, 9, 12}));
        System.out.println(MinWindowSort.sort(new int[] {1, 3, 2, 0, -1, 7, 10}));
        System.out.println(MinWindowSort.sort(new int[] {1, 2, 3}));
        System.out.println(MinWindowSort.sort(new int[] {3, 2, 1}));
        System.out.println(MinWindowSort.sort(new int[] {10, 10, 10, 10, 10, 10, 10, 10, 10, 10}));
    }
}
