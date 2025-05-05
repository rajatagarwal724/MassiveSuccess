package coding.Graphs.BFS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

public class AllPathSourceTargets {

    public List<List<Integer>> allPathsSourceTarget(int[][] graph) {
        List<List<Integer>> result = new ArrayList<>();

        int vertices = graph.length;
        Map<Integer, List<Integer>> adjList = new HashMap<>();

        for (int vertex = 0; vertex < vertices; vertex++) {
            int[] neighbours = graph[vertex];
            adjList.put(vertex, Arrays.stream(neighbours).boxed().toList());
        }

        Queue<List<Integer>> queue = new LinkedList<>();
        List<Integer> path = new ArrayList<>();
        path.add(0);

        queue.offer(path);

        while (!queue.isEmpty()) {
            List<Integer> currentPath = queue.poll();
            int node = currentPath.get(currentPath.size() - 1);

            for (int neighbour: adjList.get(node)) {
                List<Integer> newPath = new ArrayList<>(currentPath);
                newPath.add(neighbour);
                if (neighbour == vertices - 1) {
                    result.add(newPath);
                } else {
                    queue.offer(newPath);
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        var sol = new AllPathSourceTargets();
        sol.allPathsSourceTarget(new int[][]{ {1, 2}, {3}, {3}, {} }).forEach(res -> System.out.println(res));
    }
}
