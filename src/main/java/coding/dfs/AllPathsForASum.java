package coding.dfs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AllPathsForASum {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    public List<List<Integer>> findPaths(TreeNode root, int sum) {
        List<List<Integer>> allPaths = new ArrayList<>();
        List<Integer> path = new ArrayList<>();
        findPathsUtil(root, sum, path, allPaths);
        return allPaths;
    }

    private void findPathsUtil(TreeNode root, int sum, List<Integer> path, List<List<Integer>> allPaths) {
        if (null == root) {
            return;
        }
        if (null == root.left && null == root.right) {
            if (sum == path.stream().mapToInt(Integer::intValue).sum()) {
                allPaths.add(new ArrayList<>(path));
            }
            path.remove(path.size() - 1);
        }

        path.add(root.val);
        findPathsUtil(root.left, sum, path, allPaths);
        findPathsUtil(root.right, sum, path, allPaths);
    }

    public static void main(String[] args) {

    }
}
