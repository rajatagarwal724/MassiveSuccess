package coding.top150;

import java.util.HashMap;
import java.util.Map;

public class RansomNote {
    public boolean canConstruct(String ransomNote, String magazine) {
        Map<Character, Integer> ransomNoteMap = new HashMap<>();
        Map<Character, Integer> magazineMap = new HashMap<>();

        for (char ch: ransomNote.toCharArray()) {
            ransomNoteMap.put(ch, ransomNoteMap.getOrDefault(ch, 0) + 1);
        }

        for (char ch: magazine.toCharArray()) {
            magazineMap.put(ch, magazineMap.getOrDefault(ch, 0) + 1);
        }

        for (Map.Entry<Character, Integer> entry: ransomNoteMap.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();

            if (!magazineMap.containsKey(key) || magazineMap.get(key) < value) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {

    }
}
