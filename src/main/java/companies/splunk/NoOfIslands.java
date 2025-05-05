package companies.splunk;

import java.util.LinkedList;
import java.util.Queue;

public class NoOfIslands {

    public static int numDistinctIslands(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        int totalIslands = 0;

        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1) {
                    totalIslands++;
                    visitIslandsBFS(i, j, rows, cols, grid);
                }
            }
        }
        return totalIslands;
    }

    private static void visitIslandsBFS(int currentRow, int currentCol, int rows, int cols, int[][] grid) {
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[] {currentRow, currentCol});
        while (!queue.isEmpty()) {
            int[] island = queue.poll();
            int x = island[0];
            int y = island[1];

            if (x < 0 || x >= rows || y < 0 || y >= cols) {
                continue;
            }
            if (grid[x][y] == 0) {
                continue;
            }
            grid[x][y] = 0;
            queue.offer(new int[]{x - 1, y});
            queue.offer(new int[]{x + 1, y});
            queue.offer(new int[]{x, y - 1});
            queue.offer(new int[]{x, y + 1});
        }
    }

    public static void main(String[] args) {
        System.out.println(NoOfIslands.numDistinctIslands(
                new int[][] {
                        { 1, 1, 1, 0, 0 },
                        { 0, 1, 0, 0, 1 },
                        { 0, 0, 1, 1, 0 },
                        { 0, 0, 1, 0, 0 },
                        { 0, 0, 1, 0, 0 }
                }));

        System.out.println(NoOfIslands.numDistinctIslands(
                new int[][] {
                        { 0, 1, 1, 1, 0 },
                        { 0, 0, 0, 1, 1 },
                        { 0, 1, 1, 1, 0 },
                        { 0, 1, 1, 0, 0 },
                        { 0, 0, 0, 0, 0 }
                }));
    }
}
