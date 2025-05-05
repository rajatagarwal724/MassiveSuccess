package coding.trees;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

public class BottomView {

    static class TreeNode {
        int val;
        TreeNode left, right;

        TreeNode(int val) {
            this.val = val;
            this.left = null;
            this.right = null;
        }
    }

    class Pair<K,V> {
        K key;
        V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public List<Integer> bottomView(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Map<Integer, Integer> viewMap = new TreeMap<>();
        Queue<Pair<TreeNode, Integer>> queue = new LinkedList<>();
        queue.offer(new Pair<>(root, 0));

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                var pair = queue.poll();
                var node = pair.key;
                var position = pair.value;
                viewMap.put(position, node.val);

                if (null != node.left) {
                    queue.offer(new Pair<>(node.left, position - 1));
                }
                if (null != node.right) {
                    queue.offer(new Pair<>(node.right, position + 1));
                }
            }
        }
        for (Map.Entry<Integer, Integer> entry: viewMap.entrySet()) {
            result.add(entry.getValue());
        }

        return result;
    }

    public static void main(String[] args) {
        var sol = new BottomView();
        var root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        System.out.println(StringUtils.join(sol.bottomView(root)));

        root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);

        root.left.left = new TreeNode(4);
        root.right.left = new TreeNode(5);
        root.right.right = new TreeNode(6);
        System.out.println(StringUtils.join(sol.bottomView(root)));
    }
}
