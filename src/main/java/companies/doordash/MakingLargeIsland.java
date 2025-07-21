package companies.doordash;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MakingLargeIsland {

    public int largestIsland(int[][] grid) {
        int islandId = 2;
        Map<Integer, Integer> islandByAreaMap = new HashMap<>();

//        boolean[][] visited = new boolean[grid.length][grid[0].length];

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) {
                    int area = dfs(i, j, islandId, grid);
                    islandByAreaMap.put(islandId, area);
                    islandId++;
                }
            }
        }
        System.out.println(islandByAreaMap);

        if (islandByAreaMap.isEmpty()) {
            return 1;
        }

        if (islandByAreaMap.size() == 1) {
            int area = islandByAreaMap.get(2);
            if (area == (grid.length * grid[0].length)) {
                return grid.length * grid[0].length;
            }
            return area + 1;
        }

        int maxIslandArea = 1;

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                if (grid[row][col] == 0) {
                    int currentIslandArea = 1;
                    Set<Integer> visitedNeighbourId = new HashSet<>();

                    if (row + 1 < grid.length && grid[row + 1][col] != 0) {
                        visitedNeighbourId.add(grid[row + 1][col]);
                    }

                    if (0 <= (row - 1) && grid[row - 1][col] != 0) {
                        visitedNeighbourId.add(grid[row - 1][col]);
                    }

                    if (col + 1 < grid[0].length && grid[row][col + 1] != 0) {
                        visitedNeighbourId.add(grid[row][col + 1]);
                    }

                    if (0 <= (col - 1) && grid[row][col - 1] != 0) {
                        visitedNeighbourId.add(grid[row][col - 1]);
                    }

                    for (int neighIslandId: visitedNeighbourId) {
                        int area = islandByAreaMap.getOrDefault(neighIslandId, 0);
                        currentIslandArea += area;
                        maxIslandArea = Math.max(maxIslandArea, currentIslandArea);
                    }
                }
            }
        }

        return maxIslandArea;
    }

    private int dfs(int row, int col, int islandId, int[][] grid) {
        if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length || grid[row][col] != 1) {
            return 0;
        }

        grid[row][col] = islandId;

        return 1 + (dfs(row + 1, col, islandId, grid)
                + dfs(row - 1, col, islandId, grid)
                + dfs(row, col + 1, islandId, grid)
                + dfs(row, col - 1, islandId, grid));
    }

    public static void main(String[] args) {
        var sol = new MakingLargeIsland();
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
