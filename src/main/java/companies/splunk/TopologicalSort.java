package companies.splunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class TopologicalSort {

    private List<Integer> sort(int n, int[][] edges) {
        Map<Integer, Integer> inDegree = new HashMap<>();
        Map<Integer, List<Integer>> adjList = new HashMap<>();
        boolean[] visited= new boolean[n];
        Arrays.fill(visited, false);
        List<Integer> sorted = new ArrayList<>();
        Queue<Integer> queue = new LinkedList<>();

        for (int i = 0; i < n; i++) {
            inDegree.put(i, 0);
            adjList.put(i, new ArrayList<>());
        }

        for (int[] edge: edges) {
            int source = edge[0];
            int destination = edge[1];

            inDegree.put(destination, inDegree.getOrDefault(destination, 0) + 1);
            adjList.computeIfAbsent(source, dest -> new ArrayList<>()).add(destination);
        }

        for (Map.Entry<Integer, Integer> entry: inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
            }
        }

        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            sorted.add(vertex);
            visited[vertex] = true;

            for (Integer dest: adjList.get(vertex)) {
                if (!visited[dest] && inDegree.containsKey(dest)) {
                    inDegree.put(dest, inDegree.get(dest) - 1);
                    if (inDegree.get(dest) == 0) {
                        queue.offer(dest);
                        inDegree.remove(dest);
                    }
                }
            }
        }
        return sorted;
    }


    public static void main(String[] args) {
        var sol = new TopologicalSort();
        List<Integer> result = sol.sort(7, new int[][] { new int[] { 6, 4 },
                new int[] { 6, 2 }, new int[] { 5, 3 }, new int[] { 5, 4 },
                new int[] { 3, 0 }, new int[] { 3, 1 }, new int[] { 3, 2 }, new int[] { 4, 1 } });
        System.out.println(result);

        result = sol.sort(4, new int[][] { new int[] { 3, 2 },
                new int[] { 3, 0 }, new int[] { 2, 0 }, new int[] { 2, 1 } });
        System.out.println(result);

        result = sol.sort(5, new int[][] { new int[] { 4, 2 },
                new int[] { 4, 3 }, new int[] { 2, 0 }, new int[] { 2, 1 }, new int[] { 3, 1 } });
        System.out.println(result);
    }
}
