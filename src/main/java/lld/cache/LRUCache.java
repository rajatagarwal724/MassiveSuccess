package lld.cache;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class LRUCache<K, V> {
    private final int capacity;
    private final ConcurrentHashMap<K, CacheNode<K, V>> map;
    private final ConcurrentLinkedDeque<CacheNode<K, V>> accessQueue;
    private final AtomicInteger size;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new ConcurrentHashMap<>(capacity);
        this.accessQueue = new ConcurrentLinkedDeque<>();
        this.size = new AtomicInteger(0);
    }

    public V get(K key) {
        CacheNode<K, V> node = map.get(key);
        if (node == null) return null;

        // Update access order (move to end of queue)
        updateAccessOrder(node);

        return node.value;
    }

    public void put(K key, V value) {
        CacheNode<K, V> existingNode = map.get(key);

        if (existingNode != null) {
            // Update existing entry
            existingNode.value = value;
            updateAccessOrder(existingNode);
            return;
        }

        // Check if eviction is needed
        while (size.get() >= capacity) {
            evictOldest();
        }

        // Add new entry
        CacheNode<K, V> newNode = new CacheNode<>(key, value);
        map.put(key, newNode);
        accessQueue.addLast(newNode);
        size.incrementAndGet();
    }

    private void updateAccessOrder(CacheNode<K, V> node) {
        // Remove and add to end (most recently used position)
        if (accessQueue.remove(node)) {
            accessQueue.addLast(node);
        }
    }

    private void evictOldest() {
        CacheNode<K, V> oldest = accessQueue.pollFirst();
        if (oldest != null && map.remove(oldest.key) != null) {
            size.decrementAndGet();
        }
    }

    private static class CacheNode<K, V> {
        final K key;
        volatile V value;

        CacheNode(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheNode<?, ?> cacheNode = (CacheNode<?, ?>) o;
            return Objects.equals(key, cacheNode.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }
}
