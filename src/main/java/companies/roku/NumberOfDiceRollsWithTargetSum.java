package companies.roku;

public class NumberOfDiceRollsWithTargetSum {

    public int numRollsToTarget(int n, int k, int target) {
        return recurse(0, n, 0, k, target);
    }

    private int recurse(int diceIndex, int n, int currSum, int k, int target) {
        if (diceIndex == n) {
            return currSum == target ? 1 : 0;
        }

        if (currSum > target) {
            return 0;
        }

        int ways = 0;

        for (int i = 1; i <= Math.min(k, target - currSum); i++) {
            ways = ways + recurse(diceIndex + 1, n, currSum + i, k, target);
        }

        return ways;
    }

    public static void main(String[] args) {
        var sol = new NumberOfDiceRollsWithTargetSum();

        System.out.println(
                sol.numRollsToTarget(1, 6, 3)
        );

        System.out.println(
                sol.numRollsToTarget(2, 6, 7)
        );
    }
}
