package lld.SnakesAndLadder;

import lld.SnakesAndLadder.dice.DiceStrategy;
import lld.SnakesAndLadder.dice.NormalDice;
import lld.SnakesAndLadder.move.Command;
import lld.SnakesAndLadder.move.MoveCommand;
import lld.SnakesAndLadder.obstacles.Ladder;
import lld.SnakesAndLadder.obstacles.Snake;
import lld.SnakesAndLadder.players.Player;

import java.util.List;

public class SnakeAndLadderGame {

    public static void main(String[] args) {
        Board board = Board.getInstance(100);
        board.addObstacles(
                List.of(
                        new Snake(16, 6),
                        new Snake(48, 26),
                        new Snake(49, 11),
                        new Snake(56, 53),
                        new Snake(62, 19),
                        new Snake(64, 60),
                        new Snake(87, 24),
                        new Snake(93, 73),
                        new Snake(95, 75),
                        new Snake(98, 78)
                )
        );
        board.addObstacles(
                List.of(
                        new Ladder(1, 38),
                        new Ladder(4, 14),
                        new Ladder(9, 31),
                        new Ladder(21, 42),
                        new Ladder(28, 84),
                        new Ladder(36, 44),
                        new Ladder(51, 67),
                        new Ladder(71, 91),
                        new Ladder(80, 100)
                )
        );

        Game game = new Game();
        game.addPlayer(new Player("Hitti"));
        game.addPlayer(new Player("Lishi"));

        DiceStrategy diceStrategy = new NormalDice();

        while (true) {
            Player player = game.getCurrentPlayer();
            int steps = diceStrategy.rollDice();
            Command command = new MoveCommand(player, board, steps);
            command.execute();
            System.out.println(player.getName() + " rolled a " + steps + " and moved to " + player.getPosition());
            if (player.getPosition() == board.getSize()) {
                System.out.println("Game won by player : " + player.getName() + " Position: " + player.getPosition());
                break;
            }
            if (steps != 6) {
                game.nextTurn();
            }
        }
    }
}
