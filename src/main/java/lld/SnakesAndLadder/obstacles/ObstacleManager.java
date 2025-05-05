package lld.SnakesAndLadder.obstacles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObstacleManager {
    private Map<Integer, List<Obstacle>> obstaclesMap;

    public ObstacleManager() {
        this.obstaclesMap = new HashMap<>();
    }

    public int getNextPosition(int currentPosition) {
        return obstaclesMap.containsKey(currentPosition) ? obstaclesMap.get(currentPosition).get(0).getDestination(currentPosition) : currentPosition;
    }

    public void add(Obstacle obstacle) {
        obstaclesMap.computeIfAbsent(obstacle.getStartPosition(), position -> new ArrayList<>()).add(obstacle);
    }
}
