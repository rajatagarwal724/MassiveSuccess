package coding.linkedin;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class LowestCommonAncestorGeneralBinaryTree {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
            left = right = null;
        }
    }

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        Stack<TreeNode> stack = new Stack<>();
        Map<TreeNode, TreeNode> parentMap = new HashMap<>();

        stack.push(root);
        parentMap.put(root, null);

        while (!parentMap.containsKey(p) || !parentMap.containsKey(q)) {
            var parent = stack.pop();

            if (null != parent.left) {
                parentMap.put(parent.left, parent);
                stack.push(parent.left);
            }

            if (null != parent.right) {
                parentMap.put(parent.right, parent);
                stack.push(parent.right);
            }
        }

        Set<TreeNode> ancestors = new HashSet<>();
        while (p != null) {
            ancestors.add(p);
            p = parentMap.get(p);
        }

        while (!ancestors.contains(q)) {
            q = parentMap.get(q);
        }

        return q;
    }

}
