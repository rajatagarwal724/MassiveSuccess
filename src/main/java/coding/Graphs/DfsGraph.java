package coding.Graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class DfsGraph {

    private int vertices;
    private Map<Integer, List<Integer>> adjacencyList;

    public DfsGraph(int vertices) {
        this.vertices = vertices;
        this.adjacencyList = new HashMap<>();
        for (int vertex = 0; vertex < vertices; vertex++) {
            this.adjacencyList.put(vertex, new ArrayList<>());
        }
    }

    public void addEdge(int v1, int v2) {
        adjacencyList.get(v1).add(v2);
        adjacencyList.get(v2).add(v1);
    }

    public void dfs(int start) {
        boolean[] visited = new boolean[vertices];
        visited[start] = true;

        Stack<Integer> stack = new Stack<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            int pop = stack.pop();
            System.out.print(pop + " -> ");
            List<Integer> neighbors = adjacencyList.get(pop);
            for (int neighbor: neighbors) {
                if (!visited[neighbor]) {
                    stack.push(neighbor);
                    visited[neighbor] = true;
                }
            }
        }
    }

    public static void main(String[] args) {
        var sol = new DfsGraph(7);
        sol.addEdge(0, 1);
        sol.addEdge(0, 2);
        sol.addEdge(1, 3);
        sol.addEdge(1, 4);
        sol.addEdge(2, 5);
        sol.addEdge(2, 6);

        sol.dfs(0);
    }
}
