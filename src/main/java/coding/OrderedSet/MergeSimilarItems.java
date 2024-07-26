package coding.OrderedSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MergeSimilarItems {

    public List<List<Integer>> mergeSimilarItems(int[][] items1, int[][] items2) {
        List<List<Integer>> result = new ArrayList<>();

        Map<Integer, Integer> mergedItems = new TreeMap<>();

        for (int[] item: items1) {
            mergedItems.put(item[0], mergedItems.getOrDefault(item[0], 0) + item[1]);
        }

        for (int[] item: items2) {
            mergedItems.put(item[0], mergedItems.getOrDefault(item[0], 0) + item[1]);
        }

        mergedItems.forEach((item, weight) -> result.add(List.of(item, weight)));

        return result;
    }

    public static void main(String[] args) {
        var solution = new MergeSimilarItems();
        solution.mergeSimilarItems(new int[][]{{1,2}, {4,3}}, new int[][]{{2,1}, {4,3}, {3,4}})
                .forEach(System.out::println);
    }
}
