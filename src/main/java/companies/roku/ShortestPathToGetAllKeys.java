package companies.roku;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class ShortestPathToGetAllKeys {

    private int[][] DIRECTIONS = new int[][] {
            {-1, 0},
            {1, 0},
            {0, 1},
            {0, -1}
    };

    class StateD {
        int row;
        int col;
        Set<Character> keys;

        public StateD(int row, int col, Set<Character> keys) {
            this.row = row;
            this.col = col;
            this.keys = keys;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StateD stateD)) return false;

            if (row != stateD.row) return false;
            if (col != stateD.col) return false;
            return keys.equals(stateD.keys);
        }

        @Override
        public int hashCode() {
            int result = row;
            result = 31 * result + col;
            result = 31 * result + keys.hashCode();
            return result;
        }
    }

    record State(int row, int col, Set<Character> keys) {}

    public int shortestPathAllKeys(String[] grid) {
        int rows = grid.length;
        int cols = grid[0].length();
        int startRow = -1, startCol = -1, totalKeys = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char cell = grid[i].charAt(j);

                if ('@' == cell) {
                    startRow = i;
                    startCol = j;
                } else if (Character.isLowerCase(cell)) {
                    totalKeys++;
                }
            }
        }

        var startState = new State(startRow, startCol, new HashSet<>());
        Set<State> visited = new HashSet<>();
        Queue<State> queue = new LinkedList<>();
        queue.offer(startState);
        visited.add(startState);
        int steps = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                var node = queue.poll();
                int row = node.row();
                int col = node.col();
                var keys = new HashSet<>(node.keys);

                if (keys.size() == totalKeys) {
                    return steps;
                }

                for (int[] dir: DIRECTIONS) {
                    int newRow = row + dir[0];
                    int newCol = col + dir[1];

                    if (0 <= newRow && newRow < rows && 0 <= newCol && newCol < cols && grid[newRow].charAt(newCol) != '#') {
                        char cell = grid[newRow].charAt(newCol);
                        if (Character.isUpperCase(cell) && !keys.contains(Character.toLowerCase(cell))) {
                            continue;
                        }
                        var newKeys = new HashSet<>(keys);
                        if (Character.isLowerCase(cell)) {
                            newKeys.add(cell);
                        }

                        State newState = new State(newRow, newCol, newKeys);
                        if (!visited.contains(newState)) {
                            visited.add(newState);
                            queue.offer(newState);
                        }
                    }
                }
            }
            steps++;
        }
        return -1;
    }


    public static void main(String[] args) {
        var sol = new ShortestPathToGetAllKeys();
        System.out.println(
                sol.shortestPathAllKeys(
                        new String[]{
                                "@.a..",
                                "###.#",
                                "b.A.B"
                        }
                )
        );

        System.out.println(
                sol.shortestPathAllKeys(
                        new String[]{
                                "@..aA",
                                "..B#.",
                                "....b"
                        }
                )
        );
    }
}