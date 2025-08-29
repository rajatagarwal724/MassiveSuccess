package coding.top75.heaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;

public class SortCharsByFrequency {

    public String frequencySort(String str) {
        StringBuilder result = new StringBuilder();
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < str.length(); i++) {
            char elem = str.charAt(i);
            map.put(elem, map.getOrDefault(elem, 0) + 1);
        }

        Queue<Map.Entry<Character, Integer>> maxHeap = new PriorityQueue<>((e1, e2) -> {
            if (Objects.equals(e1.getValue(), e2.getValue())) {
                return e1.getKey().compareTo(e2.getKey());
            }
            return e2.getValue() - e1.getValue();
        });

        maxHeap.addAll(map.entrySet());
        while (!maxHeap.isEmpty()) {
            var elem = maxHeap.peek().getKey();
            var count = maxHeap.peek().getValue();
            maxHeap.poll();

            for (int i = 0; i < count; i++) {
                result.append(elem);
            }
        }

        return result.toString();
    }

    public String frequencyBucketSort(String str) {
        StringBuilder result = new StringBuilder();
        Map<Character, Integer> map = new HashMap<>();
        Integer maxFreq = Integer.MIN_VALUE;
        for (int i = 0; i < str.length(); i++) {
            char elem = str.charAt(i);
            map.put(elem, map.getOrDefault(elem, 0) + 1);
            maxFreq = Math.max(maxFreq, map.get(elem));
        }

        List<List<Character>> list = new ArrayList<>();
        for (int i = 0; i <= maxFreq; i++) {
            list.add(i, new ArrayList<>());
        }

        for (Map.Entry<Character, Integer> entry: map.entrySet()) {
            list.get(entry.getValue()).add(entry.getKey());
        }

        for (int i = list.size() - 1; i >= 1; i--) {
            if (!list.get(i).isEmpty()) {
                for (int j = 0; j < i; j++) {
                    result.append(list.get(i).get(0));
                }
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        var sol = new SortCharsByFrequency();
        System.out.println(sol.frequencyBucketSort("trersesess"));
    }
}
