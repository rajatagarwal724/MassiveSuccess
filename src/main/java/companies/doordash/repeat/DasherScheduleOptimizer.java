package companies.doordash.repeat;

import java.util.*;

/**
 * DoorDash Dasher Schedule Optimizer - Maximum Revenue Delivery Scheduling
 * 
 * Problem: Given a list of deliveries with start times, end times, and payments,
 * find the maximum amount of money a dasher can make by selecting non-overlapping
 * deliveries within their available time window.
 * 
 * This is a variant of the "Weighted Job Scheduling" problem.
 * 
 * Time Complexity: O(n log n) for sorting + O(n²) for DP = O(n²)
 * Space Complexity: O(n) for storing deliveries and DP array
 */
public class DasherScheduleOptimizer {
    
    static class Delivery {
        int start;
        int end;
        int pay;
        int index; // Original index for tracking
        
        public Delivery(int start, int end, int pay, int index) {
            this.start = start;
            this.end = end;
            this.pay = pay;
            this.index = index;
        }
        
        @Override
        public String toString() {
            return String.format("Delivery[%d: %d-%d, $%d]", index, start, end, pay);
        }
    }
    
    /**
     * Helper class to track both profit and selected deliveries in PQ approach
     */
    static class DeliveryState {
        int totalProfit;
        List<Delivery> selectedDeliveries;
        int lastEndTime;
        
        public DeliveryState(int totalProfit, List<Delivery> selectedDeliveries, int lastEndTime) {
            this.totalProfit = totalProfit;
            this.selectedDeliveries = new ArrayList<>(selectedDeliveries);
            this.lastEndTime = lastEndTime;
        }
        
        public DeliveryState() {
            this.totalProfit = 0;
            this.selectedDeliveries = new ArrayList<>();
            this.lastEndTime = -1;
        }
        
        public DeliveryState addDelivery(Delivery delivery) {
            List<Delivery> newSelected = new ArrayList<>(this.selectedDeliveries);
            newSelected.add(delivery);
            return new DeliveryState(this.totalProfit + delivery.pay, newSelected, delivery.end);
        }
    }
    
    /**
     * Priority Queue approach that returns both max profit and selected deliveries
     */
    public ScheduleResult maxRevenuePQWithDeliveries(int startTime, int endTime, 
                                                   int[] dStarts, int[] dEnds, int[] dPays) {
        if (dStarts == null || dStarts.length == 0) {
            return new ScheduleResult(0, new ArrayList<>());
        }

        // Create and filter valid deliveries
        List<Delivery> validDeliveries = new ArrayList<>();
        for (int i = 0; i < dStarts.length; i++) {
            if (dStarts[i] >= startTime && dEnds[i] <= endTime) {
                validDeliveries.add(new Delivery(dStarts[i], dEnds[i], dPays[i], i));
            }
        }

        if (validDeliveries.isEmpty()) {
            return new ScheduleResult(0, new ArrayList<>());
        }

        // Sort deliveries by start time for sweep line approach
        validDeliveries.sort(Comparator.comparingInt(d -> d.start));

        // Track the best state (profit + deliveries) at each point
        Map<Integer, DeliveryState> bestStateByEndTime = new HashMap<>();
        bestStateByEndTime.put(-1, new DeliveryState()); // Base case
        
        DeliveryState globalBest = new DeliveryState();

        for (Delivery delivery : validDeliveries) {
            // Find best non-conflicting state
            DeliveryState bestPrevious = new DeliveryState();
            
            for (Map.Entry<Integer, DeliveryState> entry : bestStateByEndTime.entrySet()) {
                if (entry.getKey() <= delivery.start && 
                    entry.getValue().totalProfit > bestPrevious.totalProfit) {
                    bestPrevious = entry.getValue();
                }
            }
            
            // Create new state by adding current delivery
            DeliveryState newState = bestPrevious.addDelivery(delivery);
            
            // Update best state for this end time
            DeliveryState existingState = bestStateByEndTime.get(delivery.end);
            if (existingState == null || newState.totalProfit > existingState.totalProfit) {
                bestStateByEndTime.put(delivery.end, newState);
            }
            
            // Update global best
            if (newState.totalProfit > globalBest.totalProfit) {
                globalBest = newState;
            }
        }

        return new ScheduleResult(globalBest.totalProfit, globalBest.selectedDeliveries);
    }
    
    /**
     * Original PQ method (only returns profit)
     */
    public int maxRevenuePQ(int startTime, int endTime, int[] dStarts, int[] dEnds, int[] dPays) {
        ScheduleResult result = maxRevenuePQWithDeliveries(startTime, endTime, dStarts, dEnds, dPays);
        return result.maxRevenue;
    }
    
    /**
     * Main method to find maximum revenue from delivery scheduling using DP
     */
    public int maxRevenue(int startTime, int endTime, int[] dStarts, int[] dEnds, int[] dPays) {
        if (dStarts == null || dStarts.length == 0) return 0;
        
        // Create and filter valid deliveries
        List<Delivery> validDeliveries = new ArrayList<>();
        for (int i = 0; i < dStarts.length; i++) {
            // Delivery must start after shift starts and end before shift ends
            if (dStarts[i] >= startTime && dEnds[i] <= endTime) {
                validDeliveries.add(new Delivery(dStarts[i], dEnds[i], dPays[i], i));
            }
        }
        
        if (validDeliveries.isEmpty()) return 0;
        
        // Sort deliveries by end time for optimal processing
        validDeliveries.sort(Comparator.comparingInt(d -> d.end));
        
        return solveWeightedJobScheduling(validDeliveries);
    }
    
    /**
     * Solve weighted job scheduling using dynamic programming
     */
    private int solveWeightedJobScheduling(List<Delivery> deliveries) {
        int n = deliveries.size();
        if (n == 0) return 0;
        if (n == 1) return deliveries.get(0).pay;
        
        // dp[i] = maximum revenue using deliveries 0..i
        int[] dp = new int[n];
        dp[0] = deliveries.get(0).pay;
        
        for (int i = 1; i < n; i++) {
            // Option 1: Don't take current delivery
            int withoutCurrent = dp[i - 1];
            
            // Option 2: Take current delivery
            int withCurrent = deliveries.get(i).pay;
            
            // Find the latest delivery that doesn't conflict with current
            int latestNonConflicting = findLatestNonConflicting(deliveries, i);
            if (latestNonConflicting != -1) {
                withCurrent += dp[latestNonConflicting];
            }
            
            dp[i] = Math.max(withoutCurrent, withCurrent);
        }
        
        return dp[n - 1];
    }
    
    /**
     * Enhanced DP version that returns both maximum revenue and selected deliveries
     */
    public ScheduleResult getOptimalScheduleDP(int startTime, int endTime, 
                                             int[] dStarts, int[] dEnds, int[] dPays) {
        if (dStarts == null || dStarts.length == 0) {
            return new ScheduleResult(0, new ArrayList<>());
        }
        
        // Create and filter valid deliveries
        List<Delivery> validDeliveries = new ArrayList<>();
        for (int i = 0; i < dStarts.length; i++) {
            if (dStarts[i] >= startTime && dEnds[i] <= endTime) {
                validDeliveries.add(new Delivery(dStarts[i], dEnds[i], dPays[i], i));
            }
        }
        
        if (validDeliveries.isEmpty()) {
            return new ScheduleResult(0, new ArrayList<>());
        }
        
        validDeliveries.sort(Comparator.comparingInt(d -> d.end));
        
        return solveWithTracking(validDeliveries);
    }
    
    private ScheduleResult solveWithTracking(List<Delivery> deliveries) {
        int n = deliveries.size();
        if (n == 0) return new ScheduleResult(0, new ArrayList<>());
        if (n == 1) return new ScheduleResult(deliveries.get(0).pay, Arrays.asList(deliveries.get(0)));
        
        int[] dp = new int[n];
        boolean[] take = new boolean[n]; // Whether to take delivery i
        
        dp[0] = deliveries.get(0).pay;
        take[0] = true;
        
        for (int i = 1; i < n; i++) {
            int withoutCurrent = dp[i - 1];
            int withCurrent = deliveries.get(i).pay;
            
            int latestNonConflicting = findLatestNonConflicting(deliveries, i);
            if (latestNonConflicting != -1) {
                withCurrent += dp[latestNonConflicting];
            }
            
            if (withCurrent > withoutCurrent) {
                dp[i] = withCurrent;
                take[i] = true;
            } else {
                dp[i] = withoutCurrent;
                take[i] = false;
            }
        }
        
        // Reconstruct solution
        List<Delivery> selectedDeliveries = new ArrayList<>();
        int i = n - 1;
        while (i >= 0) {
            if (take[i]) {
                selectedDeliveries.add(deliveries.get(i));
                i = findLatestNonConflicting(deliveries, i);
            } else {
                i--;
            }
        }
        
        Collections.reverse(selectedDeliveries);
        return new ScheduleResult(dp[n - 1], selectedDeliveries);
    }
    
    /**
     * Find the latest delivery that doesn't conflict with delivery at index i
     */
    private int findLatestNonConflicting(List<Delivery> deliveries, int i) {
        for (int j = i - 1; j >= 0; j--) {
            // No conflict if previous delivery ends before or when current starts
            if (deliveries.get(j).end <= deliveries.get(i).start) {
                return j;
            }
        }
        return -1;
    }
    
    /**
     * Result class for detailed schedule information
     */
    public static class ScheduleResult {
        public final int maxRevenue;
        public final List<Delivery> selectedDeliveries;
        
        public ScheduleResult(int maxRevenue, List<Delivery> selectedDeliveries) {
            this.maxRevenue = maxRevenue;
            this.selectedDeliveries = selectedDeliveries;
        }
        
        @Override
        public String toString() {
            return String.format("Max Revenue: $%d, Deliveries: %d", 
                               maxRevenue, selectedDeliveries.size());
        }
    }
    
    public static void main(String[] args) {
        DasherScheduleOptimizer optimizer = new DasherScheduleOptimizer();
        
        // Test case from problem
        System.out.println("=== DOORDASH DASHER SCHEDULE OPTIMIZER ===");
        
        int startTime = 0;
        int endTime = 10;
        int[] dStarts = {2, 3, 5, 7};
        int[] dEnds = {6, 5, 10, 11};
        int[] dPays = {5, 2, 4, 1};
        
        System.out.println("Input:");
        System.out.println("Shift time: " + startTime + " to " + endTime);
        System.out.println("Deliveries:");
        for (int i = 0; i < dStarts.length; i++) {
            System.out.println(String.format("  %d: [%d, %d] pays $%d", 
                             i, dStarts[i], dEnds[i], dPays[i]));
        }
        
        // Test basic DP approach
        int maxRevenue = optimizer.maxRevenue(startTime, endTime, dStarts, dEnds, dPays);
        System.out.println("\nMax Revenue (DP): $" + maxRevenue);
        
        // Test DP with delivery tracking
        ScheduleResult dpResult = optimizer.getOptimalScheduleDP(startTime, endTime, dStarts, dEnds, dPays);
        System.out.println("DP Result: " + dpResult);
        System.out.println("DP Selected deliveries:");
        for (Delivery d : dpResult.selectedDeliveries) {
            System.out.println("  " + d);
        }
        
        // Test PQ approach (original - profit only)
        int pqRevenue = optimizer.maxRevenuePQ(startTime, endTime, dStarts, dEnds, dPays);
        System.out.println("\nMax Revenue (PQ): $" + pqRevenue);
        
        // Test PQ approach with delivery tracking
        ScheduleResult pqResult = optimizer.maxRevenuePQWithDeliveries(startTime, endTime, dStarts, dEnds, dPays);
        System.out.println("PQ Result: " + pqResult);
        System.out.println("PQ Selected deliveries:");
        for (Delivery d : pqResult.selectedDeliveries) {
            System.out.println("  " + d);
        }
        
        // Verify all approaches give same max revenue
        System.out.println("\n=== VERIFICATION ===");
        System.out.println("DP Approach: $" + maxRevenue);
        System.out.println("DP with tracking: $" + dpResult.maxRevenue);
        System.out.println("PQ Approach: $" + pqRevenue);
        System.out.println("PQ with tracking: $" + pqResult.maxRevenue);
        
        boolean allMatch = (maxRevenue == dpResult.maxRevenue && 
                           maxRevenue == pqRevenue && 
                           maxRevenue == pqResult.maxRevenue);
        System.out.println("All approaches match: " + allMatch);
        
        // Additional test cases
        testAdditionalCases(optimizer);
    }
    
    private static void testAdditionalCases(DasherScheduleOptimizer optimizer) {
        System.out.println("\n=== ADDITIONAL TEST CASES ===");
        
        // Test case 2: Complex scenario
        System.out.println("\nTest 2: Complex overlapping deliveries");
        int[] starts2 = {1, 2, 4, 6, 8};
        int[] ends2 = {3, 5, 7, 9, 10};
        int[] pays2 = {20, 10, 15, 5, 8};
        
        ScheduleResult result2 = optimizer.maxRevenuePQWithDeliveries(0, 12, starts2, ends2, pays2);
        System.out.println("Result: " + result2);
        System.out.println("Selected deliveries:");
        for (Delivery d : result2.selectedDeliveries) {
            System.out.println("  " + d);
        }
        
        // Test case 3: No overlaps - should select all
        System.out.println("\nTest 3: Non-overlapping deliveries");
        int[] starts3 = {1, 3, 5};
        int[] ends3 = {2, 4, 6};
        int[] pays3 = {10, 15, 20};
        
        ScheduleResult result3 = optimizer.maxRevenuePQWithDeliveries(0, 10, starts3, ends3, pays3);
        System.out.println("Result: " + result3);
        System.out.println("Selected deliveries:");
        for (Delivery d : result3.selectedDeliveries) {
            System.out.println("  " + d);
        }
    }
}
