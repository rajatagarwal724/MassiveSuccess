package coding.top150;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WordPattern {

    public boolean wordPattern(String pattern, String s) {
        Map<Character, String> pattern_to_word_map = new HashMap<>();
        Map<String, Character> word_to_pattern_map = new HashMap<>();

        char[] patternArray = pattern.toCharArray();
        String[] words = s.split(StringUtils.SPACE);

        for (int i = 0; i < patternArray.length; i++) {
            char ch = patternArray[i];
            String word = words[i];

            if (!pattern_to_word_map.containsKey(ch) && !word_to_pattern_map.containsKey(word)) {
                pattern_to_word_map.put(ch, word);
                word_to_pattern_map.put(word, ch);
            } else if (!pattern_to_word_map.containsKey(ch) || !word_to_pattern_map.containsKey(word)) {
                return false;
            } else if (!(Objects.equals(pattern_to_word_map.get(ch), word) && Objects.equals(word_to_pattern_map.get(word), ch))) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        var sol = new WordPattern();
        System.out.println(sol.wordPattern("abba", "dog cat cat dog"));
        System.out.println(sol.wordPattern("abba", "dog cat cat fish"));
        System.out.println(sol.wordPattern("aaaa", "dog cat cat dog"));
    }
}
