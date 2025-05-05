package concurrency.barriers;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Solution {

    private static final CyclicBarrier barrier = new CyclicBarrier(2, () -> System.out.println("All threads have reached the barrier"));

    public static void main(String[] args) {
        Thread thread1 = new Thread(Solution::worker, "Thread_1");
        Thread thread2 = new Thread(Solution::worker, "Thread_2");

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void worker() {
        try {
            System.out.println("Thread waiting at the barrier, Thread:  " + Thread.currentThread().getName());
            barrier.await();
            System.out.println("Barrier Release, Thread:  " + Thread.currentThread().getName());

        } catch (InterruptedException | BrokenBarrierException ex) {
            ex.printStackTrace();
        }
    }
}
