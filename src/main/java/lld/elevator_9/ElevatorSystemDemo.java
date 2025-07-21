package lld.elevator_9;

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
    private final TreeSet<Integer> requests;
    private static final int MAX_FLOOR = 20;
    private static final int MIN_FLOOR = 1;

    public Elevator(int id) {
        this.id = id;
        this.currentFloor = 1;
        this.direction = Direction.IDLE;
        // Use TreeSet with custom comparator
        this.requests = new TreeSet<>((a, b) -> {
            // If elevator is going up, prioritize floors above current floor
            if (direction == Direction.UP) {
                return a.compareTo(b);
            }
            // If elevator is going down, prioritize floors below current floor
            else if (direction == Direction.DOWN) {
                return b.compareTo(a);
            }
            // If elevator is idle, prioritize the closest floor
            else {
                return Integer.compare(
                    Math.abs(a - currentFloor),
                    Math.abs(b - currentFloor)
                );
            }
        });
    }

    public void addRequest(int floor) {
        if (floor < MIN_FLOOR || floor > MAX_FLOOR) {
            throw new IllegalArgumentException("Invalid floor number");
        }

        if (floor != currentFloor) {
            requests.add(floor);
            // Update direction if elevator is idle
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

        // Get the next target floor based on current direction
        Integer nextFloor = direction == Direction.UP ? 
            requests.higher(currentFloor) : 
            requests.lower(currentFloor);

        // If no floor in current direction, switch direction
        if (nextFloor == null) {
            direction = direction == Direction.UP ? Direction.DOWN : Direction.UP;
            nextFloor = direction == Direction.UP ? 
                requests.higher(currentFloor) : 
                requests.lower(currentFloor);
        }

        if (nextFloor == null) return;

        if (nextFloor > currentFloor) {
            moveUp();
        } else if (nextFloor < currentFloor) {
            moveDown();
        } else {
            stop();
            requests.remove(currentFloor);
        }
    }

    private void moveUp() {
        direction = Direction.UP;
        currentFloor++;
        System.out.println("Elevator " + id + " moving up to floor " + currentFloor);

        if (requests.contains(currentFloor)) {
            stop();
            requests.remove(currentFloor);
        }
    }

    private void moveDown() {
        direction = Direction.DOWN;
        currentFloor--;
        System.out.println("Elevator " + id + " moving down to floor " + currentFloor);

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
        Elevator nearestElevator = null;
        int minDistance = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            int distance = Math.abs(elevator.getCurrentFloor() - floor);
            if (distance < minDistance) {
                minDistance = distance;
                nearestElevator = elevator;
            }
        }

        return nearestElevator;
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
