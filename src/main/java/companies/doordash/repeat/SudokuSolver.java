package companies.doordash.repeat;

public class SudokuSolver {

    private boolean isValid(int row, int col, char[][] board, char num) {

        for (int i = 0; i < board[0].length; i++) {
            if (board[row][i] == num) {
                return false;
            }
        }

        for (int j = 0; j < board.length; j++) {
            if (board[j][col] == num) {
                return false;
            }
        }

        int boardRow = 3 * (row/3);
        int boardCol = 3 * (col/3);

        for (int i = boardRow; i < boardRow + 3; i++) {
            for (int j = boardCol; j < boardCol + 3; j++) {
                if (board[i][j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean solve(char[][] board) {
        int M = 9, N = 9;
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] == '.') {
                    for (char val = '1'; val <= '9'; val++) {
                        if (isValid(i, j, board, val)) {
                            board[i][j] = val;
                            if (solve(board)) {
                                return true;
                            } else {
                                board[i][j] = '.';
                            }
                        }
                    }
                    // No Number works for this cell
                    return false;
                }
            }
        }
        // All Cells are filled base case
        return true;
    }

    public void solveSudoku(char board[][]) {
        if (null == board || board.length == 0) {
            return;
        }
        solve(board);
    }

    public static void main(String[] args) {
        var sol = new SudokuSolver();
//        sol.solveSudoku(
//                new char[][]{
//                        {"5","3",".",".","7",".",".",".","."}
//                }
//        );


//        char ch = Character.forDigit(400, 10);
//        System.out.println(ch);
//
//        int num = 65;
//        ch = (char) num;
//        System.out.println(ch);
//
//        int digit = 1;
//        ch = (char) ('0' + digit);  // Result: '5'
//        System.out.println(ch);
//// Or equivalently:
//        ch = (char) (48 + digit);   // 48 is ASCII value of '0'
//        System.out.println(ch);
    }
}
