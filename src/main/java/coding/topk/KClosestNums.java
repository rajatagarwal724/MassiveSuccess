package coding.topk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class KClosestNums {

    public List<Integer> findClosestElements(int[] arr, int K, int X) {
        Queue<Integer> maxHeap = new PriorityQueue<>((num1, num2) -> (Math.abs(num2 - X) - Math.abs(num1 - X)));
        for (int i = 0; i < arr.length; i++) {
            if (i < K) {
                maxHeap.offer(arr[i]);
            } else {
                if (Math.abs(arr[i] - X) < Math.abs(maxHeap.peek() - X)) {
                    maxHeap.poll();
                    maxHeap.offer(arr[i]);
                }
            }
        }

        List<Integer> result = new ArrayList<>(maxHeap);
        Collections.sort(result);
        return result;
    }

    public static void main(String[] args) {
        var sol = new KClosestNums();

        sol.findClosestElements(new int[] {5, 6, 7, 8, 9}, 3, 7).forEach(System.out::println);
        System.out.println("##################");
        sol.findClosestElements(new int[] {2, 4, 5, 6, 9}, 3, 6).forEach(System.out::println);
        System.out.println("##################");
        sol.findClosestElements(new int[] {2, 4, 5, 6, 9}, 3, 10).forEach(System.out::println);
    }
}
