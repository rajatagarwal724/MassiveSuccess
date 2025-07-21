package coding.linkedin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Design a data structure to store the strings' count with the ability to return 
 * the strings with minimum and maximum counts in O(1) time.
 */
public class AllOne {
    
    // Node class to form a doubly-linked list of buckets
    private class Node {
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
    
    // HashMap to keep track of each key and its corresponding node
    private Map<String, Node> keyNodeMap;
    // Head and tail of the doubly-linked list
    private Node head; // dummy head with minimum count
    private Node tail; // dummy tail with maximum count
    
    /** Initializes the object of the data structure. */
    public AllOne() {
        keyNodeMap = new HashMap<>();
        head = new Node(Integer.MIN_VALUE); // Dummy head
        tail = new Node(Integer.MAX_VALUE); // Dummy tail
        
        // Connect head and tail
        head.next = tail;
        tail.prev = head;
    }
    
    /**
     * Increments the count of the string key by 1.
     * If key does not exist, insert it with count 1.
     */
    public void inc(String key) {
        // If the key doesn't exist in the map
        if (!keyNodeMap.containsKey(key)) {
            // Check if there's a node with count 1
            if (head.next.count != 1) {
                addNodeAfter(new Node(1), head);
            }
            head.next.keys.add(key);
            keyNodeMap.put(key, head.next);
        } else {
            // Get the current node for the key
            Node currentNode = keyNodeMap.get(key);
            int nextCount = currentNode.count + 1;
            
            // Check if there's a node with next count
            if (currentNode.next.count != nextCount) {
                addNodeAfter(new Node(nextCount), currentNode);
            }
            
            // Move the key to the next count node
            currentNode.keys.remove(key);
            currentNode.next.keys.add(key);
            keyNodeMap.put(key, currentNode.next);
            
            // Remove current node if empty
            if (currentNode.keys.isEmpty()) {
                removeNode(currentNode);
            }
        }
    }
    
    /**
     * Decrements the count of the string key by 1.
     * If the count becomes 0, remove it from the data structure.
     */
    public void dec(String key) {
        if (!keyNodeMap.containsKey(key)) {
            return; // Key doesn't exist, nothing to do
        }
        
        Node currentNode = keyNodeMap.get(key);
        
        // If the count will become 0, remove the key completely
        if (currentNode.count == 1) {
            currentNode.keys.remove(key);
            keyNodeMap.remove(key);
        } else {
            int prevCount = currentNode.count - 1;
            
            // Check if there's a node with previous count
            if (currentNode.prev.count != prevCount) {
                addNodeAfter(new Node(prevCount), currentNode.prev);
            }
            
            // Move the key to the previous count node
            currentNode.keys.remove(key);
            currentNode.prev.keys.add(key);
            keyNodeMap.put(key, currentNode.prev);
        }
        
        // Remove current node if empty
        if (currentNode.keys.isEmpty()) {
            removeNode(currentNode);
        }
    }
    
    /** Returns one of the keys with the maximum count. */
    public String getMaxKey() {
        // If the list is empty (only head and tail)
        if (head.next == tail) {
            return "";
        }
        
        // Return any key from the node right before tail (highest count)
        return tail.prev.keys.iterator().next();
    }
    
    /** Returns one of the keys with the minimum count. */
    public String getMinKey() {
        // If the list is empty (only head and tail)
        if (head.next == tail) {
            return "";
        }
        
        // Return any key from the node right after head (lowest count)
        return head.next.keys.iterator().next();
    }
    
    // Helper method to add a new node after a specified node
    private void addNodeAfter(Node newNode, Node prevNode) {
        newNode.prev = prevNode;
        newNode.next = prevNode.next;
        prevNode.next.prev = newNode;
        prevNode.next = newNode;
    }
    
    // Helper method to remove a node from the doubly-linked list
    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    public static void main(String[] args) {
        AllOne obj = new AllOne();
        obj.inc("hello");
        obj.inc("hello");
        System.out.println(obj.getMaxKey()); // "hello"
        System.out.println(obj.getMinKey()); // "hello"
        obj.inc("leet");
        System.out.println(obj.getMaxKey()); // "hello"
        System.out.println(obj.getMinKey()); // "leet"
    }
}
