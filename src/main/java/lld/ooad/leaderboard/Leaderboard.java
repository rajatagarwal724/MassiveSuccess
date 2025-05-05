package lld.ooad.leaderboard;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Design a Leaderboard class, which has the following features:
 *
 * addScore(playerId, score): Update the leaderboard by adding score to the given player's score.
 * If there is no player with such id in the leaderboard, add him to the leaderboard with the given score.
 *
 * top(K): Return the score sum of the top K players.
 *
 * reset(playerId): Reset the score of the player with the given id to 0 (in other words erase it from the leaderboard).
 * It is guaranteed that the player was added to the leaderboard before calling this function.
 */
public class Leaderboard {
    private Map<Integer, Integer> playerScores;

    public Leaderboard() {
        playerScores = new HashMap<>();
    }

    private void addScore(int playerId, int score) {
        playerScores.put(playerId, playerScores.getOrDefault(playerId, 0) + score);
    }

    /**
     * O(NlogK)
     * @param K
     * @return
     */
    private int top(int K) {
        Queue<Map.Entry<Integer, Integer>> minHeap = new PriorityQueue<>(
                K,
                Comparator.comparingInt(Map.Entry::getValue)
        );

        for (Map.Entry<Integer, Integer> entry : playerScores.entrySet()) {
            if (minHeap.size() < K) {
                minHeap.offer(entry);
            } else {
                if (minHeap.peek().getValue() < entry.getValue()) {
                    minHeap.poll();
                    minHeap.offer(entry);
                }
            }
        }

        int sum = 0;
        while (!minHeap.isEmpty()) {
            sum += minHeap.poll().getValue();
        }

        return sum;
    }

    private void reset(int playerId) {
        playerScores.remove(playerId);
    }

    public static void main(String[] args) {
        Leaderboard leaderboard = new Leaderboard ();
        leaderboard.addScore(1,73); // leaderboard = [[1,73]];
        leaderboard.addScore(2,56); // leaderboard = [[1,73],[2,56]];
        leaderboard.addScore(3,39); // leaderboard = [[1,73],[2,56],[3,39]];
        leaderboard.addScore(4,51); // leaderboard = [[1,73],[2,56],[3,39],[4,51]];
        leaderboard.addScore(5,4); // leaderboard = [[1,73],[2,56],[3,39],[4,51],[5,4]];
        System.out.println(leaderboard.top(1)); // returns 73;
        leaderboard.reset(1); // leaderboard = [[2,56],[3,39],[4,51],[5,4]];
        leaderboard.reset(2); // leaderboard = [[3,39],[4,51],[5,4]];
        leaderboard.addScore(2,51); // leaderboard = [[2,51],[3,39],[4,51],[5,4]];
        System.out.println(leaderboard.top(3)); // returns 141 = 51 + 51 + 39;
    }
}
