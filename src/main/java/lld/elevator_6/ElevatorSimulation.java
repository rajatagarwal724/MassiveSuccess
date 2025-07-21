package lld.elevator_6;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ElevatorSimulation {
    public static void main(String[] args) {
        int totalFloors = 10;
        ElevatorSystem system = new ElevatorSystem(3, totalFloors);

        system.requestPickup(3, Direction.UP);
        system.requestPickup(6, Direction.DOWN);
        system.requestElevator(0, 8);
//        system.requestElevator(1, 100); // Invalid request

        for (int i = 0; i < 12; i++) {
            System.out.println("Step " + i);
            system.step();
            system.status();
            System.out.println("------------");
        }
    }
}


enum Direction {
    UP, DOWN, IDLE
}

@Data
class Elevator {
    private final int id;
    private int currentFloor;
    private Direction direction;
    private TreeSet<Integer> destinations;
    private final int totalFloors;

    public Elevator(int id, int totalFloors) {
        this.id = id;
        this.totalFloors = totalFloors;
        this.currentFloor = 0;
        this.direction = Direction.IDLE;
        this.destinations = new TreeSet<>();
    }

    public void requestFloor(int floor) {
        if (floor < 0 || floor >= totalFloors) {
            throw new IllegalArgumentException("");
        }

        destinations.add(floor);
        updateDirection();
    }

    private void updateDirection() {
        if (destinations.isEmpty()) {
            direction = Direction.IDLE;
        } else {
            int target = direction == Direction.DOWN ? destinations.first() : destinations.last();
            if (target > currentFloor) {
                direction = Direction.UP;
            } else if (target < currentFloor) {
                direction = Direction.DOWN;
            } else {
                direction = Direction.IDLE;
            }
        }
    }

    public void step() {
        if (destinations.isEmpty()) {
            direction = Direction.IDLE;
            return;
        }

        if (direction == Direction.UP && currentFloor < totalFloors - 1) {
            currentFloor++;
        } else if (direction == Direction.DOWN && currentFloor > 0) {
            currentFloor--;
        }

        if (destinations.contains(currentFloor)) {
            destinations.remove(currentFloor);
            System.out.println("Elevator " + id + " stopped at floor " + currentFloor);
        }
        updateDirection();
    }
}

class ElevatorSystem {
    private final List<Elevator> elevators;
    private final int totalFloors;

    public ElevatorSystem(int numElevators, int totalFloors) {
        this.totalFloors = totalFloors;
        this.elevators = new ArrayList<>();
        for (int i = 0; i < numElevators; i++) {
            elevators.add(new Elevator(i, totalFloors));
        }
    }

    public void requestPickup(int floor, Direction direction) {
        if (floor < 0 || floor >= totalFloors) {
            System.out.println("Invalid pickup floor: " + floor);
            return;
        }

        Elevator bestElevator = null;
        int minDistance = Integer.MAX_VALUE;

        for (Elevator e : elevators) {
            if (e.getDirection() == Direction.IDLE ||
                    (e.getDirection() == direction &&
                            ((direction == Direction.UP && e.getCurrentFloor() <= floor) ||
                                    (direction == Direction.DOWN && e.getCurrentFloor() >= floor)))) {
                int distance = Math.abs(e.getCurrentFloor() - floor);
                if (distance < minDistance) {
                    minDistance = distance;
                    bestElevator = e;
                }
            }
        }

        if (bestElevator == null) bestElevator = elevators.get(0); // fallback
        bestElevator.requestFloor(floor);
        System.out.println("Pickup request for floor " + floor + " assigned to Elevator " + bestElevator.getId());
    }

    public void requestElevator(int elevatorId, int floor) {
        if (elevatorId < 0 || elevatorId >= elevators.size()) {
            System.out.println("Invalid elevator ID: " + elevatorId);
            return;
        }
        elevators.get(elevatorId).requestFloor(floor);
    }

    public void step() {
        elevators.forEach(Elevator::step);
    }

    public void status() {
        elevators.forEach(System.out::println);
    }
}

