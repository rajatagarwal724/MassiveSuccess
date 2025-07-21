package coding.linkedin;

import java.util.PriorityQueue;
import java.util.Queue;

public class KthSmallestNumber {

    public int findKthSmallestNumber(int[] nums, int k) {
        Queue<Integer> maxHeap = new PriorityQueue<>((o1, o2) -> o2 - o1);

        for (int i = 0; i < k; i++) {
            maxHeap.offer(nums[i]);
        }

        for (int i = k; i < nums.length; i++) {
            var elem = nums[i];

            if (elem < maxHeap.peek()) {
                maxHeap.poll();
                maxHeap.offer(elem);
            }
        }

        return maxHeap.peek();
    }

    public static void main(String[] args) {

    }
}
