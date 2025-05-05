package coding.subset;

import java.util.ArrayList;
import java.util.List;

public class Subset {

    private List<List<Integer>> findSubsets(int[] arr) {
        List<List<Integer>> subsets = new ArrayList<>();
        subsets.add(new ArrayList<>());
        for (int num: arr) {
            int size = subsets.size();
            for (int i = 0; i < size; i++) {
                List<Integer> subset = new ArrayList<>(subsets.get(i));
                subset.add(num);
                subsets.add(subset);
            }
        }
        return subsets;
    }

    public static void main(String[] args) {
        var sol = new Subset();
        List<List<Integer>> result = sol.findSubsets(new int[] { 1, 3 });
        System.out.println("Here is the list of subsets: " + result);

        result = sol.findSubsets(new int[] { 1, 5, 3 });
        System.out.println("Here is the list of subsets: " + result);
    }
}
