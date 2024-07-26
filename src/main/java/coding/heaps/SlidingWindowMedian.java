package coding.heaps;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class SlidingWindowMedian {
    private Queue<Integer> maxHeap = null;
    private Queue<Integer> minHeap = null;

    public SlidingWindowMedian() {
        maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
        minHeap = new PriorityQueue<>();
    }

    public double[] findSlidingWindowMedian(int[] nums, int k) {
        double[] result = new double[nums.length - k + 1];

        for (int index = 0; index < nums.length; index++) {
            var elem = nums[index];
            insertNum(elem);

            if (index >= (k - 1)) {
                int resultIndex = index + 1 - k;
                result[resultIndex] = findMedian();
                int elemToRemove = nums[resultIndex];
                removeWindowElem(elemToRemove);
                rebalance();
            }
        }
        return result;
    }

    private void removeWindowElem(int elemToRemove) {
        if (elemToRemove <= maxHeap.peek()) {
            maxHeap.remove(elemToRemove);
        } else {
            minHeap.remove(elemToRemove);
        }
    }

    public void insertNum(int num) {
        if (maxHeap.isEmpty() || maxHeap.peek() >= num) {
            maxHeap.offer(num);
        } else {
            minHeap.offer(num);
        }

        rebalance();
    }

    private void rebalance() {
        if (maxHeap.size() > (minHeap.size() + 1)) {
            minHeap.offer(maxHeap.poll());
        } else if (minHeap.size() > maxHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }

    public double findMedian() {
        if (maxHeap.size() == minHeap.size()) {
            return (maxHeap.peek() + minHeap.peek())/2.0;
        }
        if (maxHeap.size() > minHeap.size()) {
            return maxHeap.peek();
        } else {
            return minHeap.peek();
        }
    }

    public static void main(String[] args) {
        var slidingWindowMedian = new SlidingWindowMedian();

        Arrays.stream(slidingWindowMedian.findSlidingWindowMedian(new int[]{1, 2, 3, 4, 5}, 2)).forEach(System.out::println);
    }
}
