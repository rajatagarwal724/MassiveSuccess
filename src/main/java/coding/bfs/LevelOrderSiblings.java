package coding.bfs;

import java.util.LinkedList;
import java.util.Queue;

public class LevelOrderSiblings {
    class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode next;

        TreeNode(int x) {
            val = x;
            left = right = next = null;
        }
    }

    public TreeNode connect(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            TreeNode prevNode = null;
            for (int i = 0; i < size; i++) {
                TreeNode currentNode = queue.poll();
                if (null != prevNode) {
                    prevNode.next = currentNode;
                }
                prevNode = currentNode;

                if (null != currentNode.left) {
                    queue.offer(currentNode.left);
                }

                if (null != currentNode.right) {
                    queue.offer(currentNode.right);
                }
            }
        }
        return root;
    }
}
