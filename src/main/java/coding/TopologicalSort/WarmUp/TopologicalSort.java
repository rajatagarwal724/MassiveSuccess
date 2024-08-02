package coding.TopologicalSort.WarmUp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

public class TopologicalSort {

    public List<Integer> sort(int vertices, int[][] edges) {
        List<Integer> sortedOrder = new ArrayList<>();

        if (vertices <= 0) {
            return new ArrayList<>();
        }

        Map<Integer, Integer> inDegree = new HashMap<>();
        Map<Integer, List<Integer>> graph = new HashMap<>();

        for (int i = 0; i < vertices; i++) {
            inDegree.put(i, 0);
            graph.put(i, new ArrayList<>());
        }

        for (int[] edge: edges) {
            int parent = edge[0];
            int child = edge[1];
            graph.get(parent).add(child);
            inDegree.put(child, inDegree.get(child) + 1);
        }

        Queue<Integer> sources = inDegree
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() == 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedList::new));

        while (!sources.isEmpty()) {
            int vertex = sources.poll();
            sortedOrder.add(vertex);

            List<Integer> children = graph.getOrDefault(vertex, new ArrayList<>());
            for (int child : children) {
                if (inDegree.containsKey(child)) {
                    inDegree.put(child, inDegree.get(child) - 1);
                    if (inDegree.get(child) == 0) {
                        inDegree.remove(child);
                        sources.offer(child);
                    }
                }
            }
        }

        return sortedOrder;
    }


    public static void main(String[] args) {
        var sol = new TopologicalSort();

        var res = sol.sort(7, new int[][]{ {6, 4}, {6, 2}, {5, 3}, {5, 4}, {3, 0}, {3, 1}, {3, 2}, {4, 1} });

        System.out.println(Arrays.toString(res.toArray()));

        res = sol.sort(4, new int[][]{ {3, 2}, {3, 0}, {2, 0}, {2, 1} });
        System.out.println(Arrays.toString(res.toArray()));

        res = sol.sort(5, new int[][]{ {4, 2}, {4, 3}, {2, 0}, {2, 1}, {3, 1} });
        System.out.println(Arrays.toString(res.toArray()));
    }
}
