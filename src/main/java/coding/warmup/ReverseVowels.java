package coding.warmup;

import java.util.Set;

public class ReverseVowels {
    private static Set<Character> vowels = Set.of('a','e','i','o','u');
    public String reverseVowels(String s) {
        char[] word = s.toCharArray();
        int start = 0, end = word.length - 1;

        while (start < end) {
            while (start < end && !isVowel(word[start])) {
                start++;
            }
            while (start < end && !isVowel(word[end])) {
                end--;
            }

            if (start < end) {
                swap(word, start++, end--);
            }
        }
        return String.valueOf(word);
    }

    private static void swap(char[] word, int start, int end) {
        char temp = word[start];
        word[start] = word[end];
        word[end] = temp;
    }

    private static boolean isVowel(char word) {
        return vowels.contains(Character.toLowerCase(word));
    }

    public static void main(String[] args) {
        var sol = new ReverseVowels();
        System.out.println(sol.reverseVowels("hello"));
        System.out.println(sol.reverseVowels("AEIOU"));
        System.out.println(sol.reverseVowels("DesignGUrus"));
    }
}
