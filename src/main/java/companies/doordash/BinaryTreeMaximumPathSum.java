package companies.doordash;

public class BinaryTreeMaximumPathSum {

    private int MAX_SUM = Integer.MIN_VALUE;

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    public int maxPathSum(TreeNode root) {
        MAX_SUM = Integer.MIN_VALUE;
        recursive(root);
        return MAX_SUM;
    }

    private int recursive(TreeNode root) {
        if (root == null) {
            return 0;
        }

        int gain_from_left = Math.max(recursive(root.left), 0);
        int gain_from_right = Math.max(recursive(root.right), 0);

        MAX_SUM = Math.max(MAX_SUM, gain_from_left + gain_from_right + root.val);

        return Math.max(root.val + gain_from_left, root.val + gain_from_right);
    }

    public static void main(String[] args) {
        var sol = new BinaryTreeMaximumPathSum();
        var root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);

        System.out.println(
                sol.maxPathSum(root)
        );

        root = new TreeNode(-10);
        root.left = new TreeNode(9);
        root.right = new TreeNode(20);
        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);

        System.out.println(
                sol.maxPathSum(root)
        );
    }
}
