package coding.linkedin;

public class BinaryTreeUpsideDown {
    
    // Definition for a binary tree node
    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        
        TreeNode() {}
        
        TreeNode(int val) {
            this.val = val;
        }
        
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }
    
    /**
     * Recursive approach to turn a binary tree upside down
     * Time Complexity: O(n) - where n is the number of nodes
     * Space Complexity: O(h) - where h is the height of the tree (recursion stack)
     */
    public TreeNode upsideDownBinaryTreeRecursive(TreeNode root) {
        if (root == null || root.left == null) {
            return root;
        }
        
        // Recursively turn the left subtree upside down and get the new root
        TreeNode newRoot = upsideDownBinaryTreeRecursive(root.left);
        
        // Update pointers: current node's left child points to current node's right child
        root.left.left = root.right;
        
        // Update pointers: current node's left child points back to current node
        root.left.right = root;
        
        // Clear original root's children to avoid cycles
        root.left = null;
        root.right = null;
        
        return newRoot;
    }
    
    /**
     * Iterative approach to turn a binary tree upside down
     * Time Complexity: O(n) - where n is the number of nodes
     * Space Complexity: O(1) - constant extra space
     */
    public TreeNode upsideDownBinaryTree(TreeNode root) {
        if (root == null) {
            return null;
        }
        
        TreeNode current = root;
        TreeNode nextLeft = null;
        TreeNode nextRight = null;
        TreeNode prev = null;
        
        while (current != null) {
            // Save the next left child before we modify pointers
            nextLeft = current.left;
            
            // Change left child to previous right child
            current.left = nextRight;
            
            // Save the next right child before we modify pointers
            nextRight = current.right;
            
            // Change right child to previous parent (root)
            current.right = prev;
            
            // Move to the next node in the original tree
            prev = current;
            current = nextLeft;
        }
        
        return prev;
    }
    
    /**
     * Example usage
     */
    public static void main(String[] args) {
        BinaryTreeUpsideDown solution = new BinaryTreeUpsideDown();
        
        // Create a sample tree:
        //       1
        //      / \
        //     2   3
        //    / \
        //   4   5
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        
        // Upside down the tree (iterative solution)
        TreeNode newRoot = solution.upsideDownBinaryTree(root);
        
        // After transformation:
        //     4
        //    / \
        //   5   2
        //      / \
        //     3   1
        System.out.println("New root value: " + newRoot.val);  // Should be 4
    }
}
