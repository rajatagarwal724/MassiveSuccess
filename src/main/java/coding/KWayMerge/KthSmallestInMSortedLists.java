package coding.KWayMerge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class KthSmallestInMSortedLists {

    static class Node {
        int elementIndex;
        int inputIndex;

        public Node(int elementIndex, int inputIndex) {
            this.elementIndex = elementIndex;
            this.inputIndex = inputIndex;
        }
    }


    public int findKthSmallest(List<List<Integer>> lists, int k) {
        int result = 0, resultIndex = 0;

        Queue<Node> minHeap = new PriorityQueue<>(
                Comparator.comparingInt(o -> lists.get(o.inputIndex).get(o.elementIndex))
        );

        for (int i = 0; i < lists.size(); i++) {
            minHeap.offer(new Node(0, i));
        }

        while (!minHeap.isEmpty()) {
            Node poll = minHeap.poll();
            resultIndex++;
            if (resultIndex == k) {
                result = lists.get(poll.inputIndex).get(poll.elementIndex);
                return result;
            }
            if (poll.elementIndex < (lists.get(poll.inputIndex).size() - 1)) {
                int elementIndex = poll.elementIndex + 1;
                minHeap.offer(new Node(elementIndex, poll.inputIndex));
            }
        }

        return result;
    }

    public static void main(String[] args) {
        var sol = new KthSmallestInMSortedLists();
        List<Integer> l1 = Arrays.asList(2, 6, 8);
        List<Integer> l2 = Arrays.asList(3, 6, 7);
        List<Integer> l3 = Arrays.asList(1, 3, 4);
        List<List<Integer>> lists = new ArrayList<>();
        lists.add(l1);
        lists.add(l2);
        lists.add(l3);
//        int result = sol.findKthSmallest(lists, 5);
//        System.out.print("Kth smallest number is: " + result);

        l1 = Arrays.asList(5, 8, 9);
        l2 = Arrays.asList(1, 7);

        int result = sol.findKthSmallest(List.of(l1, l2), 3);
        System.out.print("Kth smallest number is: " + result);
    }
}
