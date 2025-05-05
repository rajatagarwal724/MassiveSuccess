import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityTripPlanner {
    private final Map<String, List<Integer>> cityPrices;
    private final int daysPerCity;
    private final int maxBudget;

    public CityTripPlanner(Map<String, List<Integer>> cityPrices, int daysPerCity, int maxBudget) {
        this.cityPrices = cityPrices;
        this.daysPerCity = daysPerCity;
        this.maxBudget = maxBudget;
    }

    public List<List<Integer>> findPossibleTrips() {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> currentTrip = new ArrayList<>();
        List<String> cities = new ArrayList<>(cityPrices.keySet());
        
        backtrack(cities, 0, currentTrip, result);
        return result;
    }

    private void backtrack(
        List<String> cities,
        int currentCityIndex,
        List<Integer> currentTrip,
        List<List<Integer>> result
    ) {
        // Base case: if we've processed all cities
        if (currentCityIndex == cities.size()) {
            result.add(new ArrayList<>(currentTrip));
            return;
        }

        String currentCity = cities.get(currentCityIndex);
        List<Integer> prices = cityPrices.get(currentCity);

        // For each possible price in the current city
        for (int price : prices) {
            // Calculate the total cost if we add this price
            int totalCost = currentTrip.stream().mapToInt(Integer::intValue).sum() + price;
            
            // If adding this price would exceed the budget, skip it
            if (totalCost > maxBudget) {
                continue;
            }

            // Add the price to the current trip
            currentTrip.add(price);
            
            // Recursively process the next city
            backtrack(cities, currentCityIndex + 1, currentTrip, result);
            
            // Backtrack: remove the last price to try other combinations
            currentTrip.remove(currentTrip.size() - 1);
        }
    }

    public static void main(String[] args) {
        // Example usage
        Map<String, List<Integer>> cityPrices = new HashMap<>();
        cityPrices.put("Paris", List.of(100, 150));
        cityPrices.put("London", List.of(200, 250));
        
        CityTripPlanner planner = new CityTripPlanner(cityPrices, 1, 400);
        List<List<Integer>> trips = planner.findPossibleTrips();
        
        // Print all possible trips
        System.out.println("Possible trips within budget:");
        for (List<Integer> trip : trips) {
            System.out.println(trip + " (Total: " + trip.stream().mapToInt(Integer::intValue).sum() + ")");
        }
    }
} 