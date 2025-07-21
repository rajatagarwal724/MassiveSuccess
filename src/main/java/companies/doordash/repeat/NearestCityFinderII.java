package companies.doordash.repeat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class NearestCityFinderII {

    class City {
        int x,y;
        String name;

        public City(int x, int y, String name) {
            this.x = x;
            this.y = y;
            this.name = name;
        }
    }

    public String[] findNearestCitiesBrute(String[] cities, int[] x, int[] y, String[] queries) {

        Map<String, City> cityMap = new HashMap<>();
        for (int i = 0; i < cities.length; i++) {
            cityMap.put(cities[i], new City(x[i], y[i], cities[i]));
        }
        String[] res = new String[queries.length];
        int resIdx = 0;
        for (String query: queries) {
            City city = cityMap.get(query);
            if (null == city) {
                res[resIdx++] = "NONE";
                continue;
            }
            int minDistance = Integer.MAX_VALUE;
            String nearestCity = "NONE";

            String cityName = city.name;
            int queryX = city.x;
            int queryY = city.y;

            for (Map.Entry<String, City> entry: cityMap.entrySet()) {
                if (entry.getKey().equals(cityName)) {
                    continue;
                }

                if (queryX == entry.getValue().x || queryY == entry.getValue().y) {

                    int distance = Math.abs(queryY - entry.getValue().y) + Math.abs(queryX - entry.getValue().x);
                    if (distance < minDistance
                            || (distance == minDistance && ("NONE".equals(nearestCity) || entry.getKey().compareTo(nearestCity) < 0))) {
                        minDistance = distance;
                        nearestCity = entry.getKey();
                    }
                }
            }
            res[resIdx++] = nearestCity;
        }

        return res;
    }

    public String[] findNearestCities(String[] cities, int[] x, int[] y, String[] queries) {
        Map<String, int[]> cityMap = new HashMap<>();
        TreeMap<Integer, TreeMap<Integer, TreeSet<String>>> citiesByX = new TreeMap<>();
        TreeMap<Integer, TreeMap<Integer, TreeSet<String>>> citiesByY = new TreeMap<>();

        for (int i = 0; i < cities.length; i++) {
            cityMap.put(cities[i], new int[] {x[i], y[i]});
            citiesByX.computeIfAbsent(x[i], s -> new TreeMap<>())
                    .computeIfAbsent(y[i], s -> new TreeSet<>()).add(cities[i]);
            citiesByY.computeIfAbsent(y[i], s -> new TreeMap<>())
                    .computeIfAbsent(x[i], s -> new TreeSet<>()).add(cities[i]);
        }
        String[] result = new String[queries.length];
        int resIdx = 0;
        for (String query: queries) {
            int[] queryCoordinates = cityMap.get(query);
            if (null == queryCoordinates) {
                result[resIdx++] = "NONE";
                continue;
            }
            String queryCity = query;
            int queryX = queryCoordinates[0];
            int queryY = queryCoordinates[1];

            String nearestCity = find(queryCity, queryX, queryY, citiesByX, citiesByY);
            result[resIdx++] = nearestCity;
        }

        return result;
    }

    private String find(String queryCity, int queryX, int queryY,
                        TreeMap<Integer, TreeMap<Integer, TreeSet<String>>> citiesByX,
                        TreeMap<Integer, TreeMap<Integer, TreeSet<String>>> citiesByY) {

        int minDistance = Integer.MAX_VALUE;
        String nearestCity = "NONE";

        // Check cities with same X coordinate
        if (citiesByX.containsKey(queryX)) {
            TreeMap<Integer, TreeSet<String>> citiesAtSameX = citiesByX.get(queryX);
            
            for (Map.Entry<Integer, TreeSet<String>> entry : citiesAtSameX.entrySet()) {
                int candidateY = entry.getKey();
                TreeSet<String> cities = entry.getValue();
                
                for (String city : cities) {
                    if (city.equals(queryCity)) {
                        continue;
                    }
                    
                    int distance = Math.abs(candidateY - queryY);
                    if (distance < minDistance || 
                        (distance == minDistance && city.compareTo(nearestCity) < 0)) {
                        minDistance = distance;
                        nearestCity = city;
                    }
                }
            }
        }

        // Check cities with same Y coordinate
        if (citiesByY.containsKey(queryY)) {
            TreeMap<Integer, TreeSet<String>> citiesAtSameY = citiesByY.get(queryY);
            
            for (Map.Entry<Integer, TreeSet<String>> entry : citiesAtSameY.entrySet()) {
                int candidateX = entry.getKey();
                TreeSet<String> cities = entry.getValue();
                
                for (String city : cities) {
                    if (city.equals(queryCity)) {
                        continue;
                    }
                    
                    int distance = Math.abs(candidateX - queryX);
                    if (distance < minDistance || 
                        (distance == minDistance && city.compareTo(nearestCity) < 0)) {
                        minDistance = distance;
                        nearestCity = city;
                    }
                }
            }
        }

        return nearestCity;
    }

    // Optimized solution using binary search
    public String[] findNearestCitiesOptimized(String[] cities, int[] x, int[] y, String[] queries) {
        Map<String, int[]> cityMap = new HashMap<>();
        TreeMap<Integer, TreeMap<Integer, TreeSet<String>>> citiesByX = new TreeMap<>();
        TreeMap<Integer, TreeMap<Integer, TreeSet<String>>> citiesByY = new TreeMap<>();

        // Build data structures
        for (int i = 0; i < cities.length; i++) {
            cityMap.put(cities[i], new int[] {x[i], y[i]});
            citiesByX.computeIfAbsent(x[i], s -> new TreeMap<>())
                    .computeIfAbsent(y[i], s -> new TreeSet<>()).add(cities[i]);
            citiesByY.computeIfAbsent(y[i], s -> new TreeMap<>())
                    .computeIfAbsent(x[i], s -> new TreeSet<>()).add(cities[i]);
        }

        String[] result = new String[queries.length];
        
        for (int i = 0; i < queries.length; i++) {
            int[] coords = cityMap.get(queries[i]);
            if (coords == null) {
                result[i] = "NONE";
                continue;
            }
            result[i] = findNearestOptimized(queries[i], coords[0], coords[1], citiesByX, citiesByY);
        }
        
        return result;
    }

    private String findNearestOptimized(String queryCity, int queryX, int queryY,
                                      TreeMap<Integer, TreeMap<Integer, TreeSet<String>>> citiesByX,
                                      TreeMap<Integer, TreeMap<Integer, TreeSet<String>>> citiesByY) {
        
        int minDistance = Integer.MAX_VALUE;
        String nearestCity = "NONE";
        
        // Check cities with same X coordinate using binary search
        TreeMap<Integer, TreeSet<String>> sameLongitude = citiesByX.get(queryX);
        if (sameLongitude != null) {
            nearestCity = findNearestInDimension(queryCity, queryY, sameLongitude, minDistance, nearestCity);
            if (!nearestCity.equals("NONE")) {
                minDistance = Math.abs(sameLongitude.floorKey(queryY) != null ? 
                    sameLongitude.floorKey(queryY) - queryY : 
                    sameLongitude.ceilingKey(queryY) - queryY);
            }
        }
        
        // Check cities with same Y coordinate using binary search  
        TreeMap<Integer, TreeSet<String>> sameLatitude = citiesByY.get(queryY);
        if (sameLatitude != null) {
            String candidate = findNearestInDimension(queryCity, queryX, sameLatitude, minDistance, nearestCity);
            if (!candidate.equals("NONE")) {
                nearestCity = candidate;
            }
        }
        
        return nearestCity;
    }
    
    private String findNearestInDimension(String queryCity, int queryCoord, 
                                        TreeMap<Integer, TreeSet<String>> cities,
                                        int currentMinDistance, String currentNearest) {
        
        String result = currentNearest;
        int minDistance = currentMinDistance;
        
        // Check closest coordinates using binary search
        Integer floor = cities.floorKey(queryCoord);
        Integer ceiling = cities.ceilingKey(queryCoord);
        
        // Check floor (lower coordinate)
        if (floor != null) {
            int distance = Math.abs(floor - queryCoord);
            if (distance < minDistance) {
                String candidate = getBestCityAtCoordinate(cities.get(floor), queryCity);
                if (candidate != null) {
                    minDistance = distance;
                    result = candidate;
                }
            }
        }
        
        // Check ceiling (higher coordinate)
        if (ceiling != null) {
            int distance = Math.abs(ceiling - queryCoord);
            if (distance < minDistance || 
                (distance == minDistance && !result.equals("NONE"))) {
                String candidate = getBestCityAtCoordinate(cities.get(ceiling), queryCity);
                if (candidate != null && 
                    (distance < minDistance || candidate.compareTo(result) < 0)) {
                    result = candidate;
                }
            }
        }
        
        return result;
    }
    
    private String getBestCityAtCoordinate(TreeSet<String> cities, String queryCity) {
        String best = null;
        for (String city : cities) {
            if (!city.equals(queryCity)) {
                if (best == null || city.compareTo(best) < 0) {
                    best = city;
                }
            }
        }
        return best;
    }


    public static void main(String[] args) {
        NearestCityFinderII sol = new NearestCityFinderII();
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
        String[] result1 = sol.findNearestCities(cities, xCoords, yCoords, queries);
        for (int i = 0; i < queries.length; i++) {
            System.out.println(String.format("Query: %s -> Nearest: %s", queries[i], result1[i]));
        }
        System.out.println();


        System.out.println("=== BRUTE FORCE APPROACH ===");
        String[] result2 = sol.findNearestCitiesBrute(cities, xCoords, yCoords, queries);
        for (int i = 0; i < queries.length; i++) {
            System.out.println(String.format("Query: %s -> Nearest: %s", queries[i], result2[i]));
        }
        System.out.println();

//        // Test optimized approach
//        System.out.println("=== OPTIMIZED APPROACH ===");
//        String[] result2 = findNearestCitiesOptimized(cities, xCoords, yCoords, queries);
//        for (int i = 0; i < queries.length; i++) {
//            System.out.println(String.format("Query: %s -> Nearest: %s", queries[i], result2[i]));
//        }
//        System.out.println();
//
//        // Test TreeMap approach
//        System.out.println("=== TREEMAP APPROACH ===");
//        String[] result3 = findNearestCitiesTreeMap(cities, xCoords, yCoords, queries);
//        for (int i = 0; i < queries.length; i++) {
//            System.out.println(String.format("Query: %s -> Nearest: %s", queries[i], result3[i]));
//        }
//        System.out.println();
//
//        // Verify all approaches give same results
//        boolean allMatch = Arrays.equals(result1, result2) && Arrays.equals(result2, result3);
//        System.out.println("All approaches match: " + allMatch);
    }
}
