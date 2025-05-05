import java.util.*;

public class MatrixPathFinder {
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // up, down, left, right
    private static final char[] DIRECTION_CHARS = {'U', 'D', 'L', 'R'};

    public boolean canReachDestination(char[][] matrix, int[] source, int[] destination) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        boolean[][] visited = new boolean[rows][cols];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(source);
        visited[source[0]][source[1]] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int currentRow = current[0];
            int currentCol = current[1];

            if (currentRow == destination[0] && currentCol == destination[1]) {
                return true;
            }

            char currentDirection = matrix[currentRow][currentCol];
            for (int i = 0; i < DIRECTIONS.length; i++) {
                if (currentDirection == DIRECTION_CHARS[i]) {
                    int newRow = currentRow + DIRECTIONS[i][0];
                    int newCol = currentCol + DIRECTIONS[i][1];

                    if (isValid(newRow, newCol, rows, cols) && !visited[newRow][newCol]) {
                        visited[newRow][newCol] = true;
                        queue.add(new int[]{newRow, newCol});
                    }
                }
            }
        }

        return false;
    }

    private boolean isValid(int row, int col, int rows, int cols) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public static void main(String[] args) {
        MatrixPathFinder pathFinder = new MatrixPathFinder();
        char[][] matrix = {
            {'R', 'D', 'L'},
            {'U', 'R', 'D'},
            {'L', 'U', 'R'}
        };
        int[] source = {0, 0};
        int[] destination = {2, 2};

        boolean result = pathFinder.canReachDestination(matrix, source, destination);
        System.out.println("Can reach destination: " + result);
    }
} 