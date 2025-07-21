package coding.linkedin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class WordDistance {

    private Map<String, List<Integer>> indicesMap;

    public WordDistance(String[] wordsDict) {
        this.indicesMap = new HashMap<>();
        for (int i = 0; i < wordsDict.length; i++) {
            indicesMap.computeIfAbsent(wordsDict[i], s -> new ArrayList<>()).add(i);
        }
    }

    public int shortest_1(String word1, String word2) {
        var word1Indices = indicesMap.get(word1);
        var word2Indices = indicesMap.get(word2);
        int shortestDistance = Integer.MAX_VALUE;
        for (int word1Index: word1Indices) {
            for (int word2Index: word2Indices) {
                shortestDistance = Math.min(shortestDistance, Math.abs(word1Index - word2Index));
            }
        }
        return shortestDistance;
    }

    public int shortest(String word1, String word2) {
        var word1Indices = indicesMap.get(word1);
        var word2Indices = indicesMap.get(word2);
        int shortestDistance = Integer.MAX_VALUE;

        int word1Idx = 0, word2Idx = 0;

        while (word1Idx < word1Indices.size() && word2Idx < word2Indices.size()) {
            var index1 = word1Indices.get(word1Idx);
            var index2 = word2Indices.get(word2Idx);

            shortestDistance = Math.min(shortestDistance, Math.abs(index1 - index2));

            if (index1 < index2) {
                word1Idx++;
            } else {
                word2Idx++;
            }
        }

        return shortestDistance;
    }

    public static void main(String[] args) {
        var sol = new WordDistance(new String[]{"practice", "makes", "perfect", "coding", "makes"});
        System.out.println(sol.shortest("coding", "practice"));
        System.out.println(sol.shortest("makes", "coding"));
    }
}
