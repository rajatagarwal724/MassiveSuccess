package coding.linkedin;

import java.util.LinkedList;
import java.util.Queue;

public class NumberOfIslands {

    public int countIslands(int[][] matrix) {
        int totalIslands = 0;
        int rows = matrix.length;
        int cols = matrix[0].length;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (matrix[row][col] == 1) {
                    totalIslands++;
                    bfs(row, col, rows, cols, matrix);
                }
            }
        }
        return totalIslands;
    }


    private void bfs(int row, int col, int rows, int cols, int[][] matrix) {
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[] {row, col});

        while (!queue.isEmpty()) {
            var cell = queue.poll();

            row = cell[0];
            col = cell[1];

            if (row < 0 || row >= rows || col < 0 || col >= cols) {
                continue;
            }

            if (matrix[row][col] == 0) {
                continue;
            }

            matrix[row][col] = 0;

            if (isValid(row + 1, col, rows, cols))
                queue.offer(new int[] {row + 1, col});
            if (isValid(row - 1, col, rows, cols))
                queue.offer(new int[] {row - 1, col});
            if (isValid(row, col - 1, rows, cols))
                queue.offer(new int[] {row, col - 1});
            if (isValid(row, col + 1, rows, cols))
                queue.offer(new int[] {row, col + 1});
        }
    }

    private boolean isValid(int nextRow, int nextCol, int maxRows, int maxCols) {
        if (nextRow < 0 || nextRow >= maxRows || nextCol < 0 || nextCol >= maxCols) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        var sol = new NumberOfIslands();
        System.out.println(
                sol.countIslands(
                        new int[][]{
                                {}
                        }
                )
        );
    }
}
