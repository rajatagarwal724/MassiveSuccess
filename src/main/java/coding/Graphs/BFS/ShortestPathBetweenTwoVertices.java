package coding.Graphs.BFS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

public class ShortestPathBetweenTwoVertices {

    static class Node {
        String vertex;
        String path;

        public Node(String vertex, String path) {
            this.vertex = vertex;
            this.path = path;
        }
    }

    public static void main(String[] args) {
        String[][] graph = new String[][] {
                {"A", "B"},
                {"A", "C"},
                {"A", "D"},
                {"C", "E"},
                {"D", "E"},
                {"B", "E"},
                {"B", "F"},
                {"E", "F"}
        };

        System.out.println(shortestPath(graph, 6, "A", "F"));
    }

    private static String shortestPath(String[][] graph, int vertices, String source, String destination) {
        Map<String, List<String>> adjList = new HashMap<>();
        Set<String> visited = new HashSet<>();
        for (String[] edge: graph) {
            adjList.computeIfAbsent(edge[0], s -> new ArrayList<>()).add(edge[1]);
        }

        Queue<Node> queue = new LinkedList<>();
        queue.offer(new Node(source, source));
        visited.add(source);

        while (!queue.isEmpty()) {
            var currentNode = queue.poll();

            if (Objects.equals(currentNode.vertex, destination)) {
                return currentNode.path;
            }

            List<String> neighbours = adjList.getOrDefault(currentNode.vertex, new ArrayList<>());
            for (String neighbour: neighbours) {
                if (!visited.contains(neighbour)) {
                    queue.offer(new Node(neighbour, currentNode.path + neighbour));
                }
            }
        }

        return null;
    }
}
