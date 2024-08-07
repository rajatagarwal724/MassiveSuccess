package coding.Graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class BfsGraph {
    private int vertices;
    private Map<Integer, List<Integer>> adjacencyList;

    public BfsGraph(int vertices) {
        this.vertices = vertices;
        this.adjacencyList = new HashMap<>();
        for (int vertex = 0; vertex < vertices; vertex++) {
            this.adjacencyList.put(vertex, new ArrayList<>());
        }
    }

    public void addEdge(int v1, int v2) {
        this.adjacencyList.get(v1).add(v2);
        this.adjacencyList.get(v2).add(v1);
    }

    public void bfs(int start) {
        boolean[] visited = new boolean[vertices];
        Queue<Integer> queue = new LinkedList<>();
        visited[start] = true;
        queue.offer(start);

        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            System.out.print(vertex + " -> ");
            List<Integer> neighbors = adjacencyList.get(vertex);

            for (int neighbor: neighbors) {
                if (!visited[neighbor]) {
                    queue.offer(neighbor);
                    visited[neighbor] = true;
                }
            }
        }
    }

    public static void main(String[] args) {
        var graph = new BfsGraph(6);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);
        graph.addEdge(3, 5);

        graph.bfs(0);
    }
}
