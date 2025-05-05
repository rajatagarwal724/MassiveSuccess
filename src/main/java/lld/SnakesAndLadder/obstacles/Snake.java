package lld.SnakesAndLadder.obstacles;

import java.util.HashMap;
import java.util.Map;

public class Snake implements Obstacle {
    private final int head;
    private final int tail;

    public Snake(int head, int tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public int getDestination(int currentPosition) {
        return currentPosition == head ? tail : head;
    }

    @Override
    public int getStartPosition() {
        return head;
    }
}
