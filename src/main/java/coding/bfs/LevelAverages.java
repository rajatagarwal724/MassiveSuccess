package coding.bfs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LevelAverages {
    public List<Double> findLevelAverages(TreeNode root) {
        List<Double> result = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            double sum = 0;
            for (int i = 0; i < size; i++) {
                TreeNode currentNode = queue.poll();
                sum += currentNode.val;

                if (null != currentNode.left) {
                    queue.offer(currentNode.left);
                }
                if (null != currentNode.right) {
                    queue.offer(currentNode.right);
                }
            }
            result.add(sum/size);
        }

        return result;
    }
}
