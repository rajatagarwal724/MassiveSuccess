package coding.top75.heaps;

public class CountGoodNodes {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) {
            val = x;
        }

        TreeNode(int x, TreeNode left, TreeNode right) {
            val = x;
            this.left = left;
            this.right = right;
        }
    }

    public int goodNodes(TreeNode root) {
        if (null == root) {
            return 0;
        }
        return dfs(root, root.val);
    }

    private int dfs(TreeNode node, int maxValue) {
        if (null == node) {
            return 0;
        }

        int count = node.val >= maxValue ? 1 : 0;
        maxValue = Math.max(maxValue, node.val);
        count += dfs(node.left, maxValue);
        count += dfs(node.right, maxValue);
        return count;
    }

    public static void main(String[] args) {
        var sol = new CountGoodNodes();
        var root = new TreeNode(3);
        root.left = new TreeNode(1);
        root.right = new TreeNode(3);

        root.left.left = new TreeNode(3);
        root.right.left = new TreeNode(1);
        root.right.right = new TreeNode(5);

        System.out.println(sol.goodNodes(root));

        root = new TreeNode(2);
        root.left = new TreeNode(3);
        root.right = new TreeNode(4);

        root.left.left = new TreeNode(1);
        root.right.right = new TreeNode(5);

        System.out.println(sol.goodNodes(root));
    }
}
