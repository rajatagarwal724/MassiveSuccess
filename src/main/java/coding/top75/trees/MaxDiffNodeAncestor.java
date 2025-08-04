package coding.top75.trees;

public class MaxDiffNodeAncestor {


    class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    // Main function to find the maximum difference between node and ancestor
    public int maxAncestorDiff(TreeNode root) {
        return helper(root, root.val, root.val);
    }

    private int helper(TreeNode node, int minVal, int maxVal) {
        if (null == node) {
            return maxVal - minVal;
        }

        minVal = Math.min(minVal, node.val);
        maxVal = Math.max(maxVal, node.val);

        int leftDiff = helper(node.left, minVal, maxVal);
        int rightDiff = helper(node.right, minVal, maxVal);

        return Math.max(leftDiff, rightDiff);
    }
}
