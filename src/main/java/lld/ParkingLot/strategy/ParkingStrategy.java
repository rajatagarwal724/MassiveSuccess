package lld.ParkingLot.strategy;

public interface ParkingStrategy {
    void addSlot(int slotNo);
    void removeSlot(int slotNo);
    Integer getNextSlot();
}
