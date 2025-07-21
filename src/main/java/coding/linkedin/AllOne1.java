package coding.linkedin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AllOne1 {

    static class Node {
        int count;
        Set<String> keys;
        Node prev;
        Node next;

        public Node(int count) {
            this.count = count;
            this.keys = new HashSet<>();
            this.prev = null;
            this.next = null;
        }
    }

    private final Map<String, Node> keyNodeMap;
    private final Node head;
    private final Node tail;

    public AllOne1() {
        this.keyNodeMap = new HashMap<>();
        head = new Node(Integer.MIN_VALUE);
        tail = new Node(Integer.MAX_VALUE);
        head.next = tail;
        tail.prev = head;
    }

    public void inc(String key) {
        if (!keyNodeMap.containsKey(key)) {
            if (head.next.count != 1) {
                var newNode = new Node(1);
                addNode(newNode, head);
            }
            head.next.keys.add(key);
            keyNodeMap.put(key, head.next);
        } else {
            var currentNode = keyNodeMap.get(key);

            int nextCount = currentNode.count + 1;

            if (currentNode.next.count != nextCount) {
                addNode(new Node(nextCount), currentNode);
            }

            currentNode.keys.remove(key);
            currentNode.next.keys.add(key);

            keyNodeMap.put(key, currentNode.next);

            if (currentNode.keys.isEmpty()) {
                removeNode(currentNode);
            }
        }
    }

    public void dec(String key) {
       if (!keyNodeMap.containsKey(key)) {
           return;
       }

       var currentNode = keyNodeMap.get(key);

       if (currentNode.count == 1) {
           currentNode.keys.remove(key);
           keyNodeMap.remove(key);
       } else {
           int prevCount = currentNode.count - 1;

           if (currentNode.prev.count != prevCount) {
               addNode(new Node(prevCount), currentNode.prev);
           }

           currentNode.keys.remove(key);
           currentNode.prev.keys.add(key);

           keyNodeMap.put(key, currentNode.prev);
       }

        if (currentNode.keys.isEmpty()) {
            removeNode(currentNode);
        }
    }

    public String getMaxKey() {
        if (tail.prev == head) return "";
        return tail.prev.keys.stream().findFirst().get();
    }

    public String getMinKey() {
        if (head.next == tail) return "";
        return head.next.keys.stream().findFirst().get();
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    public void addNode(Node currentNode, Node prevNode) {
        currentNode.next = prevNode.next;
        currentNode.prev = prevNode;
        prevNode.next.prev = currentNode;
        prevNode.next = currentNode;
    }

    public static void main(String[] args) {

    }
}
