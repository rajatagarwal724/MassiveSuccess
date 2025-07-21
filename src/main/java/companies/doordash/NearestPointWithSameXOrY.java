package companies.doordash;

public class NearestPointWithSameXOrY {

    public int nearestValidPoint(int x, int y, int[][] points) {
        int res = -1;

        int nearestDistance = Integer.MAX_VALUE;

        for (int i = 0; i < points.length; i++) {
            int[] point = points[i];

            if (point[0] == x || point[1] == y) {
                int distance = Math.abs(x - point[0]) + Math.abs(y - point[1]);
                if (distance < nearestDistance) {
                    res = i;
                    nearestDistance = distance;
                }
            }
        }

        return res;
    }

    public static void main(String[] args) {
        var sol = new NearestPointWithSameXOrY();
        System.out.println(
                sol.nearestValidPoint(
                        3, 4, new int[][]{
                                {1, 2},
                                {3, 1},
                                {2, 4},
                                {2, 3},
                                {4, 4}
                        }
                )
        );

        System.out.println(
                sol.nearestValidPoint(
                        3, 4, new int[][]{
                                {3, 4}
                        }
                )
        );
    }
}
