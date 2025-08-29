package coding.leetcode;

import java.util.HashSet;
import java.util.Set;

/**
 * LeetCode Problem: https://leetcode.com/problems/number-of-distinct-islands/
 *
 * Given a non-empty 2D array grid of 0's and 1's, an island is a group of 1's (representing land)
 * connected 4-directionally (horizontal or vertical.) You may assume all four edges of the grid are surrounded by water.
 *
 * Count the number of distinct islands. An island is considered to be the same as another if and only if one island
 * can be translated (and not rotated or reflected) to overlap with the other.
 */
public class NumberOfDistinctIslands {

    /**
     * Counts the number of distinct islands in a grid.
     *
     * The main idea is to generate a canonical representation (a "signature" or "path string") for the shape of each island.
     * If two islands have the same shape, their signatures will be identical.
     *
     * Algorithm:
     * 1. Iterate through each cell of the grid.
     * 2. If an unvisited land cell ('1') is found, it's the start of a new island.
     * 3. Begin a DFS from this cell to explore the entire island.
     * 4. During the DFS, build a path string. Each move from the original starting cell is recorded with a direction character
     *    (e.g., 'R' for right, 'D' for down). Backtracking from a recursive call is also recorded to capture the structure.
     * 5. Store these unique path strings in a HashSet.
     * 6. The final answer is the size of the HashSet.
     *
     * Time Complexity: O(R * C), where R is rows and C is columns. Each cell is visited once.
     * Space Complexity: O(R * C) for the visited array, recursion stack, and storing path strings.
     *
     * @param grid The 2D grid of 0s and 1s.
     * @return The number of distinct islands.
     */
    public int numDistinctIslands(int[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        int rows = grid.length;
        int cols = grid[0].length;
        boolean[][] visited = new boolean[rows][cols];
        Set<String> distinctIslands = new HashSet<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1 && !visited[i][j]) {
                    StringBuilder path = new StringBuilder();
                    dfs(grid, i, j, visited, path, 'S'); // 'S' for Start
                    distinctIslands.add(path.toString());
                }
            }
        }

        return distinctIslands.size();
    }

    private void dfs(int[][] grid, int r, int c, boolean[][] visited, StringBuilder path, char direction) {
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || visited[r][c] || grid[r][c] == 0) {
            return;
        }

        visited[r][c] = true;
        path.append(direction);

        dfs(grid, r + 1, c, visited, path, 'D'); // Down
        dfs(grid, r - 1, c, visited, path, 'U'); // Up
        dfs(grid, r, c + 1, visited, path, 'R'); // Right
        dfs(grid, r, c - 1, visited, path, 'L'); // Left

        // Append a backtrack character to distinguish paths. For example, a line vs. a T-shape.
        path.append('B'); // Backtrack
    }

    public static void main(String[] args) {
        NumberOfDistinctIslands solution = new NumberOfDistinctIslands();

        // Test Case 1
        int[][] grid1 = {
            {1, 1, 0, 0, 0},
            {1, 1, 0, 0, 0},
            {0, 0, 0, 1, 1},
            {0, 0, 0, 1, 1}
        };
        System.out.println("Test Case 1: Expected: 1, Got: " + solution.numDistinctIslands(grid1)); // Should be 1

        // Test Case 2
        int[][] grid2 = {
            {1, 1, 0, 1, 1},
            {1, 0, 0, 0, 0},
            {0, 0, 0, 0, 1},
            {1, 1, 0, 1, 1}
        };
        System.out.println("Test Case 2: Expected: 3, Got: " + solution.numDistinctIslands(grid2)); // Should be 3

        // Test Case 3: Different shapes
        int[][] grid3 = {
            {1, 1, 0},
            {0, 1, 1},
            {0, 0, 0},
            {1, 1, 1},
            {0, 1, 0}
        };
        System.out.println("Test Case 3: Expected: 2, Got: " + solution.numDistinctIslands(grid3)); // Should be 2
    }
}
