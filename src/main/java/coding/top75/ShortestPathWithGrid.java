package coding.top75;

import java.util.LinkedList;
import java.util.Queue;

public class ShortestPathWithGrid {

    private int[][] DIRECTIONS = new int[][] {
            {-1, 0},
            {1, 0},
            {0, 1},
            {0, -1}
    };

    public int shortestPath(int[][] grid, int k) {
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[] {0, 0, 0, k});
        int rows = grid.length;
        int cols = grid[0].length;
        boolean[][][] visited = new boolean[rows][cols][k + 1];
        visited[0][0][k] = true;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                var node = queue.poll();
                int row = node[0];
                int col = node[1];
                int steps = node[2];
                int remainingK = node[3];

                if (row == rows - 1 && col == cols - 1) {
                    return steps;
                }

                for (int[] dir: DIRECTIONS) {
                    int newRow = row + dir[0];
                    int newCol = col + dir[1];

                    if (0 <= newRow && newRow < rows && 0 <= newCol && newCol < cols) {
                        int newK = remainingK - grid[newRow][newCol];
                        if (newK >= 0 && !visited[newRow][newCol][newK]) {
                            visited[newRow][newCol][newK] = true;
                            queue.offer(new int[] {newRow, newCol, steps + 1, newK});
                        }
                    }
                }
            }
        }

        return -1;
    }
}
