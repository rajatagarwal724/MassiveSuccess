package coding.top75.trees;

import java.util.HashMap;
import java.util.Map;

public class PathSumIII {


    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }


    public int pathSum(TreeNode root, int targetSum) {
        Map<Integer, Integer> prefixSumToCurr = new HashMap<>();
        prefixSumToCurr.put(0, 1);
        return recurse(root, 0, targetSum, prefixSumToCurr);
    }

    private int recurse(TreeNode node, int currSum, int targetSum, Map<Integer, Integer> prefixSumToCurr) {
        if (null == node) {
            return 0;
        }
        currSum += node.val;
        int numPathsToCurr = prefixSumToCurr.getOrDefault(currSum - targetSum, 0);
        prefixSumToCurr.put(currSum, prefixSumToCurr.getOrDefault(currSum, 0) + 1);

        int result = numPathsToCurr
                + recurse(node.left, currSum, targetSum, prefixSumToCurr)
                + recurse(node.right, currSum, targetSum, prefixSumToCurr);

        prefixSumToCurr.put(currSum, prefixSumToCurr.get(currSum) - 1);
        return result;
    }

    public static void main(String[] args) {
        var sol = new PathSumIII();

        var root = new TreeNode(10);
        root.left = new TreeNode(5);
        root.right = new TreeNode(-3);
        root.right.right = new TreeNode(11);
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(2);
        root.left.right.left = new TreeNode(1);

        System.out.println(sol.pathSum(root, 18));
    }
}
