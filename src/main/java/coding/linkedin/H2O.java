package coding.linkedin;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class H2O {
        private Semaphore hydrogenSemaphore = new Semaphore(2);
        private Semaphore oxygenSemaphore = new Semaphore(1);
        private CyclicBarrier barrier = new CyclicBarrier(3);

        public H2O() {

        }

        public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
            try {
                hydrogenSemaphore.acquire();

                barrier.await();
                // releaseHydrogen.run() outputs "H". Do not change or remove this line.
                releaseHydrogen.run();
            } catch (BrokenBarrierException e) {
                // Thread.currentThread().interrupt() sets the interrupt flag on the current
                // thread. This is a way to signal that the thread should stop what it's
                // doing and handle the interruption.
                Thread.currentThread().interrupt();
            } finally {
                hydrogenSemaphore.release();
            }

        }

        public void oxygen(Runnable releaseOxygen) throws InterruptedException {
            try {
                oxygenSemaphore.acquire();

                barrier.await();

                // 3. Now all three threads execute their release methods simultaneously
                // releaseOxygen.run() outputs "O". Do not change or remove this line.
                releaseOxygen.run();
            } catch (BrokenBarrierException e) {
                Thread.currentThread().interrupt();
            } finally {
                oxygenSemaphore.release();
            }
        }

//    public static void main(String[] args) {
//        System.out.println("Testing H2O Solution...");
//
//        // Test case 1: "HOH" -> should output "HHO" or similar
//        testH2O("HOH");
//
//        // Test case 2: "OOHHHH" -> should output "HHOHHO" or similar
//        testH2O("OOHHHH");
//    }
}
