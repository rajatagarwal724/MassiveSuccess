package companies.doordash;

import java.util.*;

public class ClosestDashers {
    
    static class Dasher {
        long id;
        Location lastLocation;
        int rating;
        
        public Dasher(long id, Location lastLocation, int rating) {
            this.id = id;
            this.lastLocation = lastLocation;
            this.rating = rating;
        }
        
        @Override
        public String toString() {
            return String.format("Dasher{id=%d, location=(%.2f,%.2f), rating=%d}", 
                                id, lastLocation.longitude, lastLocation.latitude, rating);
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
    
    // Approach 1: Simple sorting - O(n log n)
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
    
    // Approach 2: Heap optimization - O(n log 3) = O(n)
    public List<Long> findClosestDashersOptimized(Location restaurant, List<Dasher> dashers) {
        // Max heap to keep track of 3 closest dashers
        // We use max heap so we can easily remove the farthest when heap size > 3
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
        
        // Extract results and sort properly (since heap doesn't guarantee order)
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
        
        // Return dasher IDs
        return result.stream().map(dd -> dd.dasher.id).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    // Calculate Euclidean distance between two locations
    private double calculateDistance(Location loc1, Location loc2) {
        double deltaX = loc1.longitude - loc2.longitude;
        double deltaY = loc1.latitude - loc2.latitude;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
    
    // Alternative: Haversine formula for geographical coordinates (more accurate)
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
    
    // Mock method to simulate GetDashers()
    public List<Dasher> getDashers() {
        return Arrays.asList(
            new Dasher(11, new Location(37.7749, -122.4194), 85),  // San Francisco
            new Dasher(14, new Location(37.7849, -122.4094), 90),  // Close to SF
            new Dasher(17, new Location(37.7649, -122.4294), 75),  // Also close to SF
            new Dasher(23, new Location(37.7949, -122.3994), 80),  // Another close one
            new Dasher(31, new Location(37.8049, -122.3894), 95),  // Highest rating
            new Dasher(42, new Location(40.7128, -74.0060), 85),   // NYC (far away)
            new Dasher(55, new Location(37.7749, -122.4194), 70)   // Same location as 11, lower rating
        );
    }
    
    public static void main(String[] args) {
        ClosestDashers solution = new ClosestDashers();
        
        // Restaurant location (San Francisco)
        Location restaurant = new Location(37.7749, -122.4194);
        List<Dasher> dashers = solution.getDashers();
        
        System.out.println("Restaurant location: (" + restaurant.longitude + ", " + restaurant.latitude + ")");
        System.out.println("\nAvailable dashers:");
        for (Dasher dasher : dashers) {
            double distance = solution.calculateDistance(restaurant, dasher.lastLocation);
            System.out.printf("Dasher %d: location=(%.4f,%.4f), rating=%d, distance=%.4f\n", 
                            dasher.id, dasher.lastLocation.longitude, dasher.lastLocation.latitude, 
                            dasher.rating, distance);
        }
        
        // Test both approaches
        System.out.println("\n=== Approach 1: Simple Sorting ===");
        List<Long> result1 = solution.findClosestDashers(restaurant, dashers);
        System.out.println("3 closest dashers: " + result1);
        
        System.out.println("\n=== Approach 2: Heap Optimization ===");
        List<Long> result2 = solution.findClosestDashersOptimized(restaurant, dashers);
        System.out.println("3 closest dashers: " + result2);
        
        // Test tie-breaking scenario
        System.out.println("\n=== Tie-Breaking Test ===");
        List<Dasher> tieBreakDashers = Arrays.asList(
            new Dasher(100, new Location(37.7749, -122.4194), 90),  // Same location as restaurant
            new Dasher(101, new Location(37.7749, -122.4194), 85),  // Same location as restaurant, lower rating
            new Dasher(102, new Location(37.7748, -122.4194), 95)   // Slightly farther, but highest rating
        );
        
        System.out.println("Tie-break dashers:");
        for (Dasher dasher : tieBreakDashers) {
            double distance = solution.calculateDistance(restaurant, dasher.lastLocation);
            System.out.printf("Dasher %d: location=(%.4f,%.4f), rating=%d, distance=%.6f\n", 
                            dasher.id, dasher.lastLocation.longitude, dasher.lastLocation.latitude, 
                            dasher.rating, distance);
        }
        
        List<Long> tieResult = solution.findClosestDashers(restaurant, tieBreakDashers);
        System.out.println("Tie-break result: " + tieResult);
        System.out.println("Expected order: [100, 101, 102] (distance tie-break by rating, then by distance)");
        
        // Debug the sorting process
        System.out.println("\nDebug sorting process:");
        List<DasherDistance> debugDistances = new ArrayList<>();
        for (Dasher dasher : tieBreakDashers) {
            double distance = solution.calculateDistance(restaurant, dasher.lastLocation);
            debugDistances.add(new DasherDistance(dasher, distance));
        }
        
        System.out.println("Before sorting:");
        for (DasherDistance dd : debugDistances) {
            System.out.printf("Dasher %d: distance=%.6f, rating=%d\n", 
                            dd.dasher.id, dd.distance, dd.dasher.rating);
        }
        
        debugDistances.sort((a, b) -> {
            int distanceCompare = Double.compare(a.distance, b.distance);
            if (distanceCompare != 0) {
                return distanceCompare;
            }
            // If distances are equal, prefer higher rating
            return Integer.compare(b.dasher.rating, a.dasher.rating);
        });
        
        System.out.println("After sorting:");
        for (DasherDistance dd : debugDistances) {
            System.out.printf("Dasher %d: distance=%.6f, rating=%d\n", 
                            dd.dasher.id, dd.distance, dd.dasher.rating);
        }
        
        // Test with clearly different distances
        System.out.println("\n=== Different Distance Test ===");
        List<Dasher> distanceDashers = Arrays.asList(
            new Dasher(200, new Location(37.7759, -122.4194), 80),  // Distance 0.001
            new Dasher(201, new Location(37.7749, -122.4194), 70),  // Distance 0.000 (same as restaurant)
            new Dasher(202, new Location(37.7769, -122.4194), 90)   // Distance 0.002
        );
        
        System.out.println("Distance test dashers:");
        for (Dasher dasher : distanceDashers) {
            double distance = solution.calculateDistance(restaurant, dasher.lastLocation);
            System.out.printf("Dasher %d: distance=%.6f, rating=%d\n", 
                            dasher.id, distance, dasher.rating);
        }
        
        List<Long> distanceResult = solution.findClosestDashers(restaurant, distanceDashers);
        System.out.println("Distance result: " + distanceResult);
        System.out.println("Expected: [201, 200, 202] (by distance)");
    }
} 