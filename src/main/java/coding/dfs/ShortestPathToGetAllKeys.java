package coding.dfs;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class ShortestPathToGetAllKeys {

    // Directions: up, right, down, left
    private static final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    public int shortestPathAllKeys(String[] grid) {
        int rows = grid.length;
        int cols = grid[0].length();

        // Step 1: Find starting position and count total keys
        int startRow = -1, startCol = -1;
        int totalKeys = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char cell = grid[i].charAt(j);
                if (cell == '@') {
                    startRow = i;
                    startCol = j;
                } else if (cell >= 'a' && cell <= 'f') {
                    // Key found - update total count
                    totalKeys = Math.max(totalKeys, cell - 'a' + 1);
                }
            }
        }

        // Step 2: BFS setup
        // State: [row, col, keysMask, steps]
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{startRow, startCol, 0, 0});

        // Visited: "row,col,keysMask"
        Set<String> visited = new HashSet<>();
        visited.add(startRow + "," + startCol + ",0");

        // Target: all keys collected
        int allKeysMask = (1 << totalKeys) - 1;

        // Step 3: BFS loop
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0];
            int col = current[1];
            int keysMask = current[2];
            int steps = current[3];

            // Check if we've collected all keys
            if (keysMask == allKeysMask) {
                return steps;
            }

            // Try all 4 directions
            for (int[] dir : DIRECTIONS) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                // Boundary check
                if (newRow < 0 || newRow >= rows || newCol < 0 || newCol >= cols) {
                    continue;
                }

                char cell = grid[newRow].charAt(newCol);

                // Skip walls
                if (cell == '#') {
                    continue;
                }

                // Handle locks - check if we have the key
                if (cell >= 'A' && cell <= 'F') {
                    int lockIndex = cell - 'A';
                    if ((keysMask & (1 << lockIndex)) == 0) {
                        // Don't have the key, can't pass
                        continue;
                    }
                }

                // Handle key collection
                int newKeysMask = keysMask;
                if (cell >= 'a' && cell <= 'f') {
                    int keyIndex = cell - 'a';
                    newKeysMask |= (1 << keyIndex);  // Add this key
                }

                // Check if this state was already visited
                String stateKey = newRow + "," + newCol + "," + newKeysMask;
                if (!visited.contains(stateKey)) {
                    visited.add(stateKey);
                    queue.offer(new int[]{newRow, newCol, newKeysMask, steps + 1});
                }
            }
        }

        // If we exhaust all possibilities without collecting all keys
        return -1;
    }

    // Test the implementation
    public static void main(String[] args) {
        var solution = new ShortestPathToGetAllKeys();

        // Test case 1: Expected output: 8
        String[] grid1 = {"@.a.#", "###.#", "b.A.B"};
        System.out.println("Test 1: " + solution.shortestPathAllKeys(grid1));

        // Test case 2: Expected output: 6
//        String[] grid2 = {"@..aA", "..B#.", "....b"};
//        System.out.println("Test 2: " + solution.shortestPathAllKeys(grid2));
//
//        // Test case 3: Expected output: -1 (impossible)
//        String[] grid3 = {"@Aa"};
//        System.out.println("Test 3: " + solution.shortestPathAllKeys(grid3));
    }
}
