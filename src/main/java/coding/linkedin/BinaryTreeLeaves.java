package coding.linkedin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TreeMap;

public class BinaryTreeLeaves {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    private List<List<Integer>> solution;

    private int getHeight(TreeNode root) {

        // return -1 for null nodes
        if (root == null) {
            return -1;
        }

        // first calculate the height of the left and right children
        int leftHeight = getHeight(root.left);
        int rightHeight = getHeight(root.right);

        int currHeight = Math.max(leftHeight, rightHeight) + 1;

        if (this.solution.size() == currHeight) {
            this.solution.add(new ArrayList<>());
        }

        this.solution.get(currHeight).add(root.val);

        return currHeight;
    }

    public List<List<Integer>> findLeaves(TreeNode root) {
        this.solution = new ArrayList<>();

        getHeight(root);

        return this.solution;
    }



    public List<List<Integer>> findLeaves_1(TreeNode root) {
        TreeMap<Integer, List<Integer>> map = new TreeMap<>();
        getHeight(root, map);
        return new ArrayList<>(map.values());
    }

    private int getHeight(TreeNode root, TreeMap<Integer, List<Integer>> map) {
        if (null == root) {
            return 0;
        }

        int leftHeight = getHeight(root.left, map);
        int rightHeight = getHeight(root.right, map);

        int currentHeight = Math.max(leftHeight, rightHeight) + 1;

        map.computeIfAbsent(currentHeight, integer -> new ArrayList<>()).add(root.val);
        return currentHeight;
    }


    public static void main(String[] args) {
        var root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);

        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);

        var sol = new BinaryTreeLeaves();
        System.out.println(sol.findLeaves_1(root));
    }
}
