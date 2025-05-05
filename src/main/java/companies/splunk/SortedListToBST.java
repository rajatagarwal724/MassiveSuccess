package companies.splunk;

public class SortedListToBST {

    static class TreeNode {
        int val;
        TreeNode left, right;

        public TreeNode(int val) {
            this.val = val;
        }
    }

    private TreeNode sortedListToBST(int[] sortedList) {
        TreeNode root = sortedListToBSTUtil(sortedList, 0, sortedList.length - 1);
        return root;

    }

    private TreeNode sortedListToBSTUtil(int[] sortedList, int left, int right) {
        if (left > right) {
            return null;
        }
        int mid = left + (right - left)/2;
        TreeNode root = new TreeNode(sortedList[mid]);
        root.left = sortedListToBSTUtil(sortedList, left, mid - 1);
        root.right = sortedListToBSTUtil(sortedList, mid + 1, right);
        return root;
    }

    public static void main(String[] args) {
        SortedListToBST bstConverter = new SortedListToBST();

        int[] sortedList = {-10, -3, 0, 5, 9};  // Example input

        TreeNode root = bstConverter.sortedListToBST(sortedList);

        System.out.print("Inorder Traversal of BST: ");
        bstConverter.inorderTraversal(root);
        // Output: -10 -3 0 5 9 (which confirms it is a BST)
    }

    private void inorderTraversal(TreeNode root) {
        if (null == root) {
            return;
        }
        inorderTraversal(root.left);
        System.out.print(root.val + " ");
        inorderTraversal(root.right);
    }


}
