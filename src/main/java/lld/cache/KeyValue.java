package lld.cache;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A Key-Value storage system with support for:
 * - Single and collection values
 * - Expiry time for keys
 * - Basic operations (get, set, expire)
 * - Bulk operations (mGet, mSet)
 * - Stats and flush operations
 * - Transaction support with commit and rollback
 */
public class KeyValue<K, V> {
    private final Map<K, ValueWrapper<V>> storage;
    private final Map<K, ValueWrapper<V>> transactionStorage;
    private boolean inTransaction;
    
    public KeyValue() {
        this.storage = new ConcurrentHashMap<>();
        this.transactionStorage = new HashMap<>();
        this.inTransaction = false;
    }
    
    /**
     * Wrapper class for values that can hold single value or collection,
     * along with expiry time.
     */
    private static class ValueWrapper<V> {
        private final Object value; // Can be V or Collection<V>
        private long expiryTime; // In epoch milliseconds, 0 means no expiry
        
        public ValueWrapper(V value, long expirySeconds) {
            this.value = value;
            this.expiryTime = expirySeconds > 0 ? 
                    Instant.now().plusSeconds(expirySeconds).toEpochMilli() : 0;
        }
        
        public ValueWrapper(Collection<V> values, long expirySeconds) {
            this.value = values;
            this.expiryTime = expirySeconds > 0 ? 
                    Instant.now().plusSeconds(expirySeconds).toEpochMilli() : 0;
        }
        
        public ValueWrapper(ValueWrapper<V> other) {
            this.value = other.value;
            this.expiryTime = other.expiryTime;
        }
        
        @SuppressWarnings("unchecked")
        public V getValue() {
            return (V) value;
        }
        
        @SuppressWarnings("unchecked")
        public Collection<V> getCollection() {
            return (Collection<V>) value;
        }
        
        public boolean isCollection() {
            return value instanceof Collection;
        }
        
        public boolean isExpired() {
            return expiryTime > 0 && Instant.now().toEpochMilli() > expiryTime;
        }
        
        public long getExpiryTimeSeconds() {
            return expiryTime == 0 ? 0 : (expiryTime - Instant.now().toEpochMilli()) / 1000;
        }
    }
    
    // Basic operations
    
    /**
     * Get value for a key if it exists and is not expired
     */
    public Optional<V> get(K key) {
        Map<K, ValueWrapper<V>> targetStorage = inTransaction ? transactionStorage : storage;
        ValueWrapper<V> wrapper = targetStorage.get(key);
        
        if (wrapper == null) {
            wrapper = storage.get(key);
        }
        
        if (wrapper == null || wrapper.isExpired()) {
            if (wrapper != null && wrapper.isExpired()) {
                remove(key);
            }
            return Optional.empty();
        }
        
        if (wrapper.isCollection()) {
            return Optional.empty(); // Can't return collection as single value
        }
        
        return Optional.of(wrapper.getValue());
    }
    
    /**
     * Get collection for a key if it exists and is not expired
     */
    public Optional<Collection<V>> getCollection(K key) {
        Map<K, ValueWrapper<V>> targetStorage = inTransaction ? transactionStorage : storage;
        ValueWrapper<V> wrapper = targetStorage.get(key);
        
        if (wrapper == null) {
            wrapper = storage.get(key);
        }
        
        if (wrapper == null || wrapper.isExpired()) {
            if (wrapper != null && wrapper.isExpired()) {
                remove(key);
            }
            return Optional.empty();
        }
        
        if (!wrapper.isCollection()) {
            return Optional.of(Collections.singletonList(wrapper.getValue()));
        }
        
        return Optional.of(wrapper.getCollection());
    }
    
    /**
     * Set a single value with optional expiry time
     */
    public void set(K key, V value, long expirySeconds) {
        Map<K, ValueWrapper<V>> targetStorage = inTransaction ? transactionStorage : storage;
        targetStorage.put(key, new ValueWrapper<>(value, expirySeconds));
    }
    
    /**
     * Set a single value with no expiry
     */
    public void set(K key, V value) {
        set(key, value, 0);
    }
    
    /**
     * Set a collection of values with optional expiry time
     */
    public void setCollection(K key, Collection<V> values, long expirySeconds) {
        Map<K, ValueWrapper<V>> targetStorage = inTransaction ? transactionStorage : storage;
        targetStorage.put(key, new ValueWrapper<>(values, expirySeconds));
    }
    
    /**
     * Set a collection of values with no expiry
     */
    public void setCollection(K key, Collection<V> values) {
        setCollection(key, values, 0);
    }
    
    /**
     * Set or update expiry time for a key
     * @return true if key existed and expiry was set, false otherwise
     */
    public boolean expire(K key, long expirySeconds) {
        Map<K, ValueWrapper<V>> targetStorage = inTransaction ? transactionStorage : storage;
        ValueWrapper<V> wrapper = targetStorage.get(key);
        
        if (wrapper == null) {
            wrapper = storage.get(key);
            if (wrapper != null && inTransaction) {
                // Copy to transaction storage
                wrapper = new ValueWrapper<>(wrapper);
                transactionStorage.put(key, wrapper);
            }
        }
        
        if (wrapper == null || wrapper.isExpired()) {
            if (wrapper != null && wrapper.isExpired()) {
                remove(key);
            }
            return false;
        }
        
        wrapper.expiryTime = expirySeconds > 0 ?
                Instant.now().plusSeconds(expirySeconds).toEpochMilli() : 0;
        return true;
    }
    
    /**
     * Remove expiry from a key
     * @return true if key existed and expiry was removed, false otherwise
     */
    public boolean persist(K key) {
        return expire(key, 0);
    }
    
    /**
     * Remove a key
     * @return true if key existed and was removed, false otherwise
     */
    public boolean remove(K key) {
        if (inTransaction) {
            transactionStorage.put(key, null); // Mark for deletion
            return storage.containsKey(key) || transactionStorage.containsKey(key);
        } else {
            return storage.remove(key) != null;
        }
    }
    
    // Bulk operations
    
    /**
     * Get multiple values at once
     * @return Map of keys to values (only for keys that exist and aren't expired)
     */
    public Map<K, V> mGet(Collection<K> keys) {
        Map<K, V> result = new HashMap<>();
        for (K key : keys) {
            get(key).ifPresent(value -> result.put(key, value));
        }
        return result;
    }
    
    /**
     * Set multiple key-value pairs at once with the same expiry time
     */
    public void mSet(Map<K, V> keyValues, long expirySeconds) {
        for (Map.Entry<K, V> entry : keyValues.entrySet()) {
            set(entry.getKey(), entry.getValue(), expirySeconds);
        }
    }
    
    /**
     * Set multiple key-value pairs at once with no expiry
     */
    public void mSet(Map<K, V> keyValues) {
        mSet(keyValues, 0);
    }
    
    // System operations
    
    /**
     * Get total number of keys in the store (excluding expired keys)
     */
    public int size() {
        // Clean up expired keys first (optional, but helps provide accurate count)
        cleanupExpiredKeys();
        
        if (inTransaction) {
            // Count keys in base storage that aren't deleted in transaction
            long baseCount = storage.keySet().stream()
                    .filter(key -> !transactionStorage.containsKey(key) || transactionStorage.get(key) != null)
                    .count();
            
            // Add new keys from transaction
            long txCount = transactionStorage.values().stream()
                    .filter(wrapper -> wrapper != null)
                    .count();
            
            return (int) (baseCount + txCount);
        } else {
            return storage.size();
        }
    }
    
    /**
     * Remove all keys from the store
     */
    public void flushAll() {
        if (inTransaction) {
            for (K key : storage.keySet()) {
                transactionStorage.put(key, null); // Mark all for deletion
            }
        } else {
            storage.clear();
        }
    }
    
    private void cleanupExpiredKeys() {
        List<K> expiredKeys = storage.entrySet().stream()
                .filter(entry -> entry.getValue().isExpired())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        expiredKeys.forEach(storage::remove);
        
        if (inTransaction) {
            expiredKeys = transactionStorage.entrySet().stream()
                    .filter(entry -> entry.getValue() != null && entry.getValue().isExpired())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            
            expiredKeys.forEach(transactionStorage::remove);
        }
    }
    
    // Transaction support
    
    /**
     * Start a new transaction
     * @throws IllegalStateException if a transaction is already in progress
     */
    public void beginTransaction() {
        if (inTransaction) {
            throw new IllegalStateException("Transaction already in progress");
        }
        
        transactionStorage.clear();
        inTransaction = true;
    }
    
    /**
     * Commit the current transaction
     * @throws IllegalStateException if no transaction is in progress
     */
    public void commit() {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction in progress");
        }
        
        // Apply changes to main storage
        for (Map.Entry<K, ValueWrapper<V>> entry : transactionStorage.entrySet()) {
            if (entry.getValue() == null) {
                storage.remove(entry.getKey());
            } else {
                storage.put(entry.getKey(), entry.getValue());
            }
        }
        
        // End transaction
        transactionStorage.clear();
        inTransaction = false;
    }
    
    /**
     * Rollback the current transaction
     * @throws IllegalStateException if no transaction is in progress
     */
    public void rollback() {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction in progress");
        }
        
        // Discard all changes
        transactionStorage.clear();
        inTransaction = false;
    }
    
    /**
     * Check if a transaction is in progress
     */
    public boolean isInTransaction() {
        return inTransaction;
    }
}
