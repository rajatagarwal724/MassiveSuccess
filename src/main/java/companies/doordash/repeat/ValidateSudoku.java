package companies.doordash.repeat;

import java.util.ArrayList;
import java.util.List;

public class ValidateSudoku {

    int N;
    boolean[][] rows;
    boolean[][] cols;
    boolean[][][] boxes;
    boolean solved;
    List<Integer> emptyCells;

    public void init(char[][] board) {
        int N = board.length;
        boolean[][] rows = new boolean[N][N+1];
        boolean[][] cols = new boolean[N][N+1];
        boolean[][][] boxes = new boolean[N][N][N+1];
        emptyCells = new ArrayList<>();
        solved = false;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == '.') {
                    emptyCells.add(i * 9 + j);
                } else {
                    int value = Character.getNumericValue(board[i][j]);
                    rows[i][value] = true;
                    cols[j][value] = true;
                    boxes[i/3][j/3][value] = true;
                }
            }
        }
    }





    public void solveSudoku(char[][] board) {
        init(board);
        dfs(0, board);
    }

    private void dfs(int index, char[][] board) {
        if (index == emptyCells.size()) {
            solved = true;
            return;
        }

        int i = emptyCells.get(index) / 9;
        int j = emptyCells.get(index) % 9;


        for (int value = 1; value <= 9; value++) {
            if (!rows[i][value] && !cols[j][value] && !boxes[i/3][j/3][value]) {
                rows[i][value] = true;
                cols[j][value] = true;
                boxes[i/3][j/3][value] = true;

                board[i][j] = (char) ('0' + value);

                dfs(index + 1, board);

                if (solved) {
                    return;
                }

                rows[i][value] = false;
                cols[j][value] = false;
                boxes[i/3][j/3][value] = false;
                board[i][j] = '.';
            }
        }
    }

    public boolean isValidSudoku(char[][] board) {
        int N = board.length;
        boolean[][] rows = new boolean[N][N+1];
        boolean[][] cols = new boolean[N][N+1];
        boolean[][][] boxes = new boolean[N][N][N+1];

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (Character.isDigit(board[i][j])) {
                    int value = Character.getNumericValue(board[i][j]);
                    if (rows[i][value] || cols[j][value] || boxes[i/3][j/3][value]) {
                        return false;
                    }
                    rows[i][value] = true;
                    cols[j][value] = true;
                    boxes[i/3][j/3][value] = true;
                }
            }
        }
        return true;
    }
}
