package lld.elevator2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Data;


interface Observer {
    void update(int floorNumber, Direction direction, DoorState doorState);
}

abstract class Button {
    protected boolean status;

    public Button(boolean status) {
        this.status = status;
    }

    public void press() {
        if (!isPressed()) {
            status = true;
        } else {
            status = false;
        }
    }

    public abstract boolean isPressed();
}

enum Direction {
    UP,
    DOWN,
    DEFAULT
}

class HallButton extends Button {
    private Direction direction;

    public HallButton(boolean status, Direction direction) {
        super(status);
        this.direction = direction;
    }

    @Override
    public boolean isPressed() {
        return status;
    }

    public Direction getDirection() {
        return direction;
    }
}

@Data
class ElevatorButton extends Button {
    private int destinationFloorNumber;

    public ElevatorButton(boolean status, int destinationFloorNumber) {
        super(status);
        this.destinationFloorNumber = destinationFloorNumber;
    }

    @Override
    public boolean isPressed() {
        return status;
    }

    public int getDestinationFloorNumber() {
        return destinationFloorNumber;
    }
}

class DoorButton extends Button {

    public DoorButton(boolean status) {
        super(status);
    }

    @Override
    public boolean isPressed() {
        return status;
    }
}

@Data
class ElevatorPanel {
    private List<ElevatorButton> elevatorButtons;
    private Button openButton;
    private Button closeButton;

    public ElevatorPanel(List<ElevatorButton> elevatorButtons, Button openButton, Button closeButton) {
        this.elevatorButtons = elevatorButtons;
        this.openButton = openButton;
        this.closeButton = closeButton;
    }

    public void pressElevatorButton(int floorNumber) {
        elevatorButtons.get(floorNumber).press();
    }

    public void pressOpenButton() {
        openButton.press();
    }

    public void pressCloseButton() {
        closeButton.press();
    }
}

@Data
class HallPanel {
    private Button upButton;
    private Button downButton;

    public HallPanel(Button upButton, Button downButton) {
        this.upButton = upButton;
        this.downButton = downButton;
    }

    public void pressUpButton() {
        upButton.press();
    }

    public void pressDownButton() {
        downButton.press();
    }
}

enum DoorState {
    OPEN,
    CLOSED
}

@Data
class Door {
    private DoorState state;

    public Door(DoorState state) {
        this.state = state;
    }

    public void openDoor() {
        state = DoorState.OPEN;
    }

    public void closeDoor() {
        state = DoorState.CLOSED;
    }
}


abstract class Display implements Observer {
    protected int currentFloor;
    protected Direction currentDirection;
    protected DoorState doorState;

    public Display(int currentFloor, Direction currentDirection) {
        this.currentFloor = currentFloor;
        this.currentDirection = currentDirection;
        this.doorState = DoorState.CLOSED;
    }

    @Override
    public void update(int floorNumber, Direction direction, DoorState doorState) {
        this.currentFloor = floorNumber;
        this.currentDirection = direction;
        this.doorState = doorState;
        display();
    }

    public abstract void display();
}

@Data
class ElevatorDisplay extends Display {
    private int capacity;
    private int currentPassengers;

    public ElevatorDisplay(int currentFloor, Direction currentDirection, int capacity) {
        super(currentFloor, currentDirection);
        this.capacity = capacity;
        this.currentPassengers = 0;
    }

    @Override
    public void display() {
        System.out.println("Elevator Display:");
        System.out.println("Current Floor: " + currentFloor);
        System.out.println("Direction: " + currentDirection);
        System.out.println("Door State: " + doorState);
        System.out.println("Passengers: " + currentPassengers + "/" + capacity);
    }
}

@Data
class HallDisplay extends Display {
    public HallDisplay(int currentFloor, Direction currentDirection) {
        super(currentFloor, currentDirection);
    }

    @Override
    public void display() {
        System.out.println("Hall Display:");
        System.out.println("Current Floor: " + currentFloor);
        System.out.println("Direction: " + currentDirection);
        System.out.println("Door State: " + doorState);
    }
}

interface ElevatorState {
    void handleRequest(Elevator elevator, int destinationFloorNumber);
    void handleDoorOperation(Elevator elevator);
    void handleArrival(Elevator elevator);
}

class MovingUpElevatorState implements ElevatorState {
    @Override
    public void handleRequest(Elevator elevator, int destinationFloorNumber) {
        if (elevator.getCurrentFloor() < destinationFloorNumber) {
            System.out.println("Elevator is moving up from " + elevator.getCurrentFloor() + " to " + (elevator.getCurrentFloor() + 1));
            elevator.setCurrentFloor(elevator.getCurrentFloor() + 1);
        } else {
            // We've reached the destination
            elevator.removeRequest(destinationFloorNumber);
            elevator.setState(new IdleElevatorState());
            elevator.openDoor();
        }
    }

    @Override
    public void handleDoorOperation(Elevator elevator) {
        System.out.println("Cannot operate doors while elevator is moving");
    }

    @Override
    public void handleArrival(Elevator elevator) {
        elevator.setState(new IdleElevatorState());
        elevator.openDoor();
    }
}

class MovingDownElevatorState implements ElevatorState {
    @Override
    public void handleRequest(Elevator elevator, int destinationFloorNumber) {
        if (elevator.getCurrentFloor() > destinationFloorNumber) {
            System.out.println("Elevator is moving down from " + elevator.getCurrentFloor() + " to " + (elevator.getCurrentFloor() - 1));
            elevator.setCurrentFloor(elevator.getCurrentFloor() - 1);
        } else {
            // We've reached the destination
            elevator.removeRequest(destinationFloorNumber);
            elevator.setState(new IdleElevatorState());
            elevator.openDoor();
        }
    }

    @Override
    public void handleDoorOperation(Elevator elevator) {
        System.out.println("Cannot operate doors while elevator is moving");
    }

    @Override
    public void handleArrival(Elevator elevator) {
        elevator.setState(new IdleElevatorState());
        elevator.openDoor();
    }
}

class IdleElevatorState implements ElevatorState {
    @Override
    public void handleRequest(Elevator elevator, int destinationFloorNumber) {
        if (elevator.getCurrentFloor() == destinationFloorNumber) {
            System.out.println("Elevator is already at floor " + elevator.getCurrentFloor());
            elevator.openDoor();
            elevator.removeRequest(destinationFloorNumber);
        } else if (elevator.getCurrentFloor() < destinationFloorNumber) {
            elevator.setState(new MovingUpElevatorState());
            elevator.move();
        } else {
            elevator.setState(new MovingDownElevatorState());
            elevator.move();
        }
    }

    @Override
    public void handleDoorOperation(Elevator elevator) {
        if (elevator.getDoor().getState() == DoorState.CLOSED) {
            elevator.openDoor();
        } else {
            elevator.closeDoor();
        }
    }

    @Override
    public void handleArrival(Elevator elevator) {
        elevator.openDoor();
    }
}

        
@Data
class Elevator {
    private String id;
    private Door door;
    private ElevatorState state;
    private int currentFloor;
    private ElevatorPanel elevatorPanel;
    private Display elevatorDisplay;
    private List<Observer> observers;
    private List<Integer> pendingRequests;
    private Direction currentDirection;

    public Elevator(String id, Door door, ElevatorState state, int currentFloor, 
                   ElevatorPanel elevatorPanel, Display elevatorDisplay) {
        this.id = id;
        this.door = door;
        this.state = state;
        this.currentFloor = currentFloor;
        this.elevatorPanel = elevatorPanel;
        this.elevatorDisplay = elevatorDisplay;
        this.observers = new ArrayList<>();
        this.pendingRequests = new ArrayList<>();
        this.currentDirection = Direction.DEFAULT;
        addObserver(elevatorDisplay);
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(currentFloor, currentDirection, door.getState());
        }
    }

    public void move(int nextFloor, Direction nextDirection) {
        this.currentDirection = nextDirection;
        state.handleRequest(this, nextFloor);
        notifyObservers();
    }

    public void stop() {
        state.handleRequest(this, currentFloor);
        notifyObservers();
    }

    public void openDoor() {
        if (door.getState() == DoorState.CLOSED) {
            door.openDoor();
            notifyObservers();
        }
    }

    public void closeDoor() {
        if (door.getState() == DoorState.OPEN) {
            door.closeDoor();
            notifyObservers();
        }
    }

    public void addRequest(int floorNumber) {
        if (!pendingRequests.contains(floorNumber)) {
            pendingRequests.add(floorNumber);
            Collections.sort(pendingRequests);
        }
    }

    public void removeRequest(int floorNumber) {
        pendingRequests.remove(Integer.valueOf(floorNumber));
    }
}

@Data
class Floor {
    private int floorNumber;
    private List<HallPanel> hallPanels;
    private List<Display> hallDisplays;

    public Floor(int floorNumber, List<HallPanel> hallPanels, List<Display> hallDisplays) {
        this.floorNumber = floorNumber;
        this.hallPanels = hallPanels;
        this.hallDisplays = hallDisplays;
    }
}

@Data
class Building {
    private List<Floor> floors;
    private List<Elevator> elevators;

    public Building(List<Floor> floors, List<Elevator> elevators) {
        this.floors = floors;
        this.elevators = elevators;
    }
}

interface ElevatorSchedulingStrategy {
    Elevator selectElevator(List<Elevator> elevators, int targetFloor);
    int getNextFloor(Elevator elevator);
    Direction getNextDirection(Elevator elevator);
}

class LOOKSchedulingStrategy implements ElevatorSchedulingStrategy {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, int targetFloor) {
        Elevator bestElevator = null;
        int minDistance = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            // Skip elevators that are moving in the opposite direction
            if (elevator.getCurrentDirection() == Direction.UP && elevator.getCurrentFloor() > targetFloor) {
                continue;
            }
            if (elevator.getCurrentDirection() == Direction.DOWN && elevator.getCurrentFloor() < targetFloor) {
                continue;
            }

            int distance = Math.abs(elevator.getCurrentFloor() - targetFloor);
            if (distance < minDistance) {
                minDistance = distance;
                bestElevator = elevator;
            }
        }

        return bestElevator;
    }

    @Override
    public int getNextFloor(Elevator elevator) {
        List<Integer> pendingRequests = elevator.getPendingRequests();
        if (pendingRequests.isEmpty()) {
            return elevator.getCurrentFloor();
        }

        Direction currentDirection = elevator.getCurrentDirection();
        if (currentDirection == Direction.UP) {
            // Find the next floor above current floor
            for (int floor : pendingRequests) {
                if (floor > elevator.getCurrentFloor()) {
                    return floor;
                }
            }
            // If no floors above, change direction
            return getNextFloorInDirection(elevator, Direction.DOWN);
        } else if (currentDirection == Direction.DOWN) {
            // Find the next floor below current floor
            for (int i = pendingRequests.size() - 1; i >= 0; i--) {
                int floor = pendingRequests.get(i);
                if (floor < elevator.getCurrentFloor()) {
                    return floor;
                }
            }
            // If no floors below, change direction
            return getNextFloorInDirection(elevator, Direction.UP);
        } else {
            // If no direction set, choose the closest floor
            return getClosestFloor(elevator);
        }
    }

    @Override
    public Direction getNextDirection(Elevator elevator) {
        List<Integer> pendingRequests = elevator.getPendingRequests();
        if (pendingRequests.isEmpty()) {
            return Direction.DEFAULT;
        }

        Direction currentDirection = elevator.getCurrentDirection();
        if (currentDirection == Direction.UP) {
            // Check if there are any floors above
            for (int floor : pendingRequests) {
                if (floor > elevator.getCurrentFloor()) {
                    return Direction.UP;
                }
            }
            return Direction.DOWN;
        } else if (currentDirection == Direction.DOWN) {
            // Check if there are any floors below
            for (int floor : pendingRequests) {
                if (floor < elevator.getCurrentFloor()) {
                    return Direction.DOWN;
                }
            }
            return Direction.UP;
        } else {
            // If no direction set, determine based on closest floor
            int closestFloor = getClosestFloor(elevator);
            return closestFloor > elevator.getCurrentFloor() ? Direction.UP : Direction.DOWN;
        }
    }

    private int getNextFloorInDirection(Elevator elevator, Direction direction) {
        List<Integer> pendingRequests = elevator.getPendingRequests();
        if (direction == Direction.UP) {
            for (int floor : pendingRequests) {
                if (floor > elevator.getCurrentFloor()) {
                    return floor;
                }
            }
        } else {
            for (int i = pendingRequests.size() - 1; i >= 0; i--) {
                int floor = pendingRequests.get(i);
                if (floor < elevator.getCurrentFloor()) {
                    return floor;
                }
            }
        }
        return elevator.getCurrentFloor();
    }

    private int getClosestFloor(Elevator elevator) {
        List<Integer> pendingRequests = elevator.getPendingRequests();
        int closestFloor = pendingRequests.get(0);
        int minDistance = Math.abs(elevator.getCurrentFloor() - closestFloor);
        
        for (int floor : pendingRequests) {
            int distance = Math.abs(elevator.getCurrentFloor() - floor);
            if (distance < minDistance) {
                minDistance = distance;
                closestFloor = floor;
            }
        }
        
        return closestFloor;
    }
}

class ElevatorSystem {
    private Building building;
    private ElevatorSchedulingStrategy schedulingStrategy;

    public ElevatorSystem(Building building) {
        this.building = building;
        this.schedulingStrategy = new LOOKSchedulingStrategy();
    }

    public void setSchedulingStrategy(ElevatorSchedulingStrategy strategy) {
        this.schedulingStrategy = strategy;
    }

    public void requestElevator(int floorNumber) {
        if (floorNumber < 0 || floorNumber >= building.getFloors().size()) {
            throw new IllegalArgumentException("Invalid floor number: " + floorNumber);
        }

        Elevator elevator = schedulingStrategy.selectElevator(building.getElevators(), floorNumber);
        if (elevator == null) {
            System.out.println("No available elevator found");
            return;
        }

        elevator.addRequest(floorNumber);
        
        // If elevator is idle, start moving
        if (elevator.getState() instanceof IdleElevatorState) {
            int nextFloor = schedulingStrategy.getNextFloor(elevator);
            Direction nextDirection = schedulingStrategy.getNextDirection(elevator);
            elevator.move(nextFloor, nextDirection);
        }
    }

    public void processElevatorMovement() {
        for (Elevator elevator : building.getElevators()) {
            if (!(elevator.getState() instanceof IdleElevatorState)) {
                int nextFloor = schedulingStrategy.getNextFloor(elevator);
                Direction nextDirection = schedulingStrategy.getNextDirection(elevator);
                elevator.move(nextFloor, nextDirection);
            }
        }
    }

    public static void main(String[] args) {
        HallPanel hallPanel1 = new HallPanel(new DoorButton(false), new DoorButton(false));
        HallPanel hallPanel2 = new HallPanel(new DoorButton(false), new DoorButton(false));
        HallPanel hallPanel3 = new HallPanel(new DoorButton(false), new DoorButton(false));
        HallDisplay hallDisplay1 = new HallDisplay(0, Direction.DEFAULT);
        HallDisplay hallDisplay2 = new HallDisplay(0, Direction.DEFAULT);
        HallDisplay hallDisplay3 = new HallDisplay(0, Direction.DEFAULT);
        List<Floor> floors = IntStream.rangeClosed(0, 10).mapToObj(i -> {
            return new Floor(i, List.of(hallPanel1, hallPanel2, hallPanel3), List.of(hallDisplay1, hallDisplay2, hallDisplay3));
        }).collect(Collectors.toList());

        List<ElevatorButton> elevatorButtons = IntStream.rangeClosed(0, 10).mapToObj(i -> {
            return new ElevatorButton(false, i);
        }).collect(Collectors.toList());

        ElevatorPanel elevatorPanel = new ElevatorPanel(elevatorButtons, new DoorButton(false), new DoorButton(false));

        Elevator elevator1 = new Elevator(
            "1", new Door(DoorState.CLOSED), new IdleElevatorState(),
             0, 
             elevatorPanel, 
             new ElevatorDisplay(0, Direction.DEFAULT, 10)
        );
        Elevator elevator2 = new Elevator(
            "2", new Door(DoorState.CLOSED), new IdleElevatorState(), 
            0, 
            elevatorPanel, 
            new ElevatorDisplay(0, Direction.DEFAULT, 10)
        );
        Elevator elevator3 = new Elevator(
            "3", new Door(DoorState.CLOSED), new IdleElevatorState(), 
            0, 
            elevatorPanel, 
            new ElevatorDisplay(0, Direction.DEFAULT, 10)
        );

        List<Elevator> elevators = List.of(elevator1, elevator2, elevator3);

        ElevatorSystem elevatorSystem = new ElevatorSystem(
            new Building(floors, elevators)
        );

        elevatorSystem.requestElevator(1);
    }
}
