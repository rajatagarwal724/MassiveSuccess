package coding.top75.arrays;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HIndex {
    public int hIndex(int[] citations) {
        List<Integer> reversedArray = Arrays.stream(citations).boxed().sorted(Comparator.reverseOrder()).toList();
        int h = 0;
        for (int i = 0; i < reversedArray.size(); i++) {
            if (reversedArray.get(i) >= i + 1) {
                h = i + 1;
            } else {
                break;
            }
        }
        return h;
    }

    public int hIndexSort(int[] citations) {
        int n = citations.length;
        int[] count = new int[n + 1];

        for (int citation: citations) {
            count[Math.min(citation, n)]++;
        }

        int papers = 0;

        for (int i = n; i >= 0; i--) {
            papers+=count[i];
            if (papers >= i) {
                return i;
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        var sol = new HIndex();
//        System.out.println(sol.hIndex(new int[] {4, 3, 0, 1, 5}));
        System.out.println(sol.hIndexSort(new int[] {10, 8, 5, 4, 3, 7, 2, 1}));
//        System.out.println(sol.hIndex(new int[] {0, 1, 2, 3, 4}));
    }
}
