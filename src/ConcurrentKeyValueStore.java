import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentKeyValueStore {
    private static class ValueWrapper {
        private final double value;
        private final long expirationTime;

        public ValueWrapper(double value, long expirationTime) {
            this.value = value;
            this.expirationTime = expirationTime;
        }

        public double getValue() {
            return value;
        }

        public long getExpirationTime() {
            return expirationTime;
        }
    }

    private final ConcurrentHashMap<String, ValueWrapper> store;
    private final AtomicLong sum;
    private final AtomicInteger count;
    private final ReentrantReadWriteLock lock;

    public ConcurrentKeyValueStore() {
        this.store = new ConcurrentHashMap<>();
        this.sum = new AtomicLong(0);
        this.count = new AtomicInteger(0);
        this.lock = new ReentrantReadWriteLock();
    }

    public void put(String key, double value, long expirationTime) {
        ValueWrapper newValue = new ValueWrapper(value, expirationTime);
        
        // Use write lock for compound operations
        lock.writeLock().lock();
        try {
            ValueWrapper oldValue = store.put(key, newValue);
            
            // Update sum and count
            if (oldValue != null && oldValue.getExpirationTime() > System.currentTimeMillis()) {
                sum.addAndGet((long) (-oldValue.getValue() * 1000)); // Convert to long for atomic operations
                count.decrementAndGet();
            }
            
            sum.addAndGet((long) (value * 1000));
            count.incrementAndGet();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Double get(String key) {
        ValueWrapper wrapper = store.get(key);
        if (wrapper == null) {
            return null;
        }

        long currentTime = System.currentTimeMillis();
        if (wrapper.getExpirationTime() <= currentTime) {
            // Value has expired, remove it
            lock.writeLock().lock();
            try {
                if (store.remove(key, wrapper)) {
                    sum.addAndGet((long) (-wrapper.getValue() * 1000));
                    count.decrementAndGet();
                }
            } finally {
                lock.writeLock().unlock();
            }
            return null;
        }

        return wrapper.getValue();
    }

    public double getAverage() {
        lock.readLock().lock();
        try {
            int currentCount = count.get();
            if (currentCount == 0) {
                return 0.0;
            }
            return (double) sum.get() / (currentCount * 1000.0);
        } finally {
            lock.readLock().unlock();
        }
    }

    // Helper method to clean up expired entries
    public void cleanup() {
        long currentTime = System.currentTimeMillis();
        store.forEach((key, wrapper) -> {
            if (wrapper.getExpirationTime() <= currentTime) {
                lock.writeLock().lock();
                try {
                    if (store.remove(key, wrapper)) {
                        sum.addAndGet((long) (-wrapper.getValue() * 1000));
                        count.decrementAndGet();
                    }
                } finally {
                    lock.writeLock().unlock();
                }
            }
        });
    }
} 