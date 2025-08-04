package coding.top75.graphs;

import java.util.LinkedList;
import java.util.Queue;


/**
 * Time Complexity:- O(m * n * k)
 * In the worst-case scenario, we might explore every cell in the grid for every possible value of remaining k (from k down to 0).
 * This results in a time complexity of , where m is the number of rows and n is the number of columns in the grid.
 * Each cell can be visited in up to k different states (each state representing a different number of obstacles that can still be removed).
 * Space Complexity:- O(m * n * k)
 * The space complexity is also  due to the visited array, which keeps track of the state of each cell for each possible value of remaining k.
 * Additionally, the queue used for BFS can hold up to m * n * k elements in the worst case.
 */
public class ShortestPathWithObstaclesElimination {

    private int[][] DIRECTIONS = new int[][] {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
    };

    public int shortestPath(int[][] grid, int k) {
        Queue<int[]> queue = new LinkedList<>();
        int rows = grid.length;
        int cols = grid[0].length;
        boolean[][][] visited = new boolean[grid.length][grid[0].length][k + 1];
        visited[0][0][k] = true;
        queue.offer(new int[] {0, 0, 0, k});

        while (!queue.isEmpty()) {
            var node = queue.poll();
            int x = node[0];
            int y = node[1];
            int steps = node[2];
            int remainingK = node[3];


            if (x == rows - 1 && y == cols - 1) {
                return steps;
            }

            for (int[] dir: DIRECTIONS) {
                int newX = x + dir[0];
                int newY = y + dir[1];
                if (0 <= newX && newX < rows && 0 <= newY && newY < cols) {
                    int newK = remainingK - grid[newX][newY];
                    if (newK >= 0 && !visited[newX][newY][newK]) {
                        visited[newX][newY][newK] = true;
                        queue.offer(new int[] {newX, newY, steps + 1, newK});
                    }
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        var sol = new ShortestPathWithObstaclesElimination();
        System.out.println(
                sol.shortestPath(
                        new int[][] {
                                {0, 1, 0, 0},
                                {1, 1, 0, 1},
                                {0, 0, 0, 0},
                                {0, 1, 1, 0}
                        },
                        1
                )
        );

        System.out.println(
                sol.shortestPath(
                        new int[][] {
                                {0, 0, 0},
                                {1, 1, 0},
                                {1, 1, 0},
                                {0, 0, 0}
                        },
                        2
                )
        );
    }
}
