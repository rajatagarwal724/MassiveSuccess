package coding.bfs;

import java.util.LinkedList;
import java.util.Queue;

public class ConnectLevelOrderSiblings {
    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode next;

        TreeNode(int x) {
            val = x;
            left = right = next = null;
        }
    }


    public TreeNode connect(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            TreeNode prevNode = null;
            for (int i = 0; i < levelSize; i++) {
                TreeNode currNode = queue.poll();
                if (null != prevNode) {
                    prevNode.next = currNode;
                }
                prevNode = currNode;

                if (null != currNode.left) {
                    queue.offer(currNode.left);
                }
                if (null != currNode.right) {
                    queue.offer(currNode.right);
                }
            }
        }
        return root;
    }

    // level order traversal using 'next' pointer
    public static void printLevelOrder(TreeNode root) {
        TreeNode nextLevelRoot = root;
        while (nextLevelRoot != null) {
            TreeNode current = nextLevelRoot;
            nextLevelRoot = null;
            while (current != null) {
                System.out.print(current.val + " ");
                if (nextLevelRoot == null) {
                    if (current.left != null)
                        nextLevelRoot = current.left;
                    else if (current.right != null)
                        nextLevelRoot = current.right;
                }
                current = current.next;
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        var sol = new ConnectLevelOrderSiblings();
        TreeNode root = new TreeNode(12);
        root.left = new TreeNode(7);
        root.right = new TreeNode(1);
        root.left.left = new TreeNode(9);
        root.right.left = new TreeNode(10);
        root.right.right = new TreeNode(5);
        root = sol.connect(root);
        System.out.println("Level order traversal using 'next' pointer: ");
        printLevelOrder(root);
    }
}
