package companies.splunk;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PrintMatrixInSpiral {

    public List<Integer> spiralOrder(int[][] matrix) {
        boolean[][] visited = new boolean[matrix.length][matrix[0].length];
        List<Integer> result = new ArrayList<>();
        int[] drx = new int[] {0, 1, 0, -1};
        int[] dry = new int[] {1, 0, -1, 0};
        int rows = matrix.length;
        int cols = matrix[0].length;
        int currentDirIdx = 0;
        int r = 0, c = 0;

        for (int i = 0; i < rows * cols; i++) {
            result.add(matrix[r][c]);
            visited[r][c] = true;

            int newRow = r + drx[currentDirIdx];
            int newCol = c + dry[currentDirIdx];

            if (0 <= newRow && newRow < rows && 0 <= newCol && newCol < cols && !visited[newRow][newCol]) {
                r = newRow;
                c = newCol;
            } else {
                currentDirIdx = (currentDirIdx + 1) % 4;
                r = r + drx[currentDirIdx];
                c = c + dry[currentDirIdx];
            }
        }
        return result;
    }

    public static void main(String[] args) {
        var sol = new PrintMatrixInSpiral();
        var res = sol.spiralOrder(new int[][]{
                {1,2,3},
                {4,5,6},
                {7,8,9}
        });
        System.out.println(StringUtils.joinWith(",", res));
        res = sol.spiralOrder(new int[][]{
                {1,2,3,4},
                {5,6,7,8},
                {9,10,11,12}
        });
        System.out.println(StringUtils.joinWith(",", res));
    }
}
