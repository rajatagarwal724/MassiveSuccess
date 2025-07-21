package lld.opentable_3;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class OpenTableSystemDemo {

    public static void main(String[] args) {
        var system = OpenTableSystem.getInstance();

        // Create restaurants
        Restuarant italianRestaurant = new Restuarant(
                "R001",
                "Bella Italia",
                RestaurantType.ITALIAN
        );

        Restuarant indianRestaurant = new Restuarant(
                "R002",
                "Taj Mahal",
                RestaurantType.INDIAN
        );

        Restuarant japaneseRestaurant = new Restuarant(
                "R003",
                "Sakura",
                RestaurantType.JAPENESE
        );


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
        User user1 = new User("U001", "John Doe");
        User user2 = new User("U002", "Jane Smith");

        system.registerUser(user1);
        system.registerUser(user2);

        italianRestaurant.addTimeSlot(DayOfWeek.MONDAY, new TimeSlot(LocalTime.of(11, 0), LocalTime.of(22, 0)));
        italianRestaurant.addTimeSlot(DayOfWeek.TUESDAY, new TimeSlot(LocalTime.of(11, 0), LocalTime.of(22, 0)));
        italianRestaurant.addTimeSlot(DayOfWeek.WEDNESDAY, new TimeSlot(LocalTime.of(11, 0), LocalTime.of(22, 0)));
        italianRestaurant.addTimeSlot(DayOfWeek.THURSDAY, new TimeSlot(LocalTime.of(11, 0), LocalTime.of(22, 0)));
        italianRestaurant.addTimeSlot(DayOfWeek.FRIDAY, new TimeSlot(LocalTime.of(11, 0), LocalTime.of(22, 0)));
        italianRestaurant.addTimeSlot(DayOfWeek.SATURDAY, new TimeSlot(LocalTime.of(12, 0), LocalTime.of(23, 0)));
        italianRestaurant.addTimeSlot(DayOfWeek.SUNDAY, new TimeSlot(LocalTime.of(11, 0), LocalTime.of(21, 0)));

        // Make reservations
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalTime dinnerTime = LocalTime.of(19, 0);

        Reservation reservation1 = system.makeReservation(
                user1.getId(),
                italianRestaurant.getId(),
                2,
                LocalDateTime.of(tomorrow, dinnerTime)
        );

        System.out.println("\nReservation created: " + reservation1);

        // Search for available restaurants
        System.out.println("\nAvailable Italian restaurants tomorrow at 8 PM for 4 people:");
        List<Restuarant> availableRestaurants = system.findAvailableRestaurants(
                RestaurantType.ITALIAN,
                4,
                LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(20, 0))
        );

        for (Restuarant restaurant : availableRestaurants) {
            System.out.println(restaurant.getName());
        }

        // Cancel a reservation
        System.out.println("\nCancelling reservation: " + reservation1.getId());
        system.cancelreservation(reservation1.getId());

        // Check reservation status
        System.out.println("Reservation status: " + system.getReservation(reservation1.getId()).getStatus());
    }
}

class OpenTableSystem {
    private static volatile OpenTableSystem INSTANCE;

    private final Map<String, User> users;
    private final Map<String, Restuarant> restaurants;
    private final Map<String, Reservation> reservations;

    private OpenTableSystem() {
        this.users = new ConcurrentHashMap<>();
        this.restaurants = new ConcurrentHashMap<>();
        this.reservations = new ConcurrentHashMap<>();
    }

    public static OpenTableSystem getInstance() {
        if (null == INSTANCE) {
            synchronized (OpenTableSystem.class) {
                if (null == INSTANCE) {
                    INSTANCE = new OpenTableSystem();
                }
            }
        }
        return INSTANCE;
    }

    public Restuarant addRestaurant(Restuarant restuarant) {
        return restaurants.computeIfAbsent(restuarant.getId(), s -> restuarant);
    }

    public User registerUser(User user) {
        return users.computeIfAbsent(user.getId(), s -> user);
    }

    public Reservation makeReservation(
            final String userId,
            final String restaurantId,
            final int partySize,
            final LocalDateTime reservationTime
    ) {
        User user = users.get(userId);
        Restuarant restuarant = restaurants.get(restaurantId);

        if (!restuarant.isOpen(reservationTime)) {
            throw new RestaurantNotOpenException();
        }

        Table table = restuarant.findAvailableTable(partySize, reservationTime);

        if (null == table) {
            throw new IllegalArgumentException("No available tables for the requested time and party size");
        }

        Reservation reservation = new Reservation(
                UUID.randomUUID().toString(),
                restuarant, user, table, partySize, reservationTime,
                ReservationStatus.CONFIRMED);

        reservations.put(reservation.getId(), reservation);

        restuarant.reserveTable(reservation);
        return reservation;
    }

    public void cancelreservation(String reservationId) {
        Reservation reservation = reservations.get(reservationId);

        if (null == reservation) {
            throw new IllegalArgumentException("Reservation not found");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);

        reservation.getRestuarant().cancelReservationTable(
                reservation.getTable().getId(),
                reservation.getReservationTime()
        );
    }

    public List<Restuarant> findAvailableRestaurants(RestaurantType restaurantType, int partySize, LocalDateTime localDateTime) {
        restaurants
                .values()
                .stream()
                .filter(restuarant -> restaurantType.equals(restuarant.getType()))
                .filter(restuarant -> restuarant.isOpen(localDateTime))
                .filter(restuarant -> restuarant.hasTableAvailable(partySize, localDateTime))
                .toList();
        return new ArrayList<>();
    }

    public Reservation getReservation(String reservationId) {
        return reservations.get(reservationId);
    }
}

@Data
class Reservation {
    private final String id;
    private final Restuarant restuarant;
    private final User user;
    private final Table table;
    private final int partySize;
    private final LocalDateTime reservationTime;
    private ReservationStatus status;

    public Reservation(String id, Restuarant restuarant, User user, Table table, int partySize, LocalDateTime reservationTime, ReservationStatus status) {
        this.id = id;
        this.restuarant = restuarant;
        this.user = user;
        this.table = table;
        this.partySize = partySize;
        this.reservationTime = reservationTime;
        this.status = status;
    }
}

class RestaurantNotOpenException extends RuntimeException {
    public RestaurantNotOpenException() {
        super("Restaurant Not Open");
    }
}

@Data
@RequiredArgsConstructor
class Table {
    private final String id;
    private final int capacity;
}

enum RestaurantType {
    ITALIAN,MEXICAN, INDIAN, JAPENESE
}

enum ReservationStatus {
    PENDING, CONFIRMED, COMPLETED, CANCELLED, NO_SHOW
}

@Data
@RequiredArgsConstructor
class TimeSlot {
    private final LocalTime openTime;
    private final LocalTime closeTime;
}

@Data
class Restuarant {
    private final String id;
    private final String name;
    private final RestaurantType type;
    private final Map<String, Table> tables;
    private final Map<DayOfWeek, List<TimeSlot>> businessHours;
    private final List<Reservation> reservations;
    private final Map<String, Map<LocalDateTime, String>> tableReservations;

    public Restuarant(String id, String name, RestaurantType type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.tables = new HashMap<>();
        this.businessHours = new HashMap<>();
        for (DayOfWeek dayOfWeek: DayOfWeek.values()) {
            this.businessHours.put(dayOfWeek, new CopyOnWriteArrayList<>());
        }
        this.reservations = new ArrayList<>();
        this.tableReservations = new HashMap<>();
    }

    public void addTimeSlot(DayOfWeek dayOfWeek, TimeSlot timeSlot) {
        this.businessHours.get(dayOfWeek).add(timeSlot);
    }

    public boolean isOpen(LocalDateTime time) {
        DayOfWeek dayOfWeek = time.toLocalDate().getDayOfWeek();
        LocalTime localTime = time.toLocalTime();

        List<TimeSlot> timeSlots = this.businessHours.get(dayOfWeek);

        for (TimeSlot timeSlot: timeSlots) {
            if (!localTime.isBefore(timeSlot.getOpenTime()) && localTime.isBefore(timeSlot.getCloseTime())) {
                return true;
            }
        }
        return false;
    }

    public Table findAvailableTable(int partySize, LocalDateTime reservationTime) {
        List<Table> suitableTables = tables
                .values()
                .stream()
                .filter(table -> table.getCapacity() >= partySize)
                .sorted(Comparator.comparingInt(Table::getCapacity))
                .toList();

        return suitableTables.stream().filter(table -> {
            Map<LocalDateTime, String> reservations = tableReservations.getOrDefault(table.getId(), new HashMap<>());
            return !(
                    reservations.containsKey(reservationTime.minusHours(1))
                            || reservations.containsKey(reservationTime)
                            || reservations.containsKey(reservationTime.plusHours(1))
            );
        }).findFirst().orElse(null);
    }

    public void reserveTable(Reservation reservation) {
        tableReservations.computeIfAbsent(reservation.getTable().getId(), s -> new HashMap<>()).put(reservation.getReservationTime(), reservation.getId());
    }

    public void cancelReservationTable(String tableId, LocalDateTime reservationTime) {
        Map<LocalDateTime, String> reservations = tableReservations.get(tableId);

        reservations.remove(reservationTime);
    }

    public Table addTable(Table table) {
        return tables.computeIfAbsent(table.getId(), s -> table);
    }

    public boolean hasTableAvailable(int partySize, LocalDateTime localDateTime) {
        return Objects.nonNull(findAvailableTable(partySize, localDateTime));
    }
}

@Data
class User {
    private final String id;
    private final String name;
    private List<Reservation> reservations;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
        this.reservations = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        this.reservations.add(reservation);
    }
}
