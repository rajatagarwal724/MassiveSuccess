package coding.leetcode;

import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode Problem: https://leetcode.com/problems/path-sum-ii/
 *
 * Given the root of a binary tree and an integer targetSum, return all root-to-leaf paths
 * where the sum of the node values in the path equals targetSum.
 * Each path should be returned as a list of the node values, not node references.
 */
public class PathSumII {

    // Definition for a binary tree node.
    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    /**
     * Finds all root-to-leaf paths that sum up to the targetSum.
     *
     * @param root The root of the binary tree.
     * @param targetSum The target sum.
     * @return A list of lists, where each inner list is a valid path.
     */
    public List<List<Integer>> pathSum(TreeNode root, int targetSum) {
        List<List<Integer>> allPaths = new ArrayList<>();
        findPaths(root, targetSum, new ArrayList<>(), allPaths);
        return allPaths;
    }

    /**
     * A recursive helper function that performs a DFS traversal to find paths.
     *
     * @param node The current node in the traversal.
     * @param remainingSum The sum that still needs to be achieved.
     * @param currentPath The path taken so far from the root to the parent of the current node.
     * @param allPaths The list to store all valid paths found.
     */
    private void findPaths(TreeNode node, int remainingSum, List<Integer> currentPath, List<List<Integer>> allPaths) {
        if (node == null) {
            return;
        }

        // Add the current node to the path
        currentPath.add(node.val);

        // Check if it's a leaf node and if the path sum is correct
        if (node.left == null && node.right == null && node.val == remainingSum) {
            // A valid path is found. Add a copy of the current path to the results.
            // A copy is needed because we will backtrack and modify the currentPath list.
            allPaths.add(new ArrayList<>(currentPath));
        } else {
            // Continue the search on the children
            int newRemainingSum = remainingSum - node.val;
            findPaths(node.left, newRemainingSum, currentPath, allPaths);
            findPaths(node.right, newRemainingSum, currentPath, allPaths);
        }

        // Backtrack: remove the current node from the path to explore other branches
        currentPath.remove(currentPath.size() - 1);
    }

    public static void main(String[] args) {
        PathSumII solution = new PathSumII();

        // Construct the example tree:
        //      5
        //     / \
        //    4   8
        //   /   / \
        //  11  13  4
        // /  \    / \
        //7    2  5   1
        TreeNode root = new TreeNode(5,
            new TreeNode(4,
                new TreeNode(11,
                    new TreeNode(7),
                    new TreeNode(2)
                ),
                null
            ),
            new TreeNode(8,
                new TreeNode(13),
                new TreeNode(4,
                    new TreeNode(5),
                    new TreeNode(1)
                )
            )
        );

        int targetSum = 22;
        List<List<Integer>> result = solution.pathSum(root, targetSum);

        System.out.println("Paths with sum " + targetSum + ":");
        // Expected output:
        // [[5, 4, 11, 2], [5, 8, 4, 5]]
        for (List<Integer> path : result) {
            System.out.println(path);
        }

        System.out.println("\n-------------------------------------\n");

        // Another test case
        TreeNode root2 = new TreeNode(1, new TreeNode(2), null);
        int targetSum2 = 1;
        List<List<Integer>> result2 = solution.pathSum(root2, targetSum2);
        System.out.println("Paths with sum " + targetSum2 + ":");
        // Expected output: [] (no root-to-leaf path sums to 1)
        for (List<Integer> path : result2) {
            System.out.println(path);
        }
    }
}
