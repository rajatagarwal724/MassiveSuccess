package companies.doordash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TreeMap;

/**
 * O NLogN
 */
public class VerticalOrderTraversal {

    static class TreeNode {
        int val;
        TreeNode left, right;

        public TreeNode(int val) {
            this.val = val;
        }
    }

    record Node(int column, int row, int value, TreeNode treeNode) {}

    public List<List<Integer>> verticalTraversal(TreeNode root) {
        if (null == root) {
            return new ArrayList<>();
        }
        TreeMap<Integer, List<Node>> map = new TreeMap<>();
        Queue<Node> queue = new LinkedList<>();
        queue.offer(new Node(0, 0, root.val, root));

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                var node = queue.poll();
                int col = node.column();
                int row = node.row();
                int value = node.value();
                TreeNode treeNode = node.treeNode();

                Node newNode = new Node(col, row, value, treeNode);

                map.computeIfAbsent(node.column(), s -> new ArrayList<>()).add(newNode);

                if (null != node.treeNode().left) {
                    queue.offer(new Node(col - 1, row + 1, treeNode.left.val, treeNode.left));
                }

                if (null != node.treeNode().right) {
                    queue.offer(new Node(col + 1, row + 1, treeNode.right.val, treeNode.right));
                }
            }
        }

        List<List<Integer>> result = new ArrayList<>();
        map.forEach((key, value) -> {
            List<Integer> sortedList = new ArrayList<>();
            value.sort((o1, o2) -> {
                if (o1.column == o2.column) {
                    if (o1.row == o2.row) {
                        return o1.value - o2.value;
                    }
                    return o1.row - o2.row;
                }
                return o1.column - o2.column;
            });
            value.forEach(node -> sortedList.add(node.value()));
            result.add(sortedList);
        });

        return result;
    }

    public static void main(String[] args) {
        var sol = new VerticalOrderTraversal();
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(6);
        root.right.left = new TreeNode(5);
        root.right.right = new TreeNode(7);


        sol.verticalTraversal(root).forEach(System.out::println);

    }
}
