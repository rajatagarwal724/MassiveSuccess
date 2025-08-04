package coding.leetcode;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

public class ShortestPathToGetAllKeys_NoBitmask {

    // State class to represent the position and the set of keys collected.
    // We must override equals() and hashCode() to use this in a HashSet for 'visited' tracking.
    static class State {
        int row;
        int col;
        Set<Character> keys;

        State(int row, int col, Set<Character> keys) {
            this.row = row;
            this.col = col;
            this.keys = keys;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return row == state.row && col == state.col && keys.equals(state.keys);
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col, keys);
        }
    }

    public int shortestPathAllKeys(String[] grid) {
        int m = grid.length;
        int n = grid[0].length();
        int startRow = -1, startCol = -1;
        int totalKeys = 0;

        // Find start position and count total number of keys
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                char cell = grid[i].charAt(j);
                if (cell == '@') {
                    startRow = i;
                    startCol = j;
                } else if (Character.isLowerCase(cell)) {
                    totalKeys++;
                }
            }
        }

        Queue<State> queue = new LinkedList<>();
        Set<State> visited = new HashSet<>();

        // Initial state
        Set<Character> initialKeys = new HashSet<>();
        State startState = new State(startRow, startCol, initialKeys);
        queue.offer(startState);
        visited.add(startState);

        int steps = 0;
        int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                State currentState = queue.poll();
                int r = currentState.row;
                int c = currentState.col;
                Set<Character> currentKeys = currentState.keys;

                // Check if we have collected all keys
                if (currentKeys.size() == totalKeys) {
                    return steps;
                }

                // Explore neighbors
                for (int[] dir : dirs) {
                    int newRow = r + dir[0];
                    int newCol = c + dir[1];

                    if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n) {
                        char cell = grid[newRow].charAt(newCol);

                        if (cell == '#') { // Wall
                            continue;
                        }

                        Set<Character> newKeys = new HashSet<>(currentKeys);
                        
                        // If it's a key
                        if (Character.isLowerCase(cell)) {
                            newKeys.add(cell);
                        }
                        
                        // If it's a lock
                        if (Character.isUpperCase(cell)) {
                            // Check if we have the key for this lock
                            if (!currentKeys.contains(Character.toLowerCase(cell))) {
                                continue; // Cannot pass through the lock
                            }
                        }

                        // Create new state and check if visited
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

        return -1; // Impossible to collect all keys
    }
}
