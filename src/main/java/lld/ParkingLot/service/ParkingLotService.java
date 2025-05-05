package lld.ParkingLot.service;

import lld.ParkingLot.model.Car;
import lld.ParkingLot.model.ParkingLot;
import lld.ParkingLot.model.ParkingSlot;
import lld.ParkingLot.strategy.ParkingStrategy;

public class ParkingLotService {
    private ParkingLot parkingLot;
    private ParkingStrategy parkingStrategy;

    public void createParkingLot(ParkingLot parkingLot, ParkingStrategy parkingStrategy) {
        this.parkingLot = parkingLot;
        this.parkingStrategy = parkingStrategy;
        for (int i = 1; i <= parkingLot.getCapacity(); i++) {
            parkingStrategy.addSlot(i);
        }
    }

    public Integer park(final Car car) {
        Integer nextFreeSlot = parkingStrategy.getNextSlot();
        parkingLot.park(nextFreeSlot, car);
        parkingStrategy.removeSlot(nextFreeSlot);
        return nextFreeSlot;
    }

    public void makeSlotFree(int slotNo) {
//        ParkingSlot slot = parkingLot.(slotNo);

    }
}
