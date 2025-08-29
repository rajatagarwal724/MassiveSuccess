package coding.leetcode;

public class UniquePathsIII {

    public int uniquePathsIII(int[][] grid) {
        int startRow = -1;
        int startCol = -1;
        int emptyCells = 1; // Start with 1 to include the starting square

        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == 0) {
                    emptyCells++;
                } else if (grid[r][c] == 1) {
                    startRow = r;
                    startCol = c;
                }
            }
        }
        return backtrack(grid, startRow, startCol, emptyCells);
    }

    private int backtrack(int[][] grid, int r, int c, int emptyCells) {
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] < 0) {
            return 0;
        }

        if (grid[r][c] == 2) {
            return emptyCells == 1 ? 1 : 0;
        }

        int originalValue = grid[r][c];
        grid[r][c] = -2; // Mark as visited
        int paths = backtrack(grid, r + 1, c, emptyCells - 1) +
                    backtrack(grid, r - 1, c, emptyCells - 1) +
                    backtrack(grid, r, c + 1, emptyCells - 1) +
                    backtrack(grid, r, c - 1, emptyCells - 1);
        grid[r][c] = originalValue; // Un-mark with the original value

        return paths;
    }

    public static void main(String[] args) {
        UniquePathsIII solver = new UniquePathsIII();

        // Helper function to deep copy grids for isolated tests
        java.util.function.Function<int[][], int[][]> deepCopy = original -> {
            if (original == null) return null;
            return java.util.Arrays.stream(original).map(int[]::clone).toArray(int[][]::new);
        };

        // Example 1: Expected output: 2
        int[][] grid1 = {{1, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 2, -1}};
        System.out.println("Number of unique paths (Grid 1): " + solver.uniquePathsIII(deepCopy.apply(grid1)));

        // Example 2: Expected output: 4
        int[][] grid2 = {{1, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 2}};
        System.out.println("Number of unique paths (Grid 2): " + solver.uniquePathsIII(deepCopy.apply(grid2)));

        // Example 3: Expected output: 0
        int[][] grid3 = {{0, 1}, {2, 0}};
        System.out.println("Number of unique paths (Grid 3): " + solver.uniquePathsIII(deepCopy.apply(grid3)));

        // Example 4: Expected output: 1
        int[][] grid4 = {{1, 2}};
        System.out.println("Number of unique paths (Grid 4): " + solver.uniquePathsIII(deepCopy.apply(grid4)));
    }
}
