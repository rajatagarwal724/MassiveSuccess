public class SudokuSolver {
    
    /**
     * Solves a Sudoku puzzle using backtracking
     * @param board The 9x9 Sudoku board with '.' representing empty cells
     */
    public void solveSudoku(char[][] board) {
        if (board == null || board.length == 0) return;
        solve(board);
    }
    
    /**
     * Main backtracking function to solve Sudoku
     * @param board The Sudoku board
     * @return true if solution found, false otherwise
     */
    private boolean solve(char[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                // Find empty cell
                if (board[row][col] == '.') {
                    // Try digits 1-9
                    for (char num = '1'; num <= '9'; num++) {
                        // Check if this number can be placed here
                        if (isValid(board, row, col, num)) {
                            // Place the number
                            board[row][col] = num;
                            
                            // Recursively try to solve the rest
                            if (solve(board)) {
                                return true; // Solution found
                            } else {
                                // Backtrack: remove the number and try next
                                board[row][col] = '.';
                            }
                        }
                    }
                    // No number works for this cell
                    return false;
                }
            }
        }
        // All cells are filled (base case)
        return true;
    }
    
    /**
     * Validates if a number can be placed at the given position
     * @param board The Sudoku board
     * @param row Row index
     * @param col Column index  
     * @param num Number to validate
     * @return true if valid placement, false otherwise
     */
    private boolean isValid(char[][] board, int row, int col, char num) {
        // Check row
        for (int j = 0; j < 9; j++) {
            if (board[row][j] == num) {
                return false;
            }
        }
        
        // Check column
        for (int i = 0; i < 9; i++) {
            if (board[i][col] == num) {
                return false;
            }
        }
        
        // Check 3x3 box
        int boxRow = 3 * (row / 3);
        int boxCol = 3 * (col / 3);
        for (int i = boxRow; i < boxRow + 3; i++) {
            for (int j = boxCol; j < boxCol + 3; j++) {
                if (board[i][j] == num) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Utility method to print the Sudoku board
     */
    public void printBoard(char[][] board) {
        for (int i = 0; i < 9; i++) {
            if (i % 3 == 0 && i != 0) {
                System.out.println("---------------------");
            }
            for (int j = 0; j < 9; j++) {
                if (j % 3 == 0 && j != 0) {
                    System.out.print("| ");
                }
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
    
    public static void main(String[] args) {
        SudokuSolver solver = new SudokuSolver();
        
        // Example Sudoku puzzle
        char[][] board = {
            {'5','3','.','.','7','.','.','.','.'},
            {'6','.','.','1','9','5','.','.','.'},
            {'.','9','8','.','.','.','.','6','.'},
            {'8','.','.','.','6','.','.','.','3'},
            {'4','.','.','8','.','3','.','.','1'},
            {'7','.','.','.','2','.','.','.','6'},
            {'.','6','.','.','.','.','2','8','.'},
            {'.','.','.','4','1','9','.','.','5'},
            {'.','.','.','.','8','.','.','7','9'}
        };
        
        System.out.println("Original Sudoku:");
        solver.printBoard(board);
        
        System.out.println("\nSolving...");
        solver.solveSudoku(board);
        
        System.out.println("Solved Sudoku:");
        solver.printBoard(board);
    }
} 