package lld.elevator_4;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ElevatorSystemDemo {
    public static void main(String[] args) {
        // Initialize the elevator system with 3 elevators and 10 floors
        ElevatorSystem elevatorSystem = ElevatorSystem.getInstance();
        elevatorSystem.initialize(3, 10);

        // Create external requests (people pressing the up/down buttons)
        elevatorSystem.submitExternalRequest(new ExternalRequest(1, Direction.UP));
        elevatorSystem.submitExternalRequest(new ExternalRequest(7, Direction.DOWN));

        // Create internal requests (people inside elevator pressing floor buttons)
        elevatorSystem.submitInternalRequest(new InternalRequest(0, 5));
        elevatorSystem.submitInternalRequest(new InternalRequest(1, 3));
        elevatorSystem.submitInternalRequest(new InternalRequest(2, 9));

        // Display current state of all elevators
        elevatorSystem.displayElevatorStatus();

        // Start the system simulation
        elevatorSystem.startSimulation();

        // Let the simulation run for a while
        try {
            Thread.sleep(10000); // 10 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Add more requests while the system is running
        elevatorSystem.submitExternalRequest(new ExternalRequest(4, Direction.DOWN));
        elevatorSystem.submitInternalRequest(new InternalRequest(1, 8));

        // Display updated status
        elevatorSystem.displayElevatorStatus();

        // Wait a bit more to see final states
        try {
            Thread.sleep(10000); // 10 more seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Display final status and stop the simulation
        elevatorSystem.displayElevatorStatus();
        elevatorSystem.stopSimulation();
        
        System.out.println("Elevator simulation completed.");
    }
}

/**
 * Singleton class representing the entire elevator system
 */
class ElevatorSystem {
    private static final ElevatorSystem INSTANCE = new ElevatorSystem();
    private List<Elevator> elevators;
    private ElevatorController controller;
    private boolean isRunning;
    private ScheduledExecutorService scheduler;

    private ElevatorSystem() {
        this.elevators = new ArrayList<>();
        this.isRunning = false;
    }

    public static ElevatorSystem getInstance() {
        return INSTANCE;
    }

    public void initialize(int elevatorCount, int floorCount) {
        // Create elevators
        for (int i = 0; i < elevatorCount; i++) {
            elevators.add(new Elevator(i, floorCount));
        }
        
        // Initialize controller
        controller = new ElevatorController(elevators, floorCount);
    }

    public void submitExternalRequest(ExternalRequest request) {
        System.out.println("Received external request: " + request);
        controller.processExternalRequest(request);
    }

    public void submitInternalRequest(InternalRequest request) {
        System.out.println("Received internal request: " + request);
        if (request.getElevatorId() >= 0 && request.getElevatorId() < elevators.size()) {
            controller.processInternalRequest(request);
        } else {
            System.out.println("Invalid elevator ID: " + request.getElevatorId());
        }
    }

    public void startSimulation() {
        if (isRunning) return;
        
        isRunning = true;
        scheduler = Executors.newScheduledThreadPool(1);
        
        // Schedule elevator movement simulation
        scheduler.scheduleAtFixedRate(() -> {
            try {
                for (Elevator elevator : elevators) {
                    elevator.move();
                }
            } catch (Exception e) {
                System.err.println("Error in elevator simulation: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
        
        System.out.println("Elevator simulation started");
    }

    public void stopSimulation() {
        if (!isRunning) return;
        
        isRunning = false;
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
        
        System.out.println("Elevator simulation stopped");
    }

    public void displayElevatorStatus() {
        System.out.println("\n===== Elevator System Status =====");
        for (Elevator elevator : elevators) {
            System.out.println(elevator);
        }
        System.out.println("================================\n");
    }
}

/**
 * Direction enum for elevator movement
 */
enum Direction {
    UP, DOWN, IDLE;

    public Direction getOppositeDirection() {
        switch (this) {
            case UP: return DOWN;
            case DOWN: return UP;
            default: return IDLE;
        }
    }
}

/**
 * Enum representing the operational status of an elevator
 */
enum ElevatorStatus {
    MOVING, STOPPED, IDLE, MAINTENANCE
}

/**
 * Abstract base class for elevator requests
 */
abstract class ElevatorRequest {
    private final int floor;

    public ElevatorRequest(int floor) {
        this.floor = floor;
    }

    public int getFloor() {
        return floor;
    }
}

/**
 * Class representing an external request (pressing up/down button)
 */
class ExternalRequest extends ElevatorRequest {
    private final Direction direction;

    public ExternalRequest(int floor, Direction direction) {
        super(floor);
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "ExternalRequest{floor=" + getFloor() + ", direction=" + direction + "}";
    }
}

/**
 * Class representing an internal request (pressing a floor button inside the elevator)
 */
class InternalRequest extends ElevatorRequest {
    private final int elevatorId;

    public InternalRequest(int elevatorId, int targetFloor) {
        super(targetFloor);
        this.elevatorId = elevatorId;
    }

    public int getElevatorId() {
        return elevatorId;
    }

    @Override
    public String toString() {
        return "InternalRequest{elevatorId=" + elevatorId + ", targetFloor=" + getFloor() + "}";
    }
}

/**
 * Class representing a single elevator
 */
class Elevator {
    private final int id;
    private final int maxFloor;
    private int currentFloor;
    private Direction currentDirection;
    private ElevatorStatus status;
    private final TreeSet<Integer> upRequests;
    private final TreeSet<Integer> downRequests;
    private final Lock lock;

    public Elevator(int id, int maxFloor) {
        this.id = id;
        this.maxFloor = maxFloor;
        this.currentFloor = 0; // Ground floor
        this.currentDirection = Direction.IDLE;
        this.status = ElevatorStatus.IDLE;
        this.upRequests = new TreeSet<>();
        this.downRequests = new TreeSet<>(Collections.reverseOrder()); // Higher floors first
        this.lock = new ReentrantLock();
    }

    public void addUpRequest(int floor) {
        if (floor > currentFloor && floor <= maxFloor) {
            lock.lock();
            try {
                upRequests.add(floor);
                if (currentDirection == Direction.IDLE) {
                    currentDirection = Direction.UP;
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public void addDownRequest(int floor) {
        if (floor < currentFloor && floor >= 0) {
            lock.lock();
            try {
                downRequests.add(floor);
                if (currentDirection == Direction.IDLE) {
                    currentDirection = Direction.DOWN;
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public void addRequest(int floor) {
        if (floor == currentFloor) return;
        
        lock.lock();
        try {
            if (floor > currentFloor) {
                upRequests.add(floor);
                if (currentDirection == Direction.IDLE) {
                    currentDirection = Direction.UP;
                }
            } else {
                downRequests.add(floor);
                if (currentDirection == Direction.IDLE) {
                    currentDirection = Direction.DOWN;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void move() {
        lock.lock();
        try {
            switch (currentDirection) {
                case UP:
                    moveUp();
                    break;
                case DOWN:
                    moveDown();
                    break;
                case IDLE:
                    // If there are any pending requests, set direction
                    if (!upRequests.isEmpty()) {
                        currentDirection = Direction.UP;
                    } else if (!downRequests.isEmpty()) {
                        currentDirection = Direction.DOWN;
                    }
                    break;
            }
        } finally {
            lock.unlock();
        }
    }

    private void moveUp() {
        if (upRequests.isEmpty()) {
            // If no more up requests, check if there are down requests
            if (!downRequests.isEmpty()) {
                currentDirection = Direction.DOWN;
            } else {
                currentDirection = Direction.IDLE;
                status = ElevatorStatus.IDLE;
                return;
            }
        } else {
            status = ElevatorStatus.MOVING;
            // Move one floor up
            currentFloor++;
            System.out.println("Elevator " + id + " moved to floor " + currentFloor);

            // Check if we've reached a requested floor
            if (upRequests.contains(currentFloor)) {
                status = ElevatorStatus.STOPPED;
                upRequests.remove(currentFloor);
                System.out.println("Elevator " + id + " stopped at floor " + currentFloor);
                
                // Simulate door opening/closing
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void moveDown() {
        if (downRequests.isEmpty()) {
            // If no more down requests, check if there are up requests
            if (!upRequests.isEmpty()) {
                currentDirection = Direction.UP;
            } else {
                currentDirection = Direction.IDLE;
                status = ElevatorStatus.IDLE;
                return;
            }
        } else {
            status = ElevatorStatus.MOVING;
            // Move one floor down
            currentFloor--;
            System.out.println("Elevator " + id + " moved to floor " + currentFloor);

            // Check if we've reached a requested floor
            if (downRequests.contains(currentFloor)) {
                status = ElevatorStatus.STOPPED;
                downRequests.remove(currentFloor);
                System.out.println("Elevator " + id + " stopped at floor " + currentFloor);
                
                // Simulate door opening/closing
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public ElevatorStatus getStatus() {
        return status;
    }

    public boolean hasRequests() {
        lock.lock();
        try {
            return !upRequests.isEmpty() || !downRequests.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        lock.lock();
        try {
            return "Elevator{" +
                    "id=" + id +
                    ", floor=" + currentFloor +
                    ", direction=" + currentDirection +
                    ", status=" + status +
                    ", upRequests=" + upRequests +
                    ", downRequests=" + downRequests +
                    "}";
        } finally {
            lock.unlock();
        }
    }
}

/**
 * Controller class to manage elevator assignments and scheduling
 */
class ElevatorController {
    private final List<Elevator> elevators;
    private final int floorCount;
    private final Map<Integer, Set<ExternalRequest>> pendingExternalRequests;
    private final Lock lock;

    public ElevatorController(List<Elevator> elevators, int floorCount) {
        this.elevators = elevators;
        this.floorCount = floorCount;
        this.pendingExternalRequests = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
    }

    public void processExternalRequest(ExternalRequest request) {
        int floor = request.getFloor();
        if (floor < 0 || floor >= floorCount) {
            System.out.println("Invalid floor number: " + floor);
            return;
        }

        lock.lock();
        try {
            // Add to pending requests
            pendingExternalRequests.computeIfAbsent(floor, k -> new HashSet<>()).add(request);
            
            // Find the best elevator to handle this request
            Elevator bestElevator = findBestElevatorForExternalRequest(request);
            
            // Assign the request to the best elevator
            if (request.getDirection() == Direction.UP) {
                bestElevator.addUpRequest(floor);
            } else {
                bestElevator.addDownRequest(floor);
            }
            
            System.out.println("Assigned external request from floor " + floor + 
                              " " + request.getDirection() + 
                              " to elevator " + bestElevator.getId());
        } finally {
            lock.unlock();
        }
    }

    public void processInternalRequest(InternalRequest request) {
        int elevatorId = request.getElevatorId();
        int targetFloor = request.getFloor();
        
        if (targetFloor < 0 || targetFloor >= floorCount) {
            System.out.println("Invalid floor number: " + targetFloor);
            return;
        }
        
        lock.lock();
        try {
            Elevator elevator = elevators.get(elevatorId);
            elevator.addRequest(targetFloor);
            System.out.println("Added internal request to floor " + targetFloor + 
                              " for elevator " + elevatorId);
        } finally {
            lock.unlock();
        }
    }

    private Elevator findBestElevatorForExternalRequest(ExternalRequest request) {
        Elevator bestElevator = null;
        int lowestCost = Integer.MAX_VALUE;
        
        for (Elevator elevator : elevators) {
            int cost = calculateCost(elevator, request);
            if (cost < lowestCost) {
                lowestCost = cost;
                bestElevator = elevator;
            }
        }
        
        // If no elevator is available or suitable, pick the first one as default
        if (bestElevator == null) {
            bestElevator = elevators.get(0);
        }
        
        return bestElevator;
    }

    private int calculateCost(Elevator elevator, ExternalRequest request) {
        int requestFloor = request.getFloor();
        Direction requestDirection = request.getDirection();
        int currentFloor = elevator.getCurrentFloor();
        Direction elevatorDirection = elevator.getCurrentDirection();
        
        // Base cost is the distance between the elevator and request floor
        int cost = Math.abs(currentFloor - requestFloor);
        
        // If elevator is idle, that's the only cost
        if (elevatorDirection == Direction.IDLE) {
            return cost;
        }
        
        // If elevator is already moving in the requested direction and the request is in the same direction
        if (elevatorDirection == requestDirection) {
            // If elevator is going up and request floor is above current floor
            // OR elevator is going down and request floor is below current floor
            if ((elevatorDirection == Direction.UP && requestFloor > currentFloor) ||
                (elevatorDirection == Direction.DOWN && requestFloor < currentFloor)) {
                // Favorable case - on the way
                return cost;
            }
        }
        
        // Unfavorable case - elevator needs to change direction or finish current trips first
        // Add a penalty
        return cost + 10 * floorCount; // High penalty to prefer elevators going in the right direction
    }
}
