package coding.top75.trees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class AllNodesAtDistanceKBinaryTree {
    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    public List<Integer> distanceK(TreeNode root, TreeNode target, int K) {
        Map<TreeNode, TreeNode> parentMap = new HashMap<>();
        parentMap.put(root, null);
        buildParentMap(root, null, parentMap);

        Set<TreeNode> visited = new HashSet<>();
        Queue<TreeNode> queue = new LinkedList<>();
        // Perform BFS from the target node
        queue.offer(target);
        visited.add(target);
        int distance = 0;
        while (!queue.isEmpty()) {
            if (distance == K) {
                break;
            }
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                var node = queue.poll();
                if (null != node.left && visited.add(node.left)) {
                    queue.offer(node.left);
                }

                if (null != node.right && visited.add(node.right)) {
                    queue.offer(node.right);
                }

                if (null != parentMap.get(node) && visited.add(parentMap.get(node))) {
                    queue.offer(parentMap.get(node));
                }
            }
            distance++;
        }

        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            result.add(queue.poll().val);
        }

        return result;
    }

    private void buildParentMap(TreeNode node, TreeNode parent, Map<TreeNode, TreeNode> parentMap) {
        if (null == node) {
            return;
        }
        buildParentMap(node.left, node, parentMap);
        parentMap.put(node, parent);
        buildParentMap(node.right, node, parentMap);
    }

    public static void main(String[] args) {
        var sol = new AllNodesAtDistanceKBinaryTree();
        var root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);

        root.left.right = new TreeNode(4);

        root.right.left = new TreeNode(5);
        root.right.right = new TreeNode(6);

        System.out.println(sol.distanceK(root, root.left, 2));

    }

}
