package companies.doordash;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class ShortestDistanceFromAllBuildings {

    private int EMPTY = 0;

    private int BUILDING = 1;

    private int OBSTACLE = 2;

    private int[][] DIRECTIONS = new int[][] {
            {-1, 0},
            {1, 0},
            {0, 1},
            {0, -1}
    };

    public int shortestDistance(int[][] grid) {
        int distance = Integer.MAX_VALUE;
        int m = grid.length;
        int n = grid[0].length;

        int totalHouses = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    totalHouses++;
                }
            }
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 0) {
                    distance = Math.min(distance, bfs(i, j, grid, totalHouses));
                }
            }
        }

        if (distance == Integer.MAX_VALUE) {
            return -1;
        }

        return distance;
    }

    private int bfs(int row, int col, int[][] grid, int totalHouses) {
        Queue<int[]> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        int housesReached = 0;
        int steps = 0;
        int distanceSum = 0;

        queue.offer(new int[] {row, col});
        visited.add(row + ":" + col);

        while (!queue.isEmpty() && housesReached != totalHouses) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                var cell = queue.poll();
                int r = cell[0];
                int c = cell[1];

                if (grid[r][c] == 1) {
                    housesReached++;
                    distanceSum += steps;
                    continue;
                }

//                if (housesReached == totalHouses) {
//                    break;
//                }

                for (int[] dir: DIRECTIONS) {
                    int newR = r + dir[0];
                    int newC = c + dir[1];

                    if (0 <= newR && newR < grid.length && 0 <= newC && newC < grid[0].length
                    && grid[newR][newC] != 2 && !visited.contains(newR + ":" + newC)) {
                        visited.add(newR + ":" + newC);
                        queue.offer(new int[] {newR, newC});
                    }
                }
            }
            steps++;

        }

        if (totalHouses != housesReached) {
            for (String visitedCell: visited) {
                String[] cell = visitedCell.split(":");
                grid[Integer.parseInt(cell[0])][Integer.parseInt(cell[1])] = 2;
            }
            return Integer.MAX_VALUE;
        }

        return distanceSum;
    }

    public static void main(String[] args) {
        var sol = new ShortestDistanceFromAllBuildings();
        System.out.println(
                sol.shortestDistance(
                        new int[][] {
                                {1,0,2,0,1},
                                {0,0,0,0,0},
                                {0,0,1,0,0}
                        }
                )
        );

        System.out.println(
                sol.shortestDistance(
                        new int[][] {
                                {1,0}
                        }
                )
        );

        System.out.println(
                sol.shortestDistance(
                        new int[][] {
                                {-1}
                        }
                )
        );
    }
}
