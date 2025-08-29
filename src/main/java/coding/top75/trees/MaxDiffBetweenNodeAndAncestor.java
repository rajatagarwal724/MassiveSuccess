package coding.top75.trees;

public class MaxDiffBetweenNodeAndAncestor {

    class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    int maxDiff = Integer.MIN_VALUE;

    public int maxAncestorDiff(TreeNode root) {
        inorderTraversal(root, null);
        return maxDiff;
    }

    private void inorderTraversal(TreeNode root, TreeNode parent) {
        if (null == root) {
            return;
        }
        inorderTraversal(root.left, root);
        if (null != parent) {
            maxDiff = Math.max(maxDiff, Math.abs(parent.val - root.val));
        }
        inorderTraversal(root.right, root);
    }
}
