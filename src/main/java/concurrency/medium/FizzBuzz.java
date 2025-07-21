package concurrency.medium;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FizzBuzz {

    private static final AtomicInteger counter = new AtomicInteger(1);
    private static int MAX_VALUE = 15;

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();


    public void fizz() throws InterruptedException {
        while (true) {
            lock.lock();
            try {
                while (counter.get() <= MAX_VALUE && !(counter.get() % 3 == 0 && counter.get() % 5 != 0)) {
                    condition.await();
                }
                if (counter.get() > MAX_VALUE) {
                    break;
                }
                System.out.println("FIZZ");
                counter.getAndIncrement();
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }

    public void buzz() throws InterruptedException {
        while (true) {
            lock.lock();
            try {
                while (counter.get() <= MAX_VALUE &&
                        !(counter.get() % 3 != 0 && counter.get() % 5 == 0)) {
                    condition.await();
                }

                if (counter.get() > MAX_VALUE) {
                    break;
                }
                System.out.println("BUZZ");
                counter.getAndIncrement();
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }

    public void fizzBuzz() throws InterruptedException {
        while (true) {
            lock.lock();
            try {
                while (counter.get() <= MAX_VALUE &&
                  !(counter.get() % 3 == 0 && counter.get() % 5 == 0)) {
                    condition.await();
                }
                if (counter.get() > MAX_VALUE) {
                    break;
                }

                System.out.println("FIZZBUZZ");
                counter.getAndIncrement();
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }

    public void number() throws InterruptedException {
        while (true) {
            lock.lock();
            try {
                while (counter.get() <= MAX_VALUE &&
                        !(counter.get() % 3 != 0 && counter.get() % 5 != 0)) {
                    condition.await();
                }

                if (counter.get() > MAX_VALUE) {
                    break;
                }
                System.out.println(counter.get());
                counter.getAndIncrement();
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        var solution = new FizzBuzz();

        Thread fizzThread = new Thread(() -> {
            try {
                solution.fizz();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "fizzThread");

        Thread buzzThread = new Thread(() -> {
            try {
                solution.buzz();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "buzzThread");

        Thread fizzBuzzThread = new Thread(() -> {
            try {
                solution.fizzBuzz();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "fizzBuzzThread");

        Thread normalThread = new Thread(() -> {
            try {
                solution.number();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "normalThread");

        List<Thread> allThreads = List.of(fizzThread, buzzThread, fizzBuzzThread, normalThread);

        allThreads.forEach(thread -> thread.start());
        allThreads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }


}
