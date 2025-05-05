package lld.ParkingLot.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ParkingLot {
    private int capacity;
    private Map<Integer, ParkingSlot> slots;

    public ParkingLot(int capacity) {
        this.capacity = capacity;
        this.slots = new HashMap<>();
        for (int i = 1; i <= capacity; i++) {
            this.slots.put(i, new ParkingSlot(i));
        }
    }

    public ParkingSlot getSlot(int slotNo) {
        return slots.get(slotNo);
    }

    public ParkingSlot park(int slotNo, Car car) {
        ParkingSlot parkingSlot = slots.get(slotNo);
        parkingSlot.assignCar(car);
        return parkingSlot;
    }

    public ParkingSlot freeSlot(int slotNo) {
        ParkingSlot parkingSlot = slots.get(slotNo);
        parkingSlot.unAssignCar();
        return parkingSlot;
    }
}
