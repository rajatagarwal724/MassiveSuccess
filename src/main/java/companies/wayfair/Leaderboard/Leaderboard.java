package companies.wayfair.Leaderboard;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * https://leetcode.com/problems/design-a-leaderboard/description/
 *
 * Design a Leaderboard class, which has 3 functions:
 *
 * addScore(playerId, score): Update the leaderboard by adding score to the given player's score. If there is no player with such id in the leaderboard, add him to the leaderboard with the given score.
 * top(K): Return the score sum of the top K players.
 * reset(playerId): Reset the score of the player with the given id to 0 (in other words erase it from the leaderboard). It is guaranteed that the player was added to the leaderboard before calling this function.
 * Initially, the leaderboard is empty.
 *
 * Example 1:
 *
 * Input:
 * ["Leaderboard","addScore","addScore","addScore","addScore","addScore","top","reset","reset","addScore","top"]
 * [[],[1,73],[2,56],[3,39],[4,51],[5,4],[1],[1],[2],[2,51],[3]]
 * Output:
 * [null,null,null,null,null,null,73,null,null,null,141]
 *
 * Explanation:
 * Leaderboard leaderboard = new Leaderboard ();
 * leaderboard.addScore(1,73);   // leaderboard = [[1,73]];
 * leaderboard.addScore(2,56);   // leaderboard = [[1,73],[2,56]];
 * leaderboard.addScore(3,39);   // leaderboard = [[1,73],[2,56],[3,39]];
 * leaderboard.addScore(4,51);   // leaderboard = [[1,73],[2,56],[3,39],[4,51]];
 * leaderboard.addScore(5,4);    // leaderboard = [[1,73],[2,56],[3,39],[4,51],[5,4]];
 * leaderboard.top(1);           // returns 73;
 * leaderboard.reset(1);         // leaderboard = [[2,56],[3,39],[4,51],[5,4]];
 * leaderboard.reset(2);         // leaderboard = [[3,39],[4,51],[5,4]];
 * leaderboard.addScore(2,51);   // leaderboard = [[2,51],[3,39],[4,51],[5,4]];
 * leaderboard.top(3);           // returns 141 = 51 + 51 + 39;
 *
 *
 * Constraints:
 *
 * 1 <= playerId, K <= 10000
 * It's guaranteed that K is less than or equal to the current number of players.
 * 1 <= score <= 100
 * There will be at most 1000 function calls.
 *
 */
public class Leaderboard {

    private Map<Integer, Integer> playerScoreMap;
    private Queue<Map.Entry<Integer, Integer>> leaderboard;

    public Leaderboard() {
        playerScoreMap = new HashMap<>();
    }

    public void addScore(int playerId, int score) {
        playerScoreMap.put(playerId, playerScoreMap.getOrDefault(playerId, 0) + score);
    }

    public int top(int K) {
        leaderboard = new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));
        int index = 0;
        for (Map.Entry<Integer, Integer> entry: playerScoreMap.entrySet()) {
            if (index < K) {
                leaderboard.offer(entry);
            } else {
                if (leaderboard.peek().getValue() < entry.getValue()) {
                    leaderboard.poll();
                    leaderboard.offer(entry);
                }
            }
            index++;
        }

        int result = 0;
        for (Map.Entry<Integer, Integer> integerIntegerEntry : leaderboard) {
            result += integerIntegerEntry.getValue();
        }
        return result;
    }

    public void reset(int playerId) {
        playerScoreMap.put(playerId, 0);
    }

    public static void main(String[] args) {
        var sol = new Leaderboard();
        sol.addScore(1, 73);
        sol.addScore(2, 56);
        sol.addScore(3, 39);
        sol.addScore(4, 51);
        sol.addScore(5, 4);

        System.out.println(sol.top(1));

        sol.reset(1);

        sol.reset(2);

        sol.addScore(2, 51);

        System.out.println(sol.top(3));
    }
}
