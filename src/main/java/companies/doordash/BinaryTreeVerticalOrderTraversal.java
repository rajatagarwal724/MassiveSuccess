package companies.doordash;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TreeMap;

public class BinaryTreeVerticalOrderTraversal {

    class TreeNode {
        int val;
        TreeNode left, right;

        public TreeNode(int val) {
            this.val = val;
        }
    }

    record Node(int distance, TreeNode treeNode) {}

    public List<List<Integer>> verticalOrder(TreeNode root) {
        TreeMap<Integer, List<Integer>> map = new TreeMap<>();

        Queue<Node> queue = new LinkedList<>();
        queue.offer(new Node(0, root));

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                var node = queue.poll();
                var parent = node.treeNode;
                var dist = node.distance;
                map.computeIfAbsent(dist, s -> new ArrayList<>()).add(parent.val);

                if (null != parent.left) {
                    queue.offer(new Node(dist - 1, parent.left));
                }

                if (null != parent.right) {
                    queue.offer(new Node(dist + 1, parent.right));
                }
            }
        }

        return new ArrayList<>(map.values());
    }

    public static void main(String[] args) {

    }
}
