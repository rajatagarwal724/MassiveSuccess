package coding.topk;

import java.util.PriorityQueue;
import java.util.Queue;

public class KthLargestNumberInAStream {
    Queue<Integer> minHeap = null;

    public KthLargestNumberInAStream(int[] nums, int k) {
        this.minHeap = new PriorityQueue<>();

        for (int i = 0; i < nums.length; i++) {
            if (i < k) {
                minHeap.offer(nums[i]);
            } else {
                if (nums[i] > minHeap.peek()) {
                    minHeap.poll();
                    minHeap.offer(nums[i]);
                }
            }
        }
    }

    public int add(int num) {
        if (num > minHeap.peek()) {
            minHeap.poll();
            minHeap.offer(num);
        }
        return minHeap.peek();
    }


    public static void main(String[] args) {
        var solution = new KthLargestNumberInAStream(new int[]{3, 1, 5, 12, 2, 11}, 4);
        System.out.println(solution.add(6));
        System.out.println(solution.add(13));
        System.out.println(solution.add(4));
    }
}
