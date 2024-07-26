package coding.topk;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;

public class ConnectRopes {

    public int minimumCostToConnectRopes(int[] ropeLengths) {
        Queue<Integer> minHeap = Arrays
                .stream(ropeLengths)
                .boxed()
                .collect(Collectors.toCollection(PriorityQueue::new));

        int result = 0;

        while (minHeap.size() > 1) {
            int temp = minHeap.poll() + minHeap.poll();
            result += temp;
            minHeap.offer(temp);
        }

        return result;
    }

    public static void main(String[] args) {
        var solution = new ConnectRopes();
        System.out.println(solution.minimumCostToConnectRopes(new int[] {1, 3, 11, 5}));
        System.out.println(solution.minimumCostToConnectRopes(new int[] {3, 4, 5, 6}));
        System.out.println(solution.minimumCostToConnectRopes(new int[] {1, 3, 11, 5, 2}));
    }
}
