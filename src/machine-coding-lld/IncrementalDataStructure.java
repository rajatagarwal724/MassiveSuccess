package machine.coding.lld;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Incremental Data Structure Implementation
 * 
 * Question 1: Basic Key-Value operations (Get, Set, Delete, Update)
 * Question 2: Add prefixSearch and containsSearch (using simple string methods)
 * Question 3: Add TTL (Time To Live) support with timestamp-based operations
 * Question 4: Add simple undo and redo functionality (realistic for 90-min interview)
 */
public class IncrementalDataStructure {
    
    // Core data storage
    private final Map<String, String> data;
    
    // TTL storage: key -> expiration timestamp
    private final Map<String, Long> ttlMap;
    
    // Simple undo/redo using state snapshots (realistic for interview)
    private final Stack<DataSnapshot> undoStack;
    private final Stack<DataSnapshot> redoStack;
    private final int maxHistorySize;
    
    public IncrementalDataStructure() {
        this(10); // Smaller default for interview scenario
    }
    
    public IncrementalDataStructure(int maxHistorySize) {
        this.data = new ConcurrentHashMap<>();
        this.ttlMap = new ConcurrentHashMap<>();
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
        this.maxHistorySize = maxHistorySize;
    }
    
    /**
     * Set a key-value pair
     */
    public void set(String key, String value) {
        set(key, value, -1, System.currentTimeMillis());
    }
    
    /**
     * Set a key-value pair with TTL
     */
    public void set(String key, String value, long ttlSeconds, long currentTimestamp) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key and value cannot be null");
        }
        
        data.put(key, value);
        
        // Handle TTL
        if (ttlSeconds > 0) {
            ttlMap.put(key, currentTimestamp + (ttlSeconds * 1000));
        } else {
            ttlMap.remove(key); // Remove any existing TTL
        }
    }
    
    /**
     * Get value for a key
     */
    public String get(String key) {
        return get(key, System.currentTimeMillis());
    }
    
    /**
     * Get value for a key at a specific timestamp
     */
    public String get(String key, long currentTimestamp) {
        if (key == null) {
            return null;
        }
        
        // Check if key exists and is not expired
        if (!data.containsKey(key) || isExpired(key, currentTimestamp)) {
            return null;
        }
        
        return data.get(key);
    }
    
    /**
     * Update value for an existing key
     */
    public boolean update(String key, String newValue) {
        return update(key, newValue, System.currentTimeMillis());
    }
    
    /**
     * Update value for an existing key at a specific timestamp
     */
    public boolean update(String key, String newValue, long currentTimestamp) {
        if (key == null || newValue == null) {
            return false;
        }
        
        // Check if key exists and is not expired
        if (!data.containsKey(key) || isExpired(key, currentTimestamp)) {
            return false;
        }
        
        data.put(key, newValue);
        return true;
    }
    
    /**
     * Delete a key
     */
    public boolean delete(String key) {
        return delete(key, System.currentTimeMillis());
    }
    
    /**
     * Delete a key at a specific timestamp
     */
    public boolean delete(String key, long currentTimestamp) {
        if (key == null) {
            return false;
        }
        
        // Check if key exists and is not expired
        if (!data.containsKey(key) || isExpired(key, currentTimestamp)) {
            return false;
        }
        
        data.remove(key);
        ttlMap.remove(key);
        return true;
    }
    
    // ==================== QUESTION 2: Search Operations (Simple String Methods) ====================
    
    /**
     * Find all keys that start with the given prefix
     */
    public List<String> prefixSearch(String prefix) {
        return prefixSearch(prefix, System.currentTimeMillis());
    }
    
    /**
     * Find all keys that start with the given prefix at a specific timestamp
     */
    public List<String> prefixSearch(String prefix, long currentTimestamp) {
        if (prefix == null) {
            return new ArrayList<>();
        }
        
        List<String> result = new ArrayList<>();
        
        // Simple string-based search
        for (String key : data.keySet()) {
            if (!isExpired(key, currentTimestamp) && key.startsWith(prefix)) {
                result.add(key);
            }
        }
        
        return result;
    }
    
    /**
     * Check if any key contains the given substring
     */
    public boolean containsSearch(String substring) {
        return containsSearch(substring, System.currentTimeMillis());
    }
    
    /**
     * Check if any key contains the given substring at a specific timestamp
     */
    public boolean containsSearch(String substring, long currentTimestamp) {
        if (substring == null) {
            return false;
        }
        
        for (String key : data.keySet()) {
            if (!isExpired(key, currentTimestamp) && key.contains(substring)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get all keys that contain the given substring
     */
    public List<String> getAllContaining(String substring) {
        return getAllContaining(substring, System.currentTimeMillis());
    }
    
    /**
     * Get all keys that contain the given substring at a specific timestamp
     */
    public List<String> getAllContaining(String substring, long currentTimestamp) {
        if (substring == null) {
            return new ArrayList<>();
        }
        
        List<String> result = new ArrayList<>();
        for (String key : data.keySet()) {
            if (!isExpired(key, currentTimestamp) && key.contains(substring)) {
                result.add(key);
            }
        }
        return result;
    }
    
    // ==================== QUESTION 3: TTL Support ====================
    
    /**
     * Check if a key is expired
     */
    private boolean isExpired(String key, long currentTimestamp) {
        if (!ttlMap.containsKey(key)) {
            return false; // No TTL set, never expires
        }
        
        long expirationTime = ttlMap.get(key);
        return currentTimestamp >= expirationTime;
    }
    
    ds.set("user3", "David");
    ds.set("userProfile", "Profile");
    ds.set("customer1", "Customer1");
    
    // Test prefix search
    List<String> userKeys = ds.prefixSearch("user");
    System.out.println("Keys with prefix 'user': " + userKeys); // [user1, user3, userProfile]
            data.remove(key);
            ttlMap.remove(key);
            removeFromTrie(key);
        }
        
        return expiredKeys.size();
    }
    
    // ==================== Trie Implementation for Prefix Search ====================
    
    private static class TrieNode {
        Map<Character, TrieNode> children;
        Set<String> keys; // Store complete keys that pass through this node
        
        TrieNode() {
            children = new HashMap<>();
            keys = new HashSet<>();
        }
    }
    
    private void addToTrie(String key) {
        TrieNode current = root;
        for (char c : key.toCharArray()) {
            current.keys.add(key);
            current = current.children.computeIfAbsent(c, k -> new TrieNode());
        }
        current.keys.add(key);
    }
    
    private void removeFromTrie(String key) {
        removeFromTrieHelper(root, key, 0);
    }
    
    private boolean removeFromTrieHelper(TrieNode node, String key, int index) {
        if (index == key.length()) {
            node.keys.remove(key);
            return node.keys.isEmpty() && node.children.isEmpty();
        }
        
        char c = key.charAt(index);
        TrieNode child = node.children.get(c);
        if (child == null) {
            return false;
        }
        
        node.keys.remove(key);
        boolean shouldDeleteChild = removeFromTrieHelper(child, key, index + 1);
        
        if (shouldDeleteChild) {
            node.children.remove(c);
        }
        
        return node.keys.isEmpty() && node.children.isEmpty();
    }
    
    private List<String> searchPrefix(String prefix) {
        TrieNode current = root;
        for (char c : prefix.toCharArray()) {
            current = current.children.get(c);
            if (current == null) {
                return new ArrayList<>();
            }
        }
        return new ArrayList<>(current.keys);
    }
    
    // ==================== Utility Methods ====================
    
    /**
     * Get all active keys (non-expired)
     */
    public Set<String> getAllKeys() {
        return getAllKeys(System.currentTimeMillis());
    }
    
    /**
     * Get all active keys at a specific timestamp
     */
    public Set<String> getAllKeys(long currentTimestamp) {
        Set<String> activeKeys = new HashSet<>();
        for (String key : data.keySet()) {
            if (!isExpired(key, currentTimestamp)) {
                activeKeys.add(key);
            }
        }
        return activeKeys;
    }
    
    /**
     * Get the size of active data
     */
    public int size() {
        return size(System.currentTimeMillis());
    }
    
    /**
     * Get the size of active data at a specific timestamp
     */
    public int size(long currentTimestamp) {
        return getAllKeys(currentTimestamp).size();
    }
    
    /**
     * Check if the data structure is empty
     */
    public boolean isEmpty() {
        return isEmpty(System.currentTimeMillis());
    }
    
    /**
     * Check if the data structure is empty at a specific timestamp
     */
    public boolean isEmpty(long currentTimestamp) {
        return size(currentTimestamp) == 0;
    }
    
    // ==================== Demo and Test Methods ====================
    
    public static void main(String[] args) {
        IncrementalDataStructure ds = new IncrementalDataStructure();
        long currentTime = System.currentTimeMillis();
        
        System.out.println("=== Question 1: Basic Operations ===");
        
        // Test basic operations
        ds.set("user1", "Alice");
        ds.set("user2", "Bob");
        ds.set("admin", "Charlie");
        
        System.out.println("Get user1: " + ds.get("user1")); // Alice
        System.out.println("Get user2: " + ds.get("user2")); // Bob
        System.out.println("Size: " + ds.size()); // 3
        
        // Test update
        System.out.println("Update user1: " + ds.update("user1", "Alice Updated")); // true
        System.out.println("Get user1: " + ds.get("user1")); // Alice Updated
        
        // Test delete
        System.out.println("Delete user2: " + ds.delete("user2")); // true
        System.out.println("Get user2: " + ds.get("user2")); // null
        System.out.println("Size: " + ds.size()); // 2
        
        System.out.println("\n=== Question 2: Search Operations ===");
        
        ds.set("user3", "David");
        ds.set("userProfile", "Profile");
        ds.set("customer1", "Customer1");
        
        // Test prefix search
        List<String> userKeys = ds.prefixSearch("user");
        System.out.println("Keys with prefix 'user': " + userKeys); // [user1, user3, userProfile]
        
        // Test contains search
        System.out.println("Contains 'admin': " + ds.containsSearch("admin")); // true
        System.out.println("Contains 'xyz': " + ds.containsSearch("xyz")); // false
        
        List<String> containingUser = ds.getAllContaining("user");
        System.out.println("Keys containing 'user': " + containingUser);
        
        System.out.println("\n=== Question 3: TTL Operations ===");
        
        // Test TTL operations
        long testTime = currentTime;
        ds.set("tempKey1", "TempValue1", 5, testTime); // 5 seconds TTL
        ds.set("tempKey2", "TempValue2", 10, testTime); // 10 seconds TTL
        ds.set("permanentKey", "PermanentValue"); // No TTL
        
        System.out.println("At time " + testTime + ":");
        System.out.println("Get tempKey1: " + ds.get("tempKey1", testTime)); // TempValue1
        System.out.println("Get tempKey2: " + ds.get("tempKey2", testTime)); // TempValue2
        System.out.println("Get permanentKey: " + ds.get("permanentKey", testTime)); // PermanentValue
        
        // Test after 6 seconds
        long futureTime = testTime + 6000;
        System.out.println("\nAt time " + futureTime + " (6 seconds later):");
        System.out.println("Get tempKey1: " + ds.get("tempKey1", futureTime)); // null (expired)
        System.out.println("Get tempKey2: " + ds.get("tempKey2", futureTime)); // TempValue2
        System.out.println("Get permanentKey: " + ds.get("permanentKey", futureTime)); // PermanentValue
        
        // Test after 12 seconds
        long laterTime = testTime + 12000;
        System.out.println("\nAt time " + laterTime + " (12 seconds later):");
        System.out.println("Get tempKey1: " + ds.get("tempKey1", laterTime)); // null (expired)
        System.out.println("Get tempKey2: " + ds.get("tempKey2", laterTime)); // null (expired)
        System.out.println("Get permanentKey: " + ds.get("permanentKey", laterTime)); // PermanentValue
        
        // Test system state
        System.out.println("\nSystem state at different times:");
        System.out.println("At " + testTime + ": " + ds.getSystemState(testTime).keySet());
        System.out.println("At " + futureTime + ": " + ds.getSystemState(futureTime).keySet());
        System.out.println("At " + laterTime + ": " + ds.getSystemState(laterTime).keySet());
        
        // Test prefix search with TTL
        ds.set("temp_user1", "TempUser1", 5, testTime);
        ds.set("temp_user2", "TempUser2", 15, testTime);
        
        System.out.println("\nPrefix search 'temp' at different times:");
        System.out.println("At " + testTime + ": " + ds.prefixSearch("temp", testTime));
        System.out.println("At " + futureTime + ": " + ds.prefixSearch("temp", futureTime));
        System.out.println("At " + laterTime + ": " + ds.prefixSearch("temp", laterTime));
    }
}
