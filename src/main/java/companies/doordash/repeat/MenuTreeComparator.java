package companies.doordash.repeat;

import java.util.*;

/**
 * DoorDash Menu Tree Comparison - Detect Changes Between Menu Versions
 * 
 * Problem: Compare two menu trees (old vs new) and count total changes:
 * - Added nodes (key exists in new but not in old)
 * - Deleted nodes (key exists in old but not in new)  
 * - Changed nodes (same key but different value)
 * 
 * Time Complexity: O(N + M) where N, M are nodes in old and new trees
 * Space Complexity: O(N + M) for the maps
 */
public class MenuTreeComparator {
    
    static class Node {
        String key;
        int value;
        List<Node> children;
        
        public Node(String key, int value) {
            this.key = key;
            this.value = value;
            this.children = new ArrayList<>();
        }
        
        public Node(String key, int value, List<Node> children) {
            this.key = key;
            this.value = value;
            this.children = children != null ? children : new ArrayList<>();
        }
    }
    
    /**
     * Main method to count changed nodes between two menu trees
     * @param oldMenu Root of old menu tree
     * @param newMenu Root of new menu tree  
     * @return Total number of changed/added/deleted nodes
     */
    public int countChangedNodes(Node oldMenu, Node newMenu) {
        // Convert trees to maps for efficient comparison
        Map<String, Node> oldMap = new HashMap<>();
        Map<String, Node> newMap = new HashMap<>();
        
        // Build maps from both trees
        if (oldMenu != null) {
            buildMap(oldMenu, oldMap);
        }
        if (newMenu != null) {
            buildMap(newMenu, newMap);
        }
        
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(oldMap.keySet());
        allKeys.addAll(newMap.keySet());
        
        int changeCount = 0;
        
        for (String key : allKeys) {
            Node oldNode = oldMap.get(key);
            Node newNode = newMap.get(key);
            
            if (oldNode == null) {
                // Node was added in new menu
                changeCount++;
            } else if (newNode == null) {
                // Node was deleted from old menu
                changeCount++;
            } else if (oldNode.value != newNode.value) {
                // Node value changed
                changeCount++;
            }
            // If both exist and values are same, no change
        }
        
        return changeCount;
    }
    
    /**
     * Build a map from tree structure using DFS
     * @param node Current node to process
     * @param map Map to populate with key -> node mappings
     */
    private void buildMap(Node node, Map<String, Node> map) {
        if (node == null) return;
        
        // Add current node to map
        map.put(node.key, node);
        
        // Recursively process children
        for (Node child : node.children) {
            buildMap(child, map);
        }
    }
    
    /**
     * Enhanced version that returns detailed change information
     */
    public MenuChangeResult getDetailedChanges(Node oldMenu, Node newMenu) {
        Map<String, Node> oldMap = new HashMap<>();
        Map<String, Node> newMap = new HashMap<>();
        
        if (oldMenu != null) buildMap(oldMenu, oldMap);
        if (newMenu != null) buildMap(newMenu, newMap);
        
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(oldMap.keySet());
        allKeys.addAll(newMap.keySet());
        
        List<String> added = new ArrayList<>();
        List<String> deleted = new ArrayList<>();
        List<String> changed = new ArrayList<>();
        
        for (String key : allKeys) {
            Node oldNode = oldMap.get(key);
            Node newNode = newMap.get(key);
            
            if (oldNode == null) {
                added.add(key);
            } else if (newNode == null) {
                deleted.add(key);
            } else if (oldNode.value != newNode.value) {
                changed.add(key);
            }
        }
        
        return new MenuChangeResult(added, deleted, changed);
    }
    
    /**
     * Result class for detailed change information
     */
    public static class MenuChangeResult {
        public final List<String> addedNodes;
        public final List<String> deletedNodes;
        public final List<String> changedNodes;
        public final int totalChanges;
        
        public MenuChangeResult(List<String> added, List<String> deleted, List<String> changed) {
            this.addedNodes = added;
            this.deletedNodes = deleted;
            this.changedNodes = changed;
            this.totalChanges = added.size() + deleted.size() + changed.size();
        }
        
        @Override
        public String toString() {
            return String.format("Changes: %d total (%d added, %d deleted, %d changed)", 
                               totalChanges, addedNodes.size(), deletedNodes.size(), changedNodes.size());
        }
    }
    
    /**
     * Optimized version for very large trees using iterative approach
     */
    public int countChangedNodesIterative(Node oldMenu, Node newMenu) {
        Map<String, Node> oldMap = buildMapIterative(oldMenu);
        Map<String, Node> newMap = buildMapIterative(newMenu);
        
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(oldMap.keySet());
        allKeys.addAll(newMap.keySet());
        
        return (int) allKeys.stream()
                          .mapToLong(key -> {
                              Node oldNode = oldMap.get(key);
                              Node newNode = newMap.get(key);
                              
                              if (oldNode == null || newNode == null) {
                                  return 1; // Added or deleted
                              }
                              return oldNode.value != newNode.value ? 1 : 0; // Changed or same
                          })
                          .sum();
    }
    
    /**
     * Build map using iterative DFS to avoid stack overflow on deep trees
     */
    private Map<String, Node> buildMapIterative(Node root) {
        Map<String, Node> map = new HashMap<>();
        if (root == null) return map;
        
        Stack<Node> stack = new Stack<>();
        stack.push(root);
        
        while (!stack.isEmpty()) {
            Node current = stack.pop();
            map.put(current.key, current);
            
            // Add children to stack in reverse order to maintain DFS order
            for (int i = current.children.size() - 1; i >= 0; i--) {
                stack.push(current.children.get(i));
            }
        }
        
        return map;
    }
    
    public static void main(String[] args) {
        MenuTreeComparator comparator = new MenuTreeComparator();
        
        // Build sample old menu tree
        Node oldMenu = new Node("menu", 1);
        Node beverages = new Node("beverages", 2);
        Node food = new Node("food", 3);
        
        beverages.children.add(new Node("coffee", 5));
        beverages.children.add(new Node("tea", 3));
        food.children.add(new Node("pizza", 12));
        food.children.add(new Node("burger", 8));
        
        oldMenu.children.add(beverages);
        oldMenu.children.add(food);
        
        // Build sample new menu tree with changes
        Node newMenu = new Node("menu", 1);
        Node newBeverages = new Node("beverages", 2);
        Node newFood = new Node("food", 3);
        
        newBeverages.children.add(new Node("coffee", 6)); // Price changed
        newBeverages.children.add(new Node("tea", 3));    // Same
        newBeverages.children.add(new Node("juice", 4));  // Added
        newFood.children.add(new Node("pizza", 12));      // Same
        // burger deleted
        newFood.children.add(new Node("pasta", 10));      // Added
        
        newMenu.children.add(newBeverages);
        newMenu.children.add(newFood);
        
        // Test basic counting
        System.out.println("=== DOORDASH MENU COMPARISON ===");
        int totalChanges = comparator.countChangedNodes(oldMenu, newMenu);
        System.out.println("Total changed nodes: " + totalChanges);
        
        // Test detailed changes
        MenuChangeResult result = comparator.getDetailedChanges(oldMenu, newMenu);
        System.out.println("\nDetailed changes:");
        System.out.println(result);
        System.out.println("Added: " + result.addedNodes);
        System.out.println("Deleted: " + result.deletedNodes);
        System.out.println("Changed: " + result.changedNodes);
        
        // Test iterative version
        int iterativeResult = comparator.countChangedNodesIterative(oldMenu, newMenu);
        System.out.println("\nIterative result: " + iterativeResult);
        System.out.println("Results match: " + (totalChanges == iterativeResult));
        
        // Performance test with larger tree
        System.out.println("\n=== PERFORMANCE TEST ===");
        testPerformance(comparator);
    }
    
    /**
     * Performance test with larger trees
     */
    private static void testPerformance(MenuTreeComparator comparator) {
        // Create larger test trees
        Node largeOldMenu = createLargeMenu("old", 1000);
        Node largeNewMenu = createLargeMenu("new", 1000);
        
        long startTime = System.currentTimeMillis();
        int changes = comparator.countChangedNodes(largeOldMenu, largeNewMenu);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Large tree comparison:");
        System.out.println("Nodes processed: ~2000");
        System.out.println("Changes found: " + changes);
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
    }
    
    /**
     * Helper to create large test trees
     */
    private static Node createLargeMenu(String prefix, int nodeCount) {
        Node root = new Node(prefix + "_menu", 1);
        
        for (int i = 0; i < nodeCount / 10; i++) {
            Node category = new Node(prefix + "_category_" + i, i);
            for (int j = 0; j < 10; j++) {
                Node item = new Node(prefix + "_item_" + i + "_" + j, i * 10 + j);
                category.children.add(item);
            }
            root.children.add(category);
        }
        
        return root;
    }
}
