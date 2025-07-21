package lld.elevator_3;

import java.util.*;

// Direction enum for elevator movement
enum Direction {
    UP, DOWN, IDLE
}

// Elevator class to represent the elevator
class Elevator {
    private int currentFloor;
    private Direction direction;
    private PriorityQueue<Integer> upRequests;
    private PriorityQueue<Integer> downRequests;
    private boolean[] floorButtons;
    private int totalFloors;

    public Elevator(int totalFloors) {
        this.currentFloor = 1;
        this.direction = Direction.IDLE;
        this.upRequests = new PriorityQueue<>();
        this.downRequests = new PriorityQueue<>(Collections.reverseOrder());
        this.totalFloors = totalFloors;
        this.floorButtons = new boolean[totalFloors + 1];
    }

    public void requestFloor(int floor) {
        if (floor < 1 || floor > totalFloors) {
            System.out.println("Invalid floor request: " + floor);
            return;
        }

        if (floor == currentFloor) {
            System.out.println("Already on floor " + floor);
            return;
        }

        if (floor > currentFloor) {
            upRequests.add(floor);
        } else {
            downRequests.add(floor);
        }
        floorButtons[floor] = true;
    }

    public void move() {
        if (direction == Direction.IDLE) {
            if (!upRequests.isEmpty()) {
                direction = Direction.UP;
            } else if (!downRequests.isEmpty()) {
                direction = Direction.DOWN;
            }
        }

        if (direction == Direction.UP) {
            if (!upRequests.isEmpty()) {
                currentFloor++;
                System.out.println("Moving UP to floor: " + currentFloor);
                if (upRequests.peek() == currentFloor) {
                    upRequests.poll();
                    floorButtons[currentFloor] = false;
                    System.out.println("Stopping at floor: " + currentFloor);
                }
            } else if (!downRequests.isEmpty()) {
                direction = Direction.DOWN;
            } else {
                direction = Direction.IDLE;
            }
        } else if (direction == Direction.DOWN) {
            if (!downRequests.isEmpty()) {
                currentFloor--;
                System.out.println("Moving DOWN to floor: " + currentFloor);
                if (downRequests.peek() == currentFloor) {
                    downRequests.poll();
                    floorButtons[currentFloor] = false;
                    System.out.println("Stopping at floor: " + currentFloor);
                }
            } else if (!upRequests.isEmpty()) {
                direction = Direction.UP;
            } else {
                direction = Direction.IDLE;
            }
        }
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean hasRequests() {
        return !upRequests.isEmpty() || !downRequests.isEmpty();
    }
}

// ElevatorController class to manage multiple elevators
class ElevatorController {
    private List<Elevator> elevators;
    private int totalFloors;

    public ElevatorController(int numElevators, int totalFloors) {
        this.elevators = new ArrayList<>();
        this.totalFloors = totalFloors;
        for (int i = 0; i < numElevators; i++) {
            elevators.add(new Elevator(totalFloors));
        }
    }

    public void requestElevator(int floor) {
        Elevator bestElevator = findBestElevator(floor);
        bestElevator.requestFloor(floor);
    }

    private Elevator findBestElevator(int targetFloor) {
        Elevator bestElevator = null;
        int minDistance = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            int distance = Math.abs(elevator.getCurrentFloor() - targetFloor);
            if (distance < minDistance) {
                minDistance = distance;
                bestElevator = elevator;
            }
        }

        return bestElevator;
    }

    public void run() {
        while (true) {
            boolean anyElevatorHasRequests = false;
            for (Elevator elevator : elevators) {
                if (elevator.hasRequests()) {
                    anyElevatorHasRequests = true;
                    elevator.move();
                }
            }
            if (!anyElevatorHasRequests) {
                break;
            }
        }
    }
}

public class ElevatorDemonstration {
    public static void main(String[] args) {
        // Create an elevator controller with 2 elevators and 10 floors
        ElevatorController controller = new ElevatorController(2, 10);

        // Simulate elevator requests
        System.out.println("Starting elevator simulation...");
        
        // Request floors
        controller.requestElevator(5);
        controller.requestElevator(3);
        controller.requestElevator(7);
        controller.requestElevator(2);
        controller.requestElevator(9);

        // Run the elevator system
        controller.run();
        
        System.out.println("Elevator simulation completed!");
    }
}
