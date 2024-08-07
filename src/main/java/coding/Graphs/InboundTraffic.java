package coding.Graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InboundTraffic {

    static class Node {
        int population;
        List<Integer> neighbors;

        Node(int population) {
            this.population = population;
            this.neighbors = new ArrayList<>();
        }
    }

    private Map<Integer, Node> buildGraph(String[] cities) {
        Map<Integer, Node> graph = new HashMap<>();
        for (String cityStr : cities) {
            String[] parts = cityStr.split(":");
            int cityNum = Integer.parseInt(parts[0]);
            Node node = new Node(cityNum);
            graph.put(cityNum, node);

            if (!parts[1].isEmpty()) {
                String[] neighborsStr = parts[1].substring(1, parts[1].length() - 1).split(",");
                for (String neighborStr : neighborsStr) {
                    node.neighbors.add(Integer.parseInt(neighborStr));
                }
            }
        }
        return graph;
    }

    private void maxTraffic(Map<Integer, Node> graph) {
        Map<Integer, Map<Integer, Integer>> memo = new HashMap<>();
        Set<Integer> cities = graph.keySet();

        for (int city: cities) {
            List<Integer> neighbours = graph.get(city).neighbors;
            for (int neighbour: neighbours) {
//                bfs(neighbour, city, memo, graph);
            }
        }
    }

//    private int bfs(int city, int parent, Map<Integer, Map<Integer, Integer>> memo, Map<Integer, Node> graph) {
//
//    }


    public static void main(String[] args) {

    }
}
