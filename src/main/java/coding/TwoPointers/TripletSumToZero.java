package coding.TwoPointers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TripletSumToZero {

    public static List<List<Integer>> searchTriplets(int[] arr) {
        List<List<Integer>> triplets = new ArrayList<>();

        Arrays.sort(arr);

        for (int first = 0; first < arr.length - 2; first++) {
            if (first > 0 && arr[first] == arr[first - 1]) {
                continue;
            }
            int second = first + 1;
            int third = arr.length - 1;
            while (second < third) {
                int sum = arr[first] + arr[second] + arr[third];

                if (sum == 0) {
                    triplets.add(List.of(arr[first], arr[second], arr[third]));
                    second++;
                    third--;
                    while (second < third && arr[second] == arr[second - 1]) {
                        second++;
                    }
                    while (second < third && arr[third] == arr[third + 1]) {
                        third--;
                    }
                } else {
                    if (sum < 0) {
                        second++;
                    } else {
                        third--;
                    }
                }
            }
        }
        return triplets;
    }

    public static void main(String[] args) {
//        searchTriplets(new int[] {-1,0,1,2,-1,-4}).stream().forEach(System.out::println);

        searchTriplets(new int[] {-4,-2,-2,-2,0,1,2,2,2,3,3,4,4,6,6}).forEach(System.out::println);

    }
}
