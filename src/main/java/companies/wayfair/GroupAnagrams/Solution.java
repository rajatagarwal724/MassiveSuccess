package companies.wayfair.GroupAnagrams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {

    private static List<List<String>> getAnagrams(String[] words, String[] queries) {
        List<List<String>> result = new ArrayList<>();
        Map<String, List<String>> anagramsMap = new HashMap<>();
        for (String word: words) {
            char[] arr = word.toCharArray();
            Arrays.sort(arr);
            var sortedString = new String(arr);
            anagramsMap.computeIfAbsent(sortedString, s -> new ArrayList<>()).add(word);
        }

        for (String query: queries) {
            char[] arr = query.toCharArray();
            Arrays.sort(arr);
            var sortedString = new String(arr);
            result.add(anagramsMap.getOrDefault(sortedString, new ArrayList<>()));
        }
        return result;
    }

    public static void main(String[] args) {
        String[] words = new String[] {"duel", "speed", "dule", "cars"};
        String[] queries = new String[] {"spede", "deul"};
        getAnagrams(words, queries).forEach(strings -> {
            System.out.println("ghjkl");
            strings.forEach(System.out::println);
        });
    }
}
