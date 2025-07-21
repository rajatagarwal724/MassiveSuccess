package companies.doordash;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Complexity analysis
 *
 * Time complexity : O(mn).
 *
 * If you are having difficulty to derive the time complexity, start simple.
 *
 * Let us start with the case with only one gate. The breadth-first search takes at most m×n steps to reach all rooms, therefore the time complexity is O(mn). But what if you are doing breadth-first search from k gates?
 *
 * Once we set a room's distance, we are basically marking it as visited, which means each room is visited at most once. Therefore, the time complexity does not depend on the number of gates and is O(mn).
 *
 * Space complexity : O(mn).
 * The space complexity depends on the queue's size. We insert at most m×n points into the queue.
 */
public class WallsAndGates {

    private static int EMPTY = Integer.MAX_VALUE;
    private static int WALL = -1;
    private static int GATE = 0;

    private static int[][] DIRECTIONS = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1}
    };

    public void wallsAndGates(int[][] rooms) {
        Queue<int[]> queue = new LinkedList<>();

        for (int i = 0; i < rooms.length; i++) {
            for (int j = 0; j < rooms[0].length; j++) {
                if (rooms[i][j] == GATE) {
                    queue.offer(new int[] {i, j});
                }
            }
        }

        int totalRows = rooms.length;
        int totalCols = rooms[0].length;

        while (!queue.isEmpty()) {
            var cell = queue.poll();
            int row = cell[0];
            int col = cell[1];

            for (int[] direction: DIRECTIONS) {
                int newRow = row + direction[0];
                int newCol = col + direction[1];
                if (isValid(rooms, newRow, newCol, totalRows, totalCols)) {
                    rooms[newRow][newCol] = rooms[row][col] + 1;
                    queue.offer(new int[]{newRow, newCol});
                }
            }
        }
    }

    private boolean isValid(int[][] rooms, int row, int col, int totalRows, int totalCols) {
        return row >= 0 && row < totalRows && col >= 0 && col < totalCols && rooms[row][col] == EMPTY;
    }

    public static void main(String[] args) {
        var sol = new WallsAndGates();
        var rooms = new int[][] {
                {Integer.MAX_VALUE,-1,0,Integer.MAX_VALUE},
                {Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE,-1},
                {Integer.MAX_VALUE,-1,Integer.MAX_VALUE,-1},
                {0,-1,Integer.MAX_VALUE,Integer.MAX_VALUE}
        };

        sol.wallsAndGates(rooms);

        System.out.println(Arrays.deepToString(rooms));

        rooms = new int[][] {
                {-1}
        };

        sol.wallsAndGates(rooms);

        System.out.println(Arrays.deepToString(rooms));
    }
}
