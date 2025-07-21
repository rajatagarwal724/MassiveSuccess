package companies.doordash.repeat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Time Complexity: O(9^m)
 * Where m is the number of empty cells in the Sudoku board.
 * Explanation:
 * The algorithm uses backtracking with DFS
 * For each empty cell, it tries all 9 possible values (1-9)
 * In the worst case, if there are m empty cells, the recursion tree
 * has a branching factor of 9 and depth of m
 * This gives us O(9^m) in the worst case
 *
 * Space: O(N2) N SQUARED
 * O(M) for recursion stack
 */
public class SudokuSolverII {

    char[][] board;

    int N;
    List<Integer> emptyCells;
    boolean[][] rows;
    boolean[][] cols;
    boolean[][][] boxes;

    boolean solved;

    public void init(char[][] board) {
        this.board = board;
        N = board.length;
        emptyCells = new ArrayList<>();
        rows = new boolean[N][N + 1];
        cols = new boolean[N][N + 1];
        boxes = new boolean[N][N][N + 1];

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == '.') {
                    emptyCells.add(i * board.length + j);
                } else {
                    int value = board[i][j] - '0';
                    System.out.println(value);
                    rows[i][value] = true;
                    cols[j][value] = true;
                    boxes[i / 3][j / 3][value] = true;
                }
            }
        }

        solved = false;

        System.out.println(Arrays.deepToString(rows));
        System.out.println(Arrays.deepToString(cols));
        System.out.println(Arrays.deepToString(boxes));
        System.out.println(emptyCells);
    }

    public void solveSudoku(char[][] board) {
        init(board);
        dfs(0);
        System.out.println("--------------------------------");
        System.out.println(Arrays.deepToString(board));
    }

    private void dfs(int index) {
        if (index == emptyCells.size()) {
            solved = true;
            return;
        }

        int i = emptyCells.get(index) / N;
        int j = emptyCells.get(index) % N;

        for (int val = 1; val <= 9; val++) {
            if (!rows[i][val] && !cols[j][val] && !boxes[i/3][j/3][val]) {
                rows[i][val] = true;
                cols[j][val] = true;
                boxes[i/3][j/3][val] = true;

                board[i][j] = (char) ('0' + val);

                dfs(index + 1);

                if (solved) {
                    return;
                }

                rows[i][val] = false;
                cols[j][val] = false;
                boxes[i/3][j/3][val] = false;
            }
        }
    }

    public static void main(String[] args) {

        char sudokuChar = (char) ('0' + 1);  // Result: '5'
        System.out.println(sudokuChar);

        var sol = new SudokuSolverII();
        char[][] board = new char[][]{
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

        sol.solveSudoku(board);
    }
}
