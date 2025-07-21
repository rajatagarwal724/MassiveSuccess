package coding.linkedin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class WordLadderII {

    record Node(String word, List<String> sequence, int level) {}

    public List<List<String>> findLadders(String beginWord, String endWord, List<String> wordList) {
        Map<String, List<String>> adjacency = new HashMap<>();
        Set<String> visited = new HashSet<>();
        List<List<String>> result = new ArrayList<>();

        Queue<Node> queue = new LinkedList<>();
        queue.offer(new Node(beginWord, List.of(beginWord), 1));

        visited.add(beginWord);

        wordList.forEach(word -> {
            for (int i = 0; i < word.length(); i++) {
                var likeWord = word.substring(0, i) + "*" + word.substring(i + 1);
                adjacency.computeIfAbsent(likeWord, s -> new ArrayList<>()).add(word);
            }
        });

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                var node = queue.poll();
                var word = node.word;
                var sequence = node.sequence();
                var level = node.level;

                if (word.equals(endWord)) {
                    result.add(sequence);
                    System.out.println(level);
                    continue;
                }

                for (int j = 0; j < word.length(); j++) {
                    var nextWord = word.substring(0, j) + "*" + word.substring(j + 1);
                    for (var neighbour: adjacency.getOrDefault(nextWord, new ArrayList<>())) {
                        if (!visited.contains(neighbour)) {
                            visited.add(neighbour);
                            var newSeq = new ArrayList<>(sequence);
                            newSeq.add(neighbour);
                            queue.offer(new Node(neighbour, newSeq, level + 1));
                        }
                    }
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        var sol = new WordLadderII();
        sol.findLadders(
                "hit",  "cog", List.of("hot","dot","dog","lot","log","cog")
        ).forEach(System.out::println);
    }

}
