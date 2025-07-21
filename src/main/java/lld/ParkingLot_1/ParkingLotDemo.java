package lld.ParkingLot_1;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ParkingLotDemo {
    public static void main(String[] args) {
        // Create a parking lot with 3 floors
        ParkingLot parkingLot = ParkingLot.getInstance();
        parkingLot.initialize(3);

        // Add parking spots to different floors
        parkingLot.addParkingSpot(0, VehicleType.CAR, 5);
        parkingLot.addParkingSpot(0, VehicleType.BIKE, 10);
        parkingLot.addParkingSpot(1, VehicleType.CAR, 8);
        parkingLot.addParkingSpot(1, VehicleType.TRUCK, 2);
        parkingLot.addParkingSpot(2, VehicleType.CAR, 10);

        // Create vehicles
        Vehicle car1 = new Car("KA01AB1234");
        Vehicle car2 = new Car("KA02CD5678");
        Vehicle bike1 = new Bike("KA03EF9012");
        Vehicle truck1 = new Truck("KA04GH3456");

        // Park vehicles
        ParkingTicket ticket1 = parkingLot.parkVehicle(car1);
        System.out.println("Car 1 parked with ticket: " + ticket1.getTicketId());

        ParkingTicket ticket2 = parkingLot.parkVehicle(car2);
        System.out.println("Car 2 parked with ticket: " + ticket2.getTicketId());

        ParkingTicket ticket3 = parkingLot.parkVehicle(bike1);
        System.out.println("Bike 1 parked with ticket: " + ticket3.getTicketId());

        ParkingTicket ticket4 = parkingLot.parkVehicle(truck1);
        System.out.println("Truck 1 parked with ticket: " + ticket4.getTicketId());

        // Check available spots
        System.out.println("\nAvailable spots for CAR: " + 
                          parkingLot.getAvailableSpotsCount(VehicleType.CAR));
        
        // Let's simulate some time passing for payment calculation
        try {
            System.out.println("\nWaiting a bit before exiting vehicles...");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Exit vehicles and make payments
        Receipt receipt1 = parkingLot.exitVehicle(ticket1);
        System.out.println("\nCar 1 exited. Payment details: " + receipt1.getAmount() + " Rs");

        Receipt receipt3 = parkingLot.exitVehicle(ticket3);
        System.out.println("Bike 1 exited. Payment details: " + receipt3.getAmount() + " Rs");

        // Check available spots again
        System.out.println("\nAvailable spots for CAR after exits: " + 
                          parkingLot.getAvailableSpotsCount(VehicleType.CAR));
        System.out.println("Available spots for BIKE after exits: " + 
                          parkingLot.getAvailableSpotsCount(VehicleType.BIKE));
    }
}

// Core models
enum VehicleType {
    CAR(1),
    BIKE(1),
    TRUCK(2);
    
    private final int spotsNeeded;
    
    VehicleType(int spotsNeeded) {
        this.spotsNeeded = spotsNeeded;
    }
    
    public int getSpotsNeeded() {
        return spotsNeeded;
    }
}

abstract class Vehicle {
    private final String licenseNumber;
    private final VehicleType vehicleType;
    
    public Vehicle(String licenseNumber, VehicleType vehicleType) {
        this.licenseNumber = licenseNumber;
        this.vehicleType = vehicleType;
    }
    
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    public VehicleType getVehicleType() {
        return vehicleType;
    }
}

class Car extends Vehicle {
    public Car(String licenseNumber) {
        super(licenseNumber, VehicleType.CAR);
    }
}

class Bike extends Vehicle {
    public Bike(String licenseNumber) {
        super(licenseNumber, VehicleType.BIKE);
    }
}

class Truck extends Vehicle {
    public Truck(String licenseNumber) {
        super(licenseNumber, VehicleType.TRUCK);
    }
}

class ParkingSpot {
    private final int id;
    private final VehicleType vehicleType;
    private final int floorNumber;
    private boolean isOccupied;
    
    public ParkingSpot(int id, VehicleType vehicleType, int floorNumber) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.floorNumber = floorNumber;
        this.isOccupied = false;
    }
    
    public boolean isOccupied() {
        return isOccupied;
    }
    
    public void occupy() {
        isOccupied = true;
    }
    
    public void release() {
        isOccupied = false;
    }
}

class ParkingTicket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final List<ParkingSpot> allocatedSpots;
    private final LocalDateTime entryTime;
    
    public ParkingTicket(String ticketId, Vehicle vehicle, List<ParkingSpot> allocatedSpots) {
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.allocatedSpots = new ArrayList<>(allocatedSpots);
        this.entryTime = LocalDateTime.now();
    }
    
    public String getTicketId() {
        return ticketId;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    public List<ParkingSpot> getAllocatedSpots() {
        return allocatedSpots;
    }
    
    public LocalDateTime getEntryTime() {
        return entryTime;
    }
}

class Receipt {
    private final String receiptId;
    private final String ticketId;
    private final LocalDateTime entryTime;
    private final LocalDateTime exitTime;
    private final double amount;
    
    public Receipt(String receiptId, String ticketId, LocalDateTime entryTime, 
                  LocalDateTime exitTime, double amount) {
        this.receiptId = receiptId;
        this.ticketId = ticketId;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.amount = amount;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public String getReceiptId() {
        return receiptId;
    }
}

class ParkingFloor {
    private final int floorNumber;
    private final Map<VehicleType, List<ParkingSpot>> parkingSpots;
    
    public ParkingFloor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.parkingSpots = new HashMap<>();
        for (VehicleType type : VehicleType.values()) {
            parkingSpots.put(type, new ArrayList<>());
        }
    }
    
    public void addParkingSpot(VehicleType vehicleType, int count) {
        List<ParkingSpot> spots = parkingSpots.get(vehicleType);
        int startId = spots.size();
        for (int i = 0; i < count; i++) {
            spots.add(new ParkingSpot(startId + i, vehicleType, floorNumber));
        }
    }
    
    public List<ParkingSpot> getAvailableSpots(VehicleType vehicleType) {
        List<ParkingSpot> availableSpots = new ArrayList<>();
        for (ParkingSpot spot : parkingSpots.get(vehicleType)) {
            if (!spot.isOccupied()) {
                availableSpots.add(spot);
            }
        }
        return availableSpots;
    }
    
    public int getFloorNumber() {
        return floorNumber;
    }
}

class PaymentStrategy {
    private static final Map<VehicleType, Double> HOURLY_RATES = new HashMap<>();
    
    static {
        HOURLY_RATES.put(VehicleType.CAR, 20.0);
        HOURLY_RATES.put(VehicleType.BIKE, 10.0);
        HOURLY_RATES.put(VehicleType.TRUCK, 30.0);
    }
    
    public static double calculateFee(Vehicle vehicle, LocalDateTime entryTime, LocalDateTime exitTime) {
        Duration duration = Duration.between(entryTime, exitTime);
        long minutes = duration.toMinutes();
        double hours = Math.ceil(minutes / 60.0);
        
        // Minimum 1 hour fee
        if (hours < 1) {
            hours = 1;
        }
        
        return hours * HOURLY_RATES.get(vehicle.getVehicleType());
    }
}

// Main parking lot class using Singleton pattern
class ParkingLot {
    private static ParkingLot instance;
    private List<ParkingFloor> floors;
    private Map<String, ParkingTicket> activeTickets;
    private int ticketCounter;
    private int receiptCounter;
    
    private ParkingLot() {
        this.floors = new ArrayList<>();
        this.activeTickets = new ConcurrentHashMap<>();
        this.ticketCounter = 1000;
        this.receiptCounter = 5000;
    }
    
    public static synchronized ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot();
        }
        return instance;
    }
    
    public void initialize(int numFloors) {
        floors.clear();
        for (int i = 0; i < numFloors; i++) {
            floors.add(new ParkingFloor(i));
        }
    }
    
    public void addParkingSpot(int floorNumber, VehicleType vehicleType, int count) {
        if (floorNumber >= 0 && floorNumber < floors.size()) {
            floors.get(floorNumber).addParkingSpot(vehicleType, count);
        } else {
            throw new IllegalArgumentException("Invalid floor number");
        }
    }
    
    public ParkingTicket parkVehicle(Vehicle vehicle) {
        VehicleType vehicleType = vehicle.getVehicleType();
        int spotsNeeded = vehicleType.getSpotsNeeded();
        
        for (ParkingFloor floor : floors) {
            List<ParkingSpot> availableSpots = floor.getAvailableSpots(vehicleType);
            
            if (availableSpots.size() >= spotsNeeded) {
                List<ParkingSpot> allocatedSpots = new ArrayList<>();
                
                for (int i = 0; i < spotsNeeded; i++) {
                    ParkingSpot spot = availableSpots.get(i);
                    spot.occupy();
                    allocatedSpots.add(spot);
                }
                
                String ticketId = "TICKET-" + (++ticketCounter);
                ParkingTicket ticket = new ParkingTicket(ticketId, vehicle, allocatedSpots);
                activeTickets.put(ticketId, ticket);
                
                return ticket;
            }
        }
        
        throw new RuntimeException("No parking space available for " + vehicleType);
    }
    
    public Receipt exitVehicle(ParkingTicket ticket) {
        if (!activeTickets.containsKey(ticket.getTicketId())) {
            throw new IllegalArgumentException("Invalid ticket");
        }
        
        activeTickets.remove(ticket.getTicketId());
        
        // Release parking spots
        for (ParkingSpot spot : ticket.getAllocatedSpots()) {
            spot.release();
        }
        
        // Calculate payment
        LocalDateTime exitTime = LocalDateTime.now();
        double amount = PaymentStrategy.calculateFee(
            ticket.getVehicle(), ticket.getEntryTime(), exitTime);
        
        String receiptId = "RECEIPT-" + (++receiptCounter);
        return new Receipt(receiptId, ticket.getTicketId(), 
                         ticket.getEntryTime(), exitTime, amount);
    }
    
    public int getAvailableSpotsCount(VehicleType vehicleType) {
        int count = 0;
        for (ParkingFloor floor : floors) {
            count += floor.getAvailableSpots(vehicleType).size();
        }
        return count;
    }
}
