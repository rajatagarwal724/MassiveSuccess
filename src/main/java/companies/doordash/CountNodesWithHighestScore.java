package companies.doordash;

import java.util.*;

public class CountNodesWithHighestScore {
    
    private List<List<Integer>> children;
    private long maxScore = 0;
    private int count = 0;
    private int n;
    
    public int countHighestScoreNodes(int[] parents) {
        n = parents.length;
        children = new ArrayList<>();
        
        // Build adjacency list representation
        for (int i = 0; i < n; i++) {
            children.add(new ArrayList<>());
        }
        
        for (int i = 0; i < n; i++) {
            if (parents[i] != -1) {
                children.get(parents[i]).add(i);
            }
        }
        
        // DFS to calculate scores for each node
        dfs(0);
        
        return count;
    }
    
    private int dfs(int node) {
        long score = 1;
        int totalChildren = 0;
        
        // Calculate score contribution from each subtree
        for (int child : children.get(node)) {
            int subtreeSize = dfs(child);
            score *= subtreeSize;
            totalChildren += subtreeSize;
        }
        
        // Calculate score contribution from the remaining part of tree
        // when this node is removed
        int remaining = n - totalChildren - 1;
        if (remaining > 0) {
            score *= remaining;
        }
        
        // Update maximum score and count
        if (score > maxScore) {
            maxScore = score;
            count = 1;
        } else if (score == maxScore) {
            count++;
        }
        
        // Return the size of subtree rooted at this node
        return totalChildren + 1;
    }
    
    public static void main(String[] args) {
        CountNodesWithHighestScore solution = new CountNodesWithHighestScore();
        
        // Test case 1: parents = [-1,2,0,2]
        // Tree structure:
        //     0
        //    / \
        //   2   
        //  / \
        // 1   3
        int[] parents1 = {-1, 2, 0, 2};
        System.out.println("Test 1 - Expected: 3, Actual: " + solution.countHighestScoreNodes(parents1));
        
        // Reset for next test
        solution = new CountNodesWithHighestScore();
        
        // Test case 2: parents = [-1,2,0]
        // Tree structure:
        //   0
        //  /
        // 2
        // |
        // 1
        int[] parents2 = {-1, 2, 0};
        System.out.println("Test 2 - Expected: 2, Actual: " + solution.countHighestScoreNodes(parents2));
    }
} 