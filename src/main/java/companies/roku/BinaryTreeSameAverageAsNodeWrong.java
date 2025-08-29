package companies.roku;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class BinaryTreeSameAverageAsNodeWrong {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    public int getNodesWithSameAverage(TreeNode root) {
        Map<TreeNode, Integer> map = new HashMap<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int result = 0;
        while (!queue.isEmpty()) {
            var node = queue.poll();
            int average = getAverage(node, map);
            if (average == node.val) {
                result++;
            }
            if (null != node.left) {
                queue.offer(node.left);
            }
            if (null != node.right) {
                queue.offer(node.right);
            }
        }
        return result;
    }

    private int getAverage(TreeNode root, Map<TreeNode, Integer> cache) {
        if (null == root) {
            return 0;
        }

        if (cache.containsKey(root)) {
            return cache.get(root);
        }

        int sum = 0;
        int count = 0;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode currNode = queue.poll();
                sum += currNode.val;
                count++;
                if (null != currNode.left) {
                    queue.offer(currNode.left);
                }
                if (null != currNode.right) {
                    queue.offer(currNode.right);
                }
            }
        }
        int average = (int) Math.floor(sum / count);
        cache.put(root, average);
        return average;
    }

    public static void main(String[] args) {
        var solution = new BinaryTreeSameAverageAsNodeWrong();
        var root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        root.right.left = new TreeNode(6);
        root.right.right = new TreeNode(7);
        System.out.println(solution.getNodesWithSameAverage(root));
    }
}
