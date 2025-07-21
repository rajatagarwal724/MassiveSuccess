package companies.doordash;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class ZeroOneMatrix {

    int[][] DIRECTIONS = new int[][] {
            {-1, 0},
            {1, 0},
            {0, 1},
            {0, -1}
    };

    class State {
        int row;
        int col;
        int steps;

        public State(int row, int col, int steps) {
            this.row = row;
            this.col = col;
            this.steps = steps;
        }
    }

    public int[][] updateMatrix(int[][] mat) {
        int m = mat.length;
        int n = mat[0].length;
        int[][] matrix = new int[m][n];
        boolean[][] seen = new boolean[m][n];
        Queue<State> queue = new LinkedList<>();

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = mat[i][j];
                if (mat[i][j] == 0) {
                    queue.offer(new State(i, j, 0));
                    seen[i][j] = true;
                }
            }
        }

        while (!queue.isEmpty()) {
            var state = queue.poll();
            var row = state.row;
            var col = state.col;
            var steps = state.steps;

            for (int[] dir: DIRECTIONS) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (isValid(newRow, newCol, seen, matrix)) {
                    seen[newRow][newCol] = true;
                    matrix[newRow][newCol] = steps + 1;
                    queue.offer(new State(newRow, newCol, steps + 1));
                }
            }
        }
        return matrix;
    }

    private boolean isValid(int row, int col, boolean[][] seen, int[][] matrix) {
        return 0 <= row && row < matrix.length && 0 <= col && col < matrix[0].length
                && !seen[row][col];
    }

    public static void main(String[] args) {
        var sol = new ZeroOneMatrix();
        System.out.println(

                Arrays.deepToString(sol.updateMatrix(
                        new int[][]{
                                {0, 0, 0},
                                {0, 1, 0},
                                {0, 0, 0}
                        }
                ))
        );

        System.out.println(
                Arrays.deepToString(sol.updateMatrix(
                                new int[][]{
                                        {0,0,0},
                                        {0,1,0},
                                        {1,1,1}
                                }
                        )
                )
        );
    }
}
