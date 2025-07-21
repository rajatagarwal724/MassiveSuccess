package lld.opentable_2;

import java.time.LocalDateTime;
import java.util.*;

public class Restaurant {
    private String id;
    private String name;
    private String address;
    private String cuisine;
    private List<Table> tables;
    private List<Reservation> reservations;

    public Restaurant(String name, String address, String cuisine) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.address = address;
        this.cuisine = cuisine;
        this.tables = new ArrayList<>();
        this.reservations = new ArrayList<>();
    }

    public void addTable(Table table) {
        tables.add(table);
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public List<Table> getAvailableTables(int partySize, LocalDateTime time) {
        List<Table> availableTables = new ArrayList<>();
        for (Table table : tables) {
            if (table.getCapacity() >= partySize && isTableAvailable(table, time)) {
                availableTables.add(table);
            }
        }
        return availableTables;
    }

    private boolean isTableAvailable(Table table, LocalDateTime time) {
        // Check if table is currently occupied only if the requested time is now
        if (table.isOccupied() && time.equals(LocalDateTime.now())) {
            return false;
        }
        
        // Check for existing reservations
        for (Reservation reservation : reservations) {
            if (reservation.getTable().equals(table) && 
                reservation.getTime().equals(time)) {
                return false;
            }
        }
        return true;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCuisine() { return cuisine; }
    public List<Table> getTables() { return tables; }
    public List<Reservation> getReservations() { return reservations; }
} 