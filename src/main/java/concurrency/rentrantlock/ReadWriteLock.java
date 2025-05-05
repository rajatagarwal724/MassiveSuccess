package concurrency.rentrantlock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLock {

    private static volatile int counter = 0;
    private static final int TARGET_VALUE = 1000;

    private static final java.util.concurrent.locks.ReadWriteLock lock = new ReentrantReadWriteLock();

    public static int incrementCounter() {
        lock.writeLock().lock();
        try {
            System.out.println("Thread " + Thread.currentThread().getName() + " read value " + counter);
            Thread.sleep(1);
            if (counter < TARGET_VALUE) {
                counter++;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
        return counter;
    }

    private static int readValue() {
        lock.readLock().lock();
        try {
            System.out.println("Thread " + Thread.currentThread().getName() + " read value " + counter);
            Thread.sleep(1);
            return counter;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
        return 0;
    }

    public static void main(String[] args) {

        long start = System.currentTimeMillis();
        List<Thread> readers = new ArrayList<>();
        List<Thread> writers = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            var readerThread = new Thread(() -> {
                while (readValue() < TARGET_VALUE) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "ReaderThread-" + (i + 1));
            readers.add(readerThread);
        }

        for (int i = 0; i < 4; i++) {
            var writerThread = new Thread(() -> {
                while (incrementCounter() < TARGET_VALUE) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "WriterThread-" + (i + 1));
            writers.add(writerThread);
        }

        readers.forEach(reader -> reader.start());
        writers.forEach(writer -> writer.start());

        readers.forEach(reader -> {
            try {
                reader.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        writers.forEach(writer -> {
            try {
                writer.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        long end = System.currentTimeMillis();
        System.out.println("Total Time Taken is : " + (end - start)/1000 + " seconds");
    }
}
