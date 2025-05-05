package companies.splunk;

import java.util.HashSet;
import java.util.Set;

public class NoOfDistictIslandsDfs {

    public int numDistinctIslands(int[][] grid) {
        Set<String> distinct = new HashSet<>();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) {
                    StringBuilder islandTraversalDirections = new StringBuilder();
                    dfs(i, j, grid, islandTraversalDirections, "O");
                    distinct.add(islandTraversalDirections.toString());
                }
            }
        }
        return distinct.size();
    }

    public void dfs(int row, int col, int[][] grid, StringBuilder directions, String currentDir) {
        if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length) {
            return;
        }
        if (grid[row][col] == 0) {
            return;
        }
        grid[row][col] = 0;
        directions.append(currentDir);

        dfs(row + 1, col, grid, directions, "D");
        dfs(row - 1, col, grid, directions, "U");
        dfs(row, col + 1, grid, directions, "R");
        dfs(row, col - 1, grid, directions, "L");

        directions.append("B");
    }

    public static void main(String[] args) {
        var sol = new NoOfDistictIslandsDfs();
        System.out.println(sol.numDistinctIslands(
                new int[][]{
                        { 1,1,0 },
                        { 0,1,1 },
                        { 0,0,0 },
                        { 1,1,1 },
                        { 0,1,0 }
                }
        )
        );
    }
}
