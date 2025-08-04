package coding.top75.trees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class AllNodesDistanceK {

    class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    public List<Integer> distanceK(TreeNode root, TreeNode target, int K) {
        Map<TreeNode, TreeNode> parentMap = new HashMap<>();
        buildParentMap(root, null, parentMap);

        Set<TreeNode> seen = new HashSet<>();
        seen.add(target);

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(target);

        int distance = 0;

        while (!queue.isEmpty()) {
            if (distance == K) {
                List<Integer> result = new ArrayList<>();
                while (!queue.isEmpty()) {
                    var node = queue.poll();
                    result.add(node.val);
                }
                return result;
            }

            int size = queue.size();
            for (int i = 0; i < size; i++) {
                var node = queue.poll();
                if (null != node.left && seen.add(node.left)) {
                    queue.offer(node.left);
                }

                if (node.right != null && seen.add(node.right)) {
                    queue.offer(node.right);
                }

                if(null != parentMap.get(node) && seen.add(parentMap.get(node))) {
                    queue.offer(parentMap.get(node));
                }
            }
            distance++;
        }
        return new ArrayList<>();
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

    }
}
