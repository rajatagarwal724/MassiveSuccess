package coding.bfs;

import java.util.LinkedList;
import java.util.Queue;

public class ConnectAllSiblings {
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

    ;

    public TreeNode connect(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        TreeNode prevNode = null;

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                TreeNode currNode = queue.poll();

                if (null != prevNode) {
                    prevNode.next = currNode;
                }
                prevNode = currNode;

                if (null != currNode.left) {
                    queue.offer(currNode.left);
                }
                if (null != currNode.right) {
                    queue.offer(currNode.right);
                }
            }
        }
        return root;
    }
}
