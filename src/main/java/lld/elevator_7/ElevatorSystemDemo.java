package lld.elevator_7;

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
    MOVING, STOPPED, DOOR_OPEN, MAINTENANCE
}

// Strategy interface for elevator selection
interface ElevatorSelectionStrategy {
    Elevator selectElevator(List<Elevator> elevators, int floor);
}

// Concrete strategy: Nearest Elevator Strategy
class NearestElevatorStrategy implements ElevatorSelectionStrategy {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, int floor) {
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
}

// Concrete strategy: Direction Based Strategy
class DirectionBasedStrategy implements ElevatorSelectionStrategy {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, int floor) {
        Elevator bestElevator = null;
        int bestScore = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            // Calculate a score for each elevator (lower is better)
            int score = Math.abs(elevator.getCurrentFloor() - floor) * 2; // Base score is distance

            // Prefer elevators going in the right direction
            if (floor > elevator.getCurrentFloor() && elevator.getDirection() == Direction.UP) {
                score -= 2; // Bonus for elevators going up when floor is above
            } else if (floor < elevator.getCurrentFloor() && elevator.getDirection() == Direction.DOWN) {
                score -= 2; // Bonus for elevators going down when floor is below
            } else if (elevator.getDirection() == Direction.IDLE) {
                score -= 1; // Small bonus for idle elevators
            }

            if (score < bestScore) {
                bestScore = score;
                bestElevator = elevator;
            }
        }

        return bestElevator;
    }
}

// Concrete strategy: Load Balancing Strategy
class LoadBalancingStrategy implements ElevatorSelectionStrategy {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, int floor) {
        Elevator leastBusyElevator = null;
        int minRequests = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            int totalRequests = elevator.getUpRequestsCount() + elevator.getDownRequestsCount();
            if (totalRequests < minRequests) {
                minRequests = totalRequests;
                leastBusyElevator = elevator;
            }
        }

        return leastBusyElevator;
    }
}

// State pattern: Interface for elevator states
interface ElevatorStateHandler {
    void handle(Elevator elevator);
    void move(Elevator elevator);
    void stop(Elevator elevator);
    void openDoor(Elevator elevator);
    void closeDoor(Elevator elevator);
}

// Concrete state: Moving State
class MovingState implements ElevatorStateHandler {
    @Override
    public void handle(Elevator elevator) {
        // Already moving, continue moving in the current direction
        if (elevator.getDirection() == Direction.UP) {
            moveUp(elevator);
        } else if (elevator.getDirection() == Direction.DOWN) {
            moveDown(elevator);
        }
    }

    private void moveUp(Elevator elevator) {
        elevator.setCurrentFloor(elevator.getCurrentFloor() + 1);
        System.out.println("Elevator " + elevator.getId() + " moving up to floor " + elevator.getCurrentFloor());

        // Check if we've reached a requested floor
        PriorityQueue<Integer> upRequests = elevator.getUpRequests();
        if (upRequests.contains(elevator.getCurrentFloor())) {
            stop(elevator);
            upRequests.remove(elevator.getCurrentFloor());
            openDoor(elevator);
        }
    }

    private void moveDown(Elevator elevator) {
        elevator.setCurrentFloor(elevator.getCurrentFloor() - 1);
        System.out.println("Elevator " + elevator.getId() + " moving down to floor " + elevator.getCurrentFloor());

        // Check if we've reached a requested floor
        PriorityQueue<Integer> downRequests = elevator.getDownRequests();
        if (downRequests.contains(elevator.getCurrentFloor())) {
            stop(elevator);
            downRequests.remove(elevator.getCurrentFloor());
            openDoor(elevator);
        }
    }

    @Override
    public void move(Elevator elevator) {
        // Already moving
    }

    @Override
    public void stop(Elevator elevator) {
        System.out.println("Elevator " + elevator.getId() + " stopped at floor " + elevator.getCurrentFloor());
        elevator.setState(ElevatorState.STOPPED);
        elevator.setStateHandler(new StoppedState());
    }

    @Override
    public void openDoor(Elevator elevator) {
        // Can't open doors while moving
        System.out.println("Cannot open door while elevator is moving");
    }

    @Override
    public void closeDoor(Elevator elevator) {
        // Doors are already closed while moving
    }
}

// Concrete state: Stopped State
class StoppedState implements ElevatorStateHandler {
    @Override
    public void handle(Elevator elevator) {
        // Check if there are any pending requests
        if (elevator.getUpRequests().isEmpty() && elevator.getDownRequests().isEmpty()) {
            elevator.setDirection(Direction.IDLE);
            return;
        }

        // Determine which direction to go next
        if (elevator.getDirection() == Direction.UP || elevator.getDirection() == Direction.IDLE) {
            if (!elevator.getUpRequests().isEmpty()) {
                move(elevator);
            } else {
                elevator.setDirection(Direction.DOWN);
                move(elevator);
            }
        } else { // Direction.DOWN
            if (!elevator.getDownRequests().isEmpty()) {
                move(elevator);
            } else {
                elevator.setDirection(Direction.UP);
                move(elevator);
            }
        }
    }

    @Override
    public void move(Elevator elevator) {
        elevator.setState(ElevatorState.MOVING);
        elevator.setStateHandler(new MovingState());
        elevator.getStateHandler().handle(elevator);
    }

    @Override
    public void stop(Elevator elevator) {
        // Already stopped
    }

    @Override
    public void openDoor(Elevator elevator) {
        System.out.println("Elevator " + elevator.getId() + " door opened at floor " + elevator.getCurrentFloor());
        elevator.setState(ElevatorState.DOOR_OPEN);
        elevator.setStateHandler(new DoorOpenState());
    }

    @Override
    public void closeDoor(Elevator elevator) {
        // Door is already closed in stopped state
    }
}

// Concrete state: Door Open State
class DoorOpenState implements ElevatorStateHandler {
    @Override
    public void handle(Elevator elevator) {
        // Door is open, close it after a delay
        try {
            Thread.sleep(2000); // Simulate door staying open for 2 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        closeDoor(elevator);
    }

    @Override
    public void move(Elevator elevator) {
        // Can't move with door open
        System.out.println("Cannot move while door is open");
    }

    @Override
    public void stop(Elevator elevator) {
        // Already stopped with door open
    }

    @Override
    public void openDoor(Elevator elevator) {
        // Door is already open
    }

    @Override
    public void closeDoor(Elevator elevator) {
        System.out.println("Elevator " + elevator.getId() + " door closed at floor " + elevator.getCurrentFloor());
        elevator.setState(ElevatorState.STOPPED);
        elevator.setStateHandler(new StoppedState());
    }
}

// Command pattern: Command interface
interface ElevatorCommand {
    void execute();
}

// Concrete command: Move elevator
class MoveElevatorCommand implements ElevatorCommand {
    private final Elevator elevator;

    public MoveElevatorCommand(Elevator elevator) {
        this.elevator = elevator;
    }

    @Override
    public void execute() {
        elevator.processNextState();
    }
}

// Concrete command: Request elevator
class RequestElevatorCommand implements ElevatorCommand {
    private final Elevator elevator;
    private final int floor;

    public RequestElevatorCommand(Elevator elevator, int floor) {
        this.elevator = elevator;
        this.floor = floor;
    }

    @Override
    public void execute() {
        elevator.addRequest(floor);
        System.out.println("Added request for floor " + floor + " to elevator " + elevator.getId());
    }
}

@Data
class Elevator {
    private int id;
    private int currentFloor;
    private Direction direction;
    private ElevatorState state;
    private PriorityQueue<Integer> upRequests;
    private PriorityQueue<Integer> downRequests;
    private ElevatorStateHandler stateHandler;
    
    private static final int MAX_FLOOR = 20;
    private static final int MIN_FLOOR = 1;

    public Elevator(int id) {
        this.id = id;
        this.currentFloor = 1;
        this.direction = Direction.IDLE;
        this.state = ElevatorState.STOPPED;
        this.upRequests = new PriorityQueue<>();
        this.downRequests = new PriorityQueue<>(Collections.reverseOrder());
        this.stateHandler = new StoppedState();
    }

    public void addRequest(int floor) {
        if (floor < MIN_FLOOR || floor > MAX_FLOOR) {
            throw new IllegalArgumentException("Invalid floor number");
        }

        if (floor > currentFloor) {
            upRequests.add(floor);
            if (direction == Direction.IDLE) {
                direction = Direction.UP;
            }
        } else if (floor < currentFloor) {
            downRequests.add(floor);
            if (direction == Direction.IDLE) {
                direction = Direction.DOWN;
            }
        }
        // If floor == currentFloor, no need to add a request
    }
    
    // This method replaces the old move() method
    public void processNextState() {
        stateHandler.handle(this);
    }
    
    // For backward compatibility with existing code
    public void move() {
        processNextState();
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
class ElevatorSystem {
    private List<Elevator> elevators;
    private static final int NUM_ELEVATORS = 4;
    private ElevatorSelectionStrategy selectionStrategy;
    private ScheduledExecutorService scheduler;
    private static final long ELEVATOR_MOVEMENT_INTERVAL = 1000;
    
    // Command executor for processing elevator commands
    private Queue<ElevatorCommand> commandQueue;

    public ElevatorSystem(ElevatorSelectionStrategy strategy) {
        this.elevators = new ArrayList<>();
        this.selectionStrategy = strategy;
        this.scheduler = Executors.newScheduledThreadPool(NUM_ELEVATORS);
        this.commandQueue = new LinkedList<>();

        for (int i = 1; i <= NUM_ELEVATORS; i++) {
            elevators.add(new Elevator(i));
        }
        startElevatorScheduling();
    }

    private void startElevatorScheduling() {
        scheduler.scheduleAtFixedRate(() -> {
            for (Elevator elevator : elevators) {
                ElevatorCommand moveCommand = new MoveElevatorCommand(elevator);
                addCommand(moveCommand);
            }
            processCommands();
        }, 0, ELEVATOR_MOVEMENT_INTERVAL, TimeUnit.MILLISECONDS);
    }

    public void requestElevator(int floor) {
        if (floor < 1 || floor > 20) {
            throw new IllegalArgumentException("Invalid floor number: " + floor);
        }

        Elevator selectedElevator = selectionStrategy.selectElevator(elevators, floor);
        if (selectedElevator != null) {
            ElevatorCommand requestCommand = new RequestElevatorCommand(selectedElevator, floor);
            addCommand(requestCommand);
        }
    }
    
    private void addCommand(ElevatorCommand command) {
        commandQueue.add(command);
    }
    
    private void processCommands() {
        while (!commandQueue.isEmpty()) {
            ElevatorCommand command = commandQueue.poll();
            command.execute();
        }
    }

    public void shutdown() {
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
    }
}

// Main class to demonstrate the elevator system
public class ElevatorSystemDemo {
    public static void main(String[] args) {
        System.out.println("Starting Elevator System with different strategies\n");

        // Test with nearest elevator strategy
        System.out.println("=== Using Nearest Elevator Strategy ===");
        ElevatorSystem nearestSystem = new ElevatorSystem(new NearestElevatorStrategy());
        testElevatorSystem(nearestSystem);
        nearestSystem.shutdown();

        // Add a delay between tests
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Test with direction based strategy
        System.out.println("\n=== Using Direction Based Strategy ===");
        ElevatorSystem directionSystem = new ElevatorSystem(new DirectionBasedStrategy());
        testElevatorSystem(directionSystem);
        directionSystem.shutdown();

        // Add a delay between tests
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Test with load balancing strategy
        System.out.println("\n=== Using Load Balancing Strategy ===");
        ElevatorSystem loadBalancingSystem = new ElevatorSystem(new LoadBalancingStrategy());
        testElevatorSystem(loadBalancingSystem);
        loadBalancingSystem.shutdown();
    }

    private static void testElevatorSystem(ElevatorSystem system) {
        // Make multiple elevator requests
        system.requestElevator(5);
        system.requestElevator(10);
        system.requestElevator(3);
        system.requestElevator(18);

        // Let the system run for a while to process the requests
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Make additional requests
        system.requestElevator(7);
        system.requestElevator(12);

        // Let the system run a bit more
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
