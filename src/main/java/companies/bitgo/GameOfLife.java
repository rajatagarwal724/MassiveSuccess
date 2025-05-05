package companies.bitgo;

public class GameOfLife {

    public void gameOfLife(int[][] board) {
        int rows = board.length;
        int cols = board[0].length;

        int[][] copyBoard = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                copyBoard[i][j] = board[i][j];
            }
        }

        int[] neighbours = {0, 1, -1};

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                int elem = copyBoard[i][j];
                int liveElems = 0;
                for (int nr = 0; nr < 3; nr++) {
                    for (int ny = 0; ny < 3; ny++) {
                        if (!(neighbours[nr] == 0 && neighbours[ny] == 0)) {
                            int row = i + neighbours[nr];
                            int col = j + neighbours[ny];

                            if (row < 0 || row >= rows || col < 0 || col >= cols) {
                                continue;
                            }
                            if (copyBoard[row][col] == 1) {
                                liveElems++;
                            }

                        }
                    }
                }

                if (elem == 1) {
                    if (liveElems < 2) {
                        board[i][j] = 0;
                    } else if (liveElems > 3) {
                        board[i][j] = 0;
                    }
                } else {
                    if (liveElems == 3) {
                        board[i][j] = 1;
                    }
                }
            }
        }
    }

    public void gameOfLife_woSpace(int[][] board) {
        int rows = board.length;
        int cols = board[0].length;

        int[] neighbours = {0, 1, -1};

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                int elem = board[i][j];
                int liveElems = 0;
                for (int nr = 0; nr < 3; nr++) {
                    for (int ny = 0; ny < 3; ny++) {
                        if (!(neighbours[nr] == 0 && neighbours[ny] == 0)) {
                            int row = i + neighbours[nr];
                            int col = j + neighbours[ny];

                            if (row < 0 || row >= rows || col < 0 || col >= cols) {
                                continue;
                            }
                            if (Math.abs(board[row][col]) == 1) {
                                liveElems++;
                            }

                        }
                    }
                }

                if (elem == 1) {
                    if (liveElems < 2) {
                        board[i][j] = -1;
                    } else if (liveElems > 3) {
                        board[i][j] = -1;
                    }
                } else {
                    if (liveElems == 3) {
                        board[i][j] = 2;
                    }
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == 2) {
                    board[i][j] = 1;
                } else if (board[i][j] == -1) {
                    board[i][j] = 0;
                }
            }
        }
    }

    public static void main(String[] args) {
        var sol = new GameOfLife();
        int[][] board = new int[][] {
                {1, 1},
                {1, 0}
        };

        sol.gameOfLife(board);

        System.out.println(board);

        board = new int[][] {
                {0, 1, 0},
                {0, 0, 1},
                {1, 1, 1},
                {0, 0, 0},
        };

        sol.gameOfLife(board);
        System.out.println(board);

        board = new int[][] {
                {1, 1},
                {1, 0}
        };

        sol.gameOfLife_woSpace(board);

        System.out.println(board);

        board = new int[][] {
                {0, 1, 0},
                {0, 0, 1},
                {1, 1, 1},
                {0, 0, 0},
        };

        sol.gameOfLife_woSpace(board);
        System.out.println(board);
    }
}
