package coding.warmup;

import java.util.Arrays;
import java.util.OptionalInt;

public class Pangram {

    public boolean checkIfPangram(String sentence) {
        int[] alphabets = new int[26];
        char[] sentenceInLowerCase = sentence.toLowerCase().toCharArray();

        for (char ch: sentenceInLowerCase) {
            if (Character.isLetter(ch)) {
                alphabets[ch - 'a']++;
            }
        }

        OptionalInt zeroPresent = Arrays.stream(alphabets).filter(value -> value == 0).findFirst();

        if (zeroPresent.isPresent()) {
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        var pangram = new Pangram();
        System.out.println(pangram.checkIfPangram("TheQuickBrownFoxJumpsOverTheLazyDog"));
        System.out.println(pangram.checkIfPangram("This is not a pangram"));
    }
}
