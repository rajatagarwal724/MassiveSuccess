package coding.top75.trees;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class EvenOddTree {

    class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    public boolean isEvenOddTree(TreeNode root) {
        int level = 0;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> levelRes = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                var node = queue.poll();
                levelRes.add(node.val);
                if (null != node.left) {
                    queue.offer(node.left);
                }
                if (null != node.right) {
                    queue.offer(node.right);
                }
            }
            if (level % 2 == 0) {
                for (int i = 0; i < levelRes.size(); i++) {
                    var elem = levelRes.get(i);
                    if (elem % 2 != 1) {
                        return false;
                    }
                    if (i > 0 && elem < levelRes.get(i - 1)) {
                        return false;
                    }
                }
            } else {
                for (int i = 0; i < levelRes.size(); i++) {
                    var elem = levelRes.get(i);
                    if (elem % 2 != 0) {
                        return false;
                    }
                    if (i > 0 && elem > levelRes.get(i - 1)) {
                        return false;
                    }
                }
            }
            level++;
        }

        return true;
    }

    public static void main(String[] args) {

    }
}
