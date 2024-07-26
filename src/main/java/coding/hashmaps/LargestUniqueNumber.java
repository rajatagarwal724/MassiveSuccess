package coding.hashmaps;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class LargestUniqueNumber {

    public int largestUniqueNumber(int[] A) {
        Map<Integer, Integer> freqMap = new TreeMap<>(Comparator.reverseOrder());

        int maxUnique = -1;

        for (int elem: A) {
            freqMap.put(elem, freqMap.getOrDefault(elem, 0) + 1);
        }

        for (Map.Entry<Integer, Integer> entry: freqMap.entrySet()) {
            if (entry.getValue() == 1) {
                return entry.getKey();
            }
        }

        return maxUnique;
    }

    public int largestUniqueNumberWithoutTreeMap(int[] A) {
        Map<Integer, Integer> freqMap = new HashMap<>();

        int maxUnique = -1;

        for (int elem: A) {
            freqMap.put(elem, freqMap.getOrDefault(elem, 0) + 1);
        }

        for (Map.Entry<Integer, Integer> entry: freqMap.entrySet()) {
            if (entry.getValue() == 1) {
                maxUnique = Math.max(entry.getKey(), maxUnique);
            }
        }
        return maxUnique;
    }


    public static void main(String[] args) {
        var largestUniqueNumber = new LargestUniqueNumber();

        System.out.println(largestUniqueNumber.largestUniqueNumber(new int[] {5, 7, 3, 7, 5, 8}));
        System.out.println(largestUniqueNumber.largestUniqueNumber(new int[] {1, 2, 3, 2, 1, 4, 4}));
        System.out.println(largestUniqueNumber.largestUniqueNumber(new int[] {9, 9, 8, 8, 7, 7}));
        System.out.println("Without Tree Map");
        System.out.println(largestUniqueNumber.largestUniqueNumberWithoutTreeMap(new int[] {5, 7, 3, 7, 5, 8}));
        System.out.println(largestUniqueNumber.largestUniqueNumberWithoutTreeMap(new int[] {1, 2, 3, 2, 1, 4, 4}));
        System.out.println(largestUniqueNumber.largestUniqueNumberWithoutTreeMap(new int[] {9, 9, 8, 8, 7, 7}));
    }
}
