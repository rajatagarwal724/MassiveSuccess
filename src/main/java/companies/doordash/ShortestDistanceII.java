package companies.doordash;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class ShortestDistanceII {

    int OBSTACLE = 2;
    int HOUSE = 1;
    int EMPTY_LAND = 0;

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


        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {

                    Queue<int[]> queue = new LinkedList<>();
                    queue.offer(new int[] {i, j});

                    minDistance = Integer.MAX_VALUE;

                    int steps = 0;

                    while (!queue.isEmpty()) {
                        steps++;

                        int size = queue.size();

                        for (int level = 0; level < size; level++) {
                            var cell = queue.poll();
                            int row = cell[0];
                            int col = cell[1];

                            for (int[] dir: DIRECTIONS) {
                                int newRow = row + dir[0];
                                int newCol = col + dir[1];

                                if (0 <= newRow && newRow < m
                                        && 0 <= newCol && newCol < n
                                && grid[newRow][newCol] == EMPTY_LAND) {
                                    queue.offer(new int[] {newRow, newCol});
                                    grid[newRow][newCol]--;
                                    total[newRow][newCol]+=steps;
                                    minDistance = Math.min(minDistance, total[newRow][newCol]);
                                }
                            }
                        }
                    }
                    EMPTY_LAND--;
                }

            }
        }

        System.out.println(Arrays.deepToString(grid));
        System.out.println(Arrays.deepToString(total));

        return minDistance == Integer.MAX_VALUE ? -1 : minDistance;
    }

    public static void main(String[] args) {
        var sol = new ShortestDistanceII();
        System.out.println(
                sol.shortestDistance(
                        new int[][] {
                                {1,0,2,0,1},
                                {0,0,0,0,0},
                                {0,0,1,0,0}
                        }
                )
        );
    }
}
