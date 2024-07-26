package coding.topk.FrequencyStack;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Solution {
    private int sequenceNo;
    private Queue<Element> maxHeap = new PriorityQueue<>((e1, e2) -> {
        if (e1.frequency != e2.frequency) {
            return e2.frequency - e1.frequency;
        }
        return e2.sequenceNumber - e1.sequenceNumber;
    });

    private Map<Integer, Integer> freqMap = new HashMap<>();

    public void push(int num) {
        freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        maxHeap.offer(new Element(num, freqMap.getOrDefault(num, 0), sequenceNo++));
    }

    public int pop() {
        return maxHeap.poll().num;
    }
}

class Element {
    int num;
    int frequency;
    int sequenceNumber;

    public Element(int num, int frequency, int sequenceNumber) {
        this.num = num;
        this.frequency = frequency;
        this.sequenceNumber = sequenceNumber;
    }
}
