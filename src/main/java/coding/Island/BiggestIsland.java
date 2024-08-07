package coding.Island;

import java.util.LinkedList;
import java.util.Queue;

public class BiggestIsland {
    public int maxAreaOfIsland(int[][] matrix) {
        int biggestIslandArea = 0;
        int rows = matrix.length;
        int cols = matrix[0].length;
        boolean[][] visited = new boolean[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (matrix[row][col] == 1 && !visited[row][col]) {
                    biggestIslandArea = Math.max(biggestIslandArea, bfs(matrix, visited, row, col));
                }
            }
        }
        return biggestIslandArea;
    }

    private int bfs(int[][] matrix, boolean[][] visited, int row, int col) {
        int totalArea = 0;
        Queue<int[]> neighbours = new LinkedList<>();
        neighbours.offer(new int[] {row, col});

        while (!neighbours.isEmpty()) {
            int[] visitCell = neighbours.poll();;
            int visitRow = visitCell[0];
            int visitCol = visitCell[1];

            if (visitRow < 0 || visitCol < 0 || visitRow >= matrix.length || visitCol >= matrix[0].length) {
                continue;
            }

            if (matrix[visitRow][visitCol] == 0) {
                continue;
            }

            if (visited[visitRow][visitCol]) {
                continue;
            }
            totalArea++;
            visited[visitRow][visitCol] = true;

            neighbours.offer(new int[] {visitRow + 1, visitCol});
            neighbours.offer(new int[] {visitRow - 1, visitCol});
            neighbours.offer(new int[] {visitRow, visitCol + 1});
            neighbours.offer(new int[] {visitRow, visitCol - 1});
        }
        return totalArea;
    }

    public static void main(String[] args) {
        BiggestIsland sol = new BiggestIsland();
        System.out.println(sol.maxAreaOfIsland(
                new int[][] {
                        { 1, 1, 1, 0, 0 },
                        { 0, 1, 0, 0, 1 },
                        { 0, 0, 1, 1, 0 },
                        { 0, 1, 1, 0, 0 },
                        { 0, 0, 1, 0, 0 }
                }));
    }
}
