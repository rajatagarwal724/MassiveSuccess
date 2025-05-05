package coding.Graphs.ShortestPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Dijiktra {

    static class Node {
        int vertex;
        int distance;

        public Node(int vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }


    public static void main(String[] args) {
        int V = 6; // number of vertices
        int S = 0; // source vertex


        Map<Integer, List<Node>> adjacencyGraph = new HashMap<>();

        // Adjacency list representation of the graph
        for (int i = 0; i < V; i++) {
            adjacencyGraph.put(i, new ArrayList<>());
        }

        adjacencyGraph.get(0).add(new Node(1, 1));
        adjacencyGraph.get(0).add(new Node(2, 4));
        adjacencyGraph.get(1).add(new Node(2, 2));
        adjacencyGraph.get(1).add(new Node(3, 6));
        adjacencyGraph.get(2).add(new Node(3, 3));
        adjacencyGraph.get(3).add(new Node(4, 1));
        adjacencyGraph.get(4).add(new Node(5, 2));

        var sol = new Dijiktra();
        int[] dist = sol.dijkstra(V, adjacencyGraph, S);

        System.out.println("Shortest distances from source " + S + " are:");
        for (int i = 0; i < V; i++) {
            System.out.println("Vertex " + i + " : " + dist[i]);
        }
    }

    private int[] dijkstra(int v, Map<Integer, List<Node>> adj, int s) {
        int[] dist = new int[v];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[0] = 0;
        Queue<Node> queue = new PriorityQueue<>((o1, o2) -> {
            if (o1.vertex == o2.vertex) {
                return o1.distance - o2.distance;
            }
            return o1.vertex - o2.vertex;
        });

        queue.offer(new Node(s, 0));

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            int currentVertex = current.vertex;
            int distance = current.distance;

            List<Node> neighbours = adj.get(currentVertex);
            for (Node neighbour: neighbours) {
                int distanceToNeighbourViaCurrentVertex = distance + neighbour.distance;
                if (dist[neighbour.vertex] > distanceToNeighbourViaCurrentVertex) {
                    dist[neighbour.vertex] = distanceToNeighbourViaCurrentVertex;
                    queue.offer(new Node(neighbour.vertex, distanceToNeighbourViaCurrentVertex));
                }
            }
        }
        return dist;
    }
}
