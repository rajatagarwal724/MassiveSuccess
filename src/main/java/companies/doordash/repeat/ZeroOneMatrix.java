package companies.doordash.repeat;

import java.util.LinkedList;
import java.util.Queue;

public class ZeroOneMatrix {

    record Node(int row, int col, int steps) {}

    int[][] DIRECTIONS = new int[][] {
            {-1, 0},
            {1, 0},
            {0, 1},
            {0, -1}
    };
    public int[][] updateMatrix(int[][] mat) {
        Queue<Node> queue = new LinkedList<>();
        int m = mat.length;
        int n = mat[0].length;

        boolean[][] visited = new boolean[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (mat[i][j] == 0) {
                    queue.offer(new Node(i, j, 0));
                    visited[i][j] = true;
                }
            }
        }

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int level = 0; level < size; level++) {
                var node = queue.poll();
                for (int[] dir: DIRECTIONS) {
                    int row = node.row() + dir[0];
                    int col = node.col() + dir[1];
                    int steps = node.steps();

                    if (0 <= row && row < m && 0 <= col && col < n
                            && !visited[row][col]
                            && mat[row][col] == 1) {
                        visited[row][col] = true;
                        queue.offer(new Node(row, col, steps + 1));
                        mat[row][col] = steps + 1;
                    }
                }
            }
        }
        return mat;
    }


    public static void main(String[] args) {

    }
}

