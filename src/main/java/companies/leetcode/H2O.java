package companies.leetcode;

import java.util.concurrent.Semaphore;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * H2O Problem: Synchronize oxygen and hydrogen threads to form water molecules
 * 
 * Problem: We have oxygen and hydrogen threads that need to form water molecules (H2O).
 * Each water molecule requires exactly 1 oxygen and 2 hydrogen atoms.
 * Threads must wait at a barrier until a complete molecule can be formed.
 * 
 * Solution: Use semaphores to control the number of each type of atom,
 * and a cyclic barrier to ensure all three atoms bond together.
 */
public class H2O {
    
    private Semaphore hydrogenSemaphore;
    private Semaphore oxygenSemaphore;
    private CyclicBarrier waterBarrier;
    
    public H2O() {
        // Allow 2 hydrogen atoms to proceed
        hydrogenSemaphore = new Semaphore(2);
        // Allow 1 oxygen atom to proceed
        oxygenSemaphore = new Semaphore(1);
        // Barrier for 3 atoms (2H + 1O) to form water molecule
        waterBarrier = new CyclicBarrier(3);
    }
    
    /**
     * Called by hydrogen thread
     */
    public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
        try {
            // Acquire hydrogen permit
            hydrogenSemaphore.acquire();
            
            // Wait at barrier for other atoms
            waterBarrier.await();
            
            // Release hydrogen atom
            releaseHydrogen.run();
            
        } catch (BrokenBarrierException e) {
            // Handle barrier broken exception
            Thread.currentThread().interrupt();
        } finally {
            // Release hydrogen permit after molecule is formed
            hydrogenSemaphore.release();
        }
    }
    
    /**
     * Called by oxygen thread
     */
    public void oxygen(Runnable releaseOxygen) throws InterruptedException {
        try {
            // Acquire oxygen permit
            oxygenSemaphore.acquire();
            
            // Wait at barrier for other atoms
            waterBarrier.await();
            
            // Release oxygen atom
            releaseOxygen.run();
            
        } catch (BrokenBarrierException e) {
            // Handle barrier broken exception
            Thread.currentThread().interrupt();
        } finally {
            // Release oxygen permit after molecule is formed
            oxygenSemaphore.release();
        }
    }
    
    /**
     * Alternative solution using only semaphores
     */
    public static class H2OAlternative {
        private Semaphore hydrogenSemaphore;
        private Semaphore oxygenSemaphore;
        private int hydrogenCount;
        private final Object lock = new Object();
        
        public H2OAlternative() {
            hydrogenSemaphore = new Semaphore(0);
            oxygenSemaphore = new Semaphore(0);
            hydrogenCount = 0;
        }
        
        public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
            synchronized (lock) {
                hydrogenCount++;
                if (hydrogenCount >= 2) {
                    // We have enough hydrogen, signal oxygen
                    oxygenSemaphore.release();
                    hydrogenCount -= 2;
                } else {
                    // Wait for more hydrogen
                    lock.wait();
                }
            }
            
            // Wait for oxygen
            oxygenSemaphore.acquire();
            
            // Release hydrogen
            releaseHydrogen.run();
            
            // Signal that hydrogen is released
            hydrogenSemaphore.release();
        }
        
        public void oxygen(Runnable releaseOxygen) throws InterruptedException {
            // Wait for hydrogen signals
            hydrogenSemaphore.acquire(2);
            
            // Release oxygen
            releaseOxygen.run();
            
            synchronized (lock) {
                // Signal waiting hydrogen threads
                lock.notifyAll();
            }
        }
    }
    
    /**
     * Test the H2O solution
     */
    public static void main(String[] args) {
        System.out.println("Testing H2O Solution...");
        
        // Test case 1: "HOH" -> should output "HHO" or similar
        testH2O("HOH");
        
        // Test case 2: "OOHHHH" -> should output "HHOHHO" or similar
        testH2O("OOHHHH");
    }
    
    private static void testH2O(String input) {
        System.out.println("\nInput: " + input);
        
        H2O h2o = new H2O();
        StringBuilder result = new StringBuilder();
        
        // Create threads based on input string
        Thread[] threads = new Thread[input.length()];
        
        for (int i = 0; i < input.length(); i++) {
            final int index = i;
            char atom = input.charAt(i);
            
            if (atom == 'H') {
                threads[i] = new Thread(() -> {
                    try {
                        h2o.hydrogen(() -> {
                            synchronized (result) {
                                result.append("H");
                                System.out.print("H");
                            }
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            } else if (atom == 'O') {
                threads[i] = new Thread(() -> {
                    try {
                        h2o.oxygen(() -> {
                            synchronized (result) {
                                result.append("O");
                                System.out.print("O");
                            }
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("\nResult: " + result.toString());
        
        // Validate result
        validateResult(result.toString());
    }
    
    private static void validateResult(String result) {
        if (result.length() % 3 != 0) {
            System.out.println("❌ Invalid: Result length is not divisible by 3");
            return;
        }
        
        boolean isValid = true;
        for (int i = 0; i < result.length(); i += 3) {
            String molecule = result.substring(i, Math.min(i + 3, result.length()));
            if (molecule.length() != 3) {
                isValid = false;
                break;
            }
            
            int hCount = 0, oCount = 0;
            for (char c : molecule.toCharArray()) {
                if (c == 'H') hCount++;
                else if (c == 'O') oCount++;
            }
            
            if (hCount != 2 || oCount != 1) {
                isValid = false;
                break;
            }
        }
        
        if (isValid) {
            System.out.println("✅ Valid: Each group of 3 contains 2H + 1O");
        } else {
            System.out.println("❌ Invalid: Some groups don't contain 2H + 1O");
        }
    }
} 