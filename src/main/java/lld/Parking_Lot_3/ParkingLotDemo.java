package lld.Parking_Lot_3;

import lld.ParkingLot.model.ParkingSlot;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

public class ParkingLotDemo {

    public static void main(String[] args) {
        ParkingLot lot = ParkingLot.getInstance();
        lot.addParkingFloor(3);

        System.out.println("Total Floors: " + lot.getFloors().size() + " Keys: " + lot.getFloors().keySet());


        lot.addParkingSpots(0, VehicleType.BIKE, 2);
        lot.addParkingSpots(0, VehicleType.CAR, 2);
        lot.addParkingSpots(0, VehicleType.TRUCK, 1);

        System.out.println(lot.getFloors().get(0).getParkingSpotsByVehicleType());
        System.out.println(lot.getFloors().get(1).getParkingSpotsByVehicleType());
        System.out.println(lot.getFloors().get(2).getParkingSpotsByVehicleType());


        Vehicle v1 = new Car("KA-01-1234", "Blue");
        Vehicle v2 = new Bike("KA-02-AB12", "Black");

        Ticket t1 = lot.parkVehicle(v1);
        System.out.println(t1);

        Ticket t2 = lot.parkVehicle(v2);
        System.out.println(t2);

        System.out.println(lot.totalAvailableSpots());

        System.out.println(lot.exitVehicle(t1));
        System.out.println(lot.totalAvailableSpots());

        System.out.println(lot.exitVehicle(t2));
        System.out.println(lot.totalAvailableSpots());
    }




//        // Floor-0: 2 bike, 2 car, 1 truck
//    lot.addFloor(0, Map.of(
//    SlotType.BIKE, 2,
//    SlotType.CAR, 2,
//    SlotType.TRUCK, 1));
//
//    ParkingService svc = new ParkingService(lot);
//    Vehicle v1 = new Vehicle("KA-01-1234", VehicleType.CAR);
//    Vehicle v2 = new Vehicle("KA-02-AB12", VehicleType.BIKE);
//
//    Ticket t1 = svc.park(v1);
//    Ticket t2 = svc.park(v2);
//        System.out.println("Issued tickets:\n" + t1 + "\n" + t2);
//
//        Thread.sleep(1500); // wait 1.5s to accrue some cost
//
//    double fee = svc.unPark(t1.getTicketId());
//        System.out.printf("Un-parked %s, fee = %.2f%n", v1.getRegNo(), fee);
}

class ParkingLotService {
    private ParkingLot parkingLot;
}

enum VehicleType {
    CAR,BIKE,TRUCK
}

@Data
abstract class Vehicle {
    private final String regNo;
    private final String colour;
    private final VehicleType type;

    public Vehicle(String regNo, String colour, VehicleType type) {
        this.regNo = regNo;
        this.colour = colour;
        this.type = type;
    }
}

class Car extends Vehicle {
    public Car(String regNo, String colour) {
        super(regNo, colour, VehicleType.CAR);
    }
}

class Bike extends Vehicle {
    public Bike(String regNo, String colour) {
        super(regNo, colour, VehicleType.BIKE);
    }
}

class Truck extends Vehicle {
    public Truck(String regNo, String colour) {
        super(regNo, colour, VehicleType.TRUCK);
    }
}

@Data
class ParkingSpot implements Comparable<ParkingSpot> {
    private final int floorNo;
    private final int spotNo;
    private final VehicleType vehicleType;
    private Vehicle assignedVehicle;

    public ParkingSpot(int floorNo, int spotNo, VehicleType vehicleType) {
        this.floorNo = floorNo;
        this.spotNo = spotNo;
        this.vehicleType = vehicleType;
    }

    public boolean parkVehicle(Vehicle vehicle) {
        if (isSpotFree()) {
            this.assignedVehicle = vehicle;
            return true;
        }
        return false;
    }

    public boolean isSpotFree() {
        return Objects.isNull(assignedVehicle);
    }
    public void unParkVehicle() {
        this.assignedVehicle = null;
    }

    @Override
    public int compareTo(ParkingSpot o) {
        if (this.floorNo != o.floorNo) {
            return Integer.compare(this.floorNo, o.floorNo);
        }
        return Integer.compare(this.spotNo, o.spotNo);
    }
}

@Data
class ParkingFloor {
    private final int floorNo;
    private final Map<VehicleType, TreeSet<ParkingSpot>> parkingSpotsByVehicleType;

    public ParkingFloor(int floorNo) {
        this.floorNo = floorNo;
        this.parkingSpotsByVehicleType = new HashMap<>();
        for (VehicleType vehicleType: VehicleType.values()) {
            this.parkingSpotsByVehicleType.put(vehicleType, new TreeSet<>());
        }
    }

    public void addParkingSpots(VehicleType vehicleType, int noOfSpots) {
        TreeSet<ParkingSpot> parkingSpots = parkingSpotsByVehicleType.get(vehicleType);

        int nextSpotNo = !parkingSpots.isEmpty() ? (parkingSpots.last().getSpotNo() + 1) : 0;
        for (int spotNo = nextSpotNo; spotNo < (nextSpotNo + noOfSpots); spotNo++) {
            parkingSpots.add(new ParkingSpot(floorNo, spotNo, vehicleType));
        }
    }
}

@Data
class Ticket {
    private String ticketNo;
    private Vehicle vehicle;
    private ParkingSpot spot;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    public Ticket(Vehicle vehicle, ParkingSpot spot) {
        this.ticketNo = UUID.randomUUID().toString();
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = LocalDateTime.now();
    }
}

class Kiosk {
    private String id;

    public Kiosk(String id) {
        this.id = id;
    }

    protected void display(String message) {
        System.out.println("Message: " + message);
    }
}

class EntranceKios extends Kiosk {
    public EntranceKios(String id) {
        super(id);
    }

    public Ticket issueTicket(Vehicle vehicle, ParkingSpot spot) {
        return new Ticket(vehicle, spot);
    }
}

class ExitKiosk extends Kiosk {
    private final PaymentStrategy paymentStrategy;

    public ExitKiosk(String id, PaymentStrategy paymentStrategy) {
        super(id);
        this.paymentStrategy = paymentStrategy;
    }

    public boolean processPayment(Ticket ticket) {
        ticket.setExitTime(LocalDateTime.now());
        paymentStrategy.processPayment();
        return true;
    }
}

class PaymentStrategy {
    public boolean processPayment() {
        return true;
    }
}

@Data
class ParkingLot {
    private static volatile ParkingLot INSTANCE;
    private final TreeMap<Integer, ParkingFloor> floors;
    private EntranceKios enntranceKiosk;
    private ExitKiosk exitKiosk;
    private PaymentStrategy paymentStrategy;

    private ParkingLot() {
        this.floors = new TreeMap<>();
        this.paymentStrategy = new PaymentStrategy();
        this.enntranceKiosk = new EntranceKios(UUID.randomUUID().toString());
        this.exitKiosk = new ExitKiosk(UUID.randomUUID().toString(), paymentStrategy);
    }

    public static ParkingLot getInstance() {
        if (null == INSTANCE) {
            synchronized (ParkingLot.class) {
                if (null == INSTANCE) {
                    INSTANCE = new ParkingLot();
                }
            }
        }
        return INSTANCE;
    }

    public void addParkingFloor(int numberOfFloors) {
        int nextFloorNumber = !floors.isEmpty() ? (floors.lastKey() + 1) : 0;
        for (int floorNo = nextFloorNumber; floorNo < (nextFloorNumber + numberOfFloors); floorNo++) {
            floors.put(floorNo, new ParkingFloor(floorNo));
        }
    }

    public void addParkingSpots(int floorNo, VehicleType vehicleType, int noOfSpots) {
        ParkingFloor parkingFloor = floors.get(floorNo);
        parkingFloor.addParkingSpots(vehicleType, noOfSpots);
    }

    public ParkingSpot getNextParkingSpot(VehicleType vehicleType) {
        for (ParkingFloor parkingFloor: floors.values()) {
            Optional<ParkingSpot> parkingSpotOptional = parkingFloor.getParkingSpotsByVehicleType()
                    .get(vehicleType)
                    .stream()
                    .filter(ParkingSpot::isSpotFree)
                    .findFirst();
            if (parkingSpotOptional.isPresent()) {
                return parkingSpotOptional.get();
            }
        }
        return null;
    }

    public Ticket parkVehicle(Vehicle vehicle) {
        ParkingSpot parkingSpot = getNextParkingSpot(vehicle.getType());
        Ticket ticket = enntranceKiosk.issueTicket(vehicle, parkingSpot);
        parkingSpot.parkVehicle(vehicle);
        return ticket;
    }

    public Map<VehicleType, Integer> totalAvailableSpots() {
        return floors.values().stream()
                .flatMap(floor -> floor.getParkingSpotsByVehicleType().values().stream())
                .flatMap(TreeSet::stream)
                .filter(ParkingSpot::isSpotFree)
                .collect(Collectors.groupingBy(
                        ParkingSpot::getVehicleType,
                        () -> new EnumMap<>(VehicleType.class),
                        Collectors.reducing(0, e -> 1, Integer::sum)
                ));
    }

    public Ticket exitVehicle(Ticket ticket) {
        ParkingSpot spot = ticket.getSpot();
        spot.unParkVehicle();
        exitKiosk.processPayment(ticket);
        return ticket;
    }

    public void addEntranceKiosk() {
        this.enntranceKiosk = new EntranceKios(UUID.randomUUID().toString());
    }

    public void addExitKiosk() {
        this.exitKiosk = new ExitKiosk(UUID.randomUUID().toString(), paymentStrategy);
    }
}
