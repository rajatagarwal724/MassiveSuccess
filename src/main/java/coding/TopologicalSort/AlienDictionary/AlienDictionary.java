package coding.TopologicalSort.AlienDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

public class AlienDictionary {

    public String findOrder(String[] words) {
        Map<Character, Integer> inDegree = new HashMap<>();
        Map<Character, List<Character>> graph = new HashMap<>();
        StringBuilder result = new StringBuilder();

        for (String word: words) {
            for (int i = 0; i < word.length(); i++) {
                Character ch = word.charAt(i);
                inDegree.put(ch, 0);
                graph.put(ch, new ArrayList<>());
            }
        }

        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];

            for (int j = 0; j < Math.min(word1.length(), word2.length()); j++) {
                char parent = word1.charAt(j);
                char child = word2.charAt(j);

                if (parent != child) {
                    graph.get(parent).add(child);
                    inDegree.put(child, inDegree.get(child) + 1);
                    break;
                }
            }
        }

        Queue<Character> sources = inDegree.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedList::new));

        while (!sources.isEmpty()) {
            Character alphabet = sources.poll();
            result.append(alphabet);

            List<Character> children = graph.get(alphabet);
            for (Character child: children) {
                inDegree.put(child, inDegree.get(child) - 1);
                if (inDegree.get(child) == 0) {
                    sources.offer(child);
                }
            }
        }

        if (result.length() == inDegree.size()) {
            return result.toString();
        }

        return "";
    }


    public static void main(String[] args) {

    }
}
