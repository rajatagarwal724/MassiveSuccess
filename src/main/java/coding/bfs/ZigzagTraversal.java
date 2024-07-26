package coding.bfs;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ZigzagTraversal {

    public List<List<Integer>> traverse(TreeNode root) {
        List<List<Integer>> result = new LinkedList<List<Integer>>();

        boolean parseLeftToRight = true;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> currentLevel = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                TreeNode currentNode = queue.poll();
                if (parseLeftToRight) {
                    currentLevel.add(currentNode.val);
                } else {
                    currentLevel.add(0, currentNode.val);
                }

                if (null != currentNode.left) {
                    queue.offer(currentNode.left);
                }

                if (null != currentNode.right) {
                    queue.offer(currentNode.right);
                }
            }
            parseLeftToRight = !parseLeftToRight;
            result.add(currentLevel);
        }

        return result;
    }
}
