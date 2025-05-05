package companies.splunk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class NoOfDistinctIslands {

    class Node {
        int x;
        int y;
        String D;

        public Node(int x, int y, String d) {
            this.x = x;
            this.y = y;
            D = d;
        }
    }

    public int numDistinctIslands(int[][] grid) {
        Set<String> islandDirection = new HashSet<>();
        for(int i = 0; i < grid.length; i++) {
            for(int j = 0; j < grid[0].length; j++) {
                if(grid[i][j] == 1) {
                    StringBuilder islandTraversal = new StringBuilder();
                    visitIsland(i, j, grid, islandTraversal);
                    System.out.println(islandTraversal.toString());
                    islandDirection.add(islandTraversal.toString());
                }
            }
        }
        return islandDirection.size();
    }

    private void visitIsland(int row, int col, int[][] grid, StringBuilder islandTraversal) {
        Queue<Node> queue = new LinkedList<>();
        queue.offer(new Node(row, col, "O"));
        while (!queue.isEmpty()) {
            Node neighbour = queue.poll();
            int x = neighbour.x;
            int y = neighbour.y;
            String D = neighbour.D;

            if (x < 0 || x >= grid.length || y < 0 || y >= grid[0].length) {
                continue;
            }
            if (grid[x][y] == 0) {
                continue;
            }

            grid[x][y] = 0;
            islandTraversal.append(D);

            queue.offer(new Node(x + 1, y, "D"));
            queue.offer(new Node(x - 1, y, "U"));
            queue.offer(new Node(x, y + 1, "R"));
            queue.offer(new Node(x, y - 1, "L"));
            islandTraversal.append("Z");
        }
    }

//    class Node {
//        int x, y;
//        public Node(int x, int y) {
//            this.x = x;
//            this.y = y;
//        }
//    }
//
//    public int numDistinctIslands(int[][] grid) {
//        Set<String> uniqueIslands = new HashSet<>();
//        int rows = grid.length, cols = grid[0].length;
//        boolean[][] visited = new boolean[rows][cols];
//
//        for (int r = 0; r < rows; r++) {
//            for (int c = 0; c < cols; c++) {
//                if (grid[r][c] == 1 && !visited[r][c]) {
//                    List<String> shape = bfs(grid, visited, r, c);
//                    var dir = String.join(",", shape);
//                    System.out.println(dir);
//                    uniqueIslands.add(dir);
//                }
//            }
//        }
//
//        return uniqueIslands.size();
//    }
//
//    private List<String> bfs(int[][] grid, boolean[][] visited, int startX, int startY) {
//        Queue<Node> queue = new LinkedList<>();
//        queue.offer(new Node(startX, startY));
//        visited[startX][startY] = true;
//
//        List<String> shape = new ArrayList<>();
//        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}; // Down, Up, Right, Left
//
//        while (!queue.isEmpty()) {
//            Node node = queue.poll();
//            int x = node.x, y = node.y;
//            shape.add((x - startX) + ":" + (y - startY)); // Store relative coordinates
//
//            for (int[] dir : directions) {
//                int newX = x + dir[0];
//                int newY = y + dir[1];
//
//                if (isValid(grid, visited, newX, newY)) {
//                    queue.offer(new Node(newX, newY));
//                    visited[newX][newY] = true;
//                }
//            }
//        }
//
//        return shape;
//    }
//
//    private boolean isValid(int[][] grid, boolean[][] visited, int x, int y) {
//        return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length &&
//                grid[x][y] == 1 && !visited[x][y];
//    }

    public static void main(String[] args) {
        var sol = new NoOfDistinctIslands();
//        System.out.println(
//                sol.numDistinctIslands(
//                        new int[][]{
//                                {1,1,0,0,0},
//                                {1,1,0,0,0},
//                                {0,0,0,1,1},
//                                {0,0,0,1,1}
//                        }
//                )
//        );

//        System.out.println(
//                sol.numDistinctIslands(
//                        new int[][]{
//                                {1,1,0,1,1},
//                                {1,0,0,0,0},
//                                {0,0,0,0,1},
//                                {1,1,0,1,1}
//                        }
//                )
//        );


        System.out.println(
                sol.numDistinctIslands(
                        new int[][]{
                                {1,1,0},
                                {0,1,1},
                                {0,0,0},
                                {1,1,1},
                                {0,1,0}
                        }
                )
        );
    }
}
