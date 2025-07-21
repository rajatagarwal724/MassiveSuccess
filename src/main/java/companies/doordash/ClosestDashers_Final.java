package companies.doordash;

import java.util.*;

/**
 * DoorDash Interview Question: Find 3 Closest Dashers
 * 
 * Given a restaurant geolocation (longitude/latitude), find 3 closest Dashers 
 * who can be assigned for delivery, ordered by their distance from the restaurant.
 * In case 2 Dashers are equidistant, use Dasher rating as tie breaker (higher rating wins).
 * 
 * Time Complexity: 
 * - Approach 1 (Sorting): O(n log n)
 * - Approach 2 (Heap): O(n log k) where k=3, so O(n)
 * 
 * Space Complexity: O(n) for approach 1, O(k) for approach 2
 */
public class ClosestDashers_Final {
    
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
    
    /**
     * Approach 1: Simple Sorting
     * Time: O(n log n), Space: O(n)
     * Best for: Small datasets, simple implementation
     */
    public List<Long> findClosestDashers(Location restaurant, List<Dasher> dashers) {
        List<DasherDistance> dasherDistances = new ArrayList<>();
        
        // Calculate distances for all dashers
        for (Dasher dasher : dashers) {
            double distance = calculateDistance(restaurant, dasher.lastLocation);
            dasherDistances.add(new DasherDistance(dasher, distance));
        }
        
        // Sort by distance (ascending), then by rating (descending) for tie-breaking
        dasherDistances.sort((a, b) -> {
            int distanceCompare = Double.compare(a.distance, b.distance);
            if (distanceCompare != 0) {
                return distanceCompare;
            }
            // If distances are equal, prefer higher rating
            return Integer.compare(b.dasher.rating, a.dasher.rating);
        });
        
        // Return first 3 dasher IDs
        List<Long> result = new ArrayList<>();
        for (int i = 0; i < Math.min(3, dasherDistances.size()); i++) {
            result.add(dasherDistances.get(i).dasher.id);
        }
        
        return result;
    }
    
    /**
     * Approach 2: Heap Optimization
     * Time: O(n log k) = O(n), Space: O(k) = O(1)
     * Best for: Large datasets, memory-efficient
     */
    public List<Long> findClosestDashersOptimized(Location restaurant, List<Dasher> dashers) {
        // Max heap to keep track of 3 closest dashers
        PriorityQueue<DasherDistance> maxHeap = new PriorityQueue<>((a, b) -> {
            int distanceCompare = Double.compare(b.distance, a.distance); // Max heap by distance
            if (distanceCompare != 0) {
                return distanceCompare;
            }
            // If distances are equal, remove lower rating (keep higher rating)
            return Integer.compare(a.dasher.rating, b.dasher.rating);
        });
        
        for (Dasher dasher : dashers) {
            double distance = calculateDistance(restaurant, dasher.lastLocation);
            maxHeap.offer(new DasherDistance(dasher, distance));
            
            // Keep only 3 closest dashers
            if (maxHeap.size() > 3) {
                maxHeap.poll();
            }
        }
        
        // Extract results and sort properly
        List<DasherDistance> result = new ArrayList<>();
        while (!maxHeap.isEmpty()) {
            result.add(maxHeap.poll());
        }
        
        // Sort by distance (ascending), then by rating (descending)
        result.sort((a, b) -> {
            int distanceCompare = Double.compare(a.distance, b.distance);
            if (distanceCompare != 0) {
                return distanceCompare;
            }
            return Integer.compare(b.dasher.rating, a.dasher.rating);
        });
        
        return result.stream().map(dd -> dd.dasher.id).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Calculate Euclidean distance between two locations
     * For real-world applications, consider using Haversine formula for geographical accuracy
     */
    private double calculateDistance(Location loc1, Location loc2) {
        double deltaX = loc1.longitude - loc2.longitude;
        double deltaY = loc1.latitude - loc2.latitude;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
    
    /**
     * Haversine formula for calculating distance between two geographical points
     * More accurate for real-world GPS coordinates
     */
    private double calculateHaversineDistance(Location loc1, Location loc2) {
        final double R = 6371; // Earth's radius in kilometers
        
        double lat1Rad = Math.toRadians(loc1.latitude);
        double lat2Rad = Math.toRadians(loc2.latitude);
        double deltaLatRad = Math.toRadians(loc2.latitude - loc1.latitude);
        double deltaLngRad = Math.toRadians(loc2.longitude - loc1.longitude);
        
        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLngRad / 2) * Math.sin(deltaLngRad / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distance in kilometers
    }
    
    public static void main(String[] args) {
        ClosestDashers_Final solution = new ClosestDashers_Final();
        
        // Test data
        Location restaurant = new Location(37.7749, -122.4194); // San Francisco
        List<Dasher> dashers = Arrays.asList(
            new Dasher(11, new Location(37.7749, -122.4194), 85),  // Same location, high rating
            new Dasher(14, new Location(37.7849, -122.4094), 90),  // Close, highest rating
            new Dasher(17, new Location(37.7649, -122.4294), 75),  // Close, lower rating
            new Dasher(23, new Location(37.7949, -122.3994), 80),  // Medium distance
            new Dasher(31, new Location(37.8049, -122.3894), 95),  // Farther but excellent rating
            new Dasher(42, new Location(40.7128, -74.0060), 85),   // NYC (very far)
            new Dasher(55, new Location(37.7749, -122.4194), 70)   // Same location, lower rating
        );
        
        System.out.println("=== DoorDash: Find 3 Closest Dashers ===");
        System.out.println("Restaurant: (" + restaurant.longitude + ", " + restaurant.latitude + ")");
        
        // Test both approaches
        List<Long> result1 = solution.findClosestDashers(restaurant, dashers);
        List<Long> result2 = solution.findClosestDashersOptimized(restaurant, dashers);
        
        System.out.println("\nApproach 1 (Sorting): " + result1);
        System.out.println("Approach 2 (Heap): " + result2);
        
        // Verify results match
        System.out.println("Results match: " + result1.equals(result2));
        
        // Show detailed breakdown
        System.out.println("\nDetailed breakdown:");
        List<DasherDistance> detailed = new ArrayList<>();
        for (Dasher dasher : dashers) {
            double distance = solution.calculateDistance(restaurant, dasher.lastLocation);
            detailed.add(new DasherDistance(dasher, distance));
        }
        
        detailed.sort((a, b) -> {
            int distanceCompare = Double.compare(a.distance, b.distance);
            if (distanceCompare != 0) {
                return distanceCompare;
            }
            return Integer.compare(b.dasher.rating, a.dasher.rating);
        });
        
        System.out.println("Top 3 closest dashers:");
        for (int i = 0; i < Math.min(3, detailed.size()); i++) {
            DasherDistance dd = detailed.get(i);
            System.out.printf("%d. Dasher %d: distance=%.6f, rating=%d\n", 
                            i + 1, dd.dasher.id, dd.distance, dd.dasher.rating);
        }
    }
} 