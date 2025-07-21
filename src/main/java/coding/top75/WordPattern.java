package coding.top75;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WordPattern {

    public boolean wordPattern(String pattern, String s) {
        Map<Character, String> forwardPatternMatch = new HashMap<>();
        Map<String, Character> reversePatternMatch = new HashMap<>();

        char[] patternArray = pattern.toCharArray();
        String[] words = s.split(" ");

        if (patternArray.length != words.length) {
            return false;
        }

        for (int i = 0; i < patternArray.length; i++) {
            char ch = patternArray[i];
            String word = words[i];

            if (forwardPatternMatch.containsKey(ch)) {
                if (!Objects.equals(forwardPatternMatch.get(ch), word)) {
                    return false;
                }
            } else {
                forwardPatternMatch.put(ch, word);
            }

            if (reversePatternMatch.containsKey(word)) {
                if (reversePatternMatch.get(word) != ch) {
                    return false;
                }
            } else {
                reversePatternMatch.put(word, ch);
            }
        }

        return true;
    }

    public static void main(String[] args) {
        var sol = new WordPattern();
        System.out.println(sol.wordPattern("eegg", "dog dog cat cat"));
        System.out.println(sol.wordPattern("abca", "one two three four"));
        System.out.println(sol.wordPattern("abacac", "dog cat dog mouse dog mouse"));
    }
}
