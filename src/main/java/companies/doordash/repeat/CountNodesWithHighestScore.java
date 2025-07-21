package companies.doordash.repeat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountNodesWithHighestScore {
    
    long maxScore;
    int count;
    
    int N;

    public int countHighestScoreNodes(int[] parents) {
        N = parents.length;
        maxScore = 0;
        count = 0;
        Map<Integer, List<Integer>> adjacencyList = new HashMap<>();
        
        for (int i = 0; i < parents.length; i++) {
            if (parents[i] != -1) {
                adjacencyList.computeIfAbsent(parents[i], s -> new ArrayList<>()).add(i);
            }
        }
        
        dfs(0, adjacencyList);
        
        return count;
    }
    
    private int dfs(int node, Map<Integer, List<Integer>> children) {
        long score = 1;
        int totalChildren = 0;
        
        for (int child: children.getOrDefault(node, new ArrayList<>())) {
            int subTreeSize = dfs(child, children);
            score *= subTreeSize;
            totalChildren += subTreeSize;
        }
        
        int remainingChildren = N - totalChildren - 1;
        
        if (remainingChildren > 0) {
            score *= remainingChildren;
        }
        
        if (score > maxScore) {
            maxScore = score;
            count = 1;
        } else if (score == maxScore) {
            count++;
        }

        return totalChildren + 1;
    }

    public static void main(String[] args) {
        var sol = new CountNodesWithHighestScore();
        System.out.println(
                sol.countHighestScoreNodes(new int[] {-1,2,0,2,0})
        );

        System.out.println(
                sol.countHighestScoreNodes(new int[] {-1,2,0})
        );
    }
}
