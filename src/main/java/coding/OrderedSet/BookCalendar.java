package coding.OrderedSet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class BookCalendar {
    public static List<Boolean> book(int[][] nums) {
        List<Boolean> results = new ArrayList<>();

        TreeSet<int[]> orderedInterval = new TreeSet<>(Comparator.comparingInt(o -> o[0]));

        for (int[] interval: nums) {
            int start = interval[0];
            int end = interval[1];

            int[] event = new int[] {start, end};

            // Find the closest event that starts before this one
            int[] lower = orderedInterval.lower(event);

            // Find the closest event that starts after this one
            int[] higher = orderedInterval.higher(event);

            if ((lower == null || lower[1] <= start) && (higher == null || higher[0] >= end)) {
                orderedInterval.add(event);
                results.add(true);
            } else {
                results.add(false);
            }
        }
        return results;
    }

    public static void main(String[] args) {
        book(new int[][]{ {10, 20}, {15, 25}, {20, 30} }).forEach(System.out::println);
        System.out.println("###################");
        book(new int[][]{ {5, 10}, {10, 15}, {5, 15} }).forEach(System.out::println);
        System.out.println("###################");
        book(new int[][]{ {8, 13}, {13, 17}, {17, 20} }).forEach(System.out::println);
        System.out.println("###################");
    }
}
