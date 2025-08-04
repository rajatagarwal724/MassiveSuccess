package machine.coding.lld;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Incremental Data Structure Implementation (Simplified for 90-min Interview)
 * 
 * Question 1: Basic Key-Value operations (Get, Set, Delete, Update)
 * Question 2: Add prefixSearch and containsSearch (using simple string methods)
 * Question 3: Add TTL (Time To Live) support with timestamp-based operations
 * Question 4: Add simple undo and redo functionality (realistic for 90-min interview)
 */
public class IncrementalDataStructureSimplified {
    
    // Core data storage
    private final Map<String, String> data;
    
    // TTL storage: key -> expiration timestamp
    private final Map<String, Long> ttlMap;
    
    // Simple undo/redo using state snapshots (realistic for interview)
    private final Stack<DataSnapshot> undoStack;
    private final Stack<DataSnapshot> redoStack;
    private final int maxHistorySize;
    
    public IncrementalDataStructureSimplified() {
        this(10); // Smaller default for interview scenario
    }
    
    public IncrementalDataStructureSimplified(int maxHistorySize) {
        this.data = new ConcurrentHashMap<>();
        this.ttlMap = new ConcurrentHashMap<>();
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
        this.maxHistorySize = maxHistorySize;
    }
    
    // ==================== QUESTION 4: Simple Undo/Redo (Interview-Friendly) ====================
    
    /**
     * Simple data snapshot for undo/redo
     */
    private static class DataSnapshot {
        private final Map<String, String> dataState;
        private final Map<String, Long> ttlState;
        private final String operation;
        
        public DataSnapshot(Map<String, String> data, Map<String, Long> ttlMap, String operation) {
            this.dataState = new HashMap<>(data);
            this.ttlState = new HashMap<>(ttlMap);
            this.operation = operation;
        }
        
        public Map<String, String> getDataState() { return dataState; }
        public Map<String, Long> getTtlState() { return ttlState; }
        public String getOperation() { return operation; }
    }
    
    /**
     * Save current state before making changes
     */
    private void saveState(String operation) {
        DataSnapshot snapshot = new DataSnapshot(data, ttlMap, operation);
        undoStack.push(snapshot);
        redoStack.clear(); // Clear redo stack when new operation is performed
        
        // Limit history size
        if (undoStack.size() > maxHistorySize) {
            undoStack.remove(0);
        }
    }
    
    /**
     * Undo the last operation
     */
    public boolean undo() {
        if (undoStack.isEmpty()) {
            return false;
        }
        
        // Save current state to redo stack
        DataSnapshot currentState = new DataSnapshot(data, ttlMap, "current");
        redoStack.push(currentState);
        
        // Restore previous state
        DataSnapshot previousState = undoStack.pop();
        restoreState(previousState);
        
        return true;
    }
    
    /**
     * Redo the last undone operation
     */
    public boolean redo() {
        if (redoStack.isEmpty()) {
            return false;
        }
        
        // Save current state to undo stack
        DataSnapshot currentState = new DataSnapshot(data, ttlMap, "redo");
        undoStack.push(currentState);
        
        // Restore redo state
        DataSnapshot redoState = redoStack.pop();
        restoreState(redoState);
        
        return true;
    }
    
    /**
     * Restore state from snapshot
     */
    private void restoreState(DataSnapshot snapshot) {
        // Clear current data
        data.clear();
        ttlMap.clear();
        
        // Restore data
        data.putAll(snapshot.getDataState());
        ttlMap.putAll(snapshot.getTtlState());
    }
    
    /**
     * Check if undo is available
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    /**
     * Check if redo is available
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
    
    /**
     * Get description of last operation (for debugging)
     */
    public String getLastOperation() {
        return undoStack.isEmpty() ? "No operations" : undoStack.peek().getOperation();
    }
    
    /**
     * Clear undo/redo history
     */
    public void clearHistory() {
        undoStack.clear();
        redoStack.clear();
    }
    
    // ==================== QUESTION 1: Basic Operations (Enhanced with Undo/Redo) ====================
    
    /**
     * Set a key-value pair (with undo support)
     */
    public void set(String key, String value) {
        set(key, value, -1, System.currentTimeMillis());
    }
    
    /**
     * Set a key-value pair with TTL (with undo support)
     */
    public void set(String key, String value, long ttlSeconds, long currentTimestamp) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key and value cannot be null");
        }
        
        saveState("set " + key + " = " + value);
        
        data.put(key, value);
        
        if (ttlSeconds > 0) {
            ttlMap.put(key, currentTimestamp + (ttlSeconds * 1000));
        } else {
            ttlMap.remove(key);
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
     * Update value for an existing key (with undo support)
     */
    public boolean update(String key, String newValue) {
        return update(key, newValue, System.currentTimeMillis());
    }
    
    /**
     * Update value for an existing key at a specific timestamp (with undo support)
     */
    public boolean update(String key, String newValue, long currentTimestamp) {
        if (key == null || newValue == null) {
            return false;
        }
        
        // Check if key exists and is not expired
        if (!data.containsKey(key) || isExpired(key, currentTimestamp)) {
            return false;
        }
        
        saveState("update " + key + " = " + newValue);
        data.put(key, newValue);
        return true;
    }
    
    /**
     * Delete a key (with undo support)
     */
    public boolean delete(String key) {
        return delete(key, System.currentTimeMillis());
    }
    
    /**
     * Delete a key at a specific timestamp (with undo support)
     */
    public boolean delete(String key, long currentTimestamp) {
        if (key == null) {
            return false;
        }
        
        // Check if key exists and is not expired
        if (!data.containsKey(key) || isExpired(key, currentTimestamp)) {
            return false;
        }
        
        saveState("delete " + key);
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
    
    /**
     * Get the state of the system at a specific timestamp
     */
    public Map<String, String> getSystemState(long timestamp) {
        Map<String, String> state = new HashMap<>();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String key = entry.getKey();
            if (!isExpired(key, timestamp)) {
                state.put(key, entry.getValue());
            }
        }
        return state;
    }
    
    /**
     * Clean up expired keys
     */
    public int cleanupExpiredKeys(long currentTimestamp) {
        List<String> expiredKeys = new ArrayList<>();
        
        for (String key : data.keySet()) {
            if (isExpired(key, currentTimestamp)) {
                expiredKeys.add(key);
            }
        }
        
        for (String key : expiredKeys) {
            data.remove(key);
            ttlMap.remove(key);
        }
        
        return expiredKeys.size();
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
        IncrementalDataStructureSimplified ds = new IncrementalDataStructureSimplified();
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
        
        System.out.println("\n=== Question 4: Simple Undo/Redo Operations ===");
        
        // Create a fresh instance for undo/redo demo
        IncrementalDataStructureSimplified undoDs = new IncrementalDataStructureSimplified();
        
        // Perform some operations
        undoDs.set("key1", "value1");
        undoDs.set("key2", "value2");
        undoDs.update("key1", "updated_value1");
        undoDs.delete("key2");
        
        System.out.println("After operations:");
        System.out.println("key1: " + undoDs.get("key1")); // updated_value1
        System.out.println("key2: " + undoDs.get("key2")); // null
        System.out.println("Can undo: " + undoDs.canUndo());
        System.out.println("Can redo: " + undoDs.canRedo());
        
        // Test undo
        System.out.println("\nTesting Undo:");
        undoDs.undo(); // Undo delete key2
        System.out.println("After undo delete: key1=" + undoDs.get("key1") + ", key2=" + undoDs.get("key2"));
        
        undoDs.undo(); // Undo update key1
        System.out.println("After undo update: key1=" + undoDs.get("key1") + ", key2=" + undoDs.get("key2"));
        
        // Test redo
        System.out.println("\nTesting Redo:");
        undoDs.redo(); // Redo update key1
        System.out.println("After redo update: key1=" + undoDs.get("key1") + ", key2=" + undoDs.get("key2"));
        
        undoDs.redo(); // Redo delete key2
        System.out.println("After redo delete: key1=" + undoDs.get("key1") + ", key2=" + undoDs.get("key2"));
        
        // Test new operation clears redo
        undoDs.undo(); // Go back one step
        undoDs.set("key3", "value3"); // This should clear redo stack
        System.out.println("After new operation, can redo: " + undoDs.canRedo()); // false
        
        System.out.println("Final state: key1=" + undoDs.get("key1") + 
                         ", key2=" + undoDs.get("key2") + 
                         ", key3=" + undoDs.get("key3"));
    }
}
