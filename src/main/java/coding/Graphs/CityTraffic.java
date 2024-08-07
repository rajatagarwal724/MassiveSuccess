package coding.Graphs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.*;

public class CityTraffic {
    static class Node {
        int population;
        List<Integer> neighbors;

        Node(int population) {
            this.population = population;
            this.neighbors = new ArrayList<>();
        }
    }

    public static String maxTraffic(String[] cities) {
        // Create the graph
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

        // Memoization
        Map<Integer, Map<Integer, Integer>> memo = new HashMap<>();

        // Calculate population sums
        for (int city : graph.keySet()) {
            for (int neighbor : graph.get(city).neighbors) {
                dfs(neighbor, city, graph, memo);
            }
        }

        // Calculate maximum traffic
        Map<Integer, Integer> maxTraffic = new TreeMap<>();
        for (int city : memo.keySet()) {
            int max = 0;
            for (int child : memo.get(city).keySet()) {
                max = Math.max(max, memo.get(city).get(child));
            }
            maxTraffic.put(city, max);
        }

        // Format output
        List<String> result = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : maxTraffic.entrySet()) {
            result.add(entry.getKey() + ":" + entry.getValue());
        }
//        Collections.sort(result, (a, b) -> Integer.compare(Integer.parseInt(a.split(":")[0]), Integer.parseInt(b.split(":")[0])));
        return String.join(",", result);
    }

    // DFS with memoization
    static int dfs(int city, int parent, Map<Integer, Node> graph, Map<Integer, Map<Integer, Integer>> memo) {
        if (memo.containsKey(parent) && memo.get(parent).containsKey(city)) {
            return memo.get(parent).get(city);
        }

        int totalPopulation = city;
        for (int neighbor : graph.get(city).neighbors) {
            if (neighbor != parent) {
                totalPopulation += dfs(neighbor, city, graph, memo);
            }
        }

        memo.computeIfAbsent(parent, k -> new HashMap<>()).put(city, totalPopulation);
        return totalPopulation;
    }

    public static void main(String[] args) {
        System.out.println(maxTraffic(new String[] {"1:[5]", "2:[5]", "3:[5]", "4:[5]", "5:[1,2,3,4]"}));
        System.out.println(maxTraffic(new String[] {"1:[5]", "2:[5,18]", "3:[5,12]", "4:[5]", "5:[1,2,3,4]", "18:[2]",
                "12:[3]"}));
    }
}


