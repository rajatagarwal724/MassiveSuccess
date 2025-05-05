package lld.ParkingLot.strategy;

import java.util.Set;
import java.util.TreeSet;

public class NaturalOrderingParkingStrategy implements ParkingStrategy {
    private final TreeSet<Integer> slots;

    public NaturalOrderingParkingStrategy() {
        this.slots = new TreeSet<>();
    }

    @Override
    public void addSlot(int slotNo) {
        slots.add(slotNo);
    }

    @Override
    public void removeSlot(int slotNo) {
        slots.remove(slotNo);
    }

    @Override
    public Integer getNextSlot() {
        return slots.first();
    }
}
