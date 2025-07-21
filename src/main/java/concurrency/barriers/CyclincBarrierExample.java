package concurrency.barriers;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclincBarrierExample {

    public static void main(String[] args) {
        int numThreads = 30;

        CyclicBarrier barrier = new CyclicBarrier(numThreads,
                () -> {
                    System.out.println("All Threads reached the barrier. Let's proceed together!");
                });

        for (int i = 0; i < numThreads; i++) {
            final int threadNum = i;
            new Thread(() -> {
                System.out.println("Thread: " + threadNum + " is preparing...");
                try {
                    Thread.sleep(1000 + threadNum * 500);
                    System.out.println("Thread " + threadNum + " is waiting at the barrier.");
                    barrier.await();
                    System.out.println("Thread " + threadNum + " started working!");
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }
}
