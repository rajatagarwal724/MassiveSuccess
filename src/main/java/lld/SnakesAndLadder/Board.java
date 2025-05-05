package lld.SnakesAndLadder;

import lld.SnakesAndLadder.obstacles.Obstacle;
import lld.SnakesAndLadder.obstacles.ObstacleManager;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class Board {
    private static Board instance;
    private int size;
    private ObstacleManager obstacleManager;

    private Board(int size) {
        this.size = size;
        this.obstacleManager = new ObstacleManager();
    }

    public static Board getInstance(int size) {
        if (null == instance) {
            instance = new Board(size);
        }
        return instance;
    }

    public void addObstacles(List<Obstacle> obstacles) {
        obstacles.forEach(obstacle -> obstacleManager.add(obstacle));
    }
}
