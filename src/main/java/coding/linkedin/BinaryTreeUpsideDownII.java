package coding.linkedin;

public class BinaryTreeUpsideDownII {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        public TreeNode(int val) {
            this.val = val;
        }
    }

    public TreeNode upsideDownBinaryTree(TreeNode root) {
        if (null == root || null == root.left) {
            return root;
        }

        TreeNode newRoot = upsideDownBinaryTree(root.left);

        root.left.left = root.right;
        root.left.right = root;

        root.left = null;
        root.right = null;

        return newRoot;
    }

    public static void main(String[] args) {
        var sol = new BinaryTreeUpsideDownII();
        // Create a sample tree:
        //       1
        //      / \
        //     2   3
        //    / \
        //   4   5
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);

        // Upside down the tree (iterative solution)
        TreeNode newRoot = sol.upsideDownBinaryTree(root);

        // After transformation:
        //     4
        //    / \
        //   5   2
        //      / \
        //     3   1
        System.out.println("New root value: " + newRoot.val);  // Should be 4
    }
}
