package companies.bitgo;

import java.util.Arrays;

public class UniquePaths {
    public int uniquePaths(int m, int n) {
        int[][] grid = new int[m][n];

        for (int[] arr: grid) {
            Arrays.fill(arr, 1);
        }

        for (int row = 1; row < m; row++) {
            for (int col = 1; col < n; col++) {
                grid[row][col] = grid[row - 1][col] + grid[row][col - 1];
            }
        }
        return grid[m - 1][n - 1];
    }

    public static void main(String[] args) {
        var sol = new UniquePaths();
        System.out.println(sol.uniquePaths(3, 7));
        System.out.println(sol.uniquePaths(3, 2));
    }
}
