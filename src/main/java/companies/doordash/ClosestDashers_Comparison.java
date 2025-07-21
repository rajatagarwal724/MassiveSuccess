package companies.doordash;

import java.util.*;
import java.util.stream.Collectors;

public class ClosestDashers_Comparison {
    
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
    
    // User's original approach (with issues)
    public List<Long> findClosestDashers_Original(Location restaurant, List<Dasher> dashers) {
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

        List<Dasher> result = new ArrayList<>(maxHeap);

        Collections.sort(result, (d1, d2) -> {
            double d1Distance = distance(restaurant, d1);
            double d2Distance = distance(restaurant, d2);

            if (d1Distance == d2Distance) {
                return d2.rating - d1.rating;  // ✅ Correct tie-breaking
            }
            return Double.compare(d1Distance, d2Distance);
        });

        return result.stream().map(dasher -> dasher.id).collect(Collectors.toList());
    }
    
    // Fixed version of user's approach
    public List<Long> findClosestDashers_Fixed(Location restaurant, List<Dasher> dashers) {
        PriorityQueue<Dasher> maxHeap = new PriorityQueue<>(
                (d1, d2) -> {
                    double d1Distance = distance(restaurant, d1);
                    double d2Distance = distance(restaurant, d2);

                    if (d1Distance == d2Distance) {
                        return d2.rating - d1.rating;  // ✅ Fixed: higher rating stays in heap
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
    
    // Optimized approach: Pre-calculate distances
    static class DasherDistance {
        Dasher dasher;
        double distance;
        
        DasherDistance(Dasher dasher, double distance) {
            this.dasher = dasher;
            this.distance = distance;
        }
    }
    
    public List<Long> findClosestDashers_Optimized(Location restaurant, List<Dasher> dashers) {
        PriorityQueue<DasherDistance> maxHeap = new PriorityQueue<>((a, b) -> {
            int distanceCompare = Double.compare(b.distance, a.distance); // Max heap by distance
            if (distanceCompare != 0) {
                return distanceCompare;
            }
            // If distances are equal, remove lower rating (keep higher rating)
            return Integer.compare(a.dasher.rating, b.dasher.rating);
        });

        for (Dasher dasher : dashers) {
            double dist = distance(restaurant, dasher); // Calculate once!
            maxHeap.offer(new DasherDistance(dasher, dist));
            
            if (maxHeap.size() > 3) {
                maxHeap.poll();
            }
        }

        // Extract and sort properly
        List<DasherDistance> result = new ArrayList<>();
        while (!maxHeap.isEmpty()) {
            result.add(maxHeap.poll());
        }

        result.sort((a, b) -> {
            int distanceCompare = Double.compare(a.distance, b.distance);
            if (distanceCompare != 0) {
                return distanceCompare;
            }
            return Integer.compare(b.dasher.rating, a.dasher.rating);
        });

        return result.stream().map(dd -> dd.dasher.id).collect(Collectors.toList());
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
        ClosestDashers_Comparison solution = new ClosestDashers_Comparison();
        
        Location restaurant = new Location(37.7749, -122.4194);
        List<Dasher> dashers = Arrays.asList(
            new Dasher(11, new Location(37.7749, -122.4194), 85),  // Same location, rating 85
            new Dasher(55, new Location(37.7749, -122.4194), 70),  // Same location, rating 70  
            new Dasher(14, new Location(37.7849, -122.4094), 90),  // Close, rating 90
            new Dasher(17, new Location(37.7649, -122.4294), 75),  // Same distance as 14, rating 75
            new Dasher(23, new Location(37.7949, -122.3994), 80)   // Farther
        );
        
        System.out.println("=== Comparison of Approaches ===");
        
        List<Long> original = solution.findClosestDashers_Original(restaurant, dashers);
        List<Long> fixed = solution.findClosestDashers_Fixed(restaurant, dashers);
        List<Long> optimized = solution.findClosestDashers_Optimized(restaurant, dashers);
        
        System.out.println("Original (with bugs): " + original);
        System.out.println("Fixed version: " + fixed);
        System.out.println("Optimized version: " + optimized);
        
        System.out.println("\nExpected order analysis:");
        System.out.println("1. Dasher 11 (distance=0.000000, rating=85) - closest, higher rating");
        System.out.println("2. Dasher 55 (distance=0.000000, rating=70) - closest, lower rating");
        System.out.println("3. Dasher 14 (distance=0.014142, rating=90) - next closest");
        
        // Performance comparison
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            solution.findClosestDashers_Fixed(restaurant, dashers);
        }
        long fixedTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            solution.findClosestDashers_Optimized(restaurant, dashers);
        }
        long optimizedTime = System.nanoTime() - startTime;
        
        System.out.printf("\nPerformance (1000 runs):\n");
        System.out.printf("Fixed approach: %.2f ms\n", fixedTime / 1_000_000.0);
        System.out.printf("Optimized approach: %.2f ms\n", optimizedTime / 1_000_000.0);
        System.out.printf("Speedup: %.2fx\n", (double) fixedTime / optimizedTime);
    }
} 