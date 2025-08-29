package coding.top75.heaps;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class RearrangeString {

    public String rearrangeString(String str) {
        Map<Character, Integer> charFreqMap = new HashMap<>();
        for (int i = 0; i < str.length(); i++) {
            char elem = str.charAt(i);
            charFreqMap.put(elem, charFreqMap.getOrDefault(elem, 0) + 1);
        }
// O(n) + O(NLogN) as we are popping N times from the MaxHeap
        Queue<Map.Entry<Character, Integer>> maxHeap = new PriorityQueue<>(
                (e1, e2) -> e2.getValue() - e1.getValue()
        );
        maxHeap.addAll(charFreqMap.entrySet());
        Map.Entry<Character, Integer> prevEntry = null;
        StringBuilder res = new StringBuilder();
        while (!maxHeap.isEmpty()) {
            var entry = maxHeap.poll();
            res.append(entry.getKey());

            if (null != prevEntry && prevEntry.getValue() > 0) {
                maxHeap.offer(prevEntry);
            }
            entry.setValue(entry.getValue() - 1);
            prevEntry = entry;
        }

        return res.length() == str.length() ? res.toString() : "";
    }

    public static void main(String[] args) {
        var sol = new RearrangeString();
        System.out.println(sol.rearrangeString("aappp"));
        System.out.println(sol.rearrangeString("Programming"));
        System.out.println(sol.rearrangeString("aapa"));
    }
}
