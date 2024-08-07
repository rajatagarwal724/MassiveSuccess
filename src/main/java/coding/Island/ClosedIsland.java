package coding.Island;

import java.util.LinkedList;
import java.util.Queue;

public class ClosedIsland {
    public int countClosedIslands(int[][] matrix) {
        int countClosedIslands = 0;
        int rows = matrix.length;
        int cols = matrix[0].length;

        boolean[][] visited = new boolean[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (matrix[row][col] == 1 && !visited[row][col]) {
                    if (isClosedIsland(matrix, visited, row, col)) {
                        countClosedIslands++;
                    }
                }
            }
        }

        return countClosedIslands;
    }

    private boolean isClosedIsland(int[][] matrix, boolean[][] visited, int row, int col) {
//        Queue<int[]> queue = new LinkedList<>();
//        queue.offer(new int[] {row, col});
//
//        visited[row][col] = true;
//        boolean isClosed = true;
//
//        while (!queue.isEmpty()) {
//            int[] visitCell = queue.poll();
//            int visitRow = visitCell[0];
//            int visitCol = visitCell[1];
//
//            if (visitRow < 0 || visitCol < 0 || visitRow >= matrix.length || visitCol >= matrix[0].length) {
//                isClosed = false;
//            } else if (matrix[visitRow][visitCol] == 1 && !visited[visitRow][visitCol]) {
//                queue.offer(new int[] {})
//            }
//
//        }
        return false;
    }

    public static void main(String[] args) {
        ClosedIsland sol = new ClosedIsland();
        System.out.println(sol.countClosedIslands(
                new int[][] {
                        { 1, 1, 0, 0, 0 },
                        { 0, 1, 0, 0, 0 },
                        { 0, 0, 1, 1, 0 },
                        { 0, 1, 1, 0, 0 },
                        { 0, 0, 0, 0, 0 }
                }));

        System.out.println(sol.countClosedIslands(
                new int[][] {
                        { 0, 0, 0, 0 },
                        { 0, 1, 0, 0 },
                        { 0, 1, 0, 0 },
                        { 0, 0, 1, 0 },
                        { 0, 0, 0, 0 }
                }));
    }
}
