package coding.leetcode;

import java.util.*;

/**
 * LeetCode 864: Shortest Path to Get All Keys
 * 
 * Problem Description:
 * You are given a 2D grid of size m x n with:
 * - Empty cells represented as '.'
 * - Walls represented as '#'
 * - Keys represented as lowercase letters ('a', 'b', 'c', etc.)
 * - Locks represented as uppercase letters ('A', 'B', 'C', etc.)
 * - The starting position represented as '@'
 * 
 * You can move in any of the four cardinal directions (up, down, left, right).
 * You cannot pass through walls, and you can only pass through locks if you have collected the corresponding key.
 * You start at position '@' and need to collect all the keys in the minimum number of steps.
 * Return the minimum number of steps to collect all keys, or -1 if impossible.
 * 
 * Time Complexity: O(m * n * 2^k) where m,n are grid dimensions and k is the number of keys
 * Space Complexity: O(m * n * 2^k)
 */
public class ShortestPathToGetAllKeys {
    
    // Direction vectors for up, right, down, left
    private static final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
    
    public int shortestPathAllKeys(String[] grid) {
        int m = grid.length;
        int n = grid[0].length();
        
        // Find starting position and count total keys
        int startRow = 0, startCol = 0;
        int totalKeys = 0;
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                char cell = grid[i].charAt(j);
                if (cell == '@') {
                    startRow = i;
                    startCol = j;
                } else if (cell >= 'a' && cell <= 'f') {
                    // Count the number of keys (assume keys are 'a' through 'f')
                    totalKeys = Math.max(totalKeys, cell - 'a' + 1);
                }
            }
        }
        
        // State representation: (row, col, keys)
        // keys is a bitmask where bit i is 1 if we have the ith key
        int finalKeyState = (1 << totalKeys) - 1; // All keys collected
        
        // BFS queue: (row, col, keyState, steps)
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{startRow, startCol, 0, 0}); // Start with no keys
        
        // Visited set to avoid revisiting same state
        Set<String> visited = new HashSet<>();
        visited.add(startRow + "," + startCol + ",0");
        
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0];
            int col = current[1];
            int keys = current[2];
            int steps = current[3];
            
            // Check if we've collected all keys
            if (keys == finalKeyState) {
                return steps;
            }
            
            // Try all four directions
            for (int[] dir : DIRECTIONS) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                
                // Check if the new position is valid
                if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n) {
                    char cell = grid[newRow].charAt(newCol);
                    
                    // Skip walls
                    if (cell == '#') continue;
                    
                    // Handle locks - can only pass if we have the key
                    if (cell >= 'A' && cell <= 'F') {
                        // Check if we have the corresponding key
                        int lockIndex = cell - 'A';
                        if ((keys & (1 << lockIndex)) == 0) {
                            // Don't have the key, can't pass
                            continue;
                        }
                    }
                    
                    // Handle key collection
                    int newKeys = keys;
                    if (cell >= 'a' && cell <= 'f') {
                        int keyIndex = cell - 'a';
                        // Add this key to our collection
                        newKeys |= (1 << keyIndex);
                    }
                    
                    // Avoid revisiting states
                    String newState = newRow + "," + newCol + "," + newKeys;
                    if (!visited.contains(newState)) {
                        visited.add(newState);
                        queue.offer(new int[]{newRow, newCol, newKeys, steps + 1});
                    }
                }
            }
        }
        
        // If we've exhausted all possibilities without finding all keys
        return -1;
    }
    
    // Test cases
    public static void main(String[] args) {
        ShortestPathToGetAllKeys solution = new ShortestPathToGetAllKeys();
        
        // Test case 1
        String[] grid1 = {"@.a.#", "###.#", "b.A.B"};
        System.out.println("Test case 1: " + solution.shortestPathAllKeys(grid1)); // Expected: 8
        
        // Test case 2
        String[] grid2 = {"@..aA", "..B#.", "....b"};
        System.out.println("Test case 2: " + solution.shortestPathAllKeys(grid2)); // Expected: 6
        
        // Test case 3
        String[] grid3 = {"@Aa"};
        System.out.println("Test case 3: " + solution.shortestPathAllKeys(grid3)); // Expected: -1
    }
}
