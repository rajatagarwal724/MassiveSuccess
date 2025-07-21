package lld.flights;

import java.util.*;

class Flight {
    private String flightNumber;
    private String source;
    private String destination;
    private int totalSeats;
    private int availableSeats;
    private double price;
    private List<Booking> bookings;

    public Flight(String flightNumber, String source, String destination, int totalSeats, double price) {
        this.flightNumber = flightNumber;
        this.source = source;
        this.destination = destination;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.price = price;
        this.bookings = new ArrayList<>();
    }

    public String getFlightNumber() { return flightNumber; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public int getAvailableSeats() { return availableSeats; }
    public double getPrice() { return price; }
    public List<Booking> getBookings() { return bookings; }

    public boolean bookSeat() {
        if (availableSeats > 0) {
            availableSeats--;
            return true;
        }
        return false;
    }

    public void cancelBooking() {
        availableSeats++;
    }
}

class Passenger {
    private String name;
    private String email;
    private String phoneNumber;

    public Passenger(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
}

class Booking {
    private String bookingId;
    private Flight flight;
    private Passenger passenger;
    private Date bookingDate;
    private BookingStatus status;

    public Booking(String bookingId, Flight flight, Passenger passenger) {
        this.bookingId = bookingId;
        this.flight = flight;
        this.passenger = passenger;
        this.bookingDate = new Date();
        this.status = BookingStatus.CONFIRMED;
    }

    public String getBookingId() { return bookingId; }
    public Flight getFlight() { return flight; }
    public Passenger getPassenger() { return passenger; }
    public Date getBookingDate() { return bookingDate; }
    public BookingStatus getStatus() { return status; }

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
        flight.cancelBooking();
    }
}

enum BookingStatus {
    CONFIRMED,
    CANCELLED
}

class FlightBookingSystem {
    private List<Flight> flights;
    private Map<String, Booking> bookings;
    private static int bookingCounter = 1;

    public FlightBookingSystem() {
        this.flights = new ArrayList<>();
        this.bookings = new HashMap<>();
    }

    public void addFlight(Flight flight) {
        flights.add(flight);
    }

    public List<Flight> searchFlights(String source, String destination) {
        return flights.stream()
                .filter(f -> f.getSource().equals(source) && f.getDestination().equals(destination))
                .toList();
    }

    public Booking bookFlight(Flight flight, Passenger passenger) {
        if (flight.getAvailableSeats() > 0) {
            String bookingId = "BK" + bookingCounter++;
            Booking booking = new Booking(bookingId, flight, passenger);
            if (flight.bookSeat()) {
                bookings.put(bookingId, booking);
                flight.getBookings().add(booking);
                return booking;
            }
        }
        return null;
    }

    public boolean cancelBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking != null && booking.getStatus() == BookingStatus.CONFIRMED) {
            booking.cancel();
            return true;
        }
        return false;
    }

    public List<Booking> getPassengerBookings(Passenger passenger) {
        return bookings.values().stream()
                .filter(b -> b.getPassenger().equals(passenger))
                .toList();
    }
}

public class FlightSystemDemo {
    public static void main(String[] args) {
        // Create flight booking system
        FlightBookingSystem bookingSystem = new FlightBookingSystem();

        // Add some flights
        Flight flight1 = new Flight("F101", "New York", "London", 100, 500.0);
        Flight flight2 = new Flight("F102", "London", "Paris", 80, 300.0);
        Flight flight3 = new Flight("F103", "Paris", "New York", 120, 600.0);

        bookingSystem.addFlight(flight1);
        bookingSystem.addFlight(flight2);
        bookingSystem.addFlight(flight3);

        // Create a passenger
        Passenger passenger = new Passenger("John Doe", "john@example.com", "1234567890");

        // Search for flights
        System.out.println("Searching flights from New York to London:");
        List<Flight> availableFlights = bookingSystem.searchFlights("New York", "London");
        for (Flight flight : availableFlights) {
            System.out.println("Flight: " + flight.getFlightNumber() + 
                             ", Available Seats: " + flight.getAvailableSeats() + 
                             ", Price: $" + flight.getPrice());
        }

        // Book a flight
        if (!availableFlights.isEmpty()) {
            Booking booking = bookingSystem.bookFlight(availableFlights.get(0), passenger);
            if (booking != null) {
                System.out.println("\nBooking successful!");
                System.out.println("Booking ID: " + booking.getBookingId());
                System.out.println("Flight: " + booking.getFlight().getFlightNumber());
                System.out.println("Passenger: " + booking.getPassenger().getName());
            }
        }

        // Get passenger's bookings
        System.out.println("\nPassenger's bookings:");
        List<Booking> passengerBookings = bookingSystem.getPassengerBookings(passenger);
        for (Booking booking : passengerBookings) {
            System.out.println("Booking ID: " + booking.getBookingId() + 
                             ", Flight: " + booking.getFlight().getFlightNumber() + 
                             ", Status: " + booking.getStatus());
        }

        // Cancel a booking
        if (!passengerBookings.isEmpty()) {
            String bookingId = passengerBookings.get(0).getBookingId();
            if (bookingSystem.cancelBooking(bookingId)) {
                System.out.println("\nBooking " + bookingId + " cancelled successfully!");
            }
        }
    }
}
