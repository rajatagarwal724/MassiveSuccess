package lld.elevator_10;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Enum to represent elevator direction
enum Direction {
    UP, DOWN, IDLE
}

// Class to represent an Elevator
class Elevator {
    private final int id;
    private int currentFloor;
    private Direction direction;
    private final Set<Integer> requests;
    private static final int MAX_FLOOR = 20;
    private static final int MIN_FLOOR = 1;

    public Elevator(int id) {
        this.id = id;
        this.currentFloor = 1;
        this.direction = Direction.IDLE;
        this.requests = new HashSet<>();
    }

    public void addRequest(int floor) {
        if (floor < MIN_FLOOR || floor > MAX_FLOOR) {
            throw new IllegalArgumentException("Invalid floor number");
        }
        if (floor != currentFloor) {
            requests.add(floor);
            if (direction == Direction.IDLE) {
                direction = floor > currentFloor ? Direction.UP : Direction.DOWN;
            }
        }
    }

    public void move() {
        if (requests.isEmpty()) {
            direction = Direction.IDLE;
            return;
        }

        // Find next floor to visit
        int nextFloor = findNextFloor();
        if (nextFloor == currentFloor) {
            stop();
            requests.remove(currentFloor);
            return;
        }

        // Move towards next floor
        if (nextFloor > currentFloor) {
            moveUp();
        } else {
            moveDown();
        }
    }

    private int findNextFloor() {
        if (requests.isEmpty()) return currentFloor;

        // If going up, find closest floor above
        if (direction == Direction.UP) {
            return requests.stream()
                .filter(floor -> floor > currentFloor)
                .min(Integer::compareTo)
                .orElseGet(() -> {
                    direction = Direction.DOWN;
                    return requests.stream()
                        .filter(floor -> floor < currentFloor)
                        .max(Integer::compareTo)
                        .orElse(currentFloor);
                });
        }
        // If going down, find closest floor below
        else if (direction == Direction.DOWN) {
            return requests.stream()
                .filter(floor -> floor < currentFloor)
                .max(Integer::compareTo)
                .orElseGet(() -> {
                    direction = Direction.UP;
                    return requests.stream()
                        .filter(floor -> floor > currentFloor)
                        .min(Integer::compareTo)
                        .orElse(currentFloor);
                });
        }
        // If idle, find closest floor
        else {
            return requests.stream()
                .min(Comparator.comparingInt(floor -> Math.abs(floor - currentFloor)))
                .orElse(currentFloor);
        }
    }

    private void moveUp() {
        direction = Direction.UP;
        currentFloor++;
        System.out.println("Elevator " + id + " moving up to floor " + currentFloor);
        checkAndStop();
    }

    private void moveDown() {
        direction = Direction.DOWN;
        currentFloor--;
        System.out.println("Elevator " + id + " moving down to floor " + currentFloor);
        checkAndStop();
    }

    private void checkAndStop() {
        if (requests.contains(currentFloor)) {
            stop();
            requests.remove(currentFloor);
        }
    }

    private void stop() {
        System.out.println("Elevator " + id + " stopped at floor " + currentFloor);
        System.out.println("Elevator " + id + " door opened");
        try {
            Thread.sleep(2000); // Simulate door open/close time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Elevator " + id + " door closed");
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean hasRequests() {
        return !requests.isEmpty();
    }
}

// Class to manage multiple elevators
class ElevatorSystem {
    private final List<Elevator> elevators;
    private static final int NUM_ELEVATORS = 4;
    private final ScheduledExecutorService scheduler;

    public ElevatorSystem() {
        this.elevators = new ArrayList<>();
        this.scheduler = Executors.newScheduledThreadPool(NUM_ELEVATORS);

        // Initialize elevators
        for (int i = 1; i <= NUM_ELEVATORS; i++) {
            elevators.add(new Elevator(i));
        }

        // Start elevator movement scheduling
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
                0,
                1000, // 1 second interval
                TimeUnit.MILLISECONDS
            );
        }
    }

    public void requestElevator(int floor) {
        // Find the nearest elevator
        Elevator nearestElevator = findNearestElevator(floor);
        nearestElevator.addRequest(floor);
        System.out.println("Elevator " + nearestElevator.getCurrentFloor() + " assigned to floor " + floor);
    }

    private Elevator findNearestElevator(int floor) {
        return elevators.stream()
            .min(Comparator.comparingInt(elevator -> 
                Math.abs(elevator.getCurrentFloor() - floor)))
            .orElseThrow(() -> new IllegalStateException("No elevators available"));
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
        System.out.println("Starting Elevator System Demo\n");
        ElevatorSystem system = new ElevatorSystem();

        // Test scenario 1: Multiple requests from different floors
        System.out.println("Scenario 1: Multiple requests from different floors");
        int[] requests1 = {5, 3, 7, 2, 8};
        for (int floor : requests1) {
            System.out.println("\nRequesting elevator for floor " + floor);
            system.requestElevator(floor);
            try {
                Thread.sleep(500); // Add delay between requests
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Let the system run for a while
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Test scenario 2: Requests in opposite directions
        System.out.println("\nScenario 2: Requests in opposite directions");
        int[] requests2 = {10, 15, 12, 8, 5};
        for (int floor : requests2) {
            System.out.println("\nRequesting elevator for floor " + floor);
            system.requestElevator(floor);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Let the system run for a while
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Shutdown the system
        system.shutdown();
    }
}
