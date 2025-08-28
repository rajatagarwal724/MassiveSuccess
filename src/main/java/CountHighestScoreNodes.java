import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Solution {

    int N;
    Map<Integer, List<Integer>> adjacencyList;
    long maxScore;  // Changed from int to long
    int count;

    public void init(int N) {
        maxScore = 0;
        count = 0;
        adjacencyList = new HashMap<>();
        this.N = N;
    }

    public int countHighestScoreNodes(int[] parents) {

        init(parents.length);

        for(int i = 0; i < parents.length; i++) {
            if(parents[i] != -1) {
                adjacencyList.computeIfAbsent(parents[i], s -> new ArrayList<>()).add(i);
            }
        }

        dfs(0);
        return count;
    }

    private int dfs(int node) {
        long score = 1;  // Changed from int to long
        int children = 0;

        for(int child: adjacencyList.getOrDefault(node, new ArrayList<>())) {
            int subTreeSize = dfs(child);
            score *= subTreeSize;
            children += subTreeSize;
        }

        int remaining = N - children - 1;

        if (remaining > 0) {
            score *= remaining;
        }

        if(score > maxScore) {
            maxScore = score;
            count = 1;
        } else if(score == maxScore) {
            count++;
        }

        return children + 1;
    }
} 