package companies.doordash.repeat;

import companies.doordash.MakingLargeIsland;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class MakingALargeIsland {

    int[][] DIRECTIONS = new int[][] {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
    };

    public int largestIsland(int[][] grid) {
        if (null == grid || grid.length == 0) {
            return 0;
        }

        Map<Integer, Integer> islandByArea = new HashMap<>();
        int islandId = 2;
        int m = grid.length;
        int n = grid[0].length;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    int area = bfs(i, j, grid, islandId);
                    islandByArea.put(islandId, area);
                    islandId++;
                }
            }
        }

        if (islandByArea.isEmpty()) {
            return 1;
        }

        if (islandByArea.size() == 1) {
            int area = islandByArea.get(2);
            if (area == ((m * n))) {
                return m * n;
            }
            return area + 1;
        }

        int maxArea = Integer.MIN_VALUE;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 0) {
                    Set<Integer> neighbourSet = new HashSet<>();

                    // Check Up
                    if (0 <= (i - 1) && grid[i-1][j] != 0) {
                        neighbourSet.add(grid[i - 1][j]);
                    }

                    // Check Down
                    if ((i + 1) < m && grid[i+1][j] != 0) {
                        neighbourSet.add(grid[i + 1][j]);
                    }

                    // Check Left
                    if (0 <= (j - 1) && grid[i][j-1] != 0) {
                        neighbourSet.add(grid[i][j-1]);
                    }

                    // Check Right
                    if ((j + 1) < n && grid[i][j+1] != 0) {
                        neighbourSet.add(grid[i][j+1]);
                    }
                    int area = 0;
                    for (int neighbourId: neighbourSet) {
                        area += islandByArea.getOrDefault(neighbourId, 0);
                    }
                    maxArea = Math.max(maxArea, 1 + area);
                }
            }
        }

        return maxArea;
    }

    private int bfs(int row, int col, int[][] grid, int islandId) {
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[] {row, col});
        grid[row][col] = islandId;
        int area = 1;
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            for (int[] dir: DIRECTIONS) {
                int newRow = cell[0] + dir[0];
                int newCol = cell[1] + dir[1];

                if (0 <= newRow && newRow < grid.length
                        && 0 <= newCol && newCol < grid[0].length
                        && grid[newRow][newCol] == 1
                ) {
                    grid[newRow][newCol] = islandId;
                    queue.offer(new int[] {newRow, newCol});
                    area++;
                }
            }
        }
        return area;
    }

    public static void main(String[] args) {
        var sol = new MakingALargeIsland();
        System.out.println(
                sol.largestIsland(
                        new int[][]{
                                {1, 0},
                                {0, 1}
                        }
                )
        );

        System.out.println(
                sol.largestIsland(
                        new int[][]{
                                {1, 1},
                                {1, 0}
                        }
                )
        );

        System.out.println(
                sol.largestIsland(
                        new int[][]{
                                {1, 1},
                                {1, 1}
                        }
                )
        );
    }
}
