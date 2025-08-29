package coding.top75.trees;

public class MaximumPathSum {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    private static int globalMaximumSum;

    public int findMaximumPathSum(TreeNode root) {
        globalMaximumSum = Integer.MIN_VALUE;
        recurse(root);
        return globalMaximumSum;
    }

    private int recurse(TreeNode node) {
        if (null == node) {
            return 0;
        }
        int leftPathSum = Math.max(recurse(node.left), 0);
        int rightPathSum = Math.max(recurse(node.right), 0);

        globalMaximumSum = Math.max(globalMaximumSum, leftPathSum + rightPathSum + node.val);

        return Math.max(leftPathSum, rightPathSum) + node.val;
    }
}
