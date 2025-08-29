package coding.top75;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class WordLadder {

    class Node {
        String word;
        int steps;

        public Node(String word, int steps) {
            this.word = word;
            this.steps = steps;
        }
    }

    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Map<String, Set<String>> adjacency = new HashMap<>();
        wordList.forEach(word -> {
            for (int i = 0; i < word.length(); i++) {
                var newWord = word.substring(0, i) + "*" + word.substring(i + 1);
                adjacency.computeIfAbsent(newWord, s -> new HashSet<>()).add(word);
            }
        });
        Queue<Node> queue = new LinkedList<>();
        queue.offer(new Node(beginWord, 0));
        Set<String> visited = new HashSet<>();
        visited.add(beginWord);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                var node = queue.poll();
                var nextWord = node.word;
                var steps = node.steps;

                if (nextWord.equals(endWord)) {
                    return steps;
                }

                for (int j = 0; j < nextWord.length(); j++) {
                    var word = nextWord.substring(0, j) + "*" + nextWord.substring(j + 1);
                    for (var neighbour: adjacency.getOrDefault(word, new HashSet<>())) {
                        if (visited.add(neighbour)) {
                            queue.offer(new Node(neighbour, steps + 1));
                        }
                    }
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {

    }
}
