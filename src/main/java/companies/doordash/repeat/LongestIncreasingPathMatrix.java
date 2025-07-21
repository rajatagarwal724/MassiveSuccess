package companies.doordash.repeat;


/**
 * Time complexity : O(mn). Each vertex/cell will be calculated once and only once, and each edge will be visited once and only once. The total time complexity is then O(V+E). V is the total number of vertices and E is the total number of edges. In our problem, O(V)=O(mn), O(E)=O(4V)=O(mn).
 *
 * Space complexity : O(mn). The cache dominates the space complexity.
 */
public class LongestIncreasingPathMatrix {

    int[][] DIRECTIONS = new int[][] {
            {-1, 0},
            {1, 0},
            {0, 1},
            {0, -1}
    };

    public int longestIncreasingPath(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;

        int[][] cache = new int[m][n];
        int res = 1;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                res = Math.max(res, dfs(i, j, matrix, cache));
            }
        }

        return res;
    }

    private int dfs(int row, int col, int[][] matrix, int[][] cache) {
        if (cache[row][col] != 0) {
            return cache[row][col];
        }

        for (int[] dir: DIRECTIONS) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (
                    0 <= newRow && newRow < matrix.length
                            && 0 <= newCol && newCol < matrix[0].length
                    && matrix[newRow][newCol] > matrix[row][col]
            ) {
                cache[row][col] = Math.max(cache[row][col], dfs(newRow, newCol, matrix, cache));
            }
        }
        cache[row][col]++;
        return cache[row][col];
    }

    public static void main(String[] args) {
        var sol = new LongestIncreasingPathMatrix();
        System.out.println(
                sol.longestIncreasingPath(
                        new int[][] {
                                {9,9,4},
                                {6,6,8},
                                {2,1,1}
                        }
                )
        );

        System.out.println(
                sol.longestIncreasingPath(
                        new int[][] {
                                {3,4,5},
                                {3,2,6},
                                {2,2,1}
                        }
                )
        );

        System.out.println(
                sol.longestIncreasingPath(
                        new int[][] {
                                {1}
                        }
                )
        );


    }
}
