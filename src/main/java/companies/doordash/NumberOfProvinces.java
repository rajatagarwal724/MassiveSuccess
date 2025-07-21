package companies.doordash;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Time Complexity is O(N2) N Square
 * We iterate over all possible edges using isConnected[node] which takes O(N) time
 * for each visited node resulting in O(n2) or N Squared
 */
public class NumberOfProvinces {

    public int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        boolean[] visited = new boolean[n];
        int noOfComponents = 0;

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                bfs(i, visited, isConnected);
                noOfComponents++;
            }
        }

        return noOfComponents;
    }

    private void bfs(int index, boolean[] visited, int[][] isConnected) {
        visited[index] = true;
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(index);

        while (!queue.isEmpty()) {
            int idx = queue.poll();

            int[] connecteComponents = isConnected[idx];
            for (int i = 0; i < connecteComponents.length; i++) {
                if (!visited[i] && idx != i && connecteComponents[i] == 1) {
                    visited[i] = true;
                    queue.offer(i);
                }
            }
        }
    }

    public static void main(String[] args) {
        var sol = new NumberOfProvinces();
        System.out.println(
                sol.findCircleNum(
                        new int[][] {
                                {1,1,0},
                                {1,1,0},
                                {0,0,1}
                        }
                )
        );

        System.out.println(
                sol.findCircleNum(
                        new int[][] {
                                {1,0,0},
                                {0,1,0},
                                {0,0,1}
                        }
                )
        );
    }

}
