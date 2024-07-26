package coding.topk;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class MaximumDistinctElements {

    public int findMaximumDistinctElements(int[] nums, int k) {
        int distinctElementsCount = 0;
        Map<Integer, Integer> freqMap = new HashMap<>();
        Queue<Map.Entry<Integer, Integer>> minHeap = new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));

        for (int num: nums) {
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        }

        for (Map.Entry<Integer, Integer> entry: freqMap.entrySet()) {
            if (entry.getValue() == 1) {
                distinctElementsCount++;
            } else {
                minHeap.offer(entry);
            }
        }

        while (!minHeap.isEmpty() && k > 0) {
            Map.Entry<Integer, Integer> pollEntry = minHeap.poll();
            k = k - (pollEntry.getValue() -1);
            if (k >= 0) {
                distinctElementsCount++;
            }
        }

        if (k > 0) {
            distinctElementsCount = distinctElementsCount - k;
        }
        return distinctElementsCount;
    }


    public static void main(String[] args) {
        var sol = new MaximumDistinctElements();

        System.out.println(sol.findMaximumDistinctElements(new int[] {1, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5}, 3));
    }
}
