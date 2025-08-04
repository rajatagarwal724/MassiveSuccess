package companies.roku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TwentyFourGame {

    public boolean judgePoint24(int[] cards) {
        List<Double> list = Arrays.stream(cards).boxed().map(value -> (double) value).toList();
        return solve(list);
    }

    private boolean solve(List<Double> list) {
        if (list.size() == 1) {
            return Math.abs(list.get(0) - 24) <= 0.01;
        }

        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                double numA = list.get(i);
                double numB = list.get(j);
                List<Double> newList = new ArrayList<>();

                for (int k = 0; k < list.size(); k++) {
                    if (k != i && k != j) {
                        newList.add(list.get(k));
                    }
                }

                for (double newNum: generateAllCombinations(numA, numB)) {
                    newList.add(newNum);

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
        List<Double> combinations = new ArrayList<>();

        combinations.add(numA + numB);
        combinations.add(numA - numB);
        combinations.add(numB - numA);
        combinations.add(numA * numB);

        if (numB != 0) {
            combinations.add(numA / numB);
        }

        if (numA != 0) {
            combinations.add(numB / numA);
        }

        return combinations;
    }

    public static void main(String[] args) {
        var sol = new TwentyFourGame();
        System.out.println(sol.judgePoint24(new int[] {4,1,8,7}));
        System.out.println(sol.judgePoint24(new int[] {1,2,1,2}));
    }
}
