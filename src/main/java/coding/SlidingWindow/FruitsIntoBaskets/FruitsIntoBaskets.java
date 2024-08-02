package coding.SlidingWindow.FruitsIntoBaskets;

import java.util.HashMap;
import java.util.Map;

public class FruitsIntoBaskets {

    public int findLength(char[] arr) {
        int maxLength = 0;
        int windowStart = 0;
        Map<Character, Integer> charFreqMap = new HashMap<>();

        for (int windowEnd = 0; windowEnd < arr.length; windowEnd++) {
            char currentFruit = arr[windowEnd];
            charFreqMap.put(currentFruit, charFreqMap.getOrDefault(currentFruit, 0) + 1);

            while (charFreqMap.size() > 2) {
                char fruitInTheBasket = arr[windowStart];
                charFreqMap.put(fruitInTheBasket, charFreqMap.get(fruitInTheBasket) - 1);
                if (charFreqMap.get(fruitInTheBasket) == 0) {
                    charFreqMap.remove(fruitInTheBasket);
                }
                windowStart++;
            }
            maxLength = Math.max(maxLength, windowEnd - windowStart + 1);
        }

        return maxLength;
    }

    public static void main(String[] args) {
        var sol = new FruitsIntoBaskets();
        System.out.println(sol.findLength(new char[] {'A', 'B', 'C', 'A', 'C'}));
        System.out.println(sol.findLength(new char[] {'A', 'B', 'C', 'B', 'B', 'C'}));
    }
}
