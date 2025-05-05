package lld.elevator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller {
    private List<Elevator> elevators;

    public Controller(int numberOfElevators, int totalFloors) {
        this.elevators = IntStream
                .range(0, numberOfElevators)
                .boxed()
                .map(i -> new Elevator("Elevator_" + (i + 1), totalFloors))
                .collect(Collectors.toList());
    }

    public void handleRequest(Request request) {
        for (Elevator elevator : this.elevators) {
            if (elevator.getCurrentFloor() == request.getFloor()) {
                elevator.open_door();
                elevator.close_door();
                return;
            } else if (elevator.getCurrentFloor() < request.getFloor()) {
                while (elevator.getCurrentFloor() != request.getFloor()) {
                    elevator.move_up();
                }
                elevator.open_door();
                elevator.close_door();
                return;
            } else {
                while (elevator.getCurrentFloor() != request.getFloor()) {
                    elevator.move_down();
                }
                elevator.open_door();
                elevator.close_door();
                return;
            }
        }
    }
}
