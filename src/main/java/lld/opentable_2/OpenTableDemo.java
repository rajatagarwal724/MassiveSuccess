package lld.opentable_2;

import java.time.LocalDateTime;
import java.util.*;

public class OpenTableDemo {
    public static void main(String[] args) {
        // Create restaurant management system
        RestaurantManagementSystem rms = new RestaurantManagementSystem();
        
        // Add a restaurant
        Restaurant restaurant = new Restaurant("The Gourmet", "123 Main St", "Italian");
        rms.addRestaurant(restaurant);
        
        // Add tables to the restaurant
        restaurant.addTable(new Table(1, 4));
        restaurant.addTable(new Table(2, 2));
        restaurant.addTable(new Table(3, 6));
        
        // Create a user
        User user = new User("John Doe", "john@example.com", "1234567890");
        
        // Make a reservation
        LocalDateTime reservationTime = LocalDateTime.now().plusDays(1).withHour(19).withMinute(0);
        try {
            Reservation reservation = rms.makeReservation(restaurant, user, 4, reservationTime);
            System.out.println("Reservation successful! Reservation ID: " + reservation.getId());
        } catch (Exception e) {
            System.out.println("Reservation failed: " + e.getMessage());
        }
        
        // View restaurant's reservations
        System.out.println("\nRestaurant's Reservations:");
        for (Reservation res : restaurant.getReservations()) {
            System.out.println(res);
        }
    }
}
