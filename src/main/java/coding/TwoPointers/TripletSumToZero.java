package coding.TwoPointers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TripletSumToZero {

    public static List<List<Integer>> searchTriplets(int[] arr) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(arr);

        for(int i = 0; i < arr.length - 2; i++) {
            if (i > 0 && arr[i] == arr[i - 1]) {
                continue;
            }
            int first = arr[i];
            int left = i + 1;
            int right = arr.length - 1;

            while (left < right) {
                int second = arr[left];
                int third = arr[right];

                int sum = first + second + third;

                if (sum == 0) {
                    result.add(List.of(first, second, third));
                    left++;
                    right--;

                    while (left < right && arr[left] == arr[left - 1]) {
                        left++;
                    }
                    while (left < right && arr[right] == arr[right + 1]) {
                        right--;
                    }
                } else if (sum < 0) {
                    left++;
                } else {
                    right--;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
//        searchTriplets(new int[] {-1,0,1,2,-1,-4}).stream().forEach(System.out::println);

//        searchTriplets(new int[] {-5, 2, -1, -2, 3}).forEach(System.out::println);

        System.out.println("@#$%^&*()");

        searchTriplets(new int[]{-3, 0, 1, 2, -1, 1, -2}).forEach(System.out::println);

        System.out.println("@#$%^&*()");

        searchTriplets(new int[]{-5, 2, -1, -2, 3}).forEach(System.out::println);

    }
}
