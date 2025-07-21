package lld.database.kvstore2;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * An in-memory key-value store implementation with the following features:
 * - Basic CRUD operations (put, get, delete, exists, keys, size, clear)
 * - Time-To-Live (TTL) support for keys
 * - Transaction support (begin, commit, rollback)
 * - Thread-safe implementation with ReadWriteLock
 * - Automatic cleanup of expired entries
 */
public class KVStore {
    public static void main(String[] args) {
        System.out.println("In-Memory Key-Value Store Demo");
        
        // Create a new key-value store instance
        KeyValueStore<String, String> store = new InMemoryStore<>();
        
        // Basic operations demo
        System.out.println("\n--- Basic Operations ---");
        store.put("name", "John Doe");
        store.put("email", "john@example.com");
        store.put("location", "New York");
        
        System.out.println("Get name: " + store.get("name"));
        System.out.println("Get email: " + store.get("email"));
        System.out.println("Get nonexistent key: " + store.get("phone"));
        
        System.out.println("Size: " + store.size());
        System.out.println("Keys: " + store.keys());
        System.out.println("Exists 'location': " + store.exists("location"));
        
        store.delete("email");
        System.out.println("After deleting 'email', size: " + store.size());
        
        // TTL demo
        System.out.println("\n--- TTL Demo ---");
        store.put("temporary", "This will expire", 2000); // expires in 2 seconds
        System.out.println("Added temporary key with 2 second TTL");
        System.out.println("Get 'temporary' immediately: " + store.get("temporary"));
        
        try {
            Thread.sleep(2500); // Wait for key to expire
            System.out.println("Get 'temporary' after TTL: " + store.get("temporary"));
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted");
        }
        
        // Transaction demo
        System.out.println("\n--- Transaction Demo ---");
        Transaction<String, String> transaction = store.beginTransaction();
        System.out.println("Transaction started");
        
        transaction.put("name", "Jane Smith");
        transaction.put("age", "30");
        transaction.delete("location");
        
        System.out.println("In transaction, get 'name': " + transaction.get("name"));
        System.out.println("In store before commit, get 'name': " + store.get("name"));
        
        transaction.commit();
        System.out.println("Transaction committed");
        
        System.out.println("After commit, get 'name': " + store.get("name"));
        System.out.println("After commit, get 'location': " + store.get("location"));
        System.out.println("After commit, size: " + store.size());
        
        // Rollback demo
        System.out.println("\n--- Rollback Demo ---");
        transaction = store.beginTransaction();
        
        transaction.put("name", "Will be rolled back");
        transaction.put("new-key", "Will not be added");
        
        System.out.println("In transaction, get 'name': " + transaction.get("name"));
        
        transaction.rollback();
        System.out.println("Transaction rolled back");
        
        System.out.println("After rollback, get 'name': " + store.get("name"));
        System.out.println("After rollback, 'new-key' exists: " + store.exists("new-key"));
    }
}

// Core interface for the key-value store
interface KeyValueStore<K, V> {
    // Basic CRUD operations
    boolean put(K key, V value);
    boolean put(K key, V value, long ttlMillis);
    V get(K key);
    boolean delete(K key);
    boolean exists(K key);
    Set<K> keys();
    int size();
    void clear();
    
    // Transaction support
    Transaction<K, V> beginTransaction();
}

// Value wrapper to support TTL (Time-To-Live)
class ValueWrapper<V> {
    private final V value;
    private final long expireAt;
    
    public ValueWrapper(V value, long expireAt) {
        this.value = value;
        this.expireAt = expireAt;
    }
    
    public V getValue() {
        return value;
    }
    
    public boolean isExpired() {
        return expireAt > 0 && System.currentTimeMillis() >= expireAt;
    }
}

// In-memory implementation of the key-value store
class InMemoryStore<K, V> implements KeyValueStore<K, V> {
    private final ConcurrentHashMap<K, ValueWrapper<V>> store;
    private final ScheduledExecutorService scheduler;
    private final ReadWriteLock lock;
    
    public InMemoryStore() {
        this.store = new ConcurrentHashMap<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.lock = new ReentrantReadWriteLock();
        
        // Schedule cleanup of expired entries
        scheduler.scheduleAtFixedRate(this::cleanupExpiredEntries, 1, 1, TimeUnit.SECONDS);
    }
    
    private void cleanupExpiredEntries() {
        Iterator<Map.Entry<K, ValueWrapper<V>>> iterator = store.entrySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getValue().isExpired()) {
                iterator.remove();
            }
        }
    }
    
    @Override
    public boolean put(K key, V value) {
        return put(key, value, -1); // No expiration
    }
    
    @Override
    public boolean put(K key, V value, long ttlMillis) {
        if (key == null) return false;
        
        long expireAt = ttlMillis > 0 ? System.currentTimeMillis() + ttlMillis : -1;
        ValueWrapper<V> wrapper = new ValueWrapper<>(value, expireAt);
        
        lock.writeLock().lock();
        try {
            store.put(key, wrapper);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public V get(K key) {
        if (key == null) return null;
        
        lock.readLock().lock();
        try {
            ValueWrapper<V> wrapper = store.get(key);
            if (wrapper == null) return null;
            
            if (wrapper.isExpired()) {
                store.remove(key);
                return null;
            }
            
            return wrapper.getValue();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public boolean delete(K key) {
        if (key == null) return false;
        
        lock.writeLock().lock();
        try {
            return store.remove(key) != null;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public boolean exists(K key) {
        if (key == null) return false;
        
        lock.readLock().lock();
        try {
            ValueWrapper<V> wrapper = store.get(key);
            if (wrapper == null) return false;
            
            if (wrapper.isExpired()) {
                store.remove(key);
                return false;
            }
            
            return true;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public Set<K> keys() {
        lock.readLock().lock();
        try {
            Set<K> validKeys = new HashSet<>();
            for (Map.Entry<K, ValueWrapper<V>> entry : store.entrySet()) {
                if (!entry.getValue().isExpired()) {
                    validKeys.add(entry.getKey());
                }
            }
            return validKeys;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public int size() {
        return keys().size();
    }
    
    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            store.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public Transaction<K, V> beginTransaction() {
        return new TransactionImpl<>(this);
    }
}

// Transaction interface
interface Transaction<K, V> {
    void put(K key, V value);
    void delete(K key);
    V get(K key);
    boolean commit();
    void rollback();
}

// Transaction implementation
class TransactionImpl<K, V> implements Transaction<K, V> {
    private final KeyValueStore<K, V> store;
    private final Map<K, V> pendingPuts;
    private final Set<K> pendingDeletes;
    private final Map<K, V> originalValues;
    private boolean active;
    
    public TransactionImpl(KeyValueStore<K, V> store) {
        this.store = store;
        this.pendingPuts = new HashMap<>();
        this.pendingDeletes = new HashSet<>();
        this.originalValues = new HashMap<>();
        this.active = true;
    }
    
    @Override
    public void put(K key, V value) {
        if (!active) throw new IllegalStateException("Transaction not active");
        
        if (!originalValues.containsKey(key)) {
            originalValues.put(key, store.get(key));
        }
        
        pendingPuts.put(key, value);
        pendingDeletes.remove(key); // Remove from deletes if present
    }
    
    @Override
    public void delete(K key) {
        if (!active) throw new IllegalStateException("Transaction not active");
        
        if (!originalValues.containsKey(key)) {
            originalValues.put(key, store.get(key));
        }
        
        pendingDeletes.add(key);
        pendingPuts.remove(key); // Remove from puts if present
    }
    
    @Override
    public V get(K key) {
        if (!active) throw new IllegalStateException("Transaction not active");
        
        if (pendingDeletes.contains(key)) {
            return null; // Key marked for deletion
        }
        
        if (pendingPuts.containsKey(key)) {
            return pendingPuts.get(key); // Return pending value
        }
        
        return store.get(key); // Get from store
    }
    
    @Override
    public boolean commit() {
        if (!active) return false;
        
        try {
            // Apply all changes
            for (K key : pendingDeletes) {
                store.delete(key);
            }
            
            for (Map.Entry<K, V> entry : pendingPuts.entrySet()) {
                store.put(entry.getKey(), entry.getValue());
            }
            
            return true;
        } finally {
            active = false;
            pendingPuts.clear();
            pendingDeletes.clear();
            originalValues.clear();
        }
    }
    
    @Override
    public void rollback() {
        if (!active) return;
        
        active = false;
        pendingPuts.clear();
        pendingDeletes.clear();
        originalValues.clear();
    }
}
