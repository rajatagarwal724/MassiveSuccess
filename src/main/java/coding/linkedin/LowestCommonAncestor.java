package coding.linkedin;

public class LowestCommonAncestor {
    static class TreeNode {
      int val;
      TreeNode left;
      TreeNode right;
      TreeNode(int x) { val = x; }
    }

    public TreeNode lowestCommonAncestor(
            TreeNode root,
            TreeNode p,
            TreeNode q
    ) {

        var rootVal = root.val;
        var pVal = p.val;
        var qVal = q.val;

        if (pVal > rootVal && qVal > rootVal) {
            return lowestCommonAncestor(root.right, p, q);
        } else if (pVal < rootVal && qVal < rootVal) {
            return lowestCommonAncestor(root.left, p, q);
        } else {
            return root;
        }

    }

    public TreeNode lowestCommonAncestorIterative(
            TreeNode root,
            TreeNode p,
            TreeNode q
    ) {
        var pVal = p.val;
        var qVal = q.val;

        while (null != root) {
            var parentVal = root.val;

            if (parentVal < pVal && parentVal < qVal) {
                root = root.right;
            } else if (parentVal > pVal && parentVal > qVal) {
                root = root.left;
            } else {
                return root;
            }
        }
        return new TreeNode(Integer.MIN_VALUE);
    }

    public static void main(String[] args) {
        var sol = new LowestCommonAncestor();

        var root = new TreeNode(6);

        root.left = new TreeNode(2);
        root.right = new TreeNode(8);

        root.left.left = new TreeNode(0);
        root.left.right = new TreeNode(4);

        root.left.right.left = new TreeNode(3);
        root.left.right.right = new TreeNode(5);

        root.right.left = new TreeNode(7);
        root.right.right = new TreeNode(9);

        System.out.println(
                sol.lowestCommonAncestor(root, new TreeNode(2), new TreeNode(8)).val
        );
        System.out.println(
                sol.lowestCommonAncestor(root, new TreeNode(2), new TreeNode(4)).val
        );
        System.out.println(
                sol.lowestCommonAncestor(root, new TreeNode(2), new TreeNode(1)).val
        );

        System.out.println(
                sol.lowestCommonAncestorIterative(root, new TreeNode(2), new TreeNode(8)).val
        );
        System.out.println(
                sol.lowestCommonAncestorIterative(root, new TreeNode(2), new TreeNode(4)).val
        );
        System.out.println(
                sol.lowestCommonAncestorIterative(root, new TreeNode(2), new TreeNode(1)).val
        );
    }
}
