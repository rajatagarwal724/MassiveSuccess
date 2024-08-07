package coding.Island;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class FloodFill {

    public int[][] floodFill(int[][] matrix, int x, int y, int newColor) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        boolean[][] visited = new boolean[rows][cols];
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[] {x, y});
        int nodeColour = matrix[x][y];

        while (!queue.isEmpty()) {
            int[] visitNode = queue.poll();
            int row = visitNode[0];
            int col = visitNode[1];

            if (row < 0 || col < 0 || row >= matrix.length || col >= matrix[0].length) {
                continue;
            }

            if (matrix[row][col] != nodeColour) {
                continue;
            }

            if (visited[row][col]) {
                continue;
            }

            visited[row][col] = true;
            matrix[row][col] = newColor;

            queue.offer(new int[] {row + 1, col});
            queue.offer(new int[] {row - 1, col});
            queue.offer(new int[] {row, col + 1});
            queue.offer(new int[] {row, col - 1});
        }

        return matrix;
    }

    public static void main(String[] args) {
        FloodFill sol = new FloodFill();
        System.out.println(Arrays.deepToString(sol.floodFill(
                new int[][] {
                        { 0, 1, 1, 1, 0 },
                        { 0, 0, 0, 1, 1 },
                        { 0, 1, 1, 1, 0 },
                        { 0, 1, 1, 0, 0 },
                        { 0, 0, 0, 0, 0 }
                }, 1, 3, 2)));

        System.out.println(Arrays.deepToString(sol.floodFill(
                new int[][] {
                        { 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0 },
                        { 0, 0, 1, 1, 0 },
                        { 0, 0, 1, 0, 0 },
                        { 0, 0, 1, 0, 0 }
                }, 3, 2, 5)));

        System.out.println(Arrays.deepToString(sol.floodFill(
                new int[][] {
                        { 1, 2, 1 },
                        { 0, 1, 0 },
                        { 1, 2, 1 }
                }, 0, 1, 3)));
    }
}
