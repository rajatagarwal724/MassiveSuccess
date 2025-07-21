package coding.linkedin;

import java.util.LinkedList;
import java.util.Queue;

public class MaxAreaOfIsland {

    int[][] DIRECTIONS = new int[][] {
            {-1, 0},
            {1, 0},
            {0, 1},
            {0, -1}
    };

    public int maxAreaOfIsland(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        int maxArea = 0;
//        int[][] visited = new int[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    maxArea = Math.max(maxArea, bfs(i, j, grid));
                }
            }
        }

        return maxArea;
    }

    private int bfs(int row, int col, int[][] grid) {
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[] {row, col});
        grid[row][col] = 0;
        int steps = 0;
        while (!queue.isEmpty()) {
            var cell = queue.poll();
            steps++;

            for (int[] dir: DIRECTIONS) {
                var newRow = cell[0] + dir[0];
                var newCol = cell[1] + dir [1];

                if (isValid(newRow, newCol, grid.length, grid[0].length, grid)) {
                    grid[newRow][newCol] = 0;
                    queue.offer(new int[]{newRow, newCol});
                }
            }
        }
        return steps;
    }

    private boolean isValid(int newRow, int newCol, int m, int n, int[][] grid) {
        return 0 <= newRow && newRow < m && 0 <= newCol && newCol < n && grid[newRow][newCol] == 1;
    }

    public static void main(String[] args) {
        var sol = new MaxAreaOfIsland();

        System.out.println(
                sol.maxAreaOfIsland(
                        new int[][] {
                                {1, 1, 0, 0, 0},
                                {1, 1, 0, 0, 0},
                                {0, 0, 1, 0, 0},
                                {0, 0, 0, 1, 1}
                        }
                )
        );

        System.out.println(
                sol.maxAreaOfIsland(
                        new int[][] {
                                {0,0,1,0,0,0,0,1,0,0,0,0,0},
                                {0,0,0,0,0,0,0,1,1,1,0,0,0},
                                {0,1,1,0,1,0,0,0,0,0,0,0,0},
                                {0,1,0,0,1,1,0,0,1,0,1,0,0},
                                {0,1,0,0,1,1,0,0,1,1,1,0,0},
                                {0,0,0,0,0,0,0,0,0,0,1,0,0},
                                {0,0,0,0,0,0,0,1,1,1,0,0,0},
                                {0,0,0,0,0,0,0,1,1,0,0,0,0}
                        }
                )
        );
    }
}
