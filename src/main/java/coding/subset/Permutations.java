package coding.subset;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Permutations {

    public List<List<Integer>> findPermutationsRecursive(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        generatePermutationsRecursively(nums, 0, new LinkedList<>(), result);
        return result;
    }

    private void generatePermutationsRecursively(int[] nums, int index, List<Integer> currentPermutation, List<List<Integer>> result) {
        if (index == nums.length) {
            result.add(new ArrayList<>(currentPermutation));
        } else {
            for (int i = 0; i <= currentPermutation.size(); i++) {
                List<Integer> newPermutation = new LinkedList<>(currentPermutation);
                newPermutation.add(i, nums[index]);
                generatePermutationsRecursively(nums, index + 1, newPermutation, result);
            }
        }
    }

    public List<List<Integer>> findPermutations(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Queue<List<Integer>> permutations = new LinkedList<>();
        permutations.add(new LinkedList<>());
        for (int num: nums) {
            int size = permutations.size();
            for (int i = 0; i < size; i++) {
                List<Integer> permutation = permutations.poll();
                int oldPermSize = permutation.size();
                for (int j = 0; j <= oldPermSize; j++) {
                    List<Integer> newPermutation = new LinkedList<>(permutation);
                    newPermutation.add(j, num);
                    if (newPermutation.size() == nums.length) {
                        result.add(newPermutation);
                    } else {
                        permutations.offer(newPermutation);
                    }
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        var sol = new Permutations();
        List<List<Integer>> result = sol.findPermutations(new int[] { 1, 3, 5 });
        System.out.println("Here are all the permutations: " + result);

        List<List<Integer>> result_rec = sol.findPermutationsRecursive(new int[] { 1, 3, 5 });
        System.out.println("Here are all the permutations: " + result_rec);
    }
}
