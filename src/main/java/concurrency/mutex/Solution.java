package concurrency.mutex;

public class Solution {
    private static volatile int counter = 0;
    private static final Object lock = new Object();

    public static void runExperiment(String experimentName, Runnable task) {
        counter = 0;

        Thread t1 = new Thread(task, "Task_1");
        Thread t2 = new Thread(task, "Task_2");

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final counter value " + experimentName + ": " + counter + "\n");
    }

    public static void incrementCounterWithMutex() {
        for (int i = 0; i < 100; i++) {
            synchronized (lock) {
//                int temp = counter;
//                System.out.println("Thread " + Thread.currentThread().getName() + " incremented counter from " + temp);
                System.out.println("Thread " + Thread.currentThread().getName() + " incremented counter from " + counter);
                try {
                    Thread.sleep(1); // Sleep for 1 millisecond
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                counter = temp + 1;
                counter++;
            }
        }
    }

    public static void incrementCounterNoMutex() {
        for (int i = 0; i < 100; i++) {
            int temp = counter;
            System.out.println("Thread " + Thread.currentThread().getName() + " incremented counter from " + temp);
//            System.out.println("Thread " + Thread.currentThread().getName() + " incremented counter from " + counter);
            try {
                Thread.sleep(1); // Sleep for 1 millisecond
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter = temp + 1;
//            counter++;
        }
    }

    public static void main(String[] args) {
        runExperiment("With Mutex Experiment", Solution::incrementCounterWithMutex);
//        runExperiment("No Mutex Experiment", Solution::incrementCounterNoMutex);
    }
}

