package coding.linkedin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ShortestWordDistanceIII {

    public int shortestWordDistance_Optimized(String[] wordsDict, String word1, String word2) {
        int prevIndex = -1;
        int shortestDistance = Integer.MAX_VALUE;

        for (int i = 0; i < wordsDict.length; i++) {
            var word = wordsDict[i];
            if(!word.equals(word1) && !word.equals(word2)) {
                continue;
            }

//            if (word.equals(word1) || word.equals(word2)) {
                if (prevIndex != -1 && (word1.equals(word2) || !Objects.equals(wordsDict[prevIndex], word))) {
                    shortestDistance = Math.min(shortestDistance, Math.abs(i - prevIndex));
                }
                prevIndex = i;
//            }
        }
        return shortestDistance;
    }


    public int shortestWordDistance(String[] wordsDict, String word1, String word2) {

        Map<String, List<Integer>> map = new HashMap<>();
        for (int i = 0; i < wordsDict.length; i++) {
            map.computeIfAbsent(wordsDict[i], s -> new ArrayList<>()).add(i);
        }

        if (word1.equals(word2)) {
            var indices = map.get(word1);
            var res = Integer.MAX_VALUE;
            for (int i = 1; i < indices.size(); i++) {
                res = Math.min(res, Math.abs(indices.get(i - 1) - indices.get(i)));
            }
            return res;
        }

        var word1Indices = map.get(word1);
        var word2Indices = map.get(word2);
        var idx1 = 0;
        var idx2 = 0;
        var result = Integer.MAX_VALUE;
        while (idx1 < word1Indices.size() && idx2 < word2Indices.size()) {
            var word1Index = word1Indices.get(idx1);
            var word2Index = word2Indices.get(idx2);

            result = Math.min(result, Math.abs(word1Index - word2Index));

            if (word1Index < word2Index) {
                idx1++;
            } else {
                idx2++;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        var sol = new ShortestWordDistanceIII();
        System.out.println(
                sol.shortestWordDistance_Optimized(
                        new String[]{"practice", "makes", "perfect", "coding", "makes"},
                        "makes",
                        "coding"
                )
        );

        System.out.println(
                sol.shortestWordDistance_Optimized(
                        new String[]{"practice", "makes", "perfect", "coding", "makes"},
                        "makes",
                        "makes"
                )
        );
    }
}
