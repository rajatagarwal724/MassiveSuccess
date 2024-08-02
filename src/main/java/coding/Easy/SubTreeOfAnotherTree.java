package coding.Easy;

public class SubTreeOfAnotherTree {
    public static boolean isSubtree(TreeNode s, TreeNode t) {
        if (null == s && null == t) {
            return true;
        }
        if (null == s || null == t) {
            return false;
        }
        if (s.val == t.val) {
            return isSubtree(s.left, t.left) && isSubtree(s.right, t.right);
        }
        return isSubtree(s.left, t) || isSubtree(s.right, t);
    }

    public static void main(String[] args) {
        TreeNode s = new TreeNode(3);
        s.right = new TreeNode(5);
        s.left = new TreeNode(4);
        s.left.left = new TreeNode(1);
        s.left.right = new TreeNode(2);

        TreeNode t = new TreeNode(4);
        t.left = new TreeNode(1);
        t.right = new TreeNode(2);

        System.out.println(isSubtree(s, t));
    }
}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}
