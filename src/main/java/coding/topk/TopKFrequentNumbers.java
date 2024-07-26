package coding.topk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class TopKFrequentNumbers {

    public List<Integer> findTopKFrequentNumbers(int[] nums, int k) {
        List<Integer> topNumbers = new ArrayList<>(k);

        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int num: nums) {
            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
        }

        Queue<Map.Entry<Integer, Integer>> minHeap = new PriorityQueue<>(
                (e1, e2) -> e1.getValue() - e2.getValue()
        );

        for (Map.Entry<Integer, Integer> entry: frequencyMap.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        minHeap.forEach(entry -> topNumbers.add(entry.getKey()));

        return topNumbers;
    }

    public static void main(String[] args) {
        var solution = new TopKFrequentNumbers();
        solution.findTopKFrequentNumbers(new int[] {1, 3, 5, 12, 11, 12, 11}, 2).forEach(System.out::println);
        System.out.println("##################");

        solution.findTopKFrequentNumbers(new int[] {5, 12, 11, 3, 11}, 2).forEach(System.out::println);
        System.out.println("##################");
    }
}
