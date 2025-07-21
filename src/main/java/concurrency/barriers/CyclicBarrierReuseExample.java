package concurrency.barriers;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierReuseExample {
    public static void main(String[] args) {
        int numThreads = 3;
        int numRounds = 3;
        CyclicBarrier barrier = new CyclicBarrier(numThreads, () -> {
            System.out.println("All threads reached the barrier. Proceeding to next round!\n");
        });

        for (int i = 0; i < numThreads; i++) {
            final int threadNum = i;
            new Thread(() -> {
                for (int round = 1; round <= numRounds; round++) {
                    System.out.println("Thread " + threadNum + " is working in round " + round);
                    try {
                        Thread.sleep(500 + threadNum * 200);
                        System.out.println("Thread " + threadNum + " waiting at barrier for round " + round);
                        barrier.await(); // Wait for others in each round
                        System.out.println("Thread " + threadNum + " completed for round " + round);
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
