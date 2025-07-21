package lld.opentable_2;

import java.util.*;

public class User {
    private String id;
    private String name;
    private String email;
    private String phone;
    private List<Reservation> reservations;

    public User(String name, String email, String phone) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.reservations = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public List<Reservation> getReservations() { return reservations; }
} 