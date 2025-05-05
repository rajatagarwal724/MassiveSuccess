package coding.Graphs.BFS;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PopulateNextRightPointer {
    // Definition for a Node.
    static class Node {
        public int val;
        public Node left;
        public Node right;
        public Node next;

        public Node() {
        }

        public Node(int _val) {
            val = _val;
        }

        public Node(int _val, Node _left, Node _right, Node _next) {
            val = _val;
            left = _left;
            right = _right;
            next = _next;
        }
    }


    public Node connect(Node root) {
        if (null == root || null == root.left || null == root.right) {
            return root;
        }
        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);

        while(!queue.isEmpty()) {
            int size = queue.size();
            Node prevNode = null;
            for (int i = 0; i < size; i++) {
                Node currentNode = queue.poll();
                if (null != prevNode) {
                    prevNode.next = currentNode;
                }
                prevNode = currentNode;
                if (null != currentNode.left) {
                    queue.offer(currentNode.left);
                }

                if (null != currentNode.right) {
                    queue.offer(currentNode.right);
                }

            }

        }

        return root;
    }

    public static void main(String[] args) {
        Node root = new Node(1);

        root.left = new Node(2);
        root.right = new Node(3);

        root.left.left = new Node(4);
        root.left.right = new Node(5);

        root.right.left = new Node(6);
        root.right.right = new Node(7);

        var sol = new PopulateNextRightPointer();

        Node result = sol.connect(root);

        System.out.println(result);
    }
}
