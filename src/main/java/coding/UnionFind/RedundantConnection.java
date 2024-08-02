package coding.UnionFind;

import java.util.Arrays;

public class RedundantConnection {

    int[] parent;

    public int[] findRedundantConnection(int[][] edges) {
        parent = new int[edges.length + 1];

        for (int i = 0; i < parent.length; i++) {
            parent[i] = i;
        }

        for (int[] edge: edges) {
            int node1 = edge[0];
            int node2 = edge[1];

            if (find(node1) == find(node2)) {
                return edge;
            }

            union(node1, node2);
        }

        return new int[2];
    }

    private int find(int node) {
        if (node != parent[node]) {
            parent[node] = find(parent[node]);
        }
        return parent[node];
    }

    private void union(int node1, int node2) {
        parent[find(node1)] = find(node2);
    }


    public static void main(String[] args) {
        var sol = new RedundantConnection();
        System.out.println(
                Arrays.toString(sol.findRedundantConnection(
                        new int[][]{{1, 2}, {1, 3}, {3, 4}, {1, 4}, {1, 5}}
                ))
        );

        System.out.println(
                Arrays.toString(sol.findRedundantConnection(
                        new int[][]{{1, 2}, {2, 3}, {3, 1}, {3, 4} }
                ))
        );

        System.out.println(
                Arrays.toString(sol.findRedundantConnection(
                        new int[][]{{1, 2}, {2, 3}, {3, 4}, {4, 2}, {5, 6} }
                ))
        );
    }
}
