package companies.doordash;

import java.util.Arrays;

public class LongestIncreasingPathSum {

    int[][] DIRECTIONS = new int[][] {
            {-1, 0},
            {1, 0},
            {0, 1},
            {0, -1}
    };

    public int longestIncreasingPath_slow(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        int ans = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 2 && j == 1) {
                    ans = Math.max(ans, dfs(i, j, matrix));
                }
            }
        }

        return ans;
    }

    private int dfs(int row, int col, int[][] matrix) {
        int ans = 0;
        for (int[] dir: DIRECTIONS) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (0 <= newRow && newRow < matrix.length && 0 <= newCol && newCol < matrix[0].length
                    && matrix[newRow][newCol] > matrix[row][col]) {
                ans = Math.max(ans, dfs(newRow, newCol, matrix));
            }
        }
        ++ans;
        return ans;
    }

    public int longestIncreasingPath(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        int[][] cache = new int[m][n];
        int ans = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                ans = Math.max(ans, dfs(i, j, matrix, cache));
            }
        }
        for (int a[] : cache) {
            System.out.println(Arrays.toString(a));
        }
        return ans;
    }

    private int dfs(int row, int col, int[][] matrix, int[][] cache) {
        if (cache[row][col] != 0) {
            return cache[row][col];
        }

        for (int[] dir: DIRECTIONS) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (
                    0 <= newRow
                            && newRow < matrix.length
                            && 0 <= newCol
                            && newCol < matrix[0].length
                            && matrix[newRow][newCol] > matrix[row][col]
            ) {
                cache[row][col] = Math.max(cache[row][col], dfs(newRow, newCol, matrix, cache));
            }
        }

        return ++cache[row][col];
    }

    public static void main(String[] args) {
        var sol = new LongestIncreasingPathSum();
        System.out.println(
                sol.longestIncreasingPath(
                        new int[][] {
                                {9,9,4},
                                {6,6,8},
                                {2,1,1}
                        }
                )
        );
    }
}
