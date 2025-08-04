package companies.roku;

import java.util.HashMap;
import java.util.Map;

public class LRUCache {

    class Node {
        int key;
        int value;
        Node prev;
        Node next;

        public Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
    private Map<Integer, Node> map;
    Node head, tail;
    int capacity;
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.head = new Node(Integer.MAX_VALUE, Integer.MAX_VALUE);
        this.tail = new Node(Integer.MIN_VALUE, Integer.MIN_VALUE);
        head.next = tail;
        tail.prev = head;
        this.map = new HashMap<>();
    }

    public int get(int key) {
        if (!map.containsKey(key)) {
            return -1;
        }
        Node node = map.get(key);
        removeNode(node);
        addNodeAfterHead(node);
        return node.value;
    }

    public void put(int key, int value) {
        Node node = map.get(key);
        if (null != node) {
            node.value = value;
            removeNode(node);
        } else {
            node = new Node(key, value);
        }
        addNodeAfterHead(node);
        map.put(key, node);
        if (map.size() > capacity) {
            var lastNode = tail.prev;
            removeNode(tail.prev);
            map.remove(lastNode.key);
        }
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        node.prev = null;
        node.next = null;
    }

    private void addNodeAfterHead(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    public static void main(String[] args) {
        var lruCache = new LRUCache(2);
        lruCache.put(1, 1);
        lruCache.put(2, 2);
        System.out.println(lruCache.get(1));
        lruCache.put(3, 3);
        System.out.println(lruCache.map);

        System.out.println(lruCache.get(2));

        lruCache.put(4, 4);
        System.out.println(lruCache.map);

        System.out.println(lruCache.get(1));

        System.out.println(lruCache.get(3));
        System.out.println(lruCache.map);
        System.out.println(lruCache.get(4));
        System.out.println(lruCache.map);
    }
}
