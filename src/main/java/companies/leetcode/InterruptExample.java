package companies.leetcode;

/**
 * Demonstration of Thread.currentThread().interrupt() usage
 */
public class InterruptExample {
    
    public static void main(String[] args) {
        System.out.println("=== Interrupt Handling Demonstration ===\n");
        
        // Test 1: Without proper interrupt handling
        testWithoutInterrupt();
        
        // Test 2: With proper interrupt handling
        testWithInterrupt();
    }
    
    /**
     * Demonstrates what happens WITHOUT Thread.currentThread().interrupt()
     */
    private static void testWithoutInterrupt() {
        System.out.println("Test 1: WITHOUT Thread.currentThread().interrupt()");
        
        Thread thread = new Thread(() -> {
            try {
                // Simulate some work that might be interrupted
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("  Caught InterruptedException");
                // WRONG: Don't call Thread.currentThread().interrupt()
                System.out.println("  Interrupt status after exception: " + Thread.currentThread().isInterrupted());
                // Thread continues normally, but interrupt status is lost!
            }
            
            // Later in the code...
            System.out.println("  Checking interrupt status later: " + Thread.currentThread().isInterrupted());
            System.out.println("  Thread continues normally (this might be wrong!)");
        });
        
        thread.start();
        
        // Interrupt the thread after a short delay
        try {
            Thread.sleep(100);
            thread.interrupt();
            thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println();
    }
    
    /**
     * Demonstrates what happens WITH Thread.currentThread().interrupt()
     */
    private static void testWithInterrupt() {
        System.out.println("Test 2: WITH Thread.currentThread().interrupt()");
        
        Thread thread = new Thread(() -> {
            try {
                // Simulate some work that might be interrupted
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("  Caught InterruptedException");
                // CORRECT: Restore the interrupt status
                Thread.currentThread().interrupt();
                System.out.println("  Interrupt status after calling interrupt(): " + Thread.currentThread().isInterrupted());
            }
            
            // Later in the code...
            System.out.println("  Checking interrupt status later: " + Thread.currentThread().isInterrupted());
            
            // Now we can properly handle the interruption
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("  Thread knows it was interrupted - handling gracefully");
                return; // Exit gracefully
            }
        });
        
        thread.start();
        
        // Interrupt the thread after a short delay
        try {
            Thread.sleep(100);
            thread.interrupt();
            thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println();
    }
    
    /**
     * Example showing why interrupt status matters in real applications
     */
    public static class ProperInterruptHandler {
        
        public void processData() throws InterruptedException {
            // Check interrupt status before starting work
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("Thread was interrupted before processing");
            }
            
            // Simulate processing
            for (int i = 0; i < 10; i++) {
                // Check interrupt status periodically
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException("Processing interrupted");
                }
                
                // Do some work
                Thread.sleep(100);
            }
        }
        
        public void handleException() {
            try {
                processData();
            } catch (InterruptedException e) {
                System.out.println("Processing was interrupted");
                // Restore interrupt status so calling code knows about it
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Example showing the H2O context
     */
    public static class H2OInterruptExample {
        
        public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
            try {
                // Simulate barrier await that might be interrupted
                Thread.sleep(1000);
                releaseHydrogen.run();
            } catch (InterruptedException e) {
                // CRITICAL: Restore interrupt status
                Thread.currentThread().interrupt();
                // This ensures that if this method is called from another method,
                // that method will also know about the interruption
            }
        }
        
        public void callingMethod() throws InterruptedException {
            try {
                hydrogen(() -> System.out.println("H"));
            } catch (InterruptedException e) {
                // This catch block will be reached if hydrogen() properly
                // restores the interrupt status
                System.out.println("Calling method knows about interruption");
                throw e; // Re-throw to propagate interruption
            }
        }
    }
} 