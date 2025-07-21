package coding.linkedin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class WordLadder {

    static class Pair {
        String word;
        int level;

        public Pair(String word, int level) {
            this.word = word;
            this.level = level;
        }
    }

    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        int L = beginWord.length();
        Map<String, List<String>> combinations = new HashMap<>();

        for (String word: wordList) {
            for (int i = 0; i < L; i++) {
                var newWord = word.substring(0, i) + "*" + word.substring(i + 1, L);
                combinations.computeIfAbsent(newWord, s -> new ArrayList<>()).add(word);
            }
        }

        Queue<Pair> queue = new LinkedList<>();
        queue.offer(new Pair(beginWord, 1));

        Set<String> visited = new HashSet<>();
        visited.add(beginWord);

        while (!queue.isEmpty()) {
            var pair = queue.poll();

            var key = pair.word;
            var level = pair.level;

            for (int i = 0; i < L; i++) {
                var newWord = key.substring(0, i) + "*" + key.substring(i + 1, L);

                for (String nextWord: combinations.getOrDefault(newWord, new ArrayList<>())) {

                    if (endWord.equals(nextWord)) {
                        return level + 1;
                    }

                    if (!visited.contains(nextWord)) {
                        visited.add(nextWord);
                        queue.offer(new Pair(nextWord, level + 1));
                    }
                }
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        var sol = new WordLadder();
        System.out.println(
                sol.ladderLength(
                        "hit",
                        "cog",
                        List.of("hot","dot","dog","lot","log","cog")
                )
        );

        System.out.println(
                sol.ladderLength(
                        "hit",
                        "cog",
                        List.of("hot","dot","dog","lot","log")
                )
        );
    }
}
