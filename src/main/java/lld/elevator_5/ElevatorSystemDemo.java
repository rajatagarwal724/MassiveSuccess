package lld.elevator_5;

import lombok.Data;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Enum to represent elevator direction
enum Direction {
    UP, DOWN, IDLE
}

// State interface for elevator states
interface ElevatorState {
    void handleRequest(Elevator elevator, int floor);
    void move(Elevator elevator);
    String getStateName();
}

// Concrete state: Idle State
class IdleState implements ElevatorState {
    @Override
    public void handleRequest(Elevator elevator, int floor) {
        if (floor > elevator.getCurrentFloor()) {
            elevator.setDirection(Direction.UP);
            elevator.getUpRequests().add(floor);
            elevator.setState(new MovingUpState());
        } else if (floor < elevator.getCurrentFloor()) {
            elevator.setDirection(Direction.DOWN);
            elevator.getDownRequests().add(floor);
            elevator.setState(new MovingDownState());
        }
    }

    @Override
    public void move(Elevator elevator) {
        // Idle state doesn't move
    }

    @Override
    public String getStateName() {
        return "IDLE";
    }
}

// Concrete state: Moving Up State
class MovingUpState implements ElevatorState {
    @Override
    public void handleRequest(Elevator elevator, int floor) {
        if (floor > elevator.getCurrentFloor()) {
            elevator.getUpRequests().add(floor);
        } else {
            elevator.getDownRequests().add(floor);
        }
    }

    @Override
    public void move(Elevator elevator) {
        if (elevator.getUpRequests().isEmpty()) {
            if (elevator.getDownRequests().isEmpty()) {
                elevator.setState(new IdleState());
                elevator.setDirection(Direction.IDLE);
            } else {
                elevator.setState(new MovingDownState());
                elevator.setDirection(Direction.DOWN);
            }
            return;
        }

        elevator.setCurrentFloor(elevator.getCurrentFloor() + 1);
        System.out.println("Elevator " + elevator.getId() + " moving up to floor " + elevator.getCurrentFloor());

        if (elevator.getUpRequests().contains(elevator.getCurrentFloor())) {
            elevator.getUpRequests().remove(elevator.getCurrentFloor());
            elevator.setState(new DoorOpenState());
        }
    }

    @Override
    public String getStateName() {
        return "MOVING_UP";
    }
}

// Concrete state: Moving Down State
class MovingDownState implements ElevatorState {
    @Override
    public void handleRequest(Elevator elevator, int floor) {
        if (floor < elevator.getCurrentFloor()) {
            elevator.getDownRequests().add(floor);
        } else {
            elevator.getUpRequests().add(floor);
        }
    }

    @Override
    public void move(Elevator elevator) {
        if (elevator.getDownRequests().isEmpty()) {
            if (elevator.getUpRequests().isEmpty()) {
                elevator.setState(new IdleState());
                elevator.setDirection(Direction.IDLE);
            } else {
                elevator.setState(new MovingUpState());
                elevator.setDirection(Direction.UP);
            }
            return;
        }

        elevator.setCurrentFloor(elevator.getCurrentFloor() - 1);
        System.out.println("Elevator " + elevator.getId() + " moving down to floor " + elevator.getCurrentFloor());

        if (elevator.getDownRequests().contains(elevator.getCurrentFloor())) {
            elevator.getDownRequests().remove(elevator.getCurrentFloor());
            elevator.setState(new DoorOpenState());
        }
    }

    @Override
    public String getStateName() {
        return "MOVING_DOWN";
    }
}

// Concrete state: Door Open State
class DoorOpenState implements ElevatorState {
    @Override
    public void handleRequest(Elevator elevator, int floor) {
        if (floor > elevator.getCurrentFloor()) {
            elevator.getUpRequests().add(floor);
        } else if (floor < elevator.getCurrentFloor()) {
            elevator.getDownRequests().add(floor);
        }
    }

    @Override
    public void move(Elevator elevator) {
        System.out.println("Elevator " + elevator.getId() + " door opened at floor " + elevator.getCurrentFloor());
        // Simulate door closing after a delay
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Elevator " + elevator.getId() + " door closed at floor " + elevator.getCurrentFloor());
        
        if (!elevator.getUpRequests().isEmpty()) {
            elevator.setState(new MovingUpState());
            elevator.setDirection(Direction.UP);
        } else if (!elevator.getDownRequests().isEmpty()) {
            elevator.setState(new MovingDownState());
            elevator.setDirection(Direction.DOWN);
        } else {
            elevator.setState(new IdleState());
            elevator.setDirection(Direction.IDLE);
        }
    }

    @Override
    public String getStateName() {
        return "DOOR_OPEN";
    }
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
        this.state = new IdleState();
        this.upRequests = new PriorityQueue<>();
        this.downRequests = new PriorityQueue<>(Collections.reverseOrder());
    }

    public void addRequest(int floor) {
        if (floor < MIN_FLOOR || floor > MAX_FLOOR) {
            throw new IllegalArgumentException("Invalid floor number");
        }
        state.handleRequest(this, floor);
    }

    public void move() {
        state.move(this);
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

