package companies.doordash.repeat;

import java.util.*;

/**
 * Find Nearest City with Same X or Y Coordinate
 * 
 * Problem: Given cities with coordinates, for each query city, find the nearest city
 * that shares the same x or y coordinate. If multiple cities have the same distance,
 * return the lexicographically smallest one.
 * 
 * Time Complexity: O(N * Q) where N = number of cities, Q = number of queries
 * Space Complexity: O(N) for storing city data
 * 
 * Optimized approaches:
 * 1. HashMap grouping by x and y coordinates: O(N + Q * avg_cities_per_coordinate)
 * 2. TreeMap for sorted access: O(N log N + Q * log N)
 */
public class NearestCityFinder {
    
    static class City {
        String name;
        int x, y;
        
        public City(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
        
        @Override
        public String toString() {
            return String.format("%s(%d,%d)", name, x, y);
        }
    }
    
    /**
     * Approach 1: Brute Force
     * For each query, check all cities and find the nearest one with same x or y
     * Time: O(Q * N), Space: O(N)
     */
    public static String[] findNearestCitiesBruteForce(String[] cities, int[] xCoords, int[] yCoords, String[] queries) {
        if (cities.length != xCoords.length || cities.length != yCoords.length) {
            throw new IllegalArgumentException("Arrays must have same length");
        }
        
        // Build city objects
        List<City> cityList = new ArrayList<>();
        Map<String, City> cityMap = new HashMap<>();
        
        for (int i = 0; i < cities.length; i++) {
            City city = new City(cities[i], xCoords[i], yCoords[i]);
            cityList.add(city);
            cityMap.put(cities[i], city);
        }
        
        String[] result = new String[queries.length];
        
        for (int q = 0; q < queries.length; q++) {
            String queryCity = queries[q];
            
            if (!cityMap.containsKey(queryCity)) {
                result[q] = "NONE";
                continue;
            }
            
            City query = cityMap.get(queryCity);
            String nearestCity = findNearestCity(query, cityList);
            result[q] = nearestCity;
        }
        
        return result;
    }
    
    private static String findNearestCity(City query, List<City> cities) {
        String nearestCity = "NONE";
        int minDistance = Integer.MAX_VALUE;
        
        for (City city : cities) {
            // Skip the query city itself
            if (city.name.equals(query.name)) continue;
            
            // Check if same x or y coordinate
            if (city.x == query.x || city.y == query.y) {
                int distance = Math.abs(city.x - query.x) + Math.abs(city.y - query.y);
                
                if (distance < minDistance || 
                    (distance == minDistance && (nearestCity.equals("NONE") || city.name.compareTo(nearestCity) < 0))) {
                    minDistance = distance;
                    nearestCity = city.name;
                }
            }
        }
        
        return nearestCity;
    }
    
    /**
     * Approach 2: Optimized with HashMap grouping
     * Group cities by x and y coordinates for faster lookup
     * Time: O(N + Q * avg_cities_per_coordinate), Space: O(N)
     */
    public static String[] findNearestCitiesOptimized(String[] cities, int[] xCoords, int[] yCoords, String[] queries) {
        if (cities.length != xCoords.length || cities.length != yCoords.length) {
            throw new IllegalArgumentException("Arrays must have same length");
        }
        
        // Build city objects and maps
        Map<String, City> cityMap = new HashMap<>();
        Map<Integer, List<City>> citiesByX = new HashMap<>();
        Map<Integer, List<City>> citiesByY = new HashMap<>();
        
        for (int i = 0; i < cities.length; i++) {
            City city = new City(cities[i], xCoords[i], yCoords[i]);
            cityMap.put(cities[i], city);
            
            // Group by x coordinate
            citiesByX.computeIfAbsent(xCoords[i], k -> new ArrayList<>()).add(city);
            
            // Group by y coordinate
            citiesByY.computeIfAbsent(yCoords[i], k -> new ArrayList<>()).add(city);
        }
        
        String[] result = new String[queries.length];
        
        for (int q = 0; q < queries.length; q++) {
            String queryCity = queries[q];
            
            if (!cityMap.containsKey(queryCity)) {
                result[q] = "NONE";
                continue;
            }
            
            City query = cityMap.get(queryCity);
            String nearestCity = findNearestCityOptimized(query, citiesByX, citiesByY);
            result[q] = nearestCity;
        }
        
        return result;
    }
    
    private static String findNearestCityOptimized(City query, Map<Integer, List<City>> citiesByX, Map<Integer, List<City>> citiesByY) {
        String nearestCity = "NONE";
        int minDistance = Integer.MAX_VALUE;
        
        // Check cities with same x coordinate
        if (citiesByX.containsKey(query.x)) {
            for (City city : citiesByX.get(query.x)) {
                if (city.name.equals(query.name)) continue;
                
                int distance = Math.abs(city.y - query.y);
                if (distance < minDistance || 
                    (distance == minDistance && (nearestCity.equals("NONE") || city.name.compareTo(nearestCity) < 0))) {
                    minDistance = distance;
                    nearestCity = city.name;
                }
            }
        }
        
        // Check cities with same y coordinate
        if (citiesByY.containsKey(query.y)) {
            for (City city : citiesByY.get(query.y)) {
                if (city.name.equals(query.name)) continue;
                
                int distance = Math.abs(city.x - query.x);
                if (distance < minDistance || 
                    (distance == minDistance && (nearestCity.equals("NONE") || city.name.compareTo(nearestCity) < 0))) {
                    minDistance = distance;
                    nearestCity = city.name;
                }
            }
        }
        
        return nearestCity;
    }
    
    /**
     * Approach 3: TreeMap for sorted coordinates
     * Use TreeMap to efficiently find nearest cities on same x or y line
     * Time: O(N log N + Q log N), Space: O(N)
     */
    public static String[] findNearestCitiesTreeMap(String[] cities, int[] xCoords, int[] yCoords, String[] queries) {
        if (cities.length != xCoords.length || cities.length != yCoords.length) {
            throw new IllegalArgumentException("Arrays must have same length");
        }
        
        // Build city objects and sorted maps
        Map<String, City> cityMap = new HashMap<>();
        TreeMap<Integer, TreeMap<Integer, Set<String>>> citiesByX = new TreeMap<>(); // x -> y -> cities
        TreeMap<Integer, TreeMap<Integer, Set<String>>> citiesByY = new TreeMap<>(); // y -> x -> cities
        
        for (int i = 0; i < cities.length; i++) {
            City city = new City(cities[i], xCoords[i], yCoords[i]);
            cityMap.put(cities[i], city);
            
            // Group by x coordinate, then by y coordinate
            citiesByX.computeIfAbsent(xCoords[i], k -> new TreeMap<>())
                    .computeIfAbsent(yCoords[i], k -> new TreeSet<>())
                    .add(cities[i]);
            
            // Group by y coordinate, then by x coordinate
            citiesByY.computeIfAbsent(yCoords[i], k -> new TreeMap<>())
                    .computeIfAbsent(xCoords[i], k -> new TreeSet<>())
                    .add(cities[i]);
        }
        
        String[] result = new String[queries.length];
        
        for (int q = 0; q < queries.length; q++) {
            String queryCity = queries[q];
            
            if (!cityMap.containsKey(queryCity)) {
                result[q] = "NONE";
                continue;
            }
            
            City query = cityMap.get(queryCity);
            String nearestCity = findNearestCityTreeMap(query, citiesByX, citiesByY);
            result[q] = nearestCity;
        }
        
        return result;
    }
    
    private static String findNearestCityTreeMap(City query, 
                                                TreeMap<Integer, TreeMap<Integer, Set<String>>> citiesByX,
                                                TreeMap<Integer, TreeMap<Integer, Set<String>>> citiesByY) {
        String nearestCity = "NONE";
        int minDistance = Integer.MAX_VALUE;
        
        // Check cities with same x coordinate
        if (citiesByX.containsKey(query.x)) {
            TreeMap<Integer, Set<String>> sameXCities = citiesByX.get(query.x);
            
            // Check cities at same coordinates first (distance 0)
            if (sameXCities.containsKey(query.y)) {
                Set<String> sameCities = sameXCities.get(query.y);
                for (String city : sameCities) {
                    if (!city.equals(query.name)) {
                        if (minDistance > 0 || (minDistance == 0 && city.compareTo(nearestCity) < 0)) {
                            minDistance = 0;
                            nearestCity = city;
                        }
                    }
                }
            }
            
            // Find closest cities above and below
            Map.Entry<Integer, Set<String>> lower = sameXCities.lowerEntry(query.y);
            Map.Entry<Integer, Set<String>> higher = sameXCities.higherEntry(query.y);
            
            if (lower != null) {
                int distance = query.y - lower.getKey();
                String bestCity = lower.getValue().iterator().next(); // Lexicographically smallest
                if (distance < minDistance || 
                    (distance == minDistance && (nearestCity.equals("NONE") || bestCity.compareTo(nearestCity) < 0))) {
                    minDistance = distance;
                    nearestCity = bestCity;
                }
            }
            
            if (higher != null) {
                int distance = higher.getKey() - query.y;
                String bestCity = higher.getValue().iterator().next(); // Lexicographically smallest
                if (distance < minDistance || 
                    (distance == minDistance && (nearestCity.equals("NONE") || bestCity.compareTo(nearestCity) < 0))) {
                    minDistance = distance;
                    nearestCity = bestCity;
                }
            }
        }
        
        // Check cities with same y coordinate
        if (citiesByY.containsKey(query.y)) {
            TreeMap<Integer, Set<String>> sameYCities = citiesByY.get(query.y);
            
            // Check cities at same coordinates first (distance 0) - already handled above
            
            // Find closest cities left and right
            Map.Entry<Integer, Set<String>> lower = sameYCities.lowerEntry(query.x);
            Map.Entry<Integer, Set<String>> higher = sameYCities.higherEntry(query.x);
            
            if (lower != null) {
                int distance = query.x - lower.getKey();
                String bestCity = lower.getValue().iterator().next(); // Lexicographically smallest
                if (distance < minDistance || 
                    (distance == minDistance && (nearestCity.equals("NONE") || bestCity.compareTo(nearestCity) < 0))) {
                    minDistance = distance;
                    nearestCity = bestCity;
                }
            }
            
            if (higher != null) {
                int distance = higher.getKey() - query.x;
                String bestCity = higher.getValue().iterator().next(); // Lexicographically smallest
                if (distance < minDistance || 
                    (distance == minDistance && (nearestCity.equals("NONE") || bestCity.compareTo(nearestCity) < 0))) {
                    minDistance = distance;
                    nearestCity = bestCity;
                }
            }
        }
        
        return nearestCity;
    }
    
    /**
     * Test all approaches with sample data
     */
    public static void main(String[] args) {
        // Sample data
        String[] cities = {"Atlanta", "Boston", "Chicago", "Denver", "Miami", "Seattle", "Austin", "Phoenix"};
        int[] xCoords = {1, 2, 3, 4, 5, 6, 1, 3};
        int[] yCoords = {1, 2, 3, 4, 5, 6, 7, 3};
        String[] queries = {"Atlanta", "Boston", "Chicago", "NonExistent"};
        
        System.out.println("=== NEAREST CITY FINDER ===");
        System.out.println("Cities and Coordinates:");
        for (int i = 0; i < cities.length; i++) {
            System.out.println(String.format("  %s: (%d, %d)", cities[i], xCoords[i], yCoords[i]));
        }
        System.out.println();
        
        // Test brute force approach
        System.out.println("=== BRUTE FORCE APPROACH ===");
        String[] result1 = findNearestCitiesBruteForce(cities, xCoords, yCoords, queries);
        for (int i = 0; i < queries.length; i++) {
            System.out.println(String.format("Query: %s -> Nearest: %s", queries[i], result1[i]));
        }
        System.out.println();
        
        // Test optimized approach
        System.out.println("=== OPTIMIZED APPROACH ===");
        String[] result2 = findNearestCitiesOptimized(cities, xCoords, yCoords, queries);
        for (int i = 0; i < queries.length; i++) {
            System.out.println(String.format("Query: %s -> Nearest: %s", queries[i], result2[i]));
        }
        System.out.println();
        
        // Test TreeMap approach
        System.out.println("=== TREEMAP APPROACH ===");
        String[] result3 = findNearestCitiesTreeMap(cities, xCoords, yCoords, queries);
        for (int i = 0; i < queries.length; i++) {
            System.out.println(String.format("Query: %s -> Nearest: %s", queries[i], result3[i]));
        }
        System.out.println();
        
        // Verify all approaches give same results
        boolean allMatch = Arrays.equals(result1, result2) && Arrays.equals(result2, result3);
        System.out.println("All approaches match: " + allMatch);
        
        // Performance test with larger dataset
        performanceTest();
    }
    
    private static void performanceTest() {
        System.out.println("\n=== PERFORMANCE TEST ===");
        
        // Generate larger dataset
        int n = 1000;
        String[] cities = new String[n];
        int[] xCoords = new int[n];
        int[] yCoords = new int[n];
        
        Random random = new Random(42);
        for (int i = 0; i < n; i++) {
            cities[i] = "City" + i;
            xCoords[i] = random.nextInt(100);
            yCoords[i] = random.nextInt(100);
        }
        
        String[] queries = new String[100];
        for (int i = 0; i < 100; i++) {
            queries[i] = "City" + (i * 10);
        }
        
        // Test brute force
        long start = System.nanoTime();
        String[] result1 = findNearestCitiesBruteForce(cities, xCoords, yCoords, queries);
        long time1 = System.nanoTime() - start;
        
        // Test optimized
        start = System.nanoTime();
        String[] result2 = findNearestCitiesOptimized(cities, xCoords, yCoords, queries);
        long time2 = System.nanoTime() - start;
        
        // Test TreeMap
        start = System.nanoTime();
        String[] result3 = findNearestCitiesTreeMap(cities, xCoords, yCoords, queries);
        long time3 = System.nanoTime() - start;
        
        System.out.println(String.format("Brute Force: %.2f ms", time1 / 1e6));
        System.out.println(String.format("Optimized:   %.2f ms", time2 / 1e6));
        System.out.println(String.format("TreeMap:     %.2f ms", time3 / 1e6));
        
        boolean allMatch = Arrays.equals(result1, result2) && Arrays.equals(result2, result3);
        System.out.println("Results match: " + allMatch);
    }
} 