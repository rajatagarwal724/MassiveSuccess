package src.main.java.coding.linkedin;

import java.util.*;

/**
 * Delivery Cost Tracking System
 * 
 * Part 1: Basic tracking with optimized getTotalCost()
 * Part 2: Payment settlement functionality  
 * Part 3: Simultaneous delivery detection
 */
public class DeliveryCostTrackingSystem {
    
    // Delivery class to represent individual deliveries
    static class Delivery {
        private final long startTime;
        private final long endTime;
        private final double cost;
        private final String driverId;
        private boolean isPaid;
        
        public Delivery(String driverId, long startTime, long endTime) {
            this.driverId = driverId;
            this.startTime = startTime;
            this.endTime = endTime;
            this.cost = calculateCost(startTime, endTime);
            this.isPaid = false;
        }
        
        private double calculateCost(long start, long end) {
            long duration = end - start; // in minutes/hours
            double baseCost = 5.0; // base delivery cost
            double timeCost = duration * 0.1; // cost per time unit
            return baseCost + timeCost;
        }
        
        // Getters
        public long getStartTime() { return startTime; }
        public long getEndTime() { return endTime; }
        public double getCost() { return cost; }
        public String getDriverId() { return driverId; }
        public boolean isPaid() { return isPaid; }
        public void markAsPaid() { this.isPaid = true; }
        
        @Override
        public String toString() {
            return String.format("Delivery[driver=%s, start=%d, end=%d, cost=%.2f, paid=%s]", 
                               driverId, startTime, endTime, cost, isPaid);
        }
    }
    
    // Driver class to manage driver information
    static class Driver {
        private final String driverId;
        private final List<Delivery> deliveries;
        
        public Driver(String driverId) {
            this.driverId = driverId;
            this.deliveries = new ArrayList<>();
        }
        
        public void addDelivery(Delivery delivery) {
            deliveries.add(delivery);
        }
        
        public List<Delivery> getDeliveries() {
            return deliveries;
        }
        
        public String getDriverId() {
            return driverId;
        }
    }
    
    // Main service class
    private final Map<String, Driver> drivers;
    private final List<Delivery> allDeliveries; // For efficient operations
    private double totalCost; // Optimized: computed on delivery addition
    private double totalPaidAmount;
    
    public DeliveryCostTrackingSystem() {
        this.drivers = new HashMap<>();
        this.allDeliveries = new ArrayList<>();
        this.totalCost = 0.0;
        this.totalPaidAmount = 0.0;
    }
    
    // ==================== PART 1: Basic Functionality ====================
    
    /**
     * Add a driver to the system
     * Time Complexity: O(1)
     */
    public void addDriver(String driverId) {
        if (!drivers.containsKey(driverId)) {
            drivers.put(driverId, new Driver(driverId));
            System.out.println("Driver " + driverId + " added successfully");
        } else {
            System.out.println("Driver " + driverId + " already exists");
        }
    }
    
    /**
     * Add delivery for a driver
     * Time Complexity: O(1) - optimized by computing cost immediately
     */
    public void addDelivery(String driverId, long startTime, long endTime) {
        if (!drivers.containsKey(driverId)) {
            throw new IllegalArgumentException("Driver " + driverId + " not found. Please add driver first.");
        }
        
        if (startTime >= endTime) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        
        Delivery delivery = new Delivery(driverId, startTime, endTime);
        drivers.get(driverId).addDelivery(delivery);
        allDeliveries.add(delivery);
        
        // Optimized: Update total cost immediately
        totalCost += delivery.getCost();
        
        System.out.printf("Delivery added for driver %s: %.2f cost%n", driverId, delivery.getCost());
    }
    
    /**
     * Get total cost of all deliveries
     * Time Complexity: O(1) - optimized by maintaining running total
     */
    public double getTotalCost() {
        return totalCost;
    }
    
    // ==================== PART 2: Payment Settlement ====================
    
    /**
     * Settle payments up to a specific time
     * Time Complexity: O(n) where n is number of deliveries
     */
    public double payUpToTime(long upToTime) {
        double amountToPay = 0.0;
        
        for (Delivery delivery : allDeliveries) {
            // Pay for deliveries that ended before or at upToTime and are not already paid
            if (delivery.getEndTime() <= upToTime && !delivery.isPaid()) {
                delivery.markAsPaid();
                amountToPay += delivery.getCost();
            }
        }
        
        totalPaidAmount += amountToPay;
        System.out.printf("Paid %.2f for deliveries up to time %d%n", amountToPay, upToTime);
        return amountToPay;
    }
    
    /**
     * Get remaining cost to be paid
     * Time Complexity: O(1) - optimized using total cost and paid amount
     */
    public double getCostToBePaid() {
        return totalCost - totalPaidAmount;
    }
    
    /**
     * Alternative O(n) implementation for getCostToBePaid if we don't maintain totalPaidAmount
     */
    public double getCostToBePaidAlternative() {
        double unpaidCost = 0.0;
        for (Delivery delivery : allDeliveries) {
            if (!delivery.isPaid()) {
                unpaidCost += delivery.getCost();
            }
        }
        return unpaidCost;
    }
    
    // ==================== PART 3: Simultaneous Deliveries ====================
    
    /**
     * Find simultaneous deliveries across all drivers
     * Time Complexity: O(n log n) for sorting + O(n) for overlap detection = O(n log n)
     */
    public List<List<Delivery>> findSimultaneousDeliveries() {
        List<List<Delivery>> simultaneousGroups = new ArrayList<>();
        
        if (allDeliveries.isEmpty()) {
            return simultaneousGroups;
        }
        
        // Sort deliveries by start time
        List<Delivery> sortedDeliveries = new ArrayList<>(allDeliveries);
        sortedDeliveries.sort(Comparator.comparingLong(Delivery::getStartTime));
        
        // Find overlapping deliveries
        int i = 0;
        while (i < sortedDeliveries.size()) {
            List<Delivery> currentGroup = new ArrayList<>();
            currentGroup.add(sortedDeliveries.get(i));
            
            long groupEndTime = sortedDeliveries.get(i).getEndTime();
            int j = i + 1;
            
            // Find all deliveries that overlap with current group
            while (j < sortedDeliveries.size() && 
                   sortedDeliveries.get(j).getStartTime() < groupEndTime) {
                
                currentGroup.add(sortedDeliveries.get(j));
                // Update group end time to the maximum end time in the group
                groupEndTime = Math.max(groupEndTime, sortedDeliveries.get(j).getEndTime());
                j++;
            }
            
            // Only add groups with more than one delivery (simultaneous)
            if (currentGroup.size() > 1) {
                simultaneousGroups.add(currentGroup);
            }
            
            i = j; // Move to next non-overlapping delivery
        }
        
        return simultaneousGroups;
    }
    
    /**
     * Enhanced version: Find simultaneous deliveries with different drivers only
     */
    public List<List<Delivery>> findSimultaneousDeliveriesAcrossDrivers() {
        List<List<Delivery>> allSimultaneous = findSimultaneousDeliveries();
        List<List<Delivery>> crossDriverSimultaneous = new ArrayList<>();
        
        for (List<Delivery> group : allSimultaneous) {
            Set<String> driversInGroup = new HashSet<>();
            for (Delivery delivery : group) {
                driversInGroup.add(delivery.getDriverId());
            }
            
            // Only include groups with multiple drivers
            if (driversInGroup.size() > 1) {
                crossDriverSimultaneous.add(group);
            }
        }
        
        return crossDriverSimultaneous;
    }
    
    // ==================== Utility Methods ====================
    
    public void printDriverSummary(String driverId) {
        Driver driver = drivers.get(driverId);
        if (driver == null) {
            System.out.println("Driver not found: " + driverId);
            return;
        }
        
        System.out.println("\n=== Driver Summary: " + driverId + " ===");
        double driverTotalCost = 0.0;
        double driverPaidCost = 0.0;
        
        for (Delivery delivery : driver.getDeliveries()) {
            System.out.println(delivery);
            driverTotalCost += delivery.getCost();
            if (delivery.isPaid()) {
                driverPaidCost += delivery.getCost();
            }
        }
        
        System.out.printf("Total Cost: %.2f, Paid: %.2f, Remaining: %.2f%n", 
                         driverTotalCost, driverPaidCost, driverTotalCost - driverPaidCost);
    }
    
    public void printSystemSummary() {
        System.out.println("\n=== System Summary ===");
        System.out.printf("Total Drivers: %d%n", drivers.size());
        System.out.printf("Total Deliveries: %d%n", allDeliveries.size());
        System.out.printf("Total Cost: %.2f%n", getTotalCost());
        System.out.printf("Total Paid: %.2f%n", totalPaidAmount);
        System.out.printf("Cost To Be Paid: %.2f%n", getCostToBePaid());
    }
    
    // ==================== Test Main Method ====================
    
    public static void main(String[] args) {
        DeliveryCostTrackingSystem system = new DeliveryCostTrackingSystem();
        
        System.out.println("========== PART 1: Basic Functionality ==========");
        
        // Add drivers
        system.addDriver("DRIVER001");
        system.addDriver("DRIVER002");
        system.addDriver("DRIVER003");
        
        // Add deliveries (startTime, endTime in minutes from midnight)
        system.addDelivery("DRIVER001", 100, 150);  // 50 minutes delivery
        system.addDelivery("DRIVER001", 200, 230);  // 30 minutes delivery
        system.addDelivery("DRIVER002", 120, 180);  // 60 minutes delivery (overlaps with DRIVER001)
        system.addDelivery("DRIVER002", 300, 350);  // 50 minutes delivery
        system.addDelivery("DRIVER003", 125, 145);  // 20 minutes delivery (overlaps)
        
        System.out.printf("Total Cost: %.2f%n", system.getTotalCost());
        
        System.out.println("\n========== PART 2: Payment Settlement ==========");
        
        // Pay for deliveries completed up to time 200
        double paidAmount1 = system.payUpToTime(200);
        System.out.printf("Remaining cost to be paid: %.2f%n", system.getCostToBePaid());
        
        // Pay for more deliveries up to time 300
        double paidAmount2 = system.payUpToTime(300);
        System.out.printf("Remaining cost to be paid: %.2f%n", system.getCostToBePaid());
        
        System.out.println("\n========== PART 3: Simultaneous Deliveries ==========");
        
        List<List<Delivery>> simultaneousDeliveries = system.findSimultaneousDeliveries();
        System.out.println("Simultaneous delivery groups:");
        for (int i = 0; i < simultaneousDeliveries.size(); i++) {
            System.out.printf("Group %d:%n", i + 1);
            for (Delivery delivery : simultaneousDeliveries.get(i)) {
                System.out.printf("  %s%n", delivery);
            }
        }
        
        List<List<Delivery>> crossDriverSimultaneous = system.findSimultaneousDeliveriesAcrossDrivers();
        System.out.println("\nSimultaneous deliveries across different drivers:");
        for (int i = 0; i < crossDriverSimultaneous.size(); i++) {
            System.out.printf("Cross-Driver Group %d:%n", i + 1);
            for (Delivery delivery : crossDriverSimultaneous.get(i)) {
                System.out.printf("  %s%n", delivery);
            }
        }
        
        // Final summary
        system.printSystemSummary();
        system.printDriverSummary("DRIVER001");
    }
} 