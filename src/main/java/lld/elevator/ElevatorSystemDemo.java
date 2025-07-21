package lld.elevator;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ElevatorSystemDemo {
    public static void main(String[] args) throws InterruptedException {
        // Create an elevator system with 3 elevators and 10 floors
        ElevatorSystem elevatorSystem = new ElevatorSystem(3, 10);
        
        // Start the elevator system
        elevatorSystem.start();
        
        // Simulate external requests (people pressing up/down buttons on floors)
        elevatorSystem.addExternalRequest(new ExternalRequest(Direction.UP, 1)); // Someone on floor 1 wants to go up
        elevatorSystem.addExternalRequest(new ExternalRequest(Direction.DOWN, 7)); // Someone on floor 7 wants to go down
        
        // Wait for elevators to process these requests
        Thread.sleep(2000);
        
        // Simulate internal requests (people pressing buttons inside elevators)
        elevatorSystem.addInternalRequest(new InternalRequest(0, 8)); // Someone inside elevator 0 wants to go to floor 8
        elevatorSystem.addInternalRequest(new InternalRequest(1, 3)); // Someone inside elevator 1 wants to go to floor 3
        
        // Wait for elevators to process these requests
        Thread.sleep(5000);
        
        // Print the current status of all elevators
        elevatorSystem.printStatus();
        
        // Stop the elevator system
        elevatorSystem.stop();
    }
}

// Direction of elevator movement
enum Direction {
    UP, DOWN, IDLE
}

// Status of elevator
enum ElevatorStatus {
    MOVING, STOPPED, IDLE, MAINTENANCE
}

// Base class for elevator requests
abstract class ElevatorRequest {
    private final long timestamp;
    
    public ElevatorRequest() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public long getTimestamp() {
        return timestamp;
    }
}

// Request from inside the elevator (someone pressed a floor button)
class InternalRequest extends ElevatorRequest {
    private final int elevatorId;
    private final int destinationFloor;
    
    public InternalRequest(int elevatorId, int destinationFloor) {
        super();
        this.elevatorId = elevatorId;
        this.destinationFloor = destinationFloor;
    }
    
    public int getElevatorId() {
        return elevatorId;
    }
    
    public int getDestinationFloor() {
        return destinationFloor;
    }
}

// Request from a floor (someone pressed up/down button)
class ExternalRequest extends ElevatorRequest {
    private final Direction direction;
    private final int sourceFloor;
    
    public ExternalRequest(Direction direction, int sourceFloor) {
        super();
        this.direction = direction;
        this.sourceFloor = sourceFloor;
    }
    
    public Direction getDirection() {
        return direction;
    }
    
    public int getSourceFloor() {
        return sourceFloor;
    }
}

// Represents a single elevator
class Elevator {
    private final int id;
    private final int maxFloor;
    private int currentFloor;
    private Direction direction;
    private ElevatorStatus status;
    private final Lock lock;
    private final Set<Integer> destinationFloors;
    
    public Elevator(int id, int maxFloor) {
        this.id = id;
        this.maxFloor = maxFloor;
        this.currentFloor = 0; // Ground floor
        this.direction = Direction.IDLE;
        this.status = ElevatorStatus.IDLE;
        this.lock = new ReentrantLock();
        this.destinationFloors = new TreeSet<>(); // Sorted set for floor stops
    }
    
    public void addDestination(int floor) {
        lock.lock();
        try {
            if (floor >= 0 && floor <= maxFloor) {
                destinationFloors.add(floor);
                if (status == ElevatorStatus.IDLE) {
                    if (floor > currentFloor) {
                        direction = Direction.UP;
                    } else if (floor < currentFloor) {
                        direction = Direction.DOWN;
                    }
                    status = ElevatorStatus.MOVING;
                }
            }
        } finally {
            lock.unlock();
        }
    }
    
    public void move() {
        lock.lock();
        try {
            if (status == ElevatorStatus.MOVING) {
                if (direction == Direction.UP) {
                    currentFloor++;
                } else if (direction == Direction.DOWN) {
                    currentFloor--;
                }
                
                // Check if we've reached a destination floor
                if (destinationFloors.contains(currentFloor)) {
                    System.out.println("Elevator " + id + " has reached floor " + currentFloor);
                    destinationFloors.remove(currentFloor);
                    
                    // If no more destinations, become idle
                    if (destinationFloors.isEmpty()) {
                        status = ElevatorStatus.IDLE;
                        direction = Direction.IDLE;
                        System.out.println("Elevator " + id + " is now idle at floor " + currentFloor);
                    } else {
                        // Determine next direction
                        updateDirection();
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }
    
    private void updateDirection() {
        if (direction == Direction.UP) {
            // Check if there are any floors above current floor
            boolean hasHigherFloors = false;
            for (int floor : destinationFloors) {
                if (floor > currentFloor) {
                    hasHigherFloors = true;
                    break;
                }
            }
            
            if (!hasHigherFloors) {
                direction = Direction.DOWN;
            }
        } else if (direction == Direction.DOWN) {
            // Check if there are any floors below current floor
            boolean hasLowerFloors = false;
            for (int floor : destinationFloors) {
                if (floor < currentFloor) {
                    hasLowerFloors = true;
                    break;
                }
            }
            
            if (!hasLowerFloors) {
                direction = Direction.UP;
            }
        }
    }
    
    public int getId() {
        return id;
    }
    
    public int getCurrentFloor() {
        return currentFloor;
    }
    
    public Direction getDirection() {
        return direction;
    }
    
    public ElevatorStatus getStatus() {
        return status;
    }
    
    public Set<Integer> getDestinationFloors() {
        return new HashSet<>(destinationFloors);
    }
}

// Controls multiple elevators
class ElevatorController {
    private final List<Elevator> elevators;
    private final int maxFloor;
    private final ScheduledExecutorService scheduler;
    private final Map<Integer, List<ExternalRequest>> pendingExternalRequests;
    private final Lock lock;
    
    public ElevatorController(int numElevators, int maxFloor) {
        this.elevators = new ArrayList<>();
        this.maxFloor = maxFloor;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.pendingExternalRequests = new HashMap<>();
        this.lock = new ReentrantLock();
        
        for (int i = 0; i < numElevators; i++) {
            elevators.add(new Elevator(i, maxFloor));
        }
        
        // Initialize pending requests for each floor
        for (int i = 0; i <= maxFloor; i++) {
            pendingExternalRequests.put(i, new ArrayList<>());
        }
    }
    
    public void start() {
        // Schedule elevator movement every second
        scheduler.scheduleAtFixedRate(() -> {
            moveElevators();
            processRequests();
        }, 0, 1, TimeUnit.SECONDS);
    }
    
    public void stop() {
        scheduler.shutdown();
        try {
            scheduler.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public void addInternalRequest(InternalRequest request) {
        int elevatorId = request.getElevatorId();
        if (elevatorId >= 0 && elevatorId < elevators.size()) {
            Elevator elevator = elevators.get(elevatorId);
            elevator.addDestination(request.getDestinationFloor());
            System.out.println("Added internal request: Elevator " + elevatorId + 
                    " to floor " + request.getDestinationFloor());
        }
    }
    
    public void addExternalRequest(ExternalRequest request) {
        lock.lock();
        try {
            int floor = request.getSourceFloor();
            if (floor >= 0 && floor <= maxFloor) {
                pendingExternalRequests.get(floor).add(request);
                System.out.println("Added external request: " + request.getDirection() + 
                        " at floor " + floor);
            }
        } finally {
            lock.unlock();
        }
    }
    
    private void moveElevators() {
        for (Elevator elevator : elevators) {
            elevator.move();
        }
    }
    
    private void processRequests() {
        lock.lock();
        try {
            // Process external requests by finding the best elevator for each request
            for (int floor = 0; floor <= maxFloor; floor++) {
                List<ExternalRequest> requests = pendingExternalRequests.get(floor);
                if (!requests.isEmpty()) {
                    Iterator<ExternalRequest> iterator = requests.iterator();
                    while (iterator.hasNext()) {
                        ExternalRequest request = iterator.next();
                        Elevator bestElevator = findBestElevator(request);
                        if (bestElevator != null) {
                            bestElevator.addDestination(request.getSourceFloor());
                            iterator.remove();
                            System.out.println("Assigned external request at floor " + 
                                    request.getSourceFloor() + " to elevator " + bestElevator.getId());
                        }
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }
    
    private Elevator findBestElevator(ExternalRequest request) {
        Elevator bestElevator = null;
        int minCost = Integer.MAX_VALUE;
        
        for (Elevator elevator : elevators) {
            int cost = calculateCost(elevator, request);
            if (cost < minCost) {
                minCost = cost;
                bestElevator = elevator;
            }
        }
        
        return bestElevator;
    }
    
    private int calculateCost(Elevator elevator, ExternalRequest request) {
        int floor = request.getSourceFloor();
        Direction requestDirection = request.getDirection();
        
        if (elevator.getStatus() == ElevatorStatus.IDLE) {
            // If elevator is idle, cost is just the distance
            return Math.abs(elevator.getCurrentFloor() - floor);
        }
        
        if (elevator.getDirection() == requestDirection) {
            // If elevator is moving in the same direction
            if ((requestDirection == Direction.UP && floor >= elevator.getCurrentFloor()) ||
                (requestDirection == Direction.DOWN && floor <= elevator.getCurrentFloor())) {
                // Elevator will naturally pass the requesting floor
                return Math.abs(elevator.getCurrentFloor() - floor);
            }
        }
        
        // Otherwise, elevator needs to finish current journey then come to this floor
        // This is a simplified cost calculation
        return 3 * Math.abs(elevator.getCurrentFloor() - floor);
    }
    
    public void printStatus() {
        System.out.println("\nElevator System Status:");
        for (Elevator elevator : elevators) {
            System.out.println("Elevator " + elevator.getId() + 
                    ": Floor " + elevator.getCurrentFloor() + 
                    ", Status: " + elevator.getStatus() + 
                    ", Direction: " + elevator.getDirection() + 
                    ", Destinations: " + elevator.getDestinationFloors());
        }
        System.out.println();
    }
}

// Main elevator system
class ElevatorSystem {
    private final ElevatorController controller;
    
    public ElevatorSystem(int numElevators, int maxFloor) {
        this.controller = new ElevatorController(numElevators, maxFloor);
    }
    
    public void start() {
        controller.start();
        System.out.println("Elevator system started");
    }
    
    public void stop() {
        controller.stop();
        System.out.println("Elevator system stopped");
    }
    
    public void addInternalRequest(InternalRequest request) {
        controller.addInternalRequest(request);
    }
    
    public void addExternalRequest(ExternalRequest request) {
        controller.addExternalRequest(request);
    }
    
    public void printStatus() {
        controller.printStatus();
    }
}
