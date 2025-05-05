package companies.splunk;

import java.util.Arrays;
import java.util.List;

public class BstFromPreorderTraversal {

    class Node {
        int val;
        Node left;
        Node right;

        public Node(int val) {
            this.val = val;
            this.left = null;
            this.right = null;
        }

        public Node(int val, Node left, Node right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    private Node construct(List<Integer> pre) {
        Node root = null;
        for (int elem: pre) {
            root = constructUtil(root, elem);
        }
        return root;
    }

    private Node constructUtil(Node root, int elem) {
        if (null == root) {
            return new Node(elem);
        }

        if (root.val > elem) {
            root.left = constructUtil(root.left, elem);
        } else {
            root.right = constructUtil(root.right, elem);
        }
        return root;
    }

    private void inorder(Node node) {
        if (null == node) {
            return;
        }
        inorder(node.left);
        System.out.print(node.val + " ");
        inorder(node.right);
    }


    public static void main(String[] args) {
        var sol = new BstFromPreorderTraversal();
        List<Integer> pre = Arrays.asList(10, 5, 1, 7, 40, 50);
        Node root = sol.construct(pre);
        sol.inorder(root);
    }
}
