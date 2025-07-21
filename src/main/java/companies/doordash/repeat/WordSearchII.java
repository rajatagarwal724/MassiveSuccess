package companies.doordash.repeat;

import java.util.*;

public class WordSearchII {

    class TrieNode {
        String word;
        Map<Character, TrieNode> children = new HashMap<>();
    }

    public List<String> findWords(char[][] board, String[] words) {
        TrieNode root = new TrieNode();
        List<String> result = new ArrayList<>();

        for (String word: words) {
            TrieNode curr = root;
            for (char elem: word.toCharArray()) {
                if (!curr.children.containsKey(elem)) {
                    TrieNode newNode = new TrieNode();
                    curr.children.put(elem, newNode);
                }
                curr = curr.children.get(elem);
            }
            curr.word = word;
        }
        TrieNode parent = root;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (parent.children.containsKey(board[i][j])) {
                    dfs_trie(i, j, parent, board, result);
                }
            }
        }
        return result;
    }

    int[][] DIRECTIONS = new int[][] {
            {-1, 0},
            {1, 0},
            {0, 1},
            {0, -1}
    };

    private void dfs_trie(int row, int col, TrieNode parent, char[][] board, List<String> result) {
        Character letter = board[row][col];

        TrieNode curr = parent.children.get(letter);

        if (curr.word != null) {
            result.add(curr.word);
            curr.word = null;
        }

        board[row][col] = '#';

        for (int[] offset: DIRECTIONS) {
            int newRow = row + offset[0];
            int newCol = col + offset[1];

            if (newRow < 0 || newRow >= board.length || newCol < 0 || newCol >= board[0].length
            || !curr.children.containsKey(board[newRow][newCol])) {
                continue;
            }

            dfs_trie(newRow, newCol, curr, board, result);
        }

        board[row][col] = letter;

        if (curr.children.isEmpty()) {
            parent.children.remove(letter);
        }
    }

    public List<String> findWords_(char[][] board, String[] words) {
        List<String> result = new ArrayList<>();

        for (String word: words) {
            boolean foundWord = false;
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    if (dfs(i, j, 0, board, word)) {
                        result.add(word);
                        foundWord = true;
                        break;
                    }
                }
                if (foundWord) {
                    break;
                }
            }
        }

        return result;
    }

    private boolean dfs(int row, int col, int index, char[][] board, String word) {
        if (row < 0 || row >= board.length || col < 0 || col >= board[0].length
        || board[row][col] != word.charAt(index)) {
            return false;
        }

        if (index == (word.length() - 1)) {
            return true;
        }

        char temp = board[row][col];
        board[row][col] = '#';

        boolean res = dfs(row + 1, col, index + 1, board, word)
                || dfs(row - 1, col, index + 1, board, word)
                || dfs(row, col + 1, index + 1, board, word)
                || dfs(row, col - 1, index + 1, board, word);

        board[row][col] = temp;
        return res;
    }

    public static void main(String[] args) {
        var sol = new WordSearchII();
        System.out.println(
                sol.findWords(
                        new char[][]{
                                {'o','a','a','n'},
                                {'e','t','a','e'},
                                {'i','h','k','r'},
                                {'i','f','l','v'}
                        },
                        new String[]{"oath","pea","eat","rain"}
                )
        );
    }
}
