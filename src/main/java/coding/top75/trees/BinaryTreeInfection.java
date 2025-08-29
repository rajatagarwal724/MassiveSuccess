package coding.top75.trees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BinaryTreeInfection {


    class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }

        TreeNode(int x, TreeNode left, TreeNode right) {
            val = x;
            this.left = left;
            this.right = right;
        }
    }

    public int amountOfTime(TreeNode root, int start) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        buildGraph(root, null, graph);
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> infected = new HashSet<>();
        queue.offer(start);
        infected.add(start);
        int time = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                var node = queue.poll();
                for (int neighbour: graph.getOrDefault(node, new ArrayList<>())) {
                    if (!infected.contains(neighbour)) {
                        queue.offer(neighbour);
                        infected.add(neighbour);
                    }
                }
            }
            time++;
        }
        return time;
    }

    private void buildGraph(TreeNode node, TreeNode parent, Map<Integer, List<Integer>> graph) {
        if (null == node) {
            return;
        }
        buildGraph(node.left, node, graph);
        if (null != parent) {
            graph.computeIfAbsent(node.val, s -> new ArrayList<>()).add(parent.val);
            graph.computeIfAbsent(parent.val, s -> new ArrayList<>()).add(node.val);
        }
        buildGraph(node.right, node, graph);
    }


}
