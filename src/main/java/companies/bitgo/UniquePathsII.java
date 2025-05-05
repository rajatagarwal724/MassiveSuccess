package companies.bitgo;

public class UniquePathsII {

    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        if (obstacleGrid[0][0] == 1) {
            return 0;
        }
        obstacleGrid[0][0] = 1;
        int rows = obstacleGrid.length;
        int cols = obstacleGrid[0].length;

        for (int col = 1; col < cols; col++) {
            if (obstacleGrid[0][col] == 1) {
                obstacleGrid[0][col] = 0;
            } else {
                obstacleGrid[0][col] = obstacleGrid[0][col - 1];
            }
        }

        for (int row = 1; row < rows; row++) {
            if (obstacleGrid[row][0] == 1) {
                obstacleGrid[row][0] = 0;
            } else {
                obstacleGrid[row][0] = obstacleGrid[row - 1][0];
            }
        }

        for (int row = 1; row < rows; row++) {
            for (int col = 1; col < cols; col++) {
                if (obstacleGrid[row][col] == 1) {
                    obstacleGrid[row][col] = 0;
                } else {
                    obstacleGrid[row][col] = obstacleGrid[row - 1][col] + obstacleGrid[row][col - 1];
                }
            }
        }

        return obstacleGrid[rows - 1][cols - 1];
    }

    public static void main(String[] args) {
        var sol = new UniquePathsII();
        System.out.println(sol.uniquePathsWithObstacles(new int[][]{
                {0,0,0},
                {0,1,0},
                {0,0,0}
        }));

        System.out.println(sol.uniquePathsWithObstacles(new int[][]{
                {0,1},
                {0,0}
        }));
    }
}
