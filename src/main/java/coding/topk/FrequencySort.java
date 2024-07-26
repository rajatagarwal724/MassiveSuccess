package coding.topk;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class FrequencySort {

    public String sortCharacterByFrequency(String str) {
        StringBuilder result = new StringBuilder();

        Map<Character, Integer> charFreqMap = new HashMap<>();
        for (char elem: str.toCharArray()) {
            charFreqMap.put(elem, charFreqMap.getOrDefault(elem, 0) + 1);
        }
        Queue<Map.Entry<Character, Integer>> maxHeap = new PriorityQueue<>(
                (e1, e2) -> e2.getValue() - e1.getValue()
        );
        maxHeap.addAll(charFreqMap.entrySet());

        for (Map.Entry<Character, Integer> entry: maxHeap) {
            result.append(String.valueOf(entry.getKey()).repeat(Math.max(0, entry.getValue())));
        }

        return result.toString();
    }

    public static void main(String[] args) {
        var solution = new FrequencySort();
//        System.out.println(solution.sortCharacterByFrequency("Programming"));
//        System.out.println(solution.sortCharacterByFrequency("abcbab"));
        System.out.println(solution.sortCharacterByFrequency("aabba"));
    }
}
