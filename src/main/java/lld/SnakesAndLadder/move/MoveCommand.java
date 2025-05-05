package lld.SnakesAndLadder.move;

import lld.SnakesAndLadder.Board;
import lld.SnakesAndLadder.players.Player;

public class MoveCommand implements Command {

    private Player player;
    private Board board;
    private int steps;

    public MoveCommand(Player player, Board board, int steps) {
        this.player = player;
        this.board = board;
        this.steps = steps;
    }

    @Override
    public void execute() {
        int newPosition = player.getPosition() + steps;

        if (newPosition > board.getSize()) {
            newPosition = player.getPosition();
        } else {
            newPosition = board.getObstacleManager().getNextPosition(newPosition);
        }

        player.setPosition(newPosition);
    }
}
