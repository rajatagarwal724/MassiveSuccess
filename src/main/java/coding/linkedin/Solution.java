package coding.linkedin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Solution {

    static class Pair {
        String word;
        int level;

        public Pair(String word, int level) {
            this.word = word;
            this.level = level;
        }
    }

    // Method to find the shortest transformation sequence length
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> visited = new HashSet<>();
        Map<String, List<String>> combinations = new HashMap<>();
        Queue<Pair> queue = new LinkedList<>();

        queue.offer(new Pair(beginWord, 1));
        visited.add(beginWord);

        wordList.forEach(word -> {
            for(int i = 0; i < word.length(); i++) {
                var newWord = word.substring(0, i) + "*" + word.substring(i + 1, word.length());
                combinations.computeIfAbsent(newWord, s -> new ArrayList<>()).add(word);
            }
        });

        while(!queue.isEmpty()) {
            var node = queue.poll();

            var word = node.word;
            var level = node.level;

            if (word.equals(endWord)) {
                return level;
            }

            for(int i = 0; i < word.length(); i++) {
                var nextWordComb = word.substring(0, i) + "*" + word.substring(i + 1, word.length());
                for(var neighbour: combinations.getOrDefault(nextWordComb, new ArrayList<>())) {
                    if(neighbour.equals(endWord)) {
                        return level + 1;
                    }

                    if(!visited.contains(neighbour)) {
                        visited.add(neighbour);
                        queue.offer(new Pair(neighbour, level + 1));
                    }
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        var sol = new Solution();
        System.out.println(sol.ladderLength("hit", "hit", List.of("hit")));
    }
}
