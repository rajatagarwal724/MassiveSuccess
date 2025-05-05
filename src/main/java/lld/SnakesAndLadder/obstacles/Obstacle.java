package lld.SnakesAndLadder.obstacles;

public interface Obstacle {
    int getDestination(int currentPosition);
    int getStartPosition();
}
