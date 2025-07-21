package coding.linkedin;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class ClosestBinarySearchTreeValueII {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        public TreeNode(int val) {
            this.val = val;
        }
    }

    public List<Integer> closestKValues(TreeNode root, double target, int k) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(
                (a, b) -> (int) (Math.abs(target - b) - Math.abs(target - a))
        );
        List<Integer> values = new ArrayList<>();
        traverseTree(root, values);

        for (int i = 0; i < values.size(); i++) {
            if (i < k) {
                maxHeap.offer(values.get(i));
            } else {
                if (Math.abs(target - maxHeap.peek()) > Math.abs(target - values.get(i))) {
                    maxHeap.poll();
                    maxHeap.offer(values.get(i));
                }
            }
        }
        return new ArrayList<>(maxHeap);
    }

    public List<Integer> closestKValues_1(TreeNode root, double target, int k) {
        List<Integer> sortedList = new ArrayList<>();
        traverseTree(root, sortedList);

        int left = 0, right = sortedList.size() - k;

        while (left < right) {
            int mid = left + (right - left)/2;

            if (Math.abs(target - sortedList.get(mid + k)) < Math.abs(target - sortedList.get(mid))) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return sortedList.subList(left, left + k);
    }

    private void traverseTree(TreeNode root, List<Integer> values) {
        if (null == root) {
            return;
        }
        traverseTree(root.left, values);
        if (null != root) {
            values.add(root.val);
        }
        traverseTree(root.right, values);
    }

    public static void main(String[] args) {
        var sol = new ClosestBinarySearchTreeValueII();
        var root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(5);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);

        System.out.println(
                sol.closestKValues(root, 3.714286, 2)
        );

        System.out.println(
                sol.closestKValues_1(root, 3.714286, 2)
        );
    }
}
