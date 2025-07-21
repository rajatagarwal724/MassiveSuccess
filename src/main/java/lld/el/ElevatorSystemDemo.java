package lld.el;

import java.util.*;
import java.util.concurrent.*;

/**
 * Elevator System demo - shows creation of a building with multiple elevators
 * and processes elevator requests.
 */
public class ElevatorSystemDemo {
    public static void main(String[] args) throws InterruptedException {
        int numFloors = 10;
        int numElevators = 3;

        // Create elevator system with 10 floors and 3 elevators
        ElevatorSystem elevatorSystem = new ElevatorSystem(numFloors, numElevators);

        // Start elevator system (starts elevator controller threads)
        elevatorSystem.start();

        // External request from floor 3 to go UP
        elevatorSystem.requestElevator(3, Direction.UP);
        Thread.sleep(1000); // simulate time passing

        // External request from floor 8 to go DOWN
        elevatorSystem.requestElevator(8, Direction.DOWN);
        Thread.sleep(1500);

        // Get status of all elevators
        elevatorSystem.getElevatorStatuses().forEach(System.out::println);

        // Simulate an internal request (from inside elevator)
        ElevatorController controller = elevatorSystem.getElevatorControllers().get(0);
        controller.addInternalRequest(5); // Person inside selected floor 5

        Thread.sleep(3000);
        elevatorSystem.shutdown();
    }
}

/**
 * Main controller for the elevator system, manages multiple elevators and processes
 * requests for elevator service.
 */
class ElevatorSystem {
    private final int numFloors;
    private final List<ElevatorController> elevatorControllers;
    private final ExecutorService executorService;
    private final PriorityBlockingQueue<Request> pendingRequests;
    private final ScheduledExecutorService schedulerService;
    private volatile boolean running;

    public ElevatorSystem(int numFloors, int numElevators) {
        this.numFloors = numFloors;
        this.elevatorControllers = new ArrayList<>();
        this.executorService = Executors.newFixedThreadPool(numElevators);
        this.pendingRequests = new PriorityBlockingQueue<>();
        this.schedulerService = Executors.newSingleThreadScheduledExecutor();
        this.running = false;

        // Initialize elevator controllers
        for (int i = 0; i < numElevators; i++) {
            elevatorControllers.add(new ElevatorController(i, numFloors));
        }
    }

    public void start() {
        running = true;

        // Start each elevator controller
        for (ElevatorController controller : elevatorControllers) {
            executorService.submit(controller);
        }

        // Start request processor
        schedulerService.scheduleAtFixedRate(() -> {
            if (!pendingRequests.isEmpty()) {
                Request request = pendingRequests.poll();
                processRequest(request);
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        running = false;
        schedulerService.shutdownNow();
        executorService.shutdownNow();
        System.out.println("Elevator system shutting down...");
    }

    public void requestElevator(int floor, Direction direction) {
        if (floor < 0 || floor >= numFloors) {
            throw new IllegalArgumentException("Invalid floor number: " + floor);
        }

        System.out.printf("Request received: Floor %d, Direction %s\n", floor, direction);
        pendingRequests.add(new Request(floor, direction, System.currentTimeMillis()));
    }

    private void processRequest(Request request) {
        // Find the best elevator to handle this request
        ElevatorController bestElevator = findBestElevator(request.floor, request.direction);
        System.out.printf("Assigning elevator %d to handle request at floor %d going %s\n",
                bestElevator.getId(), request.floor, request.direction);
        bestElevator.addExternalRequest(request.floor, request.direction);
    }

    private ElevatorController findBestElevator(int requestedFloor, Direction requestedDirection) {
        // This is a simplified elevator selection algorithm
        // A more sophisticated algorithm would consider direction, load, etc.

        // First, try to find an elevator already going in the same direction
        for (ElevatorController controller : elevatorControllers) {
            Elevator elevator = controller.getElevator();

            // Elevator moving in same direction and will pass the requested floor
            if (elevator.getDirection() == requestedDirection) {
                if ((requestedDirection == Direction.UP && elevator.getCurrentFloor() < requestedFloor) ||
                        (requestedDirection == Direction.DOWN && elevator.getCurrentFloor() > requestedFloor)) {
                    return controller;
                }
            }
        }

        // Then, try to find an idle elevator
        for (ElevatorController controller : elevatorControllers) {
            if (controller.getElevator().getState() == ElevatorState.IDLE) {
                return controller;
            }
        }

        // If no ideal elevator, find closest one
        ElevatorController closestElevator = elevatorControllers.get(0);
        int minDistance = Math.abs(closestElevator.getElevator().getCurrentFloor() - requestedFloor);

        for (int i = 1; i < elevatorControllers.size(); i++) {
            ElevatorController controller = elevatorControllers.get(i);
            int distance = Math.abs(controller.getElevator().getCurrentFloor() - requestedFloor);

            if (distance < minDistance) {
                minDistance = distance;
                closestElevator = controller;
            }
        }

        return closestElevator;
    }

    public List<String> getElevatorStatuses() {
        List<String> statuses = new ArrayList<>();
        for (ElevatorController controller : elevatorControllers) {
            Elevator elevator = controller.getElevator();
            statuses.add(String.format("Elevator %d: Floor %d, State %s, Direction %s",
                    controller.getId(),
                    elevator.getCurrentFloor(),
                    elevator.getState(),
                    elevator.getDirection()));
        }
        return statuses;
    }

    public List<ElevatorController> getElevatorControllers() {
        return Collections.unmodifiableList(elevatorControllers);
    }

    private static class Request implements Comparable<Request> {
        private final int floor;
        private final Direction direction;
        private final long timestamp;

        public Request(int floor, Direction direction, long timestamp) {
            this.floor = floor;
            this.direction = direction;
            this.timestamp = timestamp;
        }

        @Override
        public int compareTo(Request other) {
            // Order by timestamp (FIFO by default)
            return Long.compare(this.timestamp, other.timestamp);
        }
    }
}

/**
 * Represents the physical elevator car and manages its state and movement.
 */
class Elevator {
    private final int id;
    private final int maxFloor;
    private int currentFloor;
    private Direction direction;
    private ElevatorState state;

    public Elevator(int id, int maxFloor) {
        this.id = id;
        this.maxFloor = maxFloor;
        this.currentFloor = 0; // Ground floor
        this.direction = Direction.NONE;
        this.state = ElevatorState.IDLE;
    }

    public void moveUp() {
        if (currentFloor < maxFloor) {
            currentFloor++;
            System.out.printf("Elevator %d moved up to floor %d\n", id, currentFloor);
        }
    }

    public void moveDown() {
        if (currentFloor > 0) {
            currentFloor--;
            System.out.printf("Elevator %d moved down to floor %d\n", id, currentFloor);
        }
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public ElevatorState getState() {
        return state;
    }

    public void setState(ElevatorState state) {
        this.state = state;
    }
}

/**
 * Manages a single elevator's operations, processes requests for that elevator,
 * and runs as a separate thread simulating elevator movement.
 */
class ElevatorController implements Runnable {
    private final int id;
    private final Elevator elevator;
    private final TreeSet<Integer> upRequests; // Floors to visit when going up
    private final TreeSet<Integer> downRequests; // Floors to visit when going down
    private volatile boolean running;

    public ElevatorController(int id, int maxFloor) {
        this.id = id;
        this.elevator = new Elevator(id, maxFloor);
        this.upRequests = new TreeSet<>();
        this.downRequests = new TreeSet<>();
        this.running = true;
    }

    public int getId() {
        return id;
    }

    public Elevator getElevator() {
        return elevator;
    }

    public void addExternalRequest(int floor, Direction direction) {
        synchronized (this) {
            if (direction == Direction.UP) {
                upRequests.add(floor);
                if (elevator.getState() == ElevatorState.IDLE) {
                    if (floor > elevator.getCurrentFloor()) {
                        elevator.setDirection(Direction.UP);
                        elevator.setState(ElevatorState.MOVING_UP);
                    } else if (floor < elevator.getCurrentFloor()) {
                        elevator.setDirection(Direction.DOWN);
                        elevator.setState(ElevatorState.MOVING_DOWN);
                    }
                }
            } else if (direction == Direction.DOWN) {
                downRequests.add(floor);
                if (elevator.getState() == ElevatorState.IDLE) {
                    if (floor > elevator.getCurrentFloor()) {
                        elevator.setDirection(Direction.UP);
                        elevator.setState(ElevatorState.MOVING_UP);
                    } else if (floor < elevator.getCurrentFloor()) {
                        elevator.setDirection(Direction.DOWN);
                        elevator.setState(ElevatorState.MOVING_DOWN);
                    }
                }
            }
            this.notifyAll(); // Wake up thread if it's waiting
        }
    }

    public void addInternalRequest(int floor) {
        synchronized (this) {
            if (floor > elevator.getCurrentFloor()) {
                upRequests.add(floor);
                if (elevator.getState() == ElevatorState.IDLE) {
                    elevator.setDirection(Direction.UP);
                    elevator.setState(ElevatorState.MOVING_UP);
                }
            } else if (floor < elevator.getCurrentFloor()) {
                downRequests.add(floor);
                if (elevator.getState() == ElevatorState.IDLE) {
                    elevator.setDirection(Direction.DOWN);
                    elevator.setState(ElevatorState.MOVING_DOWN);
                }
            }
            this.notifyAll();
        }
    }

    @Override
    public void run() {
        System.out.printf("Elevator %d controller started\n", id);

        while (running) {
            try {
                synchronized (this) {
                    // If no requests, wait
                    if (upRequests.isEmpty() && downRequests.isEmpty()) {
                        elevator.setState(ElevatorState.IDLE);
                        elevator.setDirection(Direction.NONE);
                        this.wait(1000); // Wait for requests or timeout
                        continue;
                    }
                }

                switch (elevator.getState()) {
                    case MOVING_UP:
                        processUpRequest();
                        break;
                    case MOVING_DOWN:
                        processDownRequest();
                        break;
                    case IDLE:
                        determineDirection();
                        break;
                    case MAINTENANCE:
                        // Do nothing when in maintenance
                        break;
                }

                // Simulate elevator movement time
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                System.out.println("Elevator controller interrupted");
                running = false;
            }
        }
    }

    private void processUpRequest() {
        synchronized (this) {
            // Check if we have a stop at the current floor
            Integer currentFloor = elevator.getCurrentFloor();
            if (upRequests.contains(currentFloor)) {
                upRequests.remove(currentFloor);
                System.out.printf("Elevator %d stopped at floor %d (going up)\n", id, currentFloor);
                // Simulate door opening and closing
                try { Thread.sleep(500); } catch (InterruptedException e) { }
            }

            // Find next floor to visit
            Integer nextFloor = upRequests.higher(currentFloor);

            // If no more floors above, check if we need to change direction
            if (nextFloor == null) {
                if (!downRequests.isEmpty()) {
                    elevator.setDirection(Direction.DOWN);
                    elevator.setState(ElevatorState.MOVING_DOWN);
                } else {
                    elevator.setDirection(Direction.NONE);
                    elevator.setState(ElevatorState.IDLE);
                }
            } else {
                // Move up one floor at a time until we reach the target
                elevator.moveUp();
            }
        }
    }

    private void processDownRequest() {
        synchronized (this) {
            // Check if we have a stop at the current floor
            Integer currentFloor = elevator.getCurrentFloor();
            if (downRequests.contains(currentFloor)) {
                downRequests.remove(currentFloor);
                System.out.printf("Elevator %d stopped at floor %d (going down)\n", id, currentFloor);
                // Simulate door opening and closing
                try { Thread.sleep(500); } catch (InterruptedException e) { }
            }

            // Find next floor to visit
            Integer nextFloor = downRequests.lower(currentFloor);

            // If no more floors below, check if we need to change direction
            if (nextFloor == null) {
                if (!upRequests.isEmpty()) {
                    elevator.setDirection(Direction.UP);
                    elevator.setState(ElevatorState.MOVING_UP);
                } else {
                    elevator.setDirection(Direction.NONE);
                    elevator.setState(ElevatorState.IDLE);
                }
            } else {
                // Move down one floor at a time until we reach the target
                elevator.moveDown();
            }
        }
    }

    private void determineDirection() {
        synchronized (this) {
            if (!upRequests.isEmpty()) {
                elevator.setDirection(Direction.UP);
                elevator.setState(ElevatorState.MOVING_UP);
            } else if (!downRequests.isEmpty()) {
                elevator.setDirection(Direction.DOWN);
                elevator.setState(ElevatorState.MOVING_DOWN);
            }
        }
    }

    public void shutdown() {
        running = false;
    }
}

enum Direction {
    UP, DOWN, NONE
}

enum ElevatorState {
    IDLE, MOVING_UP, MOVING_DOWN, MAINTENANCE
}
