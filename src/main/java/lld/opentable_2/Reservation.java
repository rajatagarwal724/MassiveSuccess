package lld.opentable_2;

import java.time.LocalDateTime;
import java.util.UUID;

public class Reservation {
    private String id;
    private Restaurant restaurant;
    private User user;
    private Table table;
    private LocalDateTime time;
    private int partySize;
    private ReservationStatus status;

    public Reservation(Restaurant restaurant, User user, Table table, LocalDateTime time, int partySize) {
        this.id = UUID.randomUUID().toString();
        this.restaurant = restaurant;
        this.user = user;
        this.table = table;
        this.time = time;
        this.partySize = partySize;
        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }

    // Getters
    public String getId() { return id; }
    public Restaurant getRestaurant() { return restaurant; }
    public User getUser() { return user; }
    public Table getTable() { return table; }
    public LocalDateTime getTime() { return time; }
    public int getPartySize() { return partySize; }
    public ReservationStatus getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("Reservation[id=%s, restaurant=%s, user=%s, time=%s, partySize=%d, status=%s]",
                id, restaurant.getName(), user.getName(), time, partySize, status);
    }
} 