package companies.doordash.repeat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class FindKClosestElements {

    public List<Integer> findClosestElements(int[] arr, int k, int x) {
        Queue<Integer> maxHeap = new PriorityQueue<>(
                (num1, num2) -> Math.abs(x -num2) - Math.abs(x -num1)
        );

        for (int i = 0; i < k; i++) {
            maxHeap.offer(arr[i]);
        }

        for (int i = k; i < arr.length; i++) {
            var elem = arr[i];

            if (Math.abs(x - elem) < Math.abs(x - maxHeap.peek())) {
                maxHeap.poll();
                maxHeap.offer(elem);
            }
        }
        List<Integer> res = new ArrayList<>(maxHeap);
        Collections.sort(res);
        return res;
    }

    public List<Integer> findClosestElements_1(int[] arr, int k, int x) {
        int left = 0, right = arr.length - k;

        while (left < right) {
            int mid = left + (right - left)/2;

            if ((arr[mid + k] - x) < (x - arr[mid])) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        List<Integer> result = new ArrayList<>();
        for (int i = left; i < (left + k); i++) {
            result.add(arr[i]);
        }
        return result;
    }

    public static void main(String[] args) {
        var sol = new FindKClosestElements();
        System.out.println(
                sol.findClosestElements(new int[] {1,2,3,4,5}, 4, 3)
        );
        System.out.println(
                sol.findClosestElements(new int[] {1,1,2,3,4,5}, 4, -1)
        );
        System.out.println(
                sol.findClosestElements(new int[] {1,1,1,10,10,10}, 1, 9)
        );

        System.out.println(
                sol.findClosestElements_1(new int[] {1,2,3,4,5}, 4, 3)
        );
        System.out.println(
                sol.findClosestElements_1(new int[] {1,1,2,3,4,5}, 4, -1)
        );
        System.out.println(
                sol.findClosestElements_1(new int[] {1,1,1,10,10,10}, 1, 9)
        );
    }
}
