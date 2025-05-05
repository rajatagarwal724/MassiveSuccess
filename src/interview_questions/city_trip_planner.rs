use std::collections::HashMap;
use std::vec::Vec;

pub struct TripPlanner {
    city_prices: HashMap<String, Vec<i32>>,
    days_per_city: usize,
    max_budget: i32,
}

impl TripPlanner {
    pub fn new(city_prices: HashMap<String, Vec<i32>>, days_per_city: usize, max_budget: i32) -> Self {
        TripPlanner {
            city_prices,
            days_per_city,
            max_budget,
        }
    }

    pub fn find_possible_trips(&self) -> Vec<Vec<i32>> {
        let mut result = Vec::new();
        let mut current_trip = Vec::new();
        let cities: Vec<&String> = self.city_prices.keys().collect();
        
        self.backtrack(&cities, 0, &mut current_trip, &mut result);
        result
    }

    fn backtrack(
        &self,
        cities: &[&String],
        current_city_index: usize,
        current_trip: &mut Vec<i32>,
        result: &mut Vec<Vec<i32>>,
    ) {
        // Base case: if we've processed all cities
        if current_city_index == cities.len() {
            result.push(current_trip.clone());
            return;
        }

        let current_city = cities[current_city_index];
        let prices = &self.city_prices[current_city];

        // For each possible price in the current city
        for price in prices {
            // Calculate the total cost if we add this price
            let total_cost: i32 = current_trip.iter().sum::<i32>() + price;
            
            // If adding this price would exceed the budget, skip it
            if total_cost > self.max_budget {
                continue;
            }

            // Add the price to the current trip
            current_trip.push(*price);
            
            // Recursively process the next city
            self.backtrack(cities, current_city_index + 1, current_trip, result);
            
            // Backtrack: remove the last price to try other combinations
            current_trip.pop();
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_trip_planner() {
        let mut city_prices = HashMap::new();
        city_prices.insert("Paris".to_string(), vec![100, 150]);
        city_prices.insert("London".to_string(), vec![200, 250]);
        
        let planner = TripPlanner::new(city_prices, 1, 400);
        let trips = planner.find_possible_trips();
        
        // Expected combinations:
        // [100, 200] - Paris(100) + London(200) = 300
        // [100, 250] - Paris(100) + London(250) = 350
        // [150, 200] - Paris(150) + London(200) = 350
        // [150, 250] - Paris(150) + London(250) = 400
        assert_eq!(trips.len(), 4);
    }
} 