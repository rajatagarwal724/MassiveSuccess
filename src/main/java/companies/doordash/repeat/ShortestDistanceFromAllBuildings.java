package companies.doordash.repeat;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Let N and M be the number of rows and columns in grid respectively.
 *
 * Time Complexity: O(N
 * 2
 *  ⋅M
 * 2
 *  )
 *
 * For each house, we will traverse across all reachable land.
 * This will require O(number of zeros ⋅ number of ones) time and the number of zeros and ones in the matrix is of order N⋅M.
 * Consider that when half of the values in grid are 0 and half of the values are 1, total elements in grid would be (M⋅N) so their counts are (M⋅N)/2 and (M⋅N)/2 respectively, thus giving time complexity (M⋅N)/2⋅(M⋅N)/2, that results in O(N
 * 2
 *  ⋅M
 * 2
 *  ).
 *
 * Space Complexity: O(N⋅M)
 *
 * We use an extra matrix to store distance sums, and the queue will store each matrix element at most once during each BFS call.
 * Hence, O(N⋅M) space is required.
 */

public class ShortestDistanceFromAllBuildings {

    int[][] DIRECTIONS = new int[][] {
            {-1, 0},
            {1, 0},
            {0, 1},
            {0, -1}
    };

    public int shortestDistance(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;

        int[][] total = new int[m][n];
        int minDistance = Integer.MAX_VALUE;
        int emptyLandValue = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    minDistance = Integer.MAX_VALUE;
                    Queue<int[]> queue = new LinkedList<>();
                    queue.offer(new int[] {i, j});

                    int steps = 0;
                    while (!queue.isEmpty()) {
                        steps++;
                        int size = queue.size();
                        for (int level = 0; level < size; level++) {
                            int[] cell = queue.poll();
                            int row = cell[0];
                            int col = cell[1];

                            for (int[] dir: DIRECTIONS) {
                                int newRow = row + dir[0];
                                int newCol = col + dir[1];

                                if (0 <= newRow && newRow < m
                                        && 0 <= newCol && newCol < n
                                && grid[newRow][newCol] == emptyLandValue) {
                                    grid[newRow][newCol]--;
                                    queue.offer(new int[]{newRow, newCol});
                                    total[newRow][newCol] += steps;
                                    minDistance = Math.min(minDistance, total[newRow][newCol]);
                                }
                            }
                        }
                    }
                    emptyLandValue--;
                }
            }
        }

        return minDistance == Integer.MAX_VALUE ? -1 : minDistance;
    }

    public static void main(String[] args) {
        var sol = new ShortestDistanceFromAllBuildings();
        System.out.println(
                sol.shortestDistance(
                        new int[][]{
                                {1,0,2,0,1},
                                {0,0,0,0,0},
                                {0,0,1,0,0}
                        }
                )
        );

//        System.out.println(
//                sol.shortestDistance(
//                        new int[][]{
//                                {1,0}
//                        }
//                )
//        );
//
//        System.out.println(
//                sol.shortestDistance(
//                        new int[][]{
//                                {1}
//                        }
//                )
//        );
    }
}
