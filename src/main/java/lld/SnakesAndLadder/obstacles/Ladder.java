package lld.SnakesAndLadder.obstacles;

import java.util.HashMap;
import java.util.Map;

public class Ladder implements Obstacle {

    private final int start;
    private final int end;

    public Ladder(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public int getDestination(int currentPosition) {
        return currentPosition == start ? end : start;
    }

    @Override
    public int getStartPosition() {
        return start;
    }

}
