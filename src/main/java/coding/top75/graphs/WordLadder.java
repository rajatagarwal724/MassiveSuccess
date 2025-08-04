package coding.top75.graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 *
 * Overall Time Complexity: Considering (N) as the number of words in the wordList,
 * the overall worst-case time complexity is O(N*L*A).
 */
public class WordLadder {

    class Node {
        String word;
        int steps;

        public Node(String word, int steps) {
            this.word = word;
            this.steps = steps;
        }
    }

    // Method to find the shortest transformation sequence length
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Queue<Node> queue = new LinkedList<>();
        queue.offer(new Node(beginWord, 1));
        Set<String> visited = new HashSet<>();
        visited.add(beginWord);
        Map<String, Set<String>> adjacency = new HashMap<>();
        wordList.forEach(word -> {
            for (int i = 0; i < word.length(); i++) {
                String newWord = word.substring(0, i) + "*" + word.substring(i +1);
                adjacency.computeIfAbsent(newWord, s -> new HashSet<>()).add(word);
            }
        });

        System.out.println(adjacency);

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                var node = queue.poll();
                String word = node.word;
                int steps = node.steps;

                if (word.equals(endWord)) {
                    return steps;
                }

                for (int j = 0; j < word.length(); j++) {
                    String newWord = word.substring(0, j) + "*" + word.substring(j + 1);
                    for (String nextWord: adjacency.getOrDefault(newWord, new HashSet<>())) {
                        if (!visited.contains(nextWord)) {
                            visited.add(nextWord);
                            queue.offer(new Node(nextWord, steps + 1));
                        }
                    }
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        var sol = new WordLadder();
        System.out.println(
                sol.ladderLength("bit", "pog", List.of("but", "put", "pig", "pog", "big"))
        );

        System.out.println(
                sol.ladderLength("cat", "dog", List.of("cot", "dot", "dog"))
        );

        System.out.println(
                sol.ladderLength("sing", "ring", List.of("ring"))
        );
    }
}
