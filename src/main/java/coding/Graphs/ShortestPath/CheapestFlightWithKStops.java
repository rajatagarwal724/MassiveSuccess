package coding.Graphs.ShortestPath;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class CheapestFlightWithKStops {

    static class Flight {
        int city, cost, stops;

        public Flight(int city, int cost, int stops) {
            this.city = city;
            this.cost = cost;
            this.stops = stops;
        }
    }

    static class Node {
        int vertex, dist;

        public Node(int vertex, int dist) {
            this.vertex = vertex;
            this.dist = dist;
        }
    }

    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        Map<Integer, List<Node>> adj = new HashMap<>();
        for (int i = 0; i < n; i++) {
            adj.put(i, new ArrayList<>());
        }
        for (int i = 0; i < flights.length; i++) {
            int[] arr = flights[i];
            adj.get(arr[0]).add(new Node(arr[1], arr[2]));
        }

        Queue<Flight> minHeap = new PriorityQueue<>(Comparator.comparingInt(value -> value.cost));
        minHeap.offer(new Flight(src, 0, 0));

        while (!minHeap.isEmpty()) {
            Flight currentStop = minHeap.poll();
            int currentCity = currentStop.city;
            int currentCost = currentStop.cost;
            int currentStops = currentStop.stops;

            if (currentCity == dst && currentStops <= (k+1)) {
                return currentCost;
            }

            if (currentStops > k) {
                continue;
            }

            List<Node> neighbours = adj.get(currentCity);
            for (Node neighbour: neighbours) {
                minHeap.offer(new Flight(neighbour.vertex, currentCost + neighbour.dist, currentStops + 1));
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        var sol = new CheapestFlightWithKStops();

        // Example 1
        int n1 = 5;
        int[][] flights1 = {{0, 1, 50}, {1, 2, 50}, {2, 3, 50}, {3, 4, 50}, {0, 4, 300}};
        int src1 = 1;
        int dst1 = 4;
        int k1 = 2;
        System.out.println(sol.findCheapestPrice(n1, flights1, src1, dst1, k1)); // Expected Output: 150

        // Example 2
        int n2 = 4;
        int[][] flights2 = {{0, 1, 100}, {1, 2, 200}, {2, 3, 300}, {0, 3, 500}};
        int src2 = 0;
        int dst2 = 3;
        int k2 = 1;
        System.out.println(sol.findCheapestPrice(n2, flights2, src2, dst2, k2)); // Expected Output: 500

        // Example 3
        int n3 = 3;
        int[][] flights3 = {{0, 1, 100}, {1, 2, 100}, {0, 2, 500}};
        int src3 = 0;
        int dst3 = 2;
        int k3 = 1;
        System.out.println(sol.findCheapestPrice(n3, flights3, src3, dst3, k3)); // Expected Output: 200
    }
}
