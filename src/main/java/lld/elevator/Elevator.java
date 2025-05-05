package lld.elevator;

import lombok.Getter;

public class Elevator {

    private String id;
    private int totalFloor;

    @Getter
    private int currentFloor;

    public Elevator(String id, int totalFloor) {
        this.id = id;
        this.totalFloor = totalFloor;
    }

    public int move_up() {
        if (this.currentFloor < totalFloor) {
            System.out.println("Move Up Elevator " + this.id + " from Floor: " + this.currentFloor);
            this.currentFloor++;
        }
        return this.currentFloor;
    }

    public int move_down() {
        if (this.currentFloor > 1) {
            System.out.println("Move Down Elevator " + this.id + " from Floor: " + this.currentFloor);
            this.currentFloor--;
        }
        return this.currentFloor;
    }

    public void open_door() {
        System.out.println("Open Door at Floor: " + this.currentFloor);
    }

    public void close_door() {
        System.out.println("Close Door at Floor: " + this.currentFloor);
    }

}
