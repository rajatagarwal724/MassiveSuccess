package lld.ooad.leaderboard;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class LeaderboardOptimized {

    private Map<Integer, Integer> scoreCard;
    private TreeMap<Integer, Integer> leaderBoard;

    public LeaderboardOptimized() {
        this.scoreCard = new HashMap<>();
        this.leaderBoard = new TreeMap<>(Collections.reverseOrder());
    }

    private void addScore(int playerId, int score) {
        if (scoreCard.containsKey(playerId)) {
            int previousScore = scoreCard.get(playerId);
            int newScore = previousScore + score;
            scoreCard.put(playerId, newScore);

            if (leaderBoard.containsKey(newScore)) {
                leaderBoard.put(newScore, leaderBoard.get(newScore) + 1);
            } else {
                leaderBoard.put(newScore, 1);
            }

            if (leaderBoard.containsKey(previousScore)) {
                leaderBoard.put(previousScore, leaderBoard.get(previousScore) - 1);
                if (leaderBoard.get(previousScore) == 0) {
                    leaderBoard.remove(previousScore);
                }
            }
        } else {
            scoreCard.put(playerId, score);
            leaderBoard.put(score, leaderBoard.getOrDefault(score, 0) + 1);
        }
    }

    private void reset(int playerId) {
        int score = scoreCard.get(playerId);
        scoreCard.remove(playerId);
        leaderBoard.put(score, leaderBoard.get(score) - 1);
        if (leaderBoard.get(score) == 0) {
            leaderBoard.remove(score);
        }
    }

    private int top(int K) {
        int sum = 0;
        for (Map.Entry<Integer, Integer> entry : leaderBoard.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                sum += entry.getKey();
                K--;
                if (K == 0) {
                    return sum;
                }
            }
        }
        return sum;
    }

    public static void main(String[] args) {
        var leaderboard = new LeaderboardOptimized ();
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
