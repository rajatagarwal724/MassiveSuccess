package coding.Graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathExistsInGraph {
    private boolean[] visited;
    private Map<Integer, List<Integer>> adjacencyList;

    public boolean validPath(int n, int[][] edges, int start, int end) {
        visited = new boolean[n];
        adjacencyList = new HashMap<>();

        for (int vertex = 0; vertex < n; vertex++) {
            adjacencyList.put(vertex, new ArrayList<>());
        }

        for (int[] edge: edges) {
            adjacencyList.get(edge[0]).add(edge[1]);
            adjacencyList.get(edge[1]).add(edge[0]);
        }

        dfs(visited, adjacencyList, start, end);

        return false;
    }

    private boolean dfs(boolean[] visited, Map<Integer, List<Integer>> adjacencyList, int node, int end) {
        if (node == end) {
            return true;
        }

        visited[node] = true;

        List<Integer> neighbours = adjacencyList.get(node);
        for (int neighbour: neighbours) {
            if (!visited[neighbour] && dfs(visited, adjacencyList, neighbour, end)) {
                return true;
            }
        }
        return false;
    }


    public static void main(String[] args) {

    }
}
