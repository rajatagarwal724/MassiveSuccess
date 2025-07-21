package lld.database.kvstore;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

public class InMemoryKeyValueStore {
    public static void main(String[] args) {
        // Create KV store instance
        KeyValueStore<String, Object> kvStore = new KeyValueStoreImpl<>();
        CommandProcessor commandProcessor = new CommandProcessor(kvStore);
        
        System.out.println("Welcome to In-Memory Key-Value Store");
        System.out.println("Type 'help' for commands, 'exit' to quit");
        
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();
                
                if (input.equalsIgnoreCase("exit")) {
                    break;
                }
                
                String result = commandProcessor.processCommand(input);
                System.out.println(result);
            }
        }
        
        System.out.println("Goodbye!");
    }
}

// Main Key-Value Store interface
interface KeyValueStore<K, V> {
    // Basic operations
    boolean put(K key, V value);
    boolean put(K key, V value, long ttlMillis);
    V get(K key);
    boolean delete(K key);
    boolean exists(K key);
    Set<K> keys();
    int size();
    void clear();
    
    // Atomic operations
    V getAndSet(K key, V value);
    boolean compareAndSet(K key, V expectedValue, V newValue);
    
    // Batch operations
    Map<K, V> multiGet(Collection<K> keys);
    boolean multiPut(Map<K, V> entries);
    boolean multiDelete(Collection<K> keys);
    
    // Transaction support
    Transaction<K, V> beginTransaction();
    
    // Persistence operations
    void snapshot(String filepath) throws IOException;
    void restore(String filepath) throws IOException, ClassNotFoundException;
}

// Implementation of the KeyValueStore interface
class KeyValueStoreImpl<K, V> implements KeyValueStore<K, V> {
    private final ConcurrentHashMap<K, ValueWrapper<V>> store;
    private final ScheduledExecutorService scheduler;
    private final ReadWriteLock lock;
    
    public KeyValueStoreImpl() {
        this.store = new ConcurrentHashMap<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.lock = new ReentrantReadWriteLock();
        
        // Start a periodic task to clean up expired entries
        scheduler.scheduleAtFixedRate(this::cleanupExpiredEntries, 1, 1, TimeUnit.SECONDS);
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
            
            // Check if the value has expired
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
            
            // Check if the value has expired
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
            // Filter out expired keys
            Set<K> validKeys = new HashSet<>();
            for (Map.Entry<K, ValueWrapper<V>> entry : store.entrySet()) {
                if (!entry.getValue().isExpired()) {
                    validKeys.add(entry.getKey());
                } else {
                    // Clean up expired key
                    store.remove(entry.getKey());
                }
            }
            return validKeys;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public int size() {
        lock.readLock().lock();
        try {
            // Count only non-expired entries
            int count = 0;
            for (ValueWrapper<V> wrapper : store.values()) {
                if (!wrapper.isExpired()) {
                    count++;
                }
            }
            return count;
        } finally {
            lock.readLock().unlock();
        }
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
    public V getAndSet(K key, V value) {
        if (key == null) return null;
        
        lock.writeLock().lock();
        try {
            ValueWrapper<V> oldWrapper = store.get(key);
            V oldValue = null;
            
            if (oldWrapper != null && !oldWrapper.isExpired()) {
                oldValue = oldWrapper.getValue();
            }
            
            store.put(key, new ValueWrapper<>(value, -1));
            return oldValue;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public boolean compareAndSet(K key, V expectedValue, V newValue) {
        if (key == null) return false;
        
        lock.writeLock().lock();
        try {
            ValueWrapper<V> wrapper = store.get(key);
            
            // Check if the key doesn't exist or is expired
            if (wrapper == null || wrapper.isExpired()) {
                if (expectedValue == null) {
                    store.put(key, new ValueWrapper<>(newValue, -1));
                    return true;
                }
                return false;
            }
            
            V currentValue = wrapper.getValue();
            if (Objects.equals(currentValue, expectedValue)) {
                store.put(key, new ValueWrapper<>(newValue, -1));
                return true;
            }
            
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public Map<K, V> multiGet(Collection<K> keys) {
        if (keys == null) return Collections.emptyMap();
        
        Map<K, V> result = new HashMap<>();
        lock.readLock().lock();
        try {
            for (K key : keys) {
                V value = get(key);
                if (value != null) {
                    result.put(key, value);
                }
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public boolean multiPut(Map<K, V> entries) {
        if (entries == null || entries.isEmpty()) return false;
        
        lock.writeLock().lock();
        try {
            for (Map.Entry<K, V> entry : entries.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public boolean multiDelete(Collection<K> keys) {
        if (keys == null || keys.isEmpty()) return false;
        
        lock.writeLock().lock();
        try {
            for (K key : keys) {
                store.remove(key);
            }
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public Transaction<K, V> beginTransaction() {
        return new TransactionImpl<>(this);
    }
    
    @Override
    public void snapshot(String filepath) throws IOException {
        lock.readLock().lock();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filepath))) {
            // Create a snapshot with only non-expired entries
            Map<K, ValueWrapper<V>> snapshot = new HashMap<>();
            for (Map.Entry<K, ValueWrapper<V>> entry : store.entrySet()) {
                if (!entry.getValue().isExpired()) {
                    snapshot.put(entry.getKey(), entry.getValue());
                }
            }
            oos.writeObject(snapshot);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void restore(String filepath) throws IOException, ClassNotFoundException {
        lock.writeLock().lock();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filepath))) {
            Map<K, ValueWrapper<V>> data = (Map<K, ValueWrapper<V>>) ois.readObject();
            store.clear();
            
            // Only restore non-expired entries
            for (Map.Entry<K, ValueWrapper<V>> entry : data.entrySet()) {
                if (!entry.getValue().isExpired()) {
                    store.put(entry.getKey(), entry.getValue());
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    private void cleanupExpiredEntries() {
        lock.writeLock().lock();
        try {
            Iterator<Map.Entry<K, ValueWrapper<V>>> iterator = store.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<K, ValueWrapper<V>> entry = iterator.next();
                if (entry.getValue().isExpired()) {
                    iterator.remove();
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    // For proper cleanup
    public void shutdown() {
        scheduler.shutdown();
    }
}

// Wrapper class for values to support expiration
class ValueWrapper<V> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final V value;
    private final long expireAt; // Timestamp when this value expires, -1 for no expiration
    
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
    
    public long getExpireAt() {
        return expireAt;
    }
}

// Transaction interface
interface Transaction<K, V> {
    boolean put(K key, V value);
    boolean delete(K key);
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
    public boolean put(K key, V value) {
        if (!active) return false;
        
        if (!originalValues.containsKey(key)) {
            V originalValue = store.get(key);
            if (originalValue != null) {
                originalValues.put(key, originalValue);
            }
        }
        
        pendingPuts.put(key, value);
        pendingDeletes.remove(key);
        return true;
    }
    
    @Override
    public boolean delete(K key) {
        if (!active) return false;
        
        if (!originalValues.containsKey(key)) {
            V originalValue = store.get(key);
            if (originalValue != null) {
                originalValues.put(key, originalValue);
            }
        }
        
        pendingDeletes.add(key);
        pendingPuts.remove(key);
        return true;
    }
    
    @Override
    public V get(K key) {
        if (!active) return null;
        
        if (pendingDeletes.contains(key)) {
            return null;
        }
        
        if (pendingPuts.containsKey(key)) {
            return pendingPuts.get(key);
        }
        
        return store.get(key);
    }
    
    @Override
    public boolean commit() {
        if (!active) return false;
        
        try {
            // Apply all pending changes to the store
            for (Map.Entry<K, V> entry : pendingPuts.entrySet()) {
                store.put(entry.getKey(), entry.getValue());
            }
            
            for (K key : pendingDeletes) {
                store.delete(key);
            }
            
            return true;
        } finally {
            active = false;
        }
    }
    
    @Override
    public void rollback() {
        if (!active) return;
        
        // Restore original values
        for (Map.Entry<K, V> entry : originalValues.entrySet()) {
            store.put(entry.getKey(), entry.getValue());
        }
        
        active = false;
    }
}

// Command processor for the CLI
class CommandProcessor {
    private final KeyValueStore<String, Object> kvStore;
    
    public CommandProcessor(KeyValueStore<String, Object> kvStore) {
        this.kvStore = kvStore;
    }
    
    public String processCommand(String command) {
        if (command == null || command.isEmpty()) {
            return "Error: Empty command";
        }
        
        String[] parts = command.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        
        try {
            switch (cmd) {
                case "help":
                    return getHelp();
                case "put":
                    return handlePut(parts.length > 1 ? parts[1] : "");
                case "get":
                    return handleGet(parts.length > 1 ? parts[1] : "");
                case "delete":
                    return handleDelete(parts.length > 1 ? parts[1] : "");
                case "exists":
                    return handleExists(parts.length > 1 ? parts[1] : "");
                case "keys":
                    return handleKeys();
                case "size":
                    return handleSize();
                case "clear":
                    return handleClear();
                case "snapshot":
                    return handleSnapshot(parts.length > 1 ? parts[1] : "");
                case "restore":
                    return handleRestore(parts.length > 1 ? parts[1] : "");
                default:
                    return "Error: Unknown command. Type 'help' for available commands.";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    private String getHelp() {
        return "Available commands:\n" +
               "  help                    - Show this help\n" +
               "  put <key> <value> [ttl] - Store a key-value pair with optional TTL in milliseconds\n" +
               "  get <key>               - Retrieve a value by key\n" +
               "  delete <key>            - Delete a key-value pair\n" +
               "  exists <key>            - Check if a key exists\n" +
               "  keys                    - List all keys\n" +
               "  size                    - Get the number of key-value pairs\n" +
               "  clear                   - Remove all key-value pairs\n" +
               "  snapshot <filepath>     - Save the current state to a file\n" +
               "  restore <filepath>      - Load state from a file\n" +
               "  exit                    - Exit the program";
    }
    
    private String handlePut(String args) {
        String[] parts = args.split("\\s+", 3);
        
        if (parts.length < 2) {
            return "Error: Usage: put <key> <value> [ttl]";
        }
        
        String key = parts[0];
        String value = parts[1];
        
        if (parts.length == 3) {
            try {
                long ttl = Long.parseLong(parts[2]);
                kvStore.put(key, value, ttl);
                return "Stored key '" + key + "' with TTL of " + ttl + "ms";
            } catch (NumberFormatException e) {
                return "Error: TTL must be a number";
            }
        } else {
            kvStore.put(key, value);
            return "Stored key '" + key + "'";
        }
    }
    
    private String handleGet(String key) {
        if (key.isEmpty()) {
            return "Error: Usage: get <key>";
        }
        
        Object value = kvStore.get(key);
        if (value == null) {
            return "Key '" + key + "' not found";
        }
        
        return value.toString();
    }
    
    private String handleDelete(String key) {
        if (key.isEmpty()) {
            return "Error: Usage: delete <key>";
        }
        
        boolean deleted = kvStore.delete(key);
        return deleted ? "Key '" + key + "' deleted" : "Key '" + key + "' not found";
    }
    
    private String handleExists(String key) {
        if (key.isEmpty()) {
            return "Error: Usage: exists <key>";
        }
        
        boolean exists = kvStore.exists(key);
        return exists ? "Key '" + key + "' exists" : "Key '" + key + "' does not exist";
    }
    
    private String handleKeys() {
        Set<String> keys = kvStore.keys();
        if (keys.isEmpty()) {
            return "No keys found";
        }
        
        StringBuilder sb = new StringBuilder("Keys:\n");
        for (String key : keys) {
            sb.append("  ").append(key).append("\n");
        }
        return sb.toString();
    }
    
    private String handleSize() {
        int size = kvStore.size();
        return "Store size: " + size + " key(s)";
    }
    
    private String handleClear() {
        kvStore.clear();
        return "Store cleared";
    }
    
    private String handleSnapshot(String filepath) {
        if (filepath.isEmpty()) {
            return "Error: Usage: snapshot <filepath>";
        }
        
        try {
            kvStore.snapshot(filepath);
            return "Snapshot saved to '" + filepath + "'";
        } catch (IOException e) {
            return "Error saving snapshot: " + e.getMessage();
        }
    }
    
    private String handleRestore(String filepath) {
        if (filepath.isEmpty()) {
            return "Error: Usage: restore <filepath>";
        }
        
        try {
            kvStore.restore(filepath);
            return "Store restored from '" + filepath + "'";
        } catch (IOException | ClassNotFoundException e) {
            return "Error restoring from snapshot: " + e.getMessage();
        }
    }
}
