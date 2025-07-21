package companies.doordash.repeat;

import java.util.*;

/**
 * Generate all valid delivery order patterns where pickup must happen before delivery
 * 
 * Problem: Given n delivery tasks, generate all valid sequences of 2n operations
 * where each pickup Pi must occur before its corresponding delivery Di
 * 
 * Time Complexity: O((2n)! / (n+1)!) - This is the nth Catalan number multiplied by n!
 * Space Complexity: O(n) for recursion stack and tracking sets
 */
public class DeliveryOrderScheduler {

    /**
     * Generate all valid delivery patterns using backtracking
     * @param n number of delivery tasks
     * @return list of all valid order patterns
     */
    public List<String> getAllPatterns(int n) {
        Set<String> pickedUp = new HashSet<>();
        Set<String> delivered = new HashSet<>();
        List<String> patterns = new ArrayList<>();
        List<String> currentPattern = new ArrayList<>();
        
        findPattern(pickedUp, delivered, currentPattern, patterns, n);
        
        return patterns;
    }
    
    /**
     * Backtracking helper to generate valid patterns
     */
    private void findPattern(Set<String> pickedUp, Set<String> delivered, 
                           List<String> pattern, List<String> patterns, int n) {
        
        // Base case: pattern is complete
        if (pattern.size() == n * 2) {
            patterns.add(String.join("->", pattern));
            return;
        }
        
        // Try each task
        for (int task = 1; task <= n; task++) {
            String pickup = "P" + task;
            String delivery = "D" + task;
            
            // Option 1: Pick up task (if not already picked up)
            if (!pickedUp.contains(pickup)) {
                pickedUp.add(pickup);
                pattern.add(pickup);
                
                findPattern(pickedUp, delivered, pattern, patterns, n);
                
                // Backtrack
                pattern.remove(pattern.size() - 1);
                pickedUp.remove(pickup);
            }
            
            // Option 2: Deliver task (if picked up but not delivered)
            if (pickedUp.contains(pickup) && !delivered.contains(delivery)) {
                delivered.add(delivery);
                pattern.add(delivery);
                
                findPattern(pickedUp, delivered, pattern, patterns, n);
                
                // Backtrack
                pattern.remove(pattern.size() - 1);
                delivered.remove(delivery);
            }
        }
    }
    
    // /**
    //  * Optimized version using bit manipulation instead of sets
    //  */
    // public List<String> getAllPatternsOptimized(int n) {
    //     List<String> patterns = new ArrayList<>();
    //     List<String> currentPattern = new ArrayList<>();
        
    //     // Use bitmasks: bit i represents task i+1
    //     findPatternOptimized(0, 0, currentPattern, patterns, n);
        
    //     return patterns;
    // }
    
    // private void findPatternOptimized(int pickedUpMask, int deliveredMask,
    //                                 List<String> pattern, List<String> patterns, int n) {
        
    //     if (pattern.size() == n * 2) {
    //         patterns.add(String.join("->", pattern));
    //         return;
    //     }
        
    //     for (int task = 1; task <= n; task++) {
    //         int taskBit = 1 << (task - 1);
            
    //         // Option 1: Pick up task
    //         if ((pickedUpMask & taskBit) == 0) {
    //             pattern.add("P" + task);
    //             findPatternOptimized(pickedUpMask | taskBit, deliveredMask, pattern, patterns, n);
    //             pattern.remove(pattern.size() - 1);
    //         }
            
    //         // Option 2: Deliver task
    //         if ((pickedUpMask & taskBit) != 0 && (deliveredMask & taskBit) == 0) {
    //             pattern.add("D" + task);
    //             findPatternOptimized(pickedUpMask, deliveredMask | taskBit, pattern, patterns, n);
    //             pattern.remove(pattern.size() - 1);
    //         }
    //     }
    // }
    
    // /**
    //  * Mathematical approach: Count total valid patterns without generating them
    //  * The correct formula for n distinct pickup-delivery pairs is complex.
    //  * For now, we'll use empirical counting since the closed-form is non-trivial.
    //  */
    // public long countValidPatterns(int n) {
    //     // The actual formula for distinct pickup-delivery pairs is:
    //     // This is related to ballot numbers and involves complex combinatorics
    //     // For practical purposes, we generate and count
        
    //     // Known values for verification:
    //     long[] knownCounts = {0, 1, 6, 90, 2520, 113400}; // 0-indexed
        
    //     if (n < knownCounts.length) {
    //         return knownCounts[n];
    //     }
        
    //     // For larger n, we'd need the actual formula or generation
    //     return -1; // Indicate formula needs implementation
    // }
    
    // private long binomialCoeff(int n, int k) {
    //     long result = 1;
    //     for (int i = 0; i < k; i++) {
    //         result = result * (n - i) / (i + 1);
    //     }
    //     return result;
    // }
    
    // private long factorial(int n) {
    //     long result = 1;
    //     for (int i = 2; i <= n; i++) {
    //         result *= i;
    //     }
    //     return result;
    // }
    
    // /**
    //  * Generate patterns iteratively (non-recursive approach)
    //  */
    // public List<String> getAllPatternsIterative(int n) {
    //     List<String> patterns = new ArrayList<>();
        
    //     // Use stack to simulate recursion
    //     Stack<State> stack = new Stack<>();
    //     stack.push(new State(0, 0, new ArrayList<>()));
        
    //     while (!stack.isEmpty()) {
    //         State current = stack.pop();
            
    //         if (current.pattern.size() == n * 2) {
    //             patterns.add(String.join("->", current.pattern));
    //             continue;
    //         }
            
    //         // Try each task in reverse order (for consistent ordering)
    //         for (int task = n; task >= 1; task--) {
    //             int taskBit = 1 << (task - 1);
                
    //             // Option 1: Deliver task
    //             if ((current.pickedUpMask & taskBit) != 0 && 
    //                 (current.deliveredMask & taskBit) == 0) {
    //                 List<String> newPattern = new ArrayList<>(current.pattern);
    //                 newPattern.add("D" + task);
    //                 stack.push(new State(current.pickedUpMask, 
    //                                    current.deliveredMask | taskBit, 
    //                                    newPattern));
    //             }
                
    //             // Option 2: Pick up task
    //             if ((current.pickedUpMask & taskBit) == 0) {
    //                 List<String> newPattern = new ArrayList<>(current.pattern);
    //                 newPattern.add("P" + task);
    //                 stack.push(new State(current.pickedUpMask | taskBit, 
    //                                    current.deliveredMask, 
    //                                    newPattern));
    //             }
    //         }
    //     }
        
    //     return patterns;
    // }
    
    // // Helper class for iterative approach
    // private static class State {
    //     int pickedUpMask;
    //     int deliveredMask;
    //     List<String> pattern;
        
    //     State(int pickedUpMask, int deliveredMask, List<String> pattern) {
    //         this.pickedUpMask = pickedUpMask;
    //         this.deliveredMask = deliveredMask;
    //         this.pattern = pattern;
    //     }
    // }
    
    public static void main(String[] args) {
        DeliveryOrderScheduler scheduler = new DeliveryOrderScheduler();
        
        System.out.println("=== DELIVERY ORDER SCHEDULER ===");
        
        for (int n = 1; n <= 3; n++) {
            System.out.println(String.format("\n--- n = %d ---", n));
            List<String> patterns = scheduler.getAllPatterns(n);
            System.out.println(String.format("Total patterns: %d", patterns.size()));
            
            for (String pattern : patterns) {
                System.out.println(pattern);
            }
            
            // Verify count
            // long expectedCount = scheduler.countValidPatterns(n);
            // System.out.println(String.format("Mathematical count: %d", expectedCount));
            // System.out.println(String.format("Match: %s", patterns.size() == expectedCount));
        }
        
        // Performance test
        // System.out.println("\n=== PERFORMANCE TEST ===");
        // for (int n = 1; n <= 4; n++) {
        //     long start = System.currentTimeMillis();
        //     List<String> patterns = scheduler.getAllPatternsOptimized(n);
        //     long end = System.currentTimeMillis();
            
        //     System.out.println(String.format("n=%d: %d patterns in %d ms", 
        //                                     n, patterns.size(), end - start));
        // }
    }
} 