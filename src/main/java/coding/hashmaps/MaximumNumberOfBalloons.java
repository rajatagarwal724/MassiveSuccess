package coding.hashmaps;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MaximumNumberOfBalloons {

    public int maxNumberOfBalloons(String text) {
        int minCount = 0;
        char[] balloonArray = "balloon".toCharArray();
        Map<Character, Integer> baloonCharsFreqMap = new HashMap<>();
        for (char elem: balloonArray) {
            baloonCharsFreqMap.put(elem, baloonCharsFreqMap.getOrDefault(elem, 0) + 1);
        }
        Set<Character> balloonSet = Set.of('b','a','l','o','n');

        char[] input = text.toLowerCase().toCharArray();
        Map<Character, Integer> inputFreqMap = new HashMap<>();

        for (char elem: input) {
            if (balloonSet.contains(elem)) {
                inputFreqMap.put(elem, inputFreqMap.getOrDefault(elem, 0) + 1);
            }
        }

        while (inputFreqMap.size() == baloonCharsFreqMap.size()) {
            for (Character elem: balloonSet) {
                if (!inputFreqMap.containsKey(elem) || inputFreqMap.get(elem) < baloonCharsFreqMap.get(elem)) {
                    return minCount;
                }
                inputFreqMap.put(elem, inputFreqMap.get(elem) - baloonCharsFreqMap.get(elem));
                if (inputFreqMap.get(elem) <= 0) {
                    inputFreqMap.remove(elem);
                }
            }
            minCount++;
        }
        return minCount;
    }

    public static void main(String[] args) {
        var solution = new MaximumNumberOfBalloons();
        System.out.println(solution.maxNumberOfBalloons("balloonballoon"));
        System.out.println(solution.maxNumberOfBalloons("bbaall"));
        System.out.println(solution.maxNumberOfBalloons("balloonballoooon"));

    }
}
