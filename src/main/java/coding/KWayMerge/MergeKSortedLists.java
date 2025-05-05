package coding.KWayMerge;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class MergeKSortedLists {
    static class ListNode {
        int val;
        ListNode next;

        ListNode(int value) {
            this.val = value;
        }
    }

    public ListNode merge(ListNode[] lists) {
        ListNode resultHead = null;
        ListNode resultNode = null;
        Queue<ListNode> minHeap = new PriorityQueue<>(Comparator.comparingInt(o -> o.val));

        for (ListNode node: lists) {
            if (null != node) {
                minHeap.offer(node);
            }
        }

        while (!minHeap.isEmpty()) {
            var node = minHeap.poll();
            if (null == resultHead) {
                resultNode = node;
                resultHead = resultNode;
            } else {
                resultNode.next = node;
                resultNode = resultNode.next;
            }
            if (node.next != null) {
                minHeap.offer(node.next);
            }
        }

        return resultHead;
    }

    public static void main(String[] args) {
        var sol = new MergeKSortedLists();
        ListNode l1 = new ListNode(2);
        l1.next = new ListNode(6);
        l1.next.next = new ListNode(8);

        ListNode l2 = new ListNode(3);
        l2.next = new ListNode(6);
        l2.next.next = new ListNode(7);

        ListNode l3 = new ListNode(1);
        l3.next = new ListNode(3);
        l3.next.next = new ListNode(4);

        ListNode result = sol.merge(new ListNode[] { l1, l2, l3 });
        System.out.print("Here are the elements form the merged list: ");
        while (result != null) {
            System.out.print(result.val + " ");
            result = result.next;
        }
    }
}
