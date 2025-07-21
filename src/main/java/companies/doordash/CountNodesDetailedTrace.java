package companies.doordash;

import java.util.*;

public class CountNodesDetailedTrace {
    
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
        
        System.out.println("Tree structure:");
        printTree(0, 0);
        System.out.println();
        
        // DFS to calculate scores for each node
        dfs(0);
        
        return count;
    }
    
    private void printTree(int node, int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print("  ");
        }
        System.out.println("Node " + node);
        for (int child : children.get(node)) {
            printTree(child, depth + 1);
        }
    }
    
    private int dfs(int node) {
        System.out.println("Processing node " + node + ":");
        
        long score = 1;
        int totalChildren = 0;
        List<Integer> subtreeSizes = new ArrayList<>();
        
        // Calculate score contribution from each subtree
        for (int child : children.get(node)) {
            int subtreeSize = dfs(child);
            subtreeSizes.add(subtreeSize);
            score *= subtreeSize;
            totalChildren += subtreeSize;
            System.out.println("  Child " + child + " has subtree size " + subtreeSize);
            System.out.println("  Score after multiplying by subtree: " + score);
        }
        
        // Calculate score contribution from the remaining part of tree
        int remaining = n - totalChildren - 1;
        if (remaining > 0) {
            score *= remaining;
            System.out.println("  Remaining part size: " + remaining);
            System.out.println("  Final score after multiplying by remaining: " + score);
        } else {
            System.out.println("  No remaining part (this is root)");
            System.out.println("  Final score: " + score);
        }
        
        // Show what components would exist if we remove this node
        System.out.println("  If we remove node " + node + ":");
        System.out.print("    Components: ");
        for (int size : subtreeSizes) {
            System.out.print("[size=" + size + "] ");
        }
        if (remaining > 0) {
            System.out.print("[remaining=" + remaining + "] ");
        }
        System.out.println();
        System.out.println("    Score = " + 
            subtreeSizes.stream().mapToLong(Integer::longValue).reduce(1, (a, b) -> a * b) * 
            (remaining > 0 ? remaining : 1));
        
        // Update maximum score and count
        if (score > maxScore) {
            maxScore = score;
            count = 1;
        } else if (score == maxScore) {
            count++;
        }
        
        System.out.println("  Returning subtree size: " + (totalChildren + 1));
        System.out.println();
        
        return totalChildren + 1;
    }
    
    public static void main(String[] args) {
        CountNodesDetailedTrace solution = new CountNodesDetailedTrace();
        
        // Test case: parents = [-1,2,0,2]
        int[] parents = {-1, 2, 0, 2};
        System.out.println("Input: parents = " + Arrays.toString(parents));
        System.out.println();
        
        int result = solution.countHighestScoreNodes(parents);
        System.out.println("Result: " + result + " nodes have the highest score of " + solution.maxScore);
    }
} 