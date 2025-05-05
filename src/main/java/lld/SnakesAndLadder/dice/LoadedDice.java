package lld.SnakesAndLadder.dice;

import java.util.Random;

public class LoadedDice implements DiceStrategy {

    private final Random random;

    public LoadedDice(Random random) {
        this.random = random;
    }

    @Override
    public int rollDice() {
        return random.nextInt(3) + 4;
    }
}
