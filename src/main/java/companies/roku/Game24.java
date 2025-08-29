package companies.roku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Game24 {

    public boolean judgePoint24(int[] cards) {
        List<Double> list = Arrays.stream(cards).boxed().map(elem -> (double) elem).collect(Collectors.toList());
        return solve(list);
    }

    private boolean solve(List<Double> cards) {
        if (cards.size() == 1) {
            return Math.abs(cards.get(0) - 24) <= 0.01;
        }

        for (int i = 0; i < cards.size() - 1; i++) {
            for (int j = i + 1; j < cards.size(); j++) {
                double numA = cards.get(i);
                double numB = cards.get(j);
                List<Double> newList = new ArrayList<>();
                for (int k = 0; k < cards.size(); k++) {
                    if(k != i && k != j) {
                        newList.add(cards.get(k));
                    }
                }

                for (var permutation: generateAllCombinations(numA, numB)) {
                    newList.add(permutation);
                    if (solve(newList)) {
                        return true;
                    }
                    newList.remove(newList.size() - 1);
                }
            }
        }
        return false;
    }

    private List<Double> generateAllCombinations(double numA, double numB) {
        List<Double> permutations = new ArrayList<>();
        permutations.add(numA + numB);
        permutations.add(numA - numB);
        permutations.add(numB - numA);
        permutations.add(numA * numB);

        if (numB != 0) {
            permutations.add(numA/numB);
        }

        if (numA != 0) {
            permutations.add(numB/numA);
        }

        return permutations;
    }

    public static void main(String[] args) {
        var sol = new Game24();
        System.out.println(sol.judgePoint24(new int[] {4,1,8,7}));
        System.out.println(sol.judgePoint24(new int[] {1,2,1,2}));
    }
}
