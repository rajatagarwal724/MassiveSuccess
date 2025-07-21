package coding.linkedin;

import java.util.LinkedList;
import java.util.Queue;

public class SerializeDeSerializeBinaryTree {

    // Definition for a binary tree node.
    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                var node = queue.poll();

                if (node != null) {
                    sb.append(node.val).append(",");
                    queue.offer(node.left);
                    queue.offer(node.right);
                } else {
                    sb.append("null,");
                }
            }
        }

        return sb.toString();  // Return the serialized string
    }

    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        if (data.equals("")) {
            return null;
        }

        String[] arr = data.split(",");
        TreeNode root = new TreeNode(Integer.parseInt(arr[0]));

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        for (int i = 1; i < arr.length && !queue.isEmpty(); i++) {
            var parent = queue.poll();

            if (!arr[i].equals("null")) {
                var left = new TreeNode(Integer.parseInt(arr[i]));
                parent.left = left;
                queue.offer(left);
            }

            if (++i < arr.length && !arr[i].equals("null")) {
                var right = new TreeNode(Integer.parseInt(arr[i]));
                parent.right = right;
                queue.offer(right);
            }
        }

        return root; // Return the root of the deserialized tree
    }

    public static void main(String[] args) {
        var sol = new SerializeDeSerializeBinaryTree();
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);

        root.right.left = new TreeNode(4);
        root.right.right = new TreeNode(5);
        var serializedStr = sol.serialize(root);
        System.out.println(serializedStr);

        var deserialized = sol.deserialize(serializedStr);
        System.out.println(deserialized);
    }
}
