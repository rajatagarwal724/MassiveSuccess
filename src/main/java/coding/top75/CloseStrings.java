package coding.top75;

import java.util.Map;
import java.util.TreeMap;

public class CloseStrings {

    public boolean closeStrings(String word1, String word2) {
        if (word1.length() != word2.length()) {
            return false;
        }
        TreeMap<Character, Integer> word1FreqMap = new TreeMap<>();
        TreeMap<Character, Integer> word2FreqMap = new TreeMap<>();

        for (int i = 0; i < word1.length(); i++) {
            char word1Char = word1.charAt(i);
            char word2Char = word2.charAt(i);

            word1FreqMap.compute(word1Char, (t, count) -> count == null ? 1 : count + 1);
            word2FreqMap.compute(word2Char, (t, count) -> count == null ? 1 : count + 1);
        }

        return word1FreqMap.equals(word2FreqMap);
    }

    public static void main(String[] args) {
        var sol = new CloseStrings();
        System.out.println(sol.closeStrings("aacbbc", "bbcaca"));
    }
}
