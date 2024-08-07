package coding.Island;

import java.util.LinkedList;
import java.util.Queue;

public class NumberOfIslands {

    public static int countIslands(int[][] matrix) {
        int totalIslands = 0;
        int rows = matrix.length;
        int cols = matrix[0].length;

        boolean[][] visited = new boolean[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (matrix[row][col] == 1 && !visited[row][col]) {
                    totalIslands++;
                    bfs(matrix, visited, row, col);
                }
            }
        }

        return totalIslands;
    }

    private static void bfs(int[][] matrix, boolean[][] visited, int row, int col) {
        if (row < 0 || row >= matrix.length || col < 0 || col >= matrix[0].length) {
            return;
        }
        Queue<int[]> neighbours = new LinkedList<>();
        neighbours.offer(new int[] {row, col});

        while (!neighbours.isEmpty()) {
            int[] vertex = neighbours.poll();
            int vertexRow = vertex[0];
            int vertexCol = vertex[1];
            if (vertex[0] < 0 || vertex[0] >= matrix.length || vertex[1] < 0 || vertex[1] >= matrix[0].length) {
                continue;
            }
            if (matrix[vertexRow][vertexCol] == 0 || visited[vertexRow][vertexCol]) {
                continue;
            }

            visited[vertexRow][vertexCol] = true;

            neighbours.offer(new int[] {vertexRow + 1, vertexCol});
            neighbours.offer(new int[] {vertexRow - 1, vertexCol});
            neighbours.offer(new int[] {vertexRow, vertexCol + 1});
            neighbours.offer(new int[] {vertexRow, vertexCol - 1});
        }
    }

    public static void main(String[] args) {
        System.out.println(NumberOfIslands.countIslands(
                new int[][] {
                        { 1, 1, 1, 0, 0 },
                        { 0, 1, 0, 0, 1 },
                        { 0, 0, 1, 1, 0 },
                        { 0, 0, 1, 0, 0 },
                        { 0, 0, 1, 0, 0 }
                }));

        System.out.println(NumberOfIslands.countIslands(
                new int[][] {
                        { 0, 1, 1, 1, 0 },
                        { 0, 0, 0, 1, 1 },
                        { 0, 1, 1, 1, 0 },
                        { 0, 1, 1, 0, 0 },
                        { 0, 0, 0, 0, 0 }
                }));
    }
}
