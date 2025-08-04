package companies.roku;

import java.util.LinkedList;
import java.util.Queue;

public class MaxAreaOfIslands {

    int[][] DIRECTIONS = new int[][] {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
    };

    public int maxAreaOfIsland(int[][] grid) {
        int maxArea = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) {
                    int area = bfs(i, j, grid);
                    maxArea = Math.max(area, maxArea);
                }
            }
        }

        return maxArea;
    }

    private int bfs(int row, int col, int[][] grid) {
        Queue<int[]> queue = new LinkedList<>();
        grid[row][col] = 0;
        queue.offer(new int[] {row, col});
        int area = 0;
        while (!queue.isEmpty()) {
            var node = queue.poll();
            int x = node[0];
            int y = node[1];
            area++;
            for (int[] dir: DIRECTIONS) {
                int newX = dir[0] + x;
                int newY = dir[1] + y;

                if (0 <= newX && newX < grid.length && 0 <= newY && newY < grid[0].length && grid[newX][newY] == 1) {
                    grid[newX][newY] = 0;
                    queue.offer(new int[]{newX, newY});
                }
            }
        }
        return area;
    }

    public static void main(String[] args) {
        var sol = new MaxAreaOfIslands();
        System.out.println(sol.maxAreaOfIsland(new int[][]{
                {0,0,0,0,0,0,0,0}
        }));

        System.out.println(sol.maxAreaOfIsland(new int[][]{
                {0,0,1,0,0,0,0,1,0,0,0,0,0},
                {0,0,0,0,0,0,0,1,1,1,0,0,0},
                {0,1,1,0,1,0,0,0,0,0,0,0,0},
                {0,1,0,0,1,1,0,0,1,0,1,0,0},
                {0,1,0,0,1,1,0,0,1,1,1,0,0},
                {0,0,0,0,0,0,0,0,0,0,1,0,0},
                {0,0,0,0,0,0,0,1,1,1,0,0,0},
                {0,0,0,0,0,0,0,1,1,0,0,0,0}
        }));
    }
}
