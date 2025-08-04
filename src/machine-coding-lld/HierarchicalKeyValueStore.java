package machine.coding.lld;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hierarchical Key-Value Store with Two-Level Keys and Timestamp-based Versioning
 * 
 * Structure: key -> sub_key -> List<TimestampedValue>
 * 
 * Level 1: Basic operations (set, get latest, delete)
 * Level 2: Get all values for a key, get value at specific timestamp
 * Level 3: Advanced queries and range operations
 * Level 4: Cleanup, compaction, and performance optimizations
 */
public class HierarchicalKeyValueStore {
    
    // Main storage: key -> sub_key -> List of timestamped values
    private final Map<String, Map<String, List<TimestampedValue>>> storage;
    
    public HierarchicalKeyValueStore() {
        this.storage = new ConcurrentHashMap<>();
    }
    
    /**
     * Represents a value with its timestamp
     */
    public static class TimestampedValue {
        private final String value;
        private final long timestamp;
        
        public TimestampedValue(String value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
        
        public String getValue() { return value; }
        public long getTimestamp() { return timestamp; }
        
        @Override
        public String toString() {
            return String.format("TimestampedValue{value='%s', timestamp=%d}", value, timestamp);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TimestampedValue that = (TimestampedValue) obj;
            return timestamp == that.timestamp && Objects.equals(value, that.value);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(value, timestamp);
        }
    }
    
    // ==================== LEVEL 1: Basic Operations ====================
    
    /**
     * Set a value for key, sub_key with current timestamp
     */
    public void set(String key, String subKey, String value) {
        set(key, subKey, value, System.currentTimeMillis());
    }
    
    /**
     * Set a value for key, sub_key with specific timestamp
     */
    public void set(String key, String subKey, String value, long timestamp) {
        if (key == null || subKey == null || value == null) {
            throw new IllegalArgumentException("Key, subKey, and value cannot be null");
        }
        
        storage.computeIfAbsent(key, k -> new ConcurrentHashMap<>())
               .computeIfAbsent(subKey, sk -> new ArrayList<>())
               .add(new TimestampedValue(value, timestamp));
        
        // Keep the list sorted by timestamp for efficient queries
        List<TimestampedValue> values = storage.get(key).get(subKey);
        values.sort(Comparator.comparingLong(TimestampedValue::getTimestamp));
    }
    
    /**
     * Get the latest value for key, sub_key
     */
    public String getLatest(String key, String subKey) {
        if (key == null || subKey == null) {
            return null;
        }
        
        Map<String, List<TimestampedValue>> subKeys = storage.get(key);
        if (subKeys == null) {
            return null;
        }
        
        List<TimestampedValue> values = subKeys.get(subKey);
        if (values == null || values.isEmpty()) {
            return null;
        }
        
        // Return the latest value (last in sorted list)
        return values.get(values.size() - 1).getValue();
    }
    
    /**
     * Delete a specific sub_key under a key
     */
    public boolean deleteSubKey(String key, String subKey) {
        if (key == null || subKey == null) {
            return false;
        }
        
        Map<String, List<TimestampedValue>> subKeys = storage.get(key);
        if (subKeys == null) {
            return false;
        }
        
        List<TimestampedValue> removed = subKeys.remove(subKey);
        
        // If this was the last sub_key, remove the key entirely
        if (subKeys.isEmpty()) {
            storage.remove(key);
        }
        
        return removed != null;
    }
    
    /**
     * Delete an entire key and all its sub_keys
     */
    public boolean deleteKey(String key) {
        if (key == null) {
            return false;
        }
        
        Map<String, List<TimestampedValue>> removed = storage.remove(key);
        return removed != null;
    }
    
    // ==================== LEVEL 2: Advanced Queries ====================
    
    /**
     * Get all current values for a key (latest value for each sub_key)
     */
    public Map<String, String> getAllLatestValues(String key) {
        if (key == null) {
            return new HashMap<>();
        }
        
        Map<String, List<TimestampedValue>> subKeys = storage.get(key);
        if (subKeys == null) {
            return new HashMap<>();
        }
        
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, List<TimestampedValue>> entry : subKeys.entrySet()) {
            List<TimestampedValue> values = entry.getValue();
            if (!values.isEmpty()) {
                result.put(entry.getKey(), values.get(values.size() - 1).getValue());
            }
        }
        
        return result;
    }
    
    /**
     * Get value for key, sub_key at a specific timestamp
     * Returns the latest value that was set at or before the given timestamp
     */
    public String getValueAtTimestamp(String key, String subKey, long timestamp) {
        if (key == null || subKey == null) {
            return null;
        }
        
        Map<String, List<TimestampedValue>> subKeys = storage.get(key);
        if (subKeys == null) {
            return null;
        }
        
        List<TimestampedValue> values = subKeys.get(subKey);
        if (values == null || values.isEmpty()) {
            return null;
        }
        
        // Binary search for the latest value at or before the timestamp
        int index = binarySearchTimestamp(values, timestamp);
        return index >= 0 ? values.get(index).getValue() : null;
    }
    
    /**
     * Get all values for a key at a specific timestamp
     */
    public Map<String, String> getAllValuesAtTimestamp(String key, long timestamp) {
        if (key == null) {
            return new HashMap<>();
        }
        
        Map<String, List<TimestampedValue>> subKeys = storage.get(key);
        if (subKeys == null) {
            return new HashMap<>();
        }
        
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, List<TimestampedValue>> entry : subKeys.entrySet()) {
            String subKey = entry.getKey();
            String value = getValueAtTimestamp(key, subKey, timestamp);
            if (value != null) {
                result.put(subKey, value);
            }
        }
        
        return result;
    }
    
    // ==================== LEVEL 3: Range and History Operations ====================
    
    /**
     * Get all timestamped values for key, sub_key
     */
    public List<TimestampedValue> getHistory(String key, String subKey) {
        if (key == null || subKey == null) {
            return new ArrayList<>();
        }
        
        Map<String, List<TimestampedValue>> subKeys = storage.get(key);
        if (subKeys == null) {
            return new ArrayList<>();
        }
        
        List<TimestampedValue> values = subKeys.get(subKey);
        return values != null ? new ArrayList<>(values) : new ArrayList<>();
    }
    
    /**
     * Get values for key, sub_key within a timestamp range
     */
    public List<TimestampedValue> getValuesInRange(String key, String subKey, long startTimestamp, long endTimestamp) {
        List<TimestampedValue> history = getHistory(key, subKey);
        List<TimestampedValue> result = new ArrayList<>();
        
        for (TimestampedValue tv : history) {
            if (tv.getTimestamp() >= startTimestamp && tv.getTimestamp() <= endTimestamp) {
                result.add(tv);
            }
        }
        
        return result;
    }
    
    /**
     * Get all sub_keys for a given key
     */
    public Set<String> getSubKeys(String key) {
        if (key == null) {
            return new HashSet<>();
        }
        
        Map<String, List<TimestampedValue>> subKeys = storage.get(key);
        return subKeys != null ? new HashSet<>(subKeys.keySet()) : new HashSet<>();
    }
    
    /**
     * Get all keys in the store
     */
    public Set<String> getAllKeys() {
        return new HashSet<>(storage.keySet());
    }
    
    // ==================== LEVEL 4: Maintenance and Optimization ====================
    
    /**
     * Clean up old values, keeping only the latest N values for each sub_key
     */
    public int compactHistory(String key, String subKey, int keepLatest) {
        if (key == null || subKey == null || keepLatest <= 0) {
            return 0;
        }
        
        Map<String, List<TimestampedValue>> subKeys = storage.get(key);
        if (subKeys == null) {
            return 0;
        }
        
        List<TimestampedValue> values = subKeys.get(subKey);
        if (values == null || values.size() <= keepLatest) {
            return 0;
        }
        
        int originalSize = values.size();
        int removeCount = originalSize - keepLatest;
        
        // Remove oldest values, keep the latest ones
        values.subList(0, removeCount).clear();
        
        return removeCount;
    }
    
    /**
     * Clean up values older than a specific timestamp
     */
    public int cleanupOldValues(String key, String subKey, long cutoffTimestamp) {
        if (key == null || subKey == null) {
            return 0;
        }
        
        Map<String, List<TimestampedValue>> subKeys = storage.get(key);
        if (subKeys == null) {
            return 0;
        }
        
        List<TimestampedValue> values = subKeys.get(subKey);
        if (values == null) {
            return 0;
        }
        
        int originalSize = values.size();
        values.removeIf(tv -> tv.getTimestamp() < cutoffTimestamp);
        
        return originalSize - values.size();
    }
    
    /**
     * Get statistics about the store
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        int totalKeys = storage.size();
        int totalSubKeys = 0;
        int totalValues = 0;
        
        for (Map<String, List<TimestampedValue>> subKeys : storage.values()) {
            totalSubKeys += subKeys.size();
            for (List<TimestampedValue> values : subKeys.values()) {
                totalValues += values.size();
            }
        }
        
        stats.put("totalKeys", totalKeys);
        stats.put("totalSubKeys", totalSubKeys);
        stats.put("totalValues", totalValues);
        stats.put("averageSubKeysPerKey", totalKeys > 0 ? (double) totalSubKeys / totalKeys : 0.0);
        stats.put("averageValuesPerSubKey", totalSubKeys > 0 ? (double) totalValues / totalSubKeys : 0.0);
        
        return stats;
    }
    
    // ==================== Helper Methods ====================
    
    /**
     * Binary search to find the latest value at or before the given timestamp
     */
    private int binarySearchTimestamp(List<TimestampedValue> values, long timestamp) {
        int left = 0;
        int right = values.size() - 1;
        int result = -1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            long midTimestamp = values.get(mid).getTimestamp();
            
            if (midTimestamp <= timestamp) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return result;
    }
    
    /**
     * Check if a key exists
     */
    public boolean containsKey(String key) {
        return key != null && storage.containsKey(key);
    }
    
    /**
     * Check if a sub_key exists under a key
     */
    public boolean containsSubKey(String key, String subKey) {
        if (key == null || subKey == null) {
            return false;
        }
        
        Map<String, List<TimestampedValue>> subKeys = storage.get(key);
        return subKeys != null && subKeys.containsKey(subKey);
    }
    
    /**
     * Get the size (number of keys)
     */
    public int size() {
        return storage.size();
    }
    
    /**
     * Check if the store is empty
     */
    public boolean isEmpty() {
        return storage.isEmpty();
    }
    
    /**
     * Clear all data
     */
    public void clear() {
        storage.clear();
    }
    
    // ==================== Demo and Test Methods ====================
    
    public static void main(String[] args) {
        HierarchicalKeyValueStore store = new HierarchicalKeyValueStore();
        long baseTime = System.currentTimeMillis();
        
        System.out.println("=== Level 1: Basic Operations ===");
        
        // Set some values
        store.set("user1", "name", "Alice", baseTime);
        store.set("user1", "email", "alice@example.com", baseTime + 1000);
        store.set("user1", "name", "Alice Smith", baseTime + 2000); // Update name
        store.set("user2", "name", "Bob", baseTime + 1500);
        
        // Get latest values
        System.out.println("Latest name for user1: " + store.getLatest("user1", "name")); // Alice Smith
        System.out.println("Latest email for user1: " + store.getLatest("user1", "email")); // alice@example.com
        System.out.println("Latest name for user2: " + store.getLatest("user2", "name")); // Bob
        
        System.out.println("\n=== Level 2: Advanced Queries ===");
        
        // Get all latest values for a key
        Map<String, String> user1Values = store.getAllLatestValues("user1");
        System.out.println("All latest values for user1: " + user1Values);
        
        // Get value at specific timestamp
        String nameAtTime = store.getValueAtTimestamp("user1", "name", baseTime + 1500);
        System.out.println("user1 name at time " + (baseTime + 1500) + ": " + nameAtTime); // Alice
        
        // Get all values at specific timestamp
        Map<String, String> user1AtTime = store.getAllValuesAtTimestamp("user1", baseTime + 1500);
        System.out.println("All user1 values at time " + (baseTime + 1500) + ": " + user1AtTime);
        
        System.out.println("\n=== Level 3: History and Range Operations ===");
        
        // Get history
        List<TimestampedValue> nameHistory = store.getHistory("user1", "name");
        System.out.println("Name history for user1: " + nameHistory);
        
        // Get values in range
        List<TimestampedValue> valuesInRange = store.getValuesInRange("user1", "name", 
                                                                      baseTime, baseTime + 1500);
        System.out.println("user1 name values in range: " + valuesInRange);
        
        // Get sub_keys
        Set<String> user1SubKeys = store.getSubKeys("user1");
        System.out.println("Sub-keys for user1: " + user1SubKeys);
        
        System.out.println("\n=== Level 4: Maintenance Operations ===");
        
        // Add more history
        store.set("user1", "name", "Alice Johnson", baseTime + 3000);
        store.set("user1", "name", "Alice Brown", baseTime + 4000);
        
        System.out.println("Name history before compaction: " + store.getHistory("user1", "name").size() + " entries");
        
        // Compact history
        int removed = store.compactHistory("user1", "name", 2);
        System.out.println("Removed " + removed + " old entries");
        System.out.println("Name history after compaction: " + store.getHistory("user1", "name").size() + " entries");
        
        // Statistics
        Map<String, Object> stats = store.getStatistics();
        System.out.println("Store statistics: " + stats);
        
        System.out.println("\n=== Delete Operations ===");
        
        // Delete sub_key
        boolean deletedSubKey = store.deleteSubKey("user1", "email");
        System.out.println("Deleted user1 email: " + deletedSubKey);
        System.out.println("user1 sub-keys after deletion: " + store.getSubKeys("user1"));
        
        // Delete entire key
        boolean deletedKey = store.deleteKey("user2");
        System.out.println("Deleted user2: " + deletedKey);
        System.out.println("All keys after deletion: " + store.getAllKeys());
    }
}
