package coding.bfs;

import java.util.LinkedList;
import java.util.Queue;

public class MinimumDepth {

    public int findDepth(TreeNode root) {
        int minimumTreeDepth = 0;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            minimumTreeDepth++;
            for (int i = 0; i < size; i++) {
                TreeNode currentNode = queue.poll();
                if (null == currentNode.left && null == currentNode.right) {
                    return minimumTreeDepth;
                }
                if (null != currentNode.left) {
                    queue.offer(currentNode.left);
                }
                if (null != currentNode.right) {
                    queue.offer(currentNode.right);
                }
            }
        }
        return minimumTreeDepth;
    }
}
