package coding.top75.trees;

public class TreeDiameter {
    class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    private int treeDiameter = 0;

    public int findDiameter(TreeNode root) {
        treeDiameter = 0;
        height(root);
        return treeDiameter;
    }

    private int height(TreeNode root) {
        if (null == root) {
            return 0;
        }

        int left = height(root.left);
        int right = height(root.right);

        if (left != 0 && right != 0) {
            int diameter = left + right + 1;

            treeDiameter = Math.max(treeDiameter, diameter);
        }

        return 1 + Math.max(left, right);
    }
}
