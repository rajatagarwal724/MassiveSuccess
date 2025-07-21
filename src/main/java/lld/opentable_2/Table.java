package lld.opentable_2;

import java.util.Objects;

public class Table {
    private int id;
    private int capacity;
    private boolean isOccupied;

    public Table(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.isOccupied = false;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    // Getters
    public int getId() { return id; }
    public int getCapacity() { return capacity; }
    public boolean isOccupied() { return isOccupied; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return id == table.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 