package coding.Graphs.ShortestPath.Dijikstra;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class TheMazeIIWithJumpParameter {

    static class Cell {
        int x, y, moves;

        public Cell(int x, int y, int moves) {
            this.x = x;
            this.y = y;
            this.moves = moves;
        }
    }

    public int shortestDistance(int[][] maze, int[] start, int[] destination, int k) {

        if (maze[start[0]][start[1]] == 1 || maze[destination[0]][destination[1]] == 1) {
            return -1;
        }

        if (start[0] == destination[0] && start[1] == destination[1]) {
            return 0;
        }

        int[][] directions = new int[][]{ {0, -1}, {0, 1}, {1, 0}, {-1, 0} };

        int m = maze.length;
        int n = maze[0].length;
        int[][] dist = new int[m][n];
        for (int[] row: dist) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }

        dist[start[0]][start[1]] = 0;

        Queue<Cell> minHeap = new PriorityQueue<>(Comparator.comparingInt(o -> o.moves));
        minHeap.offer(new Cell(start[0], start[1], 0));

        while (!minHeap.isEmpty()) {
            Cell current = minHeap.poll();
            int currentMoves = current.moves;
            int x = current.x;
            int y = current.y;

            if (destination[0] == x && destination[1] == y) {
                return currentMoves;
            }

        }
        return -1;
    }

    public static void main(String[] args) {
        var sol = new TheMazeIIWithJumpParameter();

        int[][] maze0 = {
                {0, 0},
                {1, 0}
        };

        int[] start0 = {0,0};
        int[] dest0 = {1, 1};
        System.out.println(sol.shortestDistance(maze0, start0, dest0, 2));

        int[][] maze_0 = {
                {0, 0},
                {1, 0}
        };

        int[] start_0 = {0,0};
        int[] dest_0 = {2, 2};
        System.out.println(sol.shortestDistance(maze_0, start_0, dest_0, 2));

//        int[][] maze1 = {
//                {0, 0, 1, 0, 0},
//                {0, 0, 0, 0, 0},
//                {0, 0, 0, 1, 0},
//                {1, 1, 0, 1, 1},
//                {0, 0, 0, 0, 0}
//        };
//        int[] start1 = {0, 1};
//        int[] destination1 = {4, 4};
//        System.out.println(sol.shortestDistance(maze1, start1, destination1)); // Output: 7
//
//        int[][] maze2 = {
//                {0, 0, 0, 0, 0},
//                {1, 1, 1, 1, 0},
//                {0, 0, 0, 0, 0},
//                {0, 1, 1, 1, 0},
//                {0, 0, 0, 0, 0}
//        };
//        int[] start2 = {0, 0};
//        int[] destination2 = {4, 4};
//        System.out.println(sol.shortestDistance(maze2, start2, destination2)); // Output: 8
//
//        int[][] maze3 = {
//                {0, 0, 1, 0, 0},
//                {0, 1, 1, 0, 0},
//                {0, 1, 0, 0, 1},
//                {1, 0, 0, 1, 0},
//                {0, 0, 0, 0, 0}
//        };
//        int[] start3 = {0, 1};
//        int[] destination3 = {3, 1};
//        System.out.println(sol.shortestDistance(maze3, start3, destination3)); // Output: -1
    }
}
