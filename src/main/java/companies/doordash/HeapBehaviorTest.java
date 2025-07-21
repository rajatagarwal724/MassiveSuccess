package companies.doordash;

import java.util.*;

public class HeapBehaviorTest {
    
    static class Dasher {
        long id;
        Location lastLocation;
        int rating;
        
        public Dasher(long id, Location lastLocation, int rating) {
            this.id = id;
            this.lastLocation = lastLocation;
            this.rating = rating;
        }
    }
    
    static class Location {
        double longitude;
        double latitude;
        
        Location(double longitude, double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }
    }
    
    static class DasherDistance {
        Dasher dasher;
        double distance;
        
        DasherDistance(Dasher dasher, double distance) {
            this.dasher = dasher;
            this.distance = distance;
        }
    }
    
    // WRONG heap logic
    public List<Long> wrongHeapLogic(Location restaurant, List<Dasher> dashers) {
        PriorityQueue<DasherDistance> maxHeap = new PriorityQueue<>((a, b) -> {
            int distanceCompare = Double.compare(b.distance, a.distance);
            if (distanceCompare != 0) {
                return distanceCompare;
            }
            // WRONG: This keeps lower-rated dashers in heap
            return Integer.compare(a.dasher.rating, b.dasher.rating);
        });

        for (Dasher dasher : dashers) {
            double dist = calculateDistance(restaurant, dasher.lastLocation);
            maxHeap.offer(new DasherDistance(dasher, dist));
            
            if (maxHeap.size() > 3) {
                DasherDistance removed = maxHeap.poll();
                System.out.println("WRONG logic removed: Dasher " + removed.dasher.id + 
                                 " (rating=" + removed.dasher.rating + ")");
            }
        }

        // Extract without final sorting to see heap behavior
        List<Long> result = new ArrayList<>();
        while (!maxHeap.isEmpty()) {
            result.add(maxHeap.poll().dasher.id);
        }
        return result;
    }
    
    // CORRECT heap logic
    public List<Long> correctHeapLogic(Location restaurant, List<Dasher> dashers) {
        PriorityQueue<DasherDistance> maxHeap = new PriorityQueue<>((a, b) -> {
            int distanceCompare = Double.compare(b.distance, a.distance);
            if (distanceCompare != 0) {
                return distanceCompare;
            }
            // CORRECT: This keeps higher-rated dashers in heap
            return Integer.compare(b.dasher.rating, a.dasher.rating);
        });

        for (Dasher dasher : dashers) {
            double dist = calculateDistance(restaurant, dasher.lastLocation);
            maxHeap.offer(new DasherDistance(dasher, dist));
            
            if (maxHeap.size() > 3) {
                DasherDistance removed = maxHeap.poll();
                System.out.println("CORRECT logic removed: Dasher " + removed.dasher.id + 
                                 " (rating=" + removed.dasher.rating + ")");
            }
        }

        // Extract without final sorting to see heap behavior
        List<Long> result = new ArrayList<>();
        while (!maxHeap.isEmpty()) {
            result.add(maxHeap.poll().dasher.id);
        }
        return result;
    }
    
    private double calculateDistance(Location loc1, Location loc2) {
        double deltaX = loc1.longitude - loc2.longitude;
        double deltaY = loc1.latitude - loc2.latitude;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
    
    public static void main(String[] args) {
        HeapBehaviorTest test = new HeapBehaviorTest();
        
        Location restaurant = new Location(37.7749, -122.4194);
        
        // Test case: All dashers at SAME location but different ratings
        List<Dasher> testDashers = Arrays.asList(
            new Dasher(100, new Location(37.7749, -122.4194), 95),  // Highest rating
            new Dasher(101, new Location(37.7749, -122.4194), 85),  // Medium rating  
            new Dasher(102, new Location(37.7749, -122.4194), 75),  // Lowest rating
            new Dasher(103, new Location(37.7749, -122.4194), 90)   // Second highest
        );
        
        System.out.println("=== Heap Behavior Test ===");
        System.out.println("All dashers have SAME distance, different ratings");
        System.out.println("We want to keep the TOP 3 highest-rated dashers: 100(95), 103(90), 101(85)");
        System.out.println("We should remove dasher 102(75) - the lowest rated");
        System.out.println();
        
        System.out.println("--- WRONG Heap Logic ---");
        List<Long> wrongResult = test.wrongHeapLogic(restaurant, testDashers);
        System.out.println("Final result: " + wrongResult);
        System.out.println();
        
        System.out.println("--- CORRECT Heap Logic ---");
        List<Long> correctResult = test.correctHeapLogic(restaurant, testDashers);
        System.out.println("Final result: " + correctResult);
        System.out.println();
        
        System.out.println("Expected behavior: Remove dasher 102 (lowest rating=75)");
        System.out.println("Keep dashers: 100(95), 103(90), 101(85)");
    }
} 