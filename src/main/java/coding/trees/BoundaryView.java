package coding.trees;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class BoundaryView {
    public static class TreeNode {
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

    public List<Integer> boundaryOfBinaryTree(TreeNode root) {
        if (null == root) {
            return new ArrayList<>();
        }
        List<Integer> result = new ArrayList<>();
        result.add(root.val);

        if (root.left == null && root.right == null) {
            return result;
        }

        if (null != root.left) {
            traverseLeft(root.left, result);
        }
        traverseLeaf(root, result);
        if (null != root.right) {
            traverseRight(root.right, result);
        }
        return result;
    }

    private void traverseLeft(TreeNode root, List<Integer> result) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (null != node.left || null != node.right) {
                    result.add(node.val);
                }
                if (null != node.left) {
                    queue.offer(node.left);
                } else if (null != node.right) {
                    queue.offer(node.right);
                }
            }
        }
    }

    private void traverseLeaf(TreeNode root, List<Integer> result) {
        if (null != root) {
            if (null == root.left && null == root.right) {
                result.add(root.val);
            } else {
                traverseLeaf(root.left, result);
                traverseLeaf(root.right, result);
            }
        }
    }

    private void traverseRight(TreeNode root, List<Integer> result) {
        Queue<TreeNode> queue = new LinkedList<>();
        Stack<Integer> stack = new Stack<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (null != node.left || null != node.right) {
                    stack.push(node.val);
                }
                if (null != node.right) {
                    queue.offer(node.right);
                } else if (null != node.left) {
                    queue.offer(node.left);
                }
            }
        }
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }
    }

    public static void main(String[] args) {
        var sol = new BoundaryView();
        var root = new TreeNode(1);
        root.right = new TreeNode(2);
        root.right.left = new TreeNode(3);
        root.right.right = new TreeNode(4);

        System.out.println(StringUtils.join(sol.boundaryOfBinaryTree(root)));

        root = new TreeNode(1);

        root.left = new TreeNode(2);
        root.right = new TreeNode(3);

        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);

        root.left.right.left = new TreeNode(7);
        root.left.right.right = new TreeNode(8);

        root.right.left = new TreeNode(6);
        root.right.left.left = new TreeNode(9);
        root.right.left.right = new TreeNode(10);

        System.out.println(StringUtils.join(sol.boundaryOfBinaryTree(root)));
    }
}
