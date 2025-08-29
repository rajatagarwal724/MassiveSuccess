package companies.roku;

import java.util.HashMap;
import java.util.Map;

public class BinaryTreeSameAverageOptimal {
    
    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        
        TreeNode() {}
        
        TreeNode(int val) {
            this.val = val;
        }
        
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }
    
    // Helper class to store subtree information
    static class SubtreeInfo {
        long sum;      // Use long to prevent overflow
        int count;
        double average;
        
        SubtreeInfo(long sum, int count) {
            this.sum = sum;
            this.count = count;
            this.average = count > 0 ? (double) sum / count : 0;
        }
    }
    
    public int countNodesWithSameAverage(TreeNode root) {
        if (root == null) return 0;
        
        Map<TreeNode, SubtreeInfo> cache = new HashMap<>();
        int[] result = {0};
        
        // Post-order traversal to calculate subtree info and count matches
        calculateSubtreeInfo(root, cache, result);
        
        return result[0];
    }
    
    private SubtreeInfo calculateSubtreeInfo(TreeNode node, Map<TreeNode, SubtreeInfo> cache, int[] result) {
        if (node == null) {
            return new SubtreeInfo(0, 0);
        }
        
        // Check cache first
        if (cache.containsKey(node)) {
            return cache.get(node);
        }
        
        // Calculate left and right subtree info recursively
        SubtreeInfo leftInfo = calculateSubtreeInfo(node.left, cache, result);
        SubtreeInfo rightInfo = calculateSubtreeInfo(node.right, cache, result);
        
        // Calculate current subtree info
        long totalSum = node.val + leftInfo.sum + rightInfo.sum;
        int totalCount = 1 + leftInfo.count + rightInfo.count;
        
        SubtreeInfo currentInfo = new SubtreeInfo(totalSum, totalCount);
        
        // Check if current node's value equals subtree average
        if (Math.abs(node.val - currentInfo.average) < 1e-9) { // Handle floating point precision
            result[0]++;
        }
        
        // Cache the result
        cache.put(node, currentInfo);
        
        return currentInfo;
    }
    
    // Alternative approach for integer averages only (floor division)
    public int countNodesWithSameAverageInteger(TreeNode root) {
        if (root == null) return 0;
        
        Map<TreeNode, SubtreeInfo> cache = new HashMap<>();
        int[] result = {0};
        
        calculateSubtreeInfoInteger(root, cache, result);
        
        return result[0];
    }
    
    private SubtreeInfo calculateSubtreeInfoInteger(TreeNode node, Map<TreeNode, SubtreeInfo> cache, int[] result) {
        if (node == null) {
            return new SubtreeInfo(0, 0);
        }
        
        if (cache.containsKey(node)) {
            return cache.get(node);
        }
        
        SubtreeInfo leftInfo = calculateSubtreeInfoInteger(node.left, cache, result);
        SubtreeInfo rightInfo = calculateSubtreeInfoInteger(node.right, cache, result);
        
        long totalSum = node.val + leftInfo.sum + rightInfo.sum;
        int totalCount = 1 + leftInfo.count + rightInfo.count;
        
        SubtreeInfo currentInfo = new SubtreeInfo(totalSum, totalCount);
        
        // For integer average (floor division)
        int integerAverage = (int) (totalSum / totalCount);
        if (node.val == integerAverage) {
            result[0]++;
        }
        
        cache.put(node, currentInfo);
        return currentInfo;
    }
    
    public static void main(String[] args) {
        BinaryTreeSameAverageOptimal solution = new BinaryTreeSameAverageOptimal();
        
        // Test case 1: Simple tree
        //       4
        //      / \
        //     8   5
        //    / \   \
        //   0   1   6
        TreeNode root1 = new TreeNode(4);
        root1.left = new TreeNode(8);
        root1.right = new TreeNode(5);
        root1.left.left = new TreeNode(0);
        root1.left.right = new TreeNode(1);
        root1.right.right = new TreeNode(6);
        
        System.out.println("Test 1 - Float average: " + solution.countNodesWithSameAverage(root1));
        System.out.println("Test 1 - Integer average: " + solution.countNodesWithSameAverageInteger(root1));
        
        // Test case 2: Tree where root has same average
        //       2
        //      / \
        //     1   3
        TreeNode root2 = new TreeNode(2);
        root2.left = new TreeNode(1);
        root2.right = new TreeNode(3);
        
        System.out.println("Test 2 - Float average: " + solution.countNodesWithSameAverage(root2));
        System.out.println("Test 2 - Integer average: " + solution.countNodesWithSameAverageInteger(root2));
        
        // Test case 3: Single node
        TreeNode root3 = new TreeNode(5);
        System.out.println("Test 3 - Single node: " + solution.countNodesWithSameAverage(root3));
    }
}
