package coding.hashmaps;

import java.util.HashMap;
import java.util.Map;

public class RansomNote {

    public boolean canConstruct(String ransomNote, String magazine) {
        Map<Character, Integer> ransomFreqMap = charFrequencyMap(ransomNote);
        Map<Character, Integer> availableCharsFreqMap = charFrequencyMap(magazine);

        for (Map.Entry<Character, Integer> entry: ransomFreqMap.entrySet()) {
            Character elem = entry.getKey();
            Integer count = entry.getValue();

            if (!availableCharsFreqMap.containsKey(elem) || count > availableCharsFreqMap.get(elem)) {
                return false;
            }
            entry.setValue(0);
            availableCharsFreqMap.put(elem, availableCharsFreqMap.get(elem) - count);
            if (availableCharsFreqMap.get(elem) <= 0) {
                availableCharsFreqMap.remove(elem);
            }
        }
        return ransomFreqMap.entrySet().stream().noneMatch(entry -> entry.getValue() == 1);
    }

    private Map<Character, Integer> charFrequencyMap(String input) {
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char elem: input.toLowerCase().toCharArray()) {
            freqMap.put(elem, freqMap.getOrDefault(elem, 0) + 1);
        }
        return freqMap;
    }

    public static void main(String[] args) {
        var solution = new RansomNote();
        System.out.println(solution.canConstruct("hello", "hellworld"));
        System.out.println(solution.canConstruct("notes", "stoned"));
        System.out.println(solution.canConstruct("apple", "pale"));
    }
}
