package lld.elevator_8;

import lombok.Data;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Enum to represent elevator direction
enum Direction {
    UP, DOWN, IDLE
}

// Enum to represent elevator state
enum ElevatorState {
    MOVING, STOPPED, DOOR_OPEN
}

// Strategy interface for elevator selection
interface ElevatorSelectionStrategy {
    Elevator selectElevator(List<Elevator> elevators, int floor);
}

// Concrete strategy: Nearest Elevator Strategy
class NearestElevatorStrategy implements ElevatorSelectionStrategy {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, int floor) {
        Elevator bestElevator = null;
        int minDistance = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            int distance = Math.abs(elevator.getCurrentFloor() - floor);
            if (distance < minDistance) {
                minDistance = distance;
                bestElevator = elevator;
            }
        }
        return bestElevator;
    }
}

// Concrete strategy: Direction Based Strategy
class DirectionBasedStrategy implements ElevatorSelectionStrategy {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, int floor) {
        Elevator bestElevator = null;
        int minDistance = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            int distance = Math.abs(elevator.getCurrentFloor() - floor);

            if (elevator.getDirection() == Direction.IDLE ||
                    (elevator.getDirection() == Direction.UP && floor > elevator.getCurrentFloor()) ||
                    (elevator.getDirection() == Direction.DOWN && floor < elevator.getCurrentFloor())) {

                if (distance < minDistance) {
                    minDistance = distance;
                    bestElevator = elevator;
                }
            }
        }

        // If no suitable elevator found, take the closest one
        if (bestElevator == null) {
            return new NearestElevatorStrategy().selectElevator(elevators, floor);
        }

        return bestElevator;
    }
}

// Concrete strategy: Load Balancing Strategy
class LoadBalancingStrategy implements ElevatorSelectionStrategy {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, int floor) {
        Elevator bestElevator = null;
        int minRequests = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            int totalRequests = elevator.getUpRequestsCount() + elevator.getDownRequestsCount();
            if (totalRequests < minRequests) {
                minRequests = totalRequests;
                bestElevator = elevator;
            }
        }
        return bestElevator;
    }
}

// Class to represent an Elevator
@Data
class Elevator {
    private int id;
    private int currentFloor;
    private Direction direction;
    private ElevatorState state;
    private PriorityQueue<Integer> upRequests;
    private PriorityQueue<Integer> downRequests;
    private static final int MAX_FLOOR = 20;
    private static final int MIN_FLOOR = 1;

    public Elevator(int id) {
        this.id = id;
        this.currentFloor = 1;
        this.direction = Direction.IDLE;
        this.state = ElevatorState.STOPPED;
        this.upRequests = new PriorityQueue<>();
        this.downRequests = new PriorityQueue<>(Collections.reverseOrder());
    }

    public void addRequest(int floor) {
        if (floor < MIN_FLOOR || floor > MAX_FLOOR) {
            throw new IllegalArgumentException("Invalid floor number");
        }

        if (floor > currentFloor) {
            upRequests.add(floor);
        } else if (floor < currentFloor) {
            downRequests.add(floor);
        }
    }

    public void move() {
        if (state == ElevatorState.DOOR_OPEN) {
            closeDoor();
        }

        if (upRequests.isEmpty() && downRequests.isEmpty()) {
            direction = Direction.IDLE;
            state = ElevatorState.STOPPED;
            return;
        }

        if (direction == Direction.UP || direction == Direction.IDLE) {
            if (!upRequests.isEmpty()) {
                moveUp();
            } else {
                direction = Direction.DOWN;
                moveDown();
            }
        } else {
            if (!downRequests.isEmpty()) {
                moveDown();
            } else {
                direction = Direction.UP;
                moveUp();
            }
        }
    }

    private void moveUp() {
        if (state != ElevatorState.MOVING) {
            state = ElevatorState.MOVING;
        }
        direction = Direction.UP;
        currentFloor++;
        System.out.println("Elevator " + id + " moving up to floor " + currentFloor);

        if (upRequests.contains(currentFloor)) {
            stop();
            upRequests.remove(currentFloor);
            openDoor();
        }
    }

    private void moveDown() {
        if (state != ElevatorState.MOVING) {
            state = ElevatorState.MOVING;
        }
        direction = Direction.DOWN;
        currentFloor--;
        System.out.println("Elevator " + id + " moving down to floor " + currentFloor);

        if (downRequests.contains(currentFloor)) {
            stop();
            downRequests.remove(currentFloor);
            openDoor();
        }
    }

    private void stop() {
        state = ElevatorState.STOPPED;
        System.out.println("Elevator " + id + " stopped at floor " + currentFloor);
    }

    private void openDoor() {
        state = ElevatorState.DOOR_OPEN;
        System.out.println("Elevator " + id + " door opened at floor " + currentFloor);
        // Simulate door closing after a delay
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        closeDoor();
    }

    private void closeDoor() {
        state = ElevatorState.STOPPED;
        System.out.println("Elevator " + id + " door closed at floor " + currentFloor);
    }

    public boolean hasRequests() {
        return !upRequests.isEmpty() || !downRequests.isEmpty();
    }

    public int getUpRequestsCount() {
        return upRequests.size();
    }

    public int getDownRequestsCount() {
        return downRequests.size();
    }
}

// Class to manage multiple elevators
@Data
class ElevatorSystem {
    private List<Elevator> elevators;
    private static final int NUM_ELEVATORS = 4;
    private ElevatorSelectionStrategy selectionStrategy;
    private ScheduledExecutorService scheduler;
    private static final long ELEVATOR_MOVEMENT_INTERVAL = 1000; // 1 second

    public ElevatorSystem(ElevatorSelectionStrategy strategy) {
        this.elevators = new ArrayList<>();
        this.selectionStrategy = strategy;
        this.scheduler = Executors.newScheduledThreadPool(NUM_ELEVATORS);

        for (int i = 1; i <= NUM_ELEVATORS; i++) {
            elevators.add(new Elevator(i));
        }
        startElevatorScheduling();
    }

    private void startElevatorScheduling() {
        for (Elevator elevator : elevators) {
            scheduler.scheduleAtFixedRate(
                    () -> {
                        if (elevator.hasRequests()) {
                            elevator.move();
                        }
                    },
                    0, // initial delay
                    ELEVATOR_MOVEMENT_INTERVAL,
                    TimeUnit.MILLISECONDS
            );
        }
    }

    public void requestElevator(int floor) {
        Elevator bestElevator = selectionStrategy.selectElevator(elevators, floor);
        bestElevator.addRequest(floor);
        System.out.println("Elevator " + bestElevator.getCurrentFloor() + " assigned to floor " + floor);
    }

    public void shutdown() {
        try {
            System.out.println("Shutting down elevator system...");
            scheduler.shutdown();
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

// Main class to demonstrate the elevator system
public class ElevatorSystemDemo {
    public static void main(String[] args) {
        // Create elevator system with different strategies
        System.out.println("Elevator System Demo with Strategy Pattern");
        System.out.println("Demonstrating different elevator selection strategies\n");

        // Test with Direction Based Strategy
        System.out.println("Testing with Direction Based Strategy:");
        ElevatorSystem directionSystem = new ElevatorSystem(new DirectionBasedStrategy());
        testElevatorSystem(directionSystem);

        // Test with Nearest Elevator Strategy
        System.out.println("\nTesting with Nearest Elevator Strategy:");
        ElevatorSystem nearestSystem = new ElevatorSystem(new NearestElevatorStrategy());
        testElevatorSystem(nearestSystem);

        // Test with Load Balancing Strategy
        System.out.println("\nTesting with Load Balancing Strategy:");
        ElevatorSystem loadBalancingSystem = new ElevatorSystem(new LoadBalancingStrategy());
        testElevatorSystem(loadBalancingSystem);

        // Keep the program running for a while to observe elevator movements
        try {
            Thread.sleep(10000); // Run for 10 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Shutdown all elevator systems
        directionSystem.shutdown();
        nearestSystem.shutdown();
        loadBalancingSystem.shutdown();
    }

    private static void testElevatorSystem(ElevatorSystem system) {
        int[] requests = {5, 3, 7, 2, 8, 10, 15, 12};
        for (int floor : requests) {
            System.out.println("\nRequesting elevator for floor " + floor);
            system.requestElevator(floor);
            try {
                Thread.sleep(500); // Add delay between requests
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}



