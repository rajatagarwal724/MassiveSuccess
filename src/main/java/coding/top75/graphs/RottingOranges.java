package coding.top75.graphs;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Time Complexity: O(m * n) The time complexity is , where (M) and (N) are the dimensions of the grid. This is because, in the worst case, each cell is visited at least once.
 * Space Complexity: O(m * n) The space complexity is  in the worst case due to the queue potentially holding all the oranges if they are all rotten initially.
 */
public class RottingOranges {

    int[][] DIRECTIONS = new int[][] {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
    };

    public int orangesRotting(int[][] grid) {
        Queue<int[]> queue = new LinkedList<>();
        int freshOranges = 0;
        int rows = grid.length;
        int cols = grid[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 2) {
                    queue.offer(new int[] {i, j});
                } else if (grid[i][j] == 1) {
                    freshOranges++;
                }
            }
        }

        if (freshOranges == 0) {
            return 0;
        }

        int noOfDays = -1;

        while (!queue.isEmpty()) {
            int size = queue.size();
            noOfDays++;
            for (int i = 0; i < size; i++) {
                var rottenNode = queue.poll();
                int x = rottenNode[0];
                int y = rottenNode[1];

                for (int[] dir: DIRECTIONS) {
                    int newX = x + dir[0];
                    int newY = y + dir[1];

                    if (0 <= newX && newX < rows && 0 <= newY && newY < cols && grid[newX][newY] == 1) {
                        grid[newX][newY] = 2;
                        queue.offer(new int[] {newX, newY});
                        freshOranges--;
                    }
                }
            }
        }


        return freshOranges > 0 ? -1 : noOfDays;
    }

    public static void main(String[] args) {
        var sol = new RottingOranges();
        System.out.println(
                sol.orangesRotting(
                        new int[][] {
                                {2,1,0,0},
                                {1,1,1,0},
                                {0,1,1,1},
                                {0,0,1,2}
                        }
                )
        );

        System.out.println(
                sol.orangesRotting(
                        new int[][] {
                                {2,1,1},
                                {1,1,0},
                                {0,1,2}
                        }
                )
        );

        System.out.println(
                sol.orangesRotting(
                        new int[][] {
                                {0,2},
                                {1,0},
                                {0,1}
                        }
                )
        );
    }
}
