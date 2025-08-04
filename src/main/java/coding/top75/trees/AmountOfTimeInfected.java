package coding.top75.trees;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class AmountOfTimeInfected {
    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        public TreeNode(int x) {
            val = x;
        }

        TreeNode(int x, TreeNode left, TreeNode right) {
            val = x;
            this.left = left;
            this.right = right;
        }
    }

    class NodeInfo {
        TreeNode parent;
        TreeNode node;

        public NodeInfo(TreeNode parent, TreeNode node) {
            this.parent = parent;
            this.node = node;
        }
    }

    public int amountOfTime(TreeNode root, int start) {
        Map<Integer, NodeInfo> parentMap = new HashMap<>();
        buildParentMap(root, null, parentMap);
        Queue<TreeNode> queue = new LinkedList<>();
        TreeNode startNodeInfo = parentMap.get(start).node;
        queue.offer(startNodeInfo);
        int minutes = -1;
        Set<TreeNode> infected = new HashSet<>();
        infected.add(startNodeInfo);

        while (!queue.isEmpty()) {
            int size = queue.size();
            minutes++;
            for (int i = 0; i < size; i++) {
                var node = queue.poll();
                if (null != node.left && infected.add(node.left)) {
                    queue.offer(node.left);
                }
                if (null != node.right && infected.add(node.right)) {
                    queue.offer(node.right);
                }

                if (null != parentMap.get(node.val).parent && infected.add(parentMap.get(node.val).parent)) {
                    queue.offer(parentMap.get(node.val).parent);
                }
            }
        }
        return minutes;
    }

    private void buildParentMap(TreeNode node, TreeNode parent, Map<Integer, NodeInfo> parentMap) {
        if (node == null) {
            return;
        }
        buildParentMap(node.left, node, parentMap);
        parentMap.put(node.val, new NodeInfo(parent, node));
        buildParentMap(node.right, node, parentMap);
    }

    public static void main(String[] args) {
        var sol = new AmountOfTimeInfected();
        var root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);

        root.left.right = new TreeNode(4);
        root.right.left = new TreeNode(5);
        root.right.right = new TreeNode(6);

        System.out.println(sol.amountOfTime(root, 3));
    }
}
