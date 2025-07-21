package companies.doordash;

import java.util.*;
import java.util.stream.Collectors;

public class TieBreakingTest {
    
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
    
    // User's original approach - exposes the bug by NOT doing final sort
    public List<Long> findClosestDashers_NoBugFix(Location restaurant, List<Dasher> dashers) {
        PriorityQueue<Dasher> maxHeap = new PriorityQueue<>(
                (d1, d2) -> {
                    double d1Distance = distance(restaurant, d1);
                    double d2Distance = distance(restaurant, d2);

                    if (d1Distance == d2Distance) {
                        return d1.rating - d2.rating;  // ❌ Wrong tie-breaking
                    }
                    return Double.compare(d2Distance, d1Distance);
                }
        );

        for (Dasher dasher: dashers) {
            maxHeap.offer(dasher);
            if (maxHeap.size() > 3) {
                maxHeap.poll();
            }
        }

        // Extract without sorting - this exposes the bug!
        List<Dasher> result = new ArrayList<>(maxHeap);
        return result.stream().map(dasher -> dasher.id).collect(Collectors.toList());
    }
    
    // Fixed version
    public List<Long> findClosestDashers_Fixed(Location restaurant, List<Dasher> dashers) {
        PriorityQueue<Dasher> maxHeap = new PriorityQueue<>(
                (d1, d2) -> {
                    double d1Distance = distance(restaurant, d1);
                    double d2Distance = distance(restaurant, d2);

                    if (d1Distance == d2Distance) {
                        return d2.rating - d1.rating;  // ✅ Fixed
                    }
                    return Double.compare(d2Distance, d1Distance);
                }
        );

        for (Dasher dasher: dashers) {
            maxHeap.offer(dasher);
            if (maxHeap.size() > 3) {
                maxHeap.poll();
            }
        }

        List<Dasher> result = new ArrayList<>(maxHeap);
        
        Collections.sort(result, (d1, d2) -> {
            double d1Distance = distance(restaurant, d1);
            double d2Distance = distance(restaurant, d2);

            if (d1Distance == d2Distance) {
                return d2.rating - d1.rating;
            }
            return Double.compare(d1Distance, d2Distance);
        });

        return result.stream().map(dasher -> dasher.id).collect(Collectors.toList());
    }
    
    private double distance(Location restaurant, Dasher dasher) {
        return distance(restaurant, dasher.lastLocation);
    }
    
    private double distance(Location loc1, Location loc2) {
        double deltaX = loc1.longitude - loc2.longitude;
        double deltaY = loc1.latitude - loc2.latitude;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
    
    public static void main(String[] args) {
        TieBreakingTest solution = new TieBreakingTest();
        
        Location restaurant = new Location(37.7749, -122.4194);
        
        // Test case: Multiple dashers with SAME distance but different ratings
        List<Dasher> testDashers = Arrays.asList(
            new Dasher(100, new Location(37.7749, -122.4194), 95),  // Same location, highest rating
            new Dasher(101, new Location(37.7749, -122.4194), 80),  // Same location, medium rating  
            new Dasher(102, new Location(37.7749, -122.4194), 70),  // Same location, lowest rating
            new Dasher(103, new Location(37.7849, -122.4094), 90),  // Different location
            new Dasher(104, new Location(37.7649, -122.4294), 60)   // Different location
        );
        
        System.out.println("=== Tie-Breaking Bug Exposure Test ===");
        System.out.println("All dashers 100, 101, 102 have SAME distance but different ratings");
        
        for (Dasher dasher : testDashers) {
            double dist = solution.distance(restaurant, dasher);
            System.out.printf("Dasher %d: distance=%.6f, rating=%d\n", 
                            dasher.id, dist, dasher.rating);
        }
        
        List<Long> buggyResult = solution.findClosestDashers_NoBugFix(restaurant, testDashers);
        List<Long> fixedResult = solution.findClosestDashers_Fixed(restaurant, testDashers);
        
        System.out.println("\nResults:");
        System.out.println("Buggy approach (no final sort): " + buggyResult);
        System.out.println("Fixed approach: " + fixedResult);
        System.out.println("Expected: [100, 101, 102] (by rating when distance is same)");
        
        System.out.println("\n" + (buggyResult.equals(fixedResult) ? "✅ Same result" : "❌ Different results - bug exposed!"));
    }
} 