package coding.linkedin;



public class WordSearch {

    public static boolean exist(char[][] board, String word) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (dfs(board, word, i, j, 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean dfs(char[][] board, String word, int currRow, int currCol, int index) {
        if (
                currRow < 0
                        || currRow >= board.length
                        || currCol < 0
                        || currCol >= board[0].length
                || board[currRow][currCol] != word.charAt(index)
        ) {
            return false;
        }

        if (index == word.length() - 1) {
            return true;
        }

        char temp = board[currRow][currCol];
        board[currRow][currCol] = '#';

        boolean result = dfs(board, word, currRow + 1, currCol, index + 1)
                || dfs(board, word, currRow - 1, currCol, index + 1)
                || dfs(board, word, currRow, currCol + 1, index + 1)
                || dfs(board, word, currRow, currCol - 1, index + 1);

        board[currRow][currCol] = temp;
        return result;
    }

    public static void main(String[] args) {
        var sol = new WordSearch();
        System.out.println(WordSearch.exist(
                new char[][]{
                        {'A', 'B', 'C' , 'E'},
                        {'S', 'F', 'C' , 'S'},
                        {'A', 'D', 'E' , 'E'}
                },
                "SEE"
        ));
    }
}
