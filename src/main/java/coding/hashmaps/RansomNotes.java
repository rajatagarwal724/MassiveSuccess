package coding.hashmaps;

import java.util.HashMap;
import java.util.Map;

public class RansomNotes {

    public boolean canConstruct(String ransomNote, String magazine) {
        Map<Character, Integer> patternFreqMap = charFreqMap(ransomNote);
        int matched = 0;
        for (char elem: magazine.toCharArray()) {
            if (patternFreqMap.containsKey(elem)) {
                patternFreqMap.put(elem, patternFreqMap.get(elem) - 1);
                if (patternFreqMap.get(elem) == 0) {
                    matched++;
                }
            }

            if (matched == patternFreqMap.size()) {
                return true;
            }
        }
        return false;
    }

    private Map<Character, Integer> charFreqMap(String note) {
        Map<Character, Integer> charFreqMap = new HashMap<>();
        for (char ch: note.toCharArray()) {
            charFreqMap.put(ch, charFreqMap.getOrDefault(ch, 0) + 1);
        }
        return charFreqMap;
    }

    public static void main(String[] args) {

    }
}
