package lld.opentable_2;

import java.time.LocalDateTime;
import java.util.*;

public class RestaurantManagementSystem {
    private List<Restaurant> restaurants;
    private List<User> users;

    public RestaurantManagementSystem() {
        this.restaurants = new ArrayList<>();
        this.users = new ArrayList<>();
    }

    public void addRestaurant(Restaurant restaurant) {
        restaurants.add(restaurant);
    }

    public void addUser(User user) {
        users.add(user);
    }

    public Reservation makeReservation(Restaurant restaurant, User user, int partySize, LocalDateTime time) throws Exception {
        // Validate restaurant exists
        if (!restaurants.contains(restaurant)) {
            throw new Exception("Restaurant not found");
        }

        // Validate user exists
        if (!users.contains(user)) {
            throw new Exception("User not found");
        }

        // Find available table
        List<Table> availableTables = restaurant.getAvailableTables(partySize, time);
        if (availableTables.isEmpty()) {
            throw new Exception("No available tables for the requested time and party size");
        }

        // Create reservation
        Table selectedTable = availableTables.get(0);
        // Only mark as occupied if the reservation is for now
        if (time.equals(LocalDateTime.now())) {
            selectedTable.setOccupied(true);
        }
        Reservation reservation = new Reservation(restaurant, user, selectedTable, time, partySize);
        
        // Update restaurant and user
        restaurant.addReservation(reservation);
        user.addReservation(reservation);

        return reservation;
    }

    public void cancelReservation(String reservationId) throws Exception {
        for (Restaurant restaurant : restaurants) {
            for (Reservation reservation : restaurant.getReservations()) {
                if (reservation.getId().equals(reservationId)) {
                    // Only mark as available if the reservation was for now
                    if (reservation.getTime().equals(LocalDateTime.now())) {
                        reservation.getTable().setOccupied(false);
                    }
                    reservation.cancel();
                    return;
                }
            }
        }
        throw new Exception("Reservation not found");
    }

    public List<Restaurant> searchRestaurants(String cuisine) {
        List<Restaurant> matchingRestaurants = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {
            if (restaurant.getCuisine().equalsIgnoreCase(cuisine)) {
                matchingRestaurants.add(restaurant);
            }
        }
        return matchingRestaurants;
    }

    // Getters
    public List<Restaurant> getRestaurants() { return restaurants; }
    public List<User> getUsers() { return users; }
} 