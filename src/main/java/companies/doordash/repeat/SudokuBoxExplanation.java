package companies.doordash.repeat;

public class SudokuBoxExplanation {

    public static void main(String[] args) {
        System.out.println("=== Understanding 3x3 Box Calculation in Sudoku ===\n");

        // Demonstrate box calculation for different positions
        demonstrateBoxCalculation();

        System.out.println("\n=== Visual Representation ===");
        printBoxMapping();

        System.out.println("\n=== Step-by-step Example ===");
        stepByStepExample();
    }

    public static void demonstrateBoxCalculation() {
        System.out.println("Position (row, col) -> Box Top-Left Corner");
        System.out.println("==========================================");

        // Test various positions
        int[][] testPositions = {
                {0, 0}, {0, 4}, {0, 8},  // Top row
                {1, 1}, {1, 5}, {1, 7},  // Second row
                {2, 2}, {2, 3}, {2, 6},  // Third row
                {3, 0}, {3, 4}, {3, 8},  // Fourth row
                {4, 1}, {4, 5}, {4, 7},  // Fifth row
                {5, 2}, {5, 3}, {5, 6},  // Sixth row
                {6, 0}, {6, 4}, {6, 8},  // Seventh row
                {7, 1}, {7, 5}, {7, 7},  // Eighth row
                {8, 2}, {8, 3}, {8, 6}   // Ninth row
        };

        for (int[] pos : testPositions) {
            int row = pos[0];
            int col = pos[1];
            int boxRow = 3 * (row / 3);
            int boxCol = 3 * (col / 3);

            System.out.printf("Position (%d, %d) -> Box starts at (%d, %d) | Box ID: %d%n",
                    row, col, boxRow, boxCol, getBoxId(row, col));
        }
    }

    public static void printBoxMapping() {
        System.out.println("Grid positions and their corresponding box IDs:");
        System.out.println("┌─────────┬─────────┬─────────┐");

        for (int row = 0; row < 9; row++) {
            if (row == 3 || row == 6) {
                System.out.println("├─────────┼─────────┼─────────┤");
            }

            System.out.print("│");
            for (int col = 0; col < 9; col++) {
                if (col == 3 || col == 6) {
                    System.out.print("│");
                }
                System.out.printf(" %d ", getBoxId(row, col));
            }
            System.out.println("│");
        }
        System.out.println("└─────────┴─────────┴─────────┘");
    }

    public static void stepByStepExample() {
        int row = 4, col = 7;  // Example position

        System.out.printf("Example: Checking position (%d, %d)%n", row, col);
        System.out.println("Step 1: Calculate box top-left corner");
        System.out.printf("  boxRow = 3 * (%d / 3) = 3 * %d = %d%n", row, row/3, 3*(row/3));
        System.out.printf("  boxCol = 3 * (%d / 3) = 3 * %d = %d%n", col, col/3, 3*(col/3));

        int boxRow = 3 * (row / 3);
        int boxCol = 3 * (col / 3);

        System.out.println("\nStep 2: The 3x3 box covers positions:");
        for (int i = boxRow; i < boxRow + 3; i++) {
            for (int j = boxCol; j < boxCol + 3; j++) {
                System.out.printf("  (%d, %d)", i, j);
            }
            System.out.println();
        }

        System.out.println("\nStep 3: Integer division explanation:");
        System.out.println("  Row 0,1,2 -> row/3 = 0 -> boxRow = 0");
        System.out.println("  Row 3,4,5 -> row/3 = 1 -> boxRow = 3");
        System.out.println("  Row 6,7,8 -> row/3 = 2 -> boxRow = 6");
        System.out.println("  Same logic applies to columns");
    }

    // Helper method to get box ID for visualization
    public static int getBoxId(int row, int col) {
        return (row / 3) * 3 + (col / 3);
    }

    // Demonstrate the actual validation logic
    public static boolean isValidInBox(char[][] board, int row, int col, char num) {
        int boxRow = 3 * (row / 3);
        int boxCol = 3 * (col / 3);

        System.out.printf("Checking if '%c' is valid in box starting at (%d, %d):%n", num, boxRow, boxCol);

        for (int i = boxRow; i < boxRow + 3; i++) {
            for (int j = boxCol; j < boxCol + 3; j++) {
                System.out.printf("  Checking position (%d, %d): '%c'", i, j, board[i][j]);
                if (board[i][j] == num) {
                    System.out.printf(" -> CONFLICT FOUND!%n");
                    return false;
                }
                System.out.println(" -> OK");
            }
        }
        return true;
    }
}
