package coding.top75.heaps;

public class ValidateBST {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) { val = x; }
    }

    public boolean isValidBST(TreeNode root) {
        if (null == root) {
            return true;
        }
        return isValidBSTUtil(root, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private boolean isValidBSTUtil(TreeNode node, int minValue, int maxValue) {
        if (null == node) {
            return true;
        }

        if (node.val <= minValue || node.val >= maxValue) {
            return false;
        }

        return isValidBSTUtil(node.left, minValue, node.val) && isValidBSTUtil(node.right, node.val, maxValue);
    }

    public static void main(String[] args) {
        var sol = new ValidateBST();

//        TreeNode root = new TreeNode()
    }
}
