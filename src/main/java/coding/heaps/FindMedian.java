package coding.heaps;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class FindMedian {

    private Queue<Integer> maxHeap = null;
    private Queue<Integer> minHeap = null;

    public FindMedian() {
        maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
        minHeap = new PriorityQueue<>();

    }

    public void insertNum(int num) {
        if (maxHeap.isEmpty() || (maxHeap.peek() >= num)) {
            maxHeap.offer(num);
        } else {
            minHeap.offer(num);
        }

        if (Math.abs(maxHeap.size() - minHeap.size()) > 1) {
            if (minHeap.size() > maxHeap.size()) {
                maxHeap.offer(minHeap.poll());
            } else {
                minHeap.offer(maxHeap.poll());
            }
        }
    }

    public double findMedian() {
        if (maxHeap.size() == minHeap.size()) {
            int max = minHeap.peek();
            int min = maxHeap.peek();
            return (max + min) / 2.0;
        } else {
            if (maxHeap.size() > minHeap.size()) {
                return maxHeap.peek();
            } else {
                return minHeap.peek();
            }
        }
    }

    public static void main(String[] args) {
        var sol = new FindMedian();
        sol.insertNum(5);
        sol.insertNum(1);
        sol.insertNum(4);
        System.out.println(sol.findMedian());
        sol = new FindMedian();
        sol.insertNum(3);
        sol.insertNum(1);
        System.out.println(sol.findMedian());
    }

}
