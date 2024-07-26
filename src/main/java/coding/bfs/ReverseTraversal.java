package coding.bfs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ReverseTraversal {

    public List<List<Integer>> traverse(TreeNode root) {
        List<List<Integer>> result = new LinkedList<List<Integer>>();

        Queue<TreeNode> queue = new LinkedList<>();

        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> currentLevel = new ArrayList<>();
            for (int i= 0; i < size; i++) {
                TreeNode currentNode = queue.poll();
                currentLevel.add(currentNode.val);

                if (null != currentNode.left) {
                    queue.offer(currentNode.left);
                }
                if (null != currentNode.right) {
                    queue.offer(currentNode.right);
                }
            }
            result.add(0, currentLevel);
        }

        return result;
    }
}
