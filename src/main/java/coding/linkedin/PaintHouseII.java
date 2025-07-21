package coding.linkedin;

import java.util.Arrays;

public class PaintHouseII {

    public int minCostII(int[][] costs) {
        int houses = costs.length;
        int numOfColours = costs[0].length;

        int[] prevHouseCosts = costs[0].clone();

        for (int currHouse = 1; currHouse < houses; currHouse++) {
            int[] currHouseCosts = costs[currHouse].clone();

            int min1Cost = Integer.MAX_VALUE, min2Cost = Integer.MAX_VALUE, min1Idx = -1;

            for (int prevCostsIdx = 0; prevCostsIdx < numOfColours; prevCostsIdx++) {
                var cost = prevHouseCosts[prevCostsIdx];
                if (cost < min1Cost) {
                    min2Cost = min1Cost;
                    min1Cost = cost;
                    min1Idx = prevCostsIdx;
                } else if (cost < min2Cost){
                    min2Cost = cost;
                }
            }

            for (int currCostIdx = 0; currCostIdx < numOfColours; currCostIdx++) {
                if (currCostIdx == min1Idx) {
                    currHouseCosts[currCostIdx] += min2Cost;
                } else {
                    currHouseCosts[currCostIdx] += min1Cost;
                }
            }
            prevHouseCosts = currHouseCosts;
        }

        return Arrays.stream(prevHouseCosts).min().getAsInt();
    }

    public static void main(String[] args) {
        var sol = new PaintHouseII();
        System.out.println(
                sol.minCostII(
                        new int[][]{
                                {1, 5, 3},
                                {2, 9, 4}
                        }
                )
        );

        System.out.println(
                sol.minCostII(
                        new int[][]{
                                {1, 3},
                                {2, 4}
                        }
                )
        );
    }
}
