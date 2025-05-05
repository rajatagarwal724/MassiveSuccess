package companies.splunk;

import java.util.LinkedList;
import java.util.Queue;

class Node {
    int data;
    Node left, right;

    Node(int data) {
        this.data = data;
        left = right = null;
    }
}

public class BoundaryView {

    // Function to print the boundary view of the binary tree
    public static void printBoundary(Node root) {

    }

    // Sample usage
    public static void main(String[] args) {
        /*
                1
              /   \
             2     3
            / \   / \
           4   5 6   7
              /     / \
             8     9  10
        */
        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);
        root.left.left = new Node(4);
        root.left.right = new Node(5);
        root.left.right.left = new Node(8);
        root.right.left = new Node(6);
        root.right.right = new Node(7);
        root.right.right.left = new Node(9);
        root.right.right.right = new Node(10);

        System.out.println("Boundary view (level order):");
        printBoundary(root);
    }
}

