package coding.linkedin;

public class PaintHouse {

    public int minCost(int[][] costs) {

        int costRed = 0, costBlue = 0, costGreen = 0;

        for (int[] cost: costs) {
            int prevCostRed = costRed, prevCostBlue = costBlue, prevCostGreen = costGreen;

            costRed = cost[0] + Math.min(prevCostBlue, prevCostGreen);
            costBlue = cost[1] + Math.min(prevCostRed, prevCostGreen);
            costGreen = cost[2] + Math.min(prevCostBlue, prevCostRed);
        }

        return Math.min(costRed, Math.min(costBlue, costGreen));
    }

    public static void main(String[] args) {
        var sol = new PaintHouse();
        System.out.println(
                sol.minCost(
                        new int[][]{
                                {17,2,17},
                                {16,16,5},
                                {14,3,19}
                        }
                )
        );

        System.out.println(
                sol.minCost(
                        new int[][]{
                                {7,6,2}
                        }
                )
        );
    }
}
