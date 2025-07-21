package companies.doordash.repeat;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;


/**
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

    private int OBSTACLE = -1;
    private int GATE = 0;
    private int EMPTY_ROOM = 2147483647;

    private int[][] DIRECTIONS = new int[][] {
            {-1, 0},
            {1, 0},
            {0, 1},
            {0, -1}
    };

    public void wallsAndGates(int[][] rooms) {
        int m = rooms.length;
        int n = rooms[0].length;

        Queue<int[]> queue = new LinkedList<>();

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (rooms[i][j] == 0) {
                    queue.offer(new int[] {i, j});
                }
            }
        }

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int[] cell = queue.poll();
                int row = cell[0];
                int col = cell[1];

                for (int[] dir: DIRECTIONS) {
                    int newRow = row + dir[0];
                    int newCol = col + dir[1];

                    if (0 <= newRow && newRow < m && 0 <= newCol && newCol < n && rooms[newRow][newCol] == EMPTY_ROOM) {
                        rooms[newRow][newCol] = rooms[row][col] + 1;
                        queue.offer(new int[] {newRow, newCol});
                    }
                }
            }
        }

        System.out.println(Arrays.deepToString(rooms));
    }

    public static void main(String[] args) {
        var sol = new WallsAndGates();
        sol.wallsAndGates(
                new int[][]{
                        {sol.EMPTY_ROOM,-1,0, sol.EMPTY_ROOM},
                        {sol.EMPTY_ROOM, sol.EMPTY_ROOM, sol.EMPTY_ROOM,-1},
                        {sol.EMPTY_ROOM,-1, sol.EMPTY_ROOM,-1},
                        {0,-1, sol.EMPTY_ROOM, sol.EMPTY_ROOM}
                }
        );
    }
}
