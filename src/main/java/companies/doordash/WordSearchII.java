package companies.doordash;

import java.util.ArrayList;
import java.util.List;

public class WordSearchII {

    public List<String> findWords(char[][] board, String[] words) {
        List<String> res = new ArrayList<>();

        for (String word: words) {
            if (dfsForWord(board, word)) {
                res.add(word);
            }
        }
        return res;
    }

    private boolean dfsForWord(char[][] board, String word) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (DFS(i, j, word, board, 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean DFS(int row, int col, String word, char[][] board, int index) {
        if (row < 0 || row >= board.length || col < 0 || col >= board[0].length ) {
            return false;
        }

        if (board[row][col] != word.charAt(index)) {
            return false;
        }

        if (index == (word.length() - 1)) {
            return true;
        }

        char temp = board[row][col];
        board[row][col] = '#';
        boolean res = DFS(row + 1, col, word, board, index + 1)
                || DFS(row - 1, col, word, board, index + 1)
                || DFS(row, col + 1, word, board, index + 1)
                || DFS(row, col - 1, word, board, index + 1);
        board[row][col] = temp;
        return res;
    }

    public static void main(String[] args) {
        var sol = new WordSearchII();
        sol.findWords(
                new char[][]{
                        {'o','a','a','n'},
                        {'e','t','a','e'},
                        {'i','h','k','r'},
                        {'i','f','l','v'}
                },
                new String[]{"oath","pea","eat","rain"}
        ).forEach(System.out::println);
    }
}
