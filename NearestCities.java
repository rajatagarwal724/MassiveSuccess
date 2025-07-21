import java.util.*;

public class NearestCities {
    
    static class MinInfo {
        String city;
        double dist;
        
        MinInfo() {
            this.city = null;
            this.dist = Double.POSITIVE_INFINITY;
        }
    }
    
    public static List<String> findNearestCities(int[] xList, int[] yList, String[] cities, String[] queryCities) {
        Map<Integer, List<CityCoord>> xi = new HashMap<>();
        Map<Integer, List<CityCoord>> yi = new HashMap<>();
        Map<String, int[]> cityCoords = new HashMap<>();
        
        // Build the data structures
        for (int i = 0; i < cities.length; i++) {
            int x = xList[i];
            int y = yList[i];
            String city = cities[i];
            
            xi.computeIfAbsent(x, k -> new ArrayList<>()).add(new CityCoord(y, city));
            yi.computeIfAbsent(y, k -> new ArrayList<>()).add(new CityCoord(x, city));
            cityCoords.put(city, new int[]{x, y});
        }
        
        // Sort the lists
        for (List<CityCoord> list : xi.values()) {
            Collections.sort(list, (a, b) -> Integer.compare(a.coord, b.coord));
        }
        for (List<CityCoord> list : yi.values()) {
            Collections.sort(list, (a, b) -> Integer.compare(a.coord, b.coord));
        }
        
        List<String> ans = new ArrayList<>();
        for (String queryCity : queryCities) {
            if (!cityCoords.containsKey(queryCity)) {
                ans.add(null);
                continue;
            }
            
            int[] coords = cityCoords.get(queryCity);
            int x = coords[0];
            int y = coords[1];
            
            String nearestCity = getNearestCity(xi, yi, x, y, queryCity);
            ans.add(nearestCity);
        }
        
        return ans;
    }
    
    private static String getNearestCity(Map<Integer, List<CityCoord>> xi, 
                                       Map<Integer, List<CityCoord>> yi, 
                                       int x, int y, String queryCity) {
        MinInfo mins = new MinInfo();
        
        List<CityCoord> xCities = xi.get(x);
        if (xCities != null) {
            findMins(0, xCities.size() - 1, xCities, y, queryCity, mins);
        }
        
        List<CityCoord> yCities = yi.get(y);
        if (yCities != null) {
            findMins(0, yCities.size() - 1, yCities, x, queryCity, mins);
        }
        
        return mins.city;
    }
    
    private static void findMins(int left, int right, List<CityCoord> axisCities, 
                                int axisToCompare, String queryCity, MinInfo mins) {
        Random random = new Random();
        
        while (left <= right) {
            int mid = random.nextInt(right - left + 1) + left;
            String midCity = axisCities.get(mid).city;
            int midAxis = axisCities.get(mid).coord;
            
            if (midCity.equals(queryCity)) {
                // Check left neighbor
                if (mid > 0) {
                    String leftCity = axisCities.get(mid - 1).city;
                    int leftAxis = axisCities.get(mid - 1).coord;
                    int leftDist = Math.abs(axisToCompare - leftAxis);
                    updateMins(leftDist, leftCity, mins);
                }
                
                // Check right neighbor
                if (mid < axisCities.size() - 1) {
                    String rightCity = axisCities.get(mid + 1).city;
                    int rightAxis = axisCities.get(mid + 1).coord;
                    int rightDist = Math.abs(axisToCompare - rightAxis);
                    updateMins(rightDist, rightCity, mins);
                }
                
                break;
            }
            
            if (midAxis < axisToCompare) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
    }
    
    private static void updateMins(int dist, String city, MinInfo mins) {
        if (dist < mins.dist) {
            mins.dist = dist;
            mins.city = city;
        } else if (dist == mins.dist && mins.city != null) {
            mins.city = mins.city.compareTo(city) < 0 ? mins.city : city;
        }
    }
    
    static class CityCoord {
        int coord;
        String city;
        
        CityCoord(int coord, String city) {
            this.coord = coord;
            this.city = city;
        }
    }
    
    public static void main(String[] args) {
        String[] cities = {"axx", "axy", "az", "axd", "aa", "abc", "abs"};
        int[] xs = {0, 1, 2, 4, 5, 0, 1};
        int[] ys = {1, 2, 5, 3, 4, 2, 0};
        String[] queryCities = {"axx", "axy", "abs"};
        
        List<String> nearestCities = findNearestCities(xs, ys, cities, queryCities);
        System.out.println(nearestCities);
    }
} 