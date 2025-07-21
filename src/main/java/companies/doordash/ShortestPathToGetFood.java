package companies.doordash;

import java.util.LinkedList;
import java.util.Queue;

public class ShortestPathToGetFood {

    record Node(int row, int col, int distance) {}

    private final char MY_LOCATION = '*';
    private final char FOOD = '#';
    private final char FREE_SPACE = '0';
    private final char OBSTACLE = 'X';

    int[][] DIRECTIONS = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

    public int getFood(char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        boolean[][] visited = new boolean[rows][cols];
        int locX = -1, locY = -1;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (grid[row][col] == MY_LOCATION) {
                    locX = row;
                    locY = col;
                    break;
                }
            }
        }

        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(locX, locY, 0));
        visited[locX][locY] = true;

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                var node = queue.poll();

                for (int[] dir : DIRECTIONS) {
                    int newX = node.row() + dir[0];
                    int newY = node.col() + dir[1];

                    if (grid[newX][newY] == FOOD) {
                        return node.distance;
                    }

                    if (newX >= 0 && newX < rows && newY >= 0 && newY < cols && grid[newX][newY] != OBSTACLE && !visited[newX][newY]) {
                        visited[newX][newY] = true;
                        queue.add(new Node(newX, newY, node.distance + 1));
                    }
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        var sol = new ShortestPathToGetFood();
        var res = sol.getFood(new char[][]{
                {'X','X','X','X','X','X'},
                {'X','*','0','0','0','X'},
                {'X','0','0','#','0','X'},
                {'X','X','X','X','X','X'},
        });
        System.out.println(res);
    }
}
