package coding.bfs;

import java.util.LinkedList;
import java.util.Queue;

public class LevelOrderSuccessor {
    public TreeNode findSuccessor(TreeNode root, int key) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        TreeNode prevNode = null;

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int i = 0 ; i < size; i++) {
                TreeNode currNode = queue.poll();

                if (null != prevNode) {
                    return currNode;
                }
                if (currNode.val == key) {
                    prevNode = currNode;
                }

                if (null != currNode.left) {
                    queue.offer(currNode.left);
                }

                if (null != currNode.right) {
                    queue.offer(currNode.right);
                }
            }
        }

        return prevNode;
    }
}
