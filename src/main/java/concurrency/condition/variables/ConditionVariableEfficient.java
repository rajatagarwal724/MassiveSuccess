package concurrency.condition.variables;

public class ConditionVariableEfficient {

    private static final Object mutex = new Object();
    private static int sharedNumber = 0;
    private static boolean ready = false;


    private static void producer() {
        synchronized (mutex) {
            sharedNumber = 42;
            ready = true;
            System.out.println("Producer has produced the shared number : " + sharedNumber);
            mutex.notify();
        }
    }

    private static void consumer() {
        synchronized (mutex) {
            while (!ready) {
                try {
                    System.out.println("Start Waiting for the Producer, Thread: " + Thread.currentThread().getName());
                    mutex.wait();
                    System.out.println("Consumer has consumed the shared number : " + sharedNumber);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread was interrupted, Thread: " + Thread.currentThread().getName());
                }
            }
        }
    }

    public static void main(String[] args) {
        Thread producer = new Thread(ConditionVariableEfficient::producer, "Producer");
        Thread consumer = new Thread(ConditionVariableEfficient::consumer, "Consumer");
        try {
            consumer.start();
            Thread.sleep(5000L);
            producer.start();

            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
