package lld.ParkingLot.model;

import lombok.Data;

import java.util.Objects;

@Data
public class ParkingSlot {
    private int slotNo;
    private Car parkedCar;

    public ParkingSlot(int slotNo) {
        this.slotNo = slotNo;
    }

    public boolean isSlotFree() {
        return Objects.nonNull(parkedCar);
    }

    public void assignCar(Car car) {
        this.parkedCar = car;
    }

    public void unAssignCar() {
        this.parkedCar = null;
    }
}
