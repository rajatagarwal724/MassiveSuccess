package coding.Graphs.ShortestPath.Dijikstra;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class TheMazeII {

    public int shortestDistance(int[][] maze, int[] start, int[] destination) {
        int m = maze.length;
        int n = maze[0].length;
        int[][] distances = new int[m][n];
        for (int i = 0; i < distances.length; i++) {
            Arrays.fill(distances[i], Integer.MAX_VALUE);
        }
        distances[start[0]][start[1]] = 0;
        Queue<int[]> minHeap = new PriorityQueue<>(Comparator.comparingInt(o -> o[2]));
        minHeap.offer(new int[] {start[0], start[1], 0});

        int[][] directions = new int[][] { {0,-1}, {0,1}, {1,0}, {-1,0} };

        while (!minHeap.isEmpty()) {
            int[] curr = minHeap.poll();
            int x = curr[0], y = curr[1], dist = curr[2];  // Current position and distance
            if (x == destination[0] && y == destination[1]) {  // If destination is reached
                return dist;  // Return the distance
            }
            for (int[] dir: directions) {
                int nx = x, ny = y, newDist = dist;
                while (nx >= 0 && nx < m && ny >= 0 && ny < n && maze[nx][ny] == 0) {
                    nx += dir[0];
                    ny += dir[1];
                    newDist++;
                }
                nx -= dir[0];
                ny -= dir[1];
                newDist--;

                if (newDist < distances[nx][ny]) {
                    distances[nx][ny] = newDist;
                    minHeap.offer(new int[] {nx, ny, newDist});
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        var sol = new TheMazeII();

        int[][] maze1 = {
                {0, 0, 1, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 1, 0},
                {1, 1, 0, 1, 1},
                {0, 0, 0, 0, 0}
        };
        int[] start1 = {0, 1};
        int[] destination1 = {4, 4};
        System.out.println(sol.shortestDistance(maze1, start1, destination1)); // Output: 7

        int[][] maze2 = {
                {0, 0, 0, 0, 0},
                {1, 1, 1, 1, 0},
                {0, 0, 0, 0, 0},
                {0, 1, 1, 1, 0},
                {0, 0, 0, 0, 0}
        };
        int[] start2 = {0, 0};
        int[] destination2 = {4, 4};
        System.out.println(sol.shortestDistance(maze2, start2, destination2)); // Output: 8

        int[][] maze3 = {
                {0, 0, 1, 0, 0},
                {0, 1, 1, 0, 0},
                {0, 1, 0, 0, 1},
                {1, 0, 0, 1, 0},
                {0, 0, 0, 0, 0}
        };
        int[] start3 = {0, 1};
        int[] destination3 = {3, 1};
        System.out.println(sol.shortestDistance(maze3, start3, destination3)); // Output: -1
    }
}
