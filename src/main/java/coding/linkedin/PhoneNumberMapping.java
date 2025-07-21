package coding.linkedin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneNumberMapping {

    private static final String[] KNOWN_WORDS = new String[] {"careers", "linkedin", "hiring", "interview", "linkedgo"};
    
    // Map to store the digit corresponding to each letter
    private static final Map<Character, Character> letterToDigitMap = new HashMap<>();
    
    static {
        // Initialize the mapping from letters to digits
        populateLetterToDigitMap();
    }
    
    /**
     * Populates the map with the standard phone keypad mapping
     */
    private static void populateLetterToDigitMap() {
        // Digit 2: a, b, c
        letterToDigitMap.put('a', '2');
        letterToDigitMap.put('b', '2');
        letterToDigitMap.put('c', '2');
        
        // Digit 3: d, e, f
        letterToDigitMap.put('d', '3');
        letterToDigitMap.put('e', '3');
        letterToDigitMap.put('f', '3');
        
        // Digit 4: g, h, i
        letterToDigitMap.put('g', '4');
        letterToDigitMap.put('h', '4');
        letterToDigitMap.put('i', '4');
        
        // Digit 5: j, k, l
        letterToDigitMap.put('j', '5');
        letterToDigitMap.put('k', '5');
        letterToDigitMap.put('l', '5');
        
        // Digit 6: m, n, o
        letterToDigitMap.put('m', '6');
        letterToDigitMap.put('n', '6');
        letterToDigitMap.put('o', '6');
        
        // Digit 7: p, q, r, s
        letterToDigitMap.put('p', '7');
        letterToDigitMap.put('q', '7');
        letterToDigitMap.put('r', '7');
        letterToDigitMap.put('s', '7');
        
        // Digit 8: t, u, v
        letterToDigitMap.put('t', '8');
        letterToDigitMap.put('u', '8');
        letterToDigitMap.put('v', '8');
        
        // Digit 9: w, x, y, z
        letterToDigitMap.put('w', '9');
        letterToDigitMap.put('x', '9');
        letterToDigitMap.put('y', '9');
        letterToDigitMap.put('z', '9');
    }
    
    /**
     * Converts a word to its digit representation based on phone keypad
     * @param word The word to convert
     * @return The digit representation of the word
     */
    private static String wordToDigits(String word) {
        StringBuilder digits = new StringBuilder();
        for (char c : word.toLowerCase().toCharArray()) {
            if (letterToDigitMap.containsKey(c)) {
                digits.append(letterToDigitMap.get(c));
            }
        }
        return digits.toString();
    }
    
    /**
     * Finds all known words that can be formed from the phone number
     * @param phoneNumber The phone number to check
     * @return List of matching words
     */
    public static List<String> findMatchingWords(String phoneNumber) {
        List<String> matches = new ArrayList<>();
        
        for (String word : KNOWN_WORDS) {
            String digitRepresentation = wordToDigits(word);
            if (digitRepresentation.equals(phoneNumber)) {
                matches.add(word);
            }
        }
        
        return matches;
    }

    public static void main(String[] args) {
        // Example 1
        String phoneNumber1 = "2273377";
        List<String> matches1 = findMatchingWords(phoneNumber1);
        System.out.println("Phone number: " + phoneNumber1);
        System.out.println("Matching words: " + matches1);
        
        // Example 2
        String phoneNumber2 = "54653346";
        List<String> matches2 = findMatchingWords(phoneNumber2);
        System.out.println("Phone number: " + phoneNumber2);
        System.out.println("Matching words: " + matches2);
    }
}
