package concurrency.semaphore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkerPool {
    private static final AtomicInteger counter = new AtomicInteger();
    private static final Semaphore semaphore = new Semaphore(5);
    private static final int TARGET_VALUE = 10000;

    private static void worker() {
        while (true) {
            try {
                semaphore.acquire();
                int currentValue = counter.get();
                if (currentValue >= TARGET_VALUE) {
                    break;
                }
                System.out.println("Thread " + Thread.currentThread().getName() + " incremented counter from " + currentValue);
                if (!counter.compareAndSet(currentValue, currentValue + 1)) {
                    // If CAS failed, someone else incremented the counter
                    continue;
                }
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread was interrupted");
            } finally {
                semaphore.release();
            }
        }
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        List<Thread> workers = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            workers.add(new Thread(WorkerPool::worker, "Thread-" + (i + 1)));
        }

        workers.forEach(Thread::start);

        workers.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread was interrupted");
            }
        });

        long end = System.currentTimeMillis();
        System.out.println("Time taken: " + (end - start)/1000 + " seconds");
        System.out.println("Counter: " + counter.get());

    }
}
