package concurrency.condition.variables;

public class ConditionVariableInefficient {

    private static final Object mutex = new Object();
    private static int sharedNumber;
    private static boolean ready = false;

    private static void producer() {
        synchronized (mutex) {
            sharedNumber = 42;
            ready = true;
            System.out.println("Producer has produced the shared number : " + sharedNumber);
        }
    }

    private static void consumer() {
        while (true) {
            synchronized (mutex) {
                if (ready) {
                    System.out.println("Consumer has consumed the shared number : " + sharedNumber);
                    break;
                }
            }

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread was interrupted, Thread: " + Thread.currentThread().getName());
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread producerThread = new Thread(ConditionVariableInefficient::producer, "Producer");
        Thread consumerThread = new Thread(ConditionVariableInefficient::consumer, "Consumer");

        consumerThread.start();
        Thread.sleep(1000);
        producerThread.start();


        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread was interrupted");
        }
    }
}
