package coding.top75.trees;

public class PsedoPalindromicPaths {

    class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    public int pseudoPalindromicPaths(TreeNode root) {
        int[] count = new int[10];
        return dfs(root, count);
    }

    private int dfs(TreeNode node, int[] count) {
        if (null == node) {
            return 0;
        }
        count[node.val]++;
        int result = 0;

        if (node.left == null && node.right == null) {
            if (isPsedoPalindromic(count)) {
                return 1;
            }
        } else {
            result = dfs(node.left, count) + dfs(node.right, count);
        }

        count[node.val]--;
        return result;
    }

    private boolean isPsedoPalindromic(int[] count) {
        int oddCount = 0;
        for (int i = 0; i < count.length; i++) {
            if (count[i] % 2 == 1) {
                oddCount++;
            }
        }

        return oddCount <=1;
    }

    public static void main(String[] args) {

    }
}
