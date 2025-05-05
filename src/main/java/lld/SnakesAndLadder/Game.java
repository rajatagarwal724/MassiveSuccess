package lld.SnakesAndLadder;

import lld.SnakesAndLadder.players.Player;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private List<Player> players;
    private int currentPlayerIndex;

    public Game() {
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void notifyPlayers(String message) {
        for (Player player: players) {
            player.update(message);
        }
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public int nextTurn() {
        return currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }
}
