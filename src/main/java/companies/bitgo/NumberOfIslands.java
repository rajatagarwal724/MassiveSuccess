package companies.bitgo;

import java.util.LinkedList;
import java.util.Queue;

public class NumberOfIslands {

    public int numIslands(char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

//        boolean[][] visited = new boolean[rows][cols];
        int numOfIslands = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if(grid[row][col] == '1') {
                    numOfIslands++;
                    dfs(row, col, grid);
                }
            }
        }

        return numOfIslands;
    }

    private void dfs(int row, int col, char[][] grid) {
        if (row < 0 || col < 0 || row >= grid.length || col >= grid[0].length || grid[row][col] == '0') {
            return;
        }

        grid[row][col] = '0';
        dfs(row + 1, col, grid);
        dfs(row - 1, col, grid);
        dfs(row, col + 1, grid);
        dfs(row, col - 1, grid);
    }

    private void bfs(int row, int col, char[][] grid) {
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{ row, col });

        while (!queue.isEmpty()) {
            int[] poll = queue.poll();
            int r = poll[0];
            int c = poll[1];

            if (r < 0 || c < 0 || r >= grid.length || c >= grid[0].length || grid[r][c] == '0') {
                continue;
            }
            grid[r][c] = '0';
            queue.offer(new int[]{r + 1, c}); // DOWN
            queue.offer(new int[]{r -1, c}); // UP
            queue.offer(new int[]{r, c + 1}); // RIGHT
            queue.offer(new int[]{r, c - 1}); // LEFT
        }
    }

    public static void main(String[] args) {
        var solution = new NumberOfIslands();
        char[][] grid = {
                {'1','1','1','1','0'},
                {'1','1','0','1','0'},
                {'1','1','0','0','0'},
                {'0','0','0','0','0'},
        };
        System.out.println(solution.numIslands(grid));
    }
}
