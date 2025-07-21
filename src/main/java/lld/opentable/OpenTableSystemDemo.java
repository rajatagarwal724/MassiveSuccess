package lld.opentable;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class OpenTableSystemDemo {
    public static void main(String[] args) {
        // Initialize the OpenTable system
        OpenTableSystem system = OpenTableSystem.getInstance();
        
        // Create restaurants
        Restaurant italianRestaurant = new Restaurant(
                "R001",
                "Bella Italia",
                "123 Main St, New York",
                "Italian cuisine with authentic flavors",
                Cuisine.ITALIAN,
                PriceRange.MEDIUM);
                
        Restaurant indianRestaurant = new Restaurant(
                "R002",
                "Taj Mahal",
                "456 Broadway, New York",
                "Authentic Indian dishes",
                Cuisine.INDIAN,
                PriceRange.MEDIUM);
                
        Restaurant japaneseRestaurant = new Restaurant(
                "R003",
                "Sakura",
                "789 5th Ave, New York",
                "Fresh sushi and Japanese cuisine",
                Cuisine.JAPANESE,
                PriceRange.HIGH);
        
        // Add restaurants to the system
        system.addRestaurant(italianRestaurant);
        system.addRestaurant(indianRestaurant);
        system.addRestaurant(japaneseRestaurant);
        
        // Add tables to restaurants
        italianRestaurant.addTable(new Table("T1", 2));
        italianRestaurant.addTable(new Table("T2", 4));
        italianRestaurant.addTable(new Table("T3", 6));
        
        indianRestaurant.addTable(new Table("T1", 2));
        indianRestaurant.addTable(new Table("T2", 4));
        indianRestaurant.addTable(new Table("T3", 8));
        
        japaneseRestaurant.addTable(new Table("T1", 2));
        japaneseRestaurant.addTable(new Table("T2", 2));
        japaneseRestaurant.addTable(new Table("T3", 4));
        japaneseRestaurant.addTable(new Table("T4", 6));
        
        // Register users
        User user1 = new User("U001", "John Doe", "john@example.com", "123-456-7890");
        User user2 = new User("U002", "Jane Smith", "jane@example.com", "987-654-3210");
        
        system.registerUser(user1);
        system.registerUser(user2);
        
        // Set restaurant hours
        BusinessHours italianHours = new BusinessHours();
        italianHours.addHoursForDay(DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(22, 0));
        italianHours.addHoursForDay(DayOfWeek.TUESDAY, LocalTime.of(11, 0), LocalTime.of(22, 0));
        italianHours.addHoursForDay(DayOfWeek.WEDNESDAY, LocalTime.of(11, 0), LocalTime.of(22, 0));
        italianHours.addHoursForDay(DayOfWeek.THURSDAY, LocalTime.of(11, 0), LocalTime.of(22, 0));
        italianHours.addHoursForDay(DayOfWeek.FRIDAY, LocalTime.of(11, 0), LocalTime.of(23, 0));
        italianHours.addHoursForDay(DayOfWeek.SATURDAY, LocalTime.of(12, 0), LocalTime.of(23, 0));
        italianHours.addHoursForDay(DayOfWeek.SUNDAY, LocalTime.of(12, 0), LocalTime.of(21, 0));
        italianRestaurant.setBusinessHours(italianHours);
        
        // Make reservations
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalTime dinnerTime = LocalTime.of(19, 0);
        
        Reservation reservation1 = system.makeReservation(
                user1.getId(),
                italianRestaurant.getId(),
                tomorrow,
                dinnerTime,
                2,
                "Birthday celebration");
        
        System.out.println("\nReservation created: " + reservation1);
        
        // Search for available restaurants
        System.out.println("\nAvailable Italian restaurants tomorrow at 8 PM for 4 people:");
        List<Restaurant> availableRestaurants = system.findAvailableRestaurants(
                tomorrow,
                LocalTime.of(20, 0),
                4,
                Cuisine.ITALIAN);
        
        for (Restaurant restaurant : availableRestaurants) {
            System.out.println(restaurant.getName());
        }
        
        // Add a review
        system.addReview(new Review(
                "REV001",
                user1.getId(),
                italianRestaurant.getId(),
                4.5,
                "Great food and service!",
                LocalDateTime.now()));
        
        // Get restaurant by rating
        System.out.println("\nTop rated restaurants:");
        List<Restaurant> topRated = system.getRestaurantsByRating(4.0);
        for (Restaurant restaurant : topRated) {
            System.out.println(restaurant.getName() + " - Average rating: " + restaurant.getAverageRating());
        }
        
        // Cancel a reservation
        System.out.println("\nCancelling reservation: " + reservation1.getId());
        system.cancelReservation(reservation1.getId());
        
        // Check reservation status
        System.out.println("Reservation status: " + system.getReservation(reservation1.getId()).getStatus());
    }
}

// OpenTable System (Singleton)
class OpenTableSystem {
    private static OpenTableSystem instance;
    private final Map<String, Restaurant> restaurants;
    private final Map<String, User> users;
    private final Map<String, Reservation> reservations;
    private final Map<String, List<Review>> restaurantReviews;
    private final Lock lock;
    
    private OpenTableSystem() {
        this.restaurants = new ConcurrentHashMap<>();
        this.users = new ConcurrentHashMap<>();
        this.reservations = new ConcurrentHashMap<>();
        this.restaurantReviews = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
    }
    
    public static OpenTableSystem getInstance() {
        if (instance == null) {
            synchronized (OpenTableSystem.class) {
                if (instance == null) {
                    instance = new OpenTableSystem();
                }
            }
        }
        return instance;
    }
    
    public void addRestaurant(Restaurant restaurant) {
        restaurants.put(restaurant.getId(), restaurant);
    }
    
    public void registerUser(User user) {
        users.put(user.getId(), user);
    }
    
    public Reservation makeReservation(String userId, String restaurantId, LocalDate date, 
                                      LocalTime time, int partySize, String specialRequests) {
        lock.lock();
        try {
            User user = users.get(userId);
            Restaurant restaurant = restaurants.get(restaurantId);
            
            if (user == null || restaurant == null) {
                throw new IllegalArgumentException("User or restaurant not found");
            }
            
            // Check if the restaurant is open at the requested time
            if (!restaurant.isOpenAt(date.getDayOfWeek(), time)) {
                throw new IllegalArgumentException("Restaurant is not open at the requested time");
            }
            
            // Find an available table
            Table availableTable = restaurant.findAvailableTable(date, time, partySize);
            if (availableTable == null) {
                throw new IllegalArgumentException("No available tables for the requested time and party size");
            }
            
            // Create reservation
            String reservationId = "RES" + System.currentTimeMillis();
            Reservation reservation = new Reservation(
                    reservationId,
                    userId,
                    restaurantId,
                    availableTable.getId(),
                    date,
                    time,
                    partySize,
                    specialRequests,
                    ReservationStatus.CONFIRMED);
            
            // Save the reservation
            reservations.put(reservationId, reservation);
            
            // Mark the table as reserved
            restaurant.reserveTable(availableTable.getId(), date, time, reservationId);
            
            return reservation;
        } finally {
            lock.unlock();
        }
    }
    
    public void cancelReservation(String reservationId) {
        lock.lock();
        try {
            Reservation reservation = reservations.get(reservationId);
            if (reservation == null) {
                throw new IllegalArgumentException("Reservation not found");
            }
            
            Restaurant restaurant = restaurants.get(reservation.getRestaurantId());
            if (restaurant == null) {
                throw new IllegalArgumentException("Restaurant not found");
            }
            
            // Update reservation status
            reservation.setStatus(ReservationStatus.CANCELLED);
            
            // Free up the table
            restaurant.cancelTableReservation(reservation.getTableId(), 
                                             reservation.getDate(), 
                                             reservation.getTime());
        } finally {
            lock.unlock();
        }
    }
    
    public List<Restaurant> findAvailableRestaurants(LocalDate date, LocalTime time, 
                                                  int partySize, Cuisine cuisine) {
        return restaurants.values().stream()
                .filter(restaurant -> restaurant.getCuisine() == cuisine)
                .filter(restaurant -> restaurant.isOpenAt(date.getDayOfWeek(), time))
                .filter(restaurant -> restaurant.hasAvailableTable(date, time, partySize))
                .collect(Collectors.toList());
    }
    
    public void addReview(Review review) {
        lock.lock();
        try {
            String restaurantId = review.getRestaurantId();
            restaurantReviews.computeIfAbsent(restaurantId, k -> new ArrayList<>()).add(review);
            
            // Update restaurant rating
            Restaurant restaurant = restaurants.get(restaurantId);
            if (restaurant != null) {
                double averageRating = calculateAverageRating(restaurantId);
                restaurant.setAverageRating(averageRating);
            }
        } finally {
            lock.unlock();
        }
    }
    
    private double calculateAverageRating(String restaurantId) {
        List<Review> reviews = restaurantReviews.getOrDefault(restaurantId, Collections.emptyList());
        if (reviews.isEmpty()) {
            return 0.0;
        }
        
        double sum = reviews.stream().mapToDouble(Review::getRating).sum();
        return sum / reviews.size();
    }
    
    public List<Restaurant> getRestaurantsByRating(double minRating) {
        return restaurants.values().stream()
                .filter(restaurant -> restaurant.getAverageRating() >= minRating)
                .sorted(Comparator.comparing(Restaurant::getAverageRating).reversed())
                .collect(Collectors.toList());
    }
    
    public Reservation getReservation(String reservationId) {
        return reservations.get(reservationId);
    }
}

// User class
@Data
@AllArgsConstructor
class User {
    private final String id;
    private String name;
    private String email;
    private String phone;
}

// Restaurant class
@Data
class Restaurant {
    private final String id;
    private String name;
    private String address;
    private String description;
    private Cuisine cuisine;
    private PriceRange priceRange;
    private BusinessHours businessHours;
    private final Map<String, Table> tables;
    private double averageRating;
    private final Map<String, Map<LocalDateTime, String>> tableReservations; // tableId -> datetime -> reservationId
    
    public Restaurant(String id, String name, String address, String description, 
                     Cuisine cuisine, PriceRange priceRange) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.cuisine = cuisine;
        this.priceRange = priceRange;
        this.tables = new HashMap<>();
        this.averageRating = 0.0;
        this.tableReservations = new HashMap<>();
        this.businessHours = new BusinessHours();
    }
    
    public void addTable(Table table) {
        tables.put(table.getId(), table);
        tableReservations.put(table.getId(), new HashMap<>());
    }
    
    public boolean isOpenAt(DayOfWeek day, LocalTime time) {
        return businessHours.isOpenAt(day, time);
    }
    
    public Table findAvailableTable(LocalDate date, LocalTime time, int partySize) {
        // Find tables that can accommodate the party size
        List<Table> suitableTables = tables.values().stream()
                .filter(table -> table.getCapacity() >= partySize)
                .sorted(Comparator.comparing(Table::getCapacity))
                .toList();
        
        // Check if any of these tables are available at the requested time
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        
        for (Table table : suitableTables) {
            if (!isTableReserved(table.getId(), dateTime)) {
                return table;
            }
        }
        
        return null;
    }
    
    public boolean hasAvailableTable(LocalDate date, LocalTime time, int partySize) {
        return findAvailableTable(date, time, partySize) != null;
    }
    
    private boolean isTableReserved(String tableId, LocalDateTime dateTime) {
        Map<LocalDateTime, String> reservations = tableReservations.get(tableId);
        if (reservations == null) {
            return false;
        }
        
        // Check for reservations within a 2-hour window (assuming reservations last 2 hours)
        for (int i = -1; i <= 1; i++) {
            if (reservations.containsKey(dateTime.plusHours(i))) {
                return true;
            }
        }
        
        return false;
    }
    
    public void reserveTable(String tableId, LocalDate date, LocalTime time, String reservationId) {
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        Map<LocalDateTime, String> reservations = tableReservations.get(tableId);
        if (reservations != null) {
            reservations.put(dateTime, reservationId);
        }
    }
    
    public void cancelTableReservation(String tableId, LocalDate date, LocalTime time) {
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        Map<LocalDateTime, String> reservations = tableReservations.get(tableId);
        if (reservations != null) {
            reservations.remove(dateTime);
        }
    }
}

// Table class
@Data
@AllArgsConstructor
class Table {
    private final String id;
    private final int capacity;
}

// Reservation class
@Data
class Reservation {
    private final String id;
    private final String userId;
    private final String restaurantId;
    private final String tableId;
    private final LocalDate date;
    private final LocalTime time;
    private final int partySize;
    private final String specialRequests;
    private ReservationStatus status;
    
    public Reservation(String id, String userId, String restaurantId, String tableId,
                      LocalDate date, LocalTime time, int partySize, String specialRequests,
                      ReservationStatus status) {
        this.id = id;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.tableId = tableId;
        this.date = date;
        this.time = time;
        this.partySize = partySize;
        this.specialRequests = specialRequests;
        this.status = status;
    }
}

// Review class
class Review {
    private final String id;
    private final String userId;
    private final String restaurantId;
    private final double rating;
    private final String comment;
    private final LocalDateTime timestamp;
    
    public Review(String id, String userId, String restaurantId, double rating, 
                 String comment, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
    }
    
    public String getId() {
        return id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getRestaurantId() {
        return restaurantId;
    }
    
    public double getRating() {
        return rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

// BusinessHours class
class BusinessHours {
    private final Map<DayOfWeek, List<TimeSlot>> hoursByDay;
    
    public BusinessHours() {
        hoursByDay = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            hoursByDay.put(day, new ArrayList<>());
        }
    }
    
    public void addHoursForDay(DayOfWeek day, LocalTime openTime, LocalTime closeTime) {
        hoursByDay.get(day).add(new TimeSlot(openTime, closeTime));
    }
    
    public boolean isOpenAt(DayOfWeek day, LocalTime time) {
        List<TimeSlot> slots = hoursByDay.get(day);
        if (slots == null || slots.isEmpty()) {
            return false;
        }
        
        for (TimeSlot slot : slots) {
            if (time.compareTo(slot.getOpenTime()) >= 0 && time.compareTo(slot.getCloseTime()) < 0) {
                return true;
            }
        }
        
        return false;
    }
}

// TimeSlot class
@Data
class TimeSlot {
    private final LocalTime openTime;
    private final LocalTime closeTime;
    
    public TimeSlot(LocalTime openTime, LocalTime closeTime) {
        this.openTime = openTime;
        this.closeTime = closeTime;
    }
}

// Enums
enum Cuisine {
    ITALIAN, INDIAN, CHINESE, JAPANESE, MEXICAN, AMERICAN, FRENCH, THAI, MEDITERRANEAN, OTHER
}

enum PriceRange {
    LOW, MEDIUM, HIGH, VERY_HIGH
}

enum ReservationStatus {
    PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW
}
