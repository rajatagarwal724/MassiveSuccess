package coding.top75.graphs;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;


/**
 * Time Complexity:
 * The time complexity of the algorithm is O(M * N), where (m) is the number of rows and (n) is the number of columns in the grid. This is because each cell is processed at most once, and there are  cells in total.
 *
 * Space Complexity: O(M * N)
 * The space complexity is also , which accounts for the space used by the queue. In the worst case, all the empty rooms could be added to the queue.
 */
public class WallsAndGates {
    private int[][] DIRECTIONS = new int[][] {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1}
    };


    public int[][] wallsAndGates(int[][] rooms) {
        Queue<int[]> queue = new LinkedList<>();
        for (int i = 0; i < rooms.length; i++) {
            for (int j = 0; j < rooms[i].length; j++) {
                if (rooms[i][j] == 0) {
                    queue.offer(new int[] {i, j});
                }
            }
        }

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                var node = queue.poll();
                for (int[] dir: DIRECTIONS) {
                    int newX = node[0] + dir[0];
                    int newY = node[1] + dir[1];

                    if (0 <= newX && newX < rooms.length && 0 <= newY && newY < rooms[0].length && rooms[newX][newY] == 2147483647) {
                        rooms[newX][newY] = rooms[node[0]][node[1]] + 1;
                        queue.offer(new int[] {newX, newY});
                    }
                }
            }
        }

        return rooms;
    }

    public static void main(String[] args) {
        var sol = new WallsAndGates();
        System.out.println(
                Arrays.deepToString(sol.wallsAndGates(
                        new int[][] {
                                {2147483647, -1, 0},
                                {2147483647, 2147483647, 2147483647},
                                {2147483647, -1, 2147483647}
                        }
                ))
        );
    }
}
