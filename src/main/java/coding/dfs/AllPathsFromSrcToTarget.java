package coding.dfs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AllPathsFromSrcToTarget {

    // Method to find all paths from source to target
    public List<List<Integer>> allPathsSourceTarget(int[][] graph) {
        List<List<Integer>> result = new ArrayList<>();
        Map<Integer, List<Integer>> adjacency = new HashMap<>();

        for (int i = 0; i < graph.length; i++) {
            int[] neighbours = graph[i];
            adjacency.computeIfAbsent(i, s -> new ArrayList<>())
                    .addAll(
                            Arrays.stream(neighbours).boxed().toList()
                    );
        }

        List<Integer> currentPath = new ArrayList<>();
        currentPath.add(0);
        allPathsSourceTargetRecursive(0, graph.length - 1, adjacency, currentPath, result);
        result.forEach(System.out::println);
        return result;
    }

    private void allPathsSourceTargetRecursive(int node, int target, Map<Integer, List<Integer>> adjacency, List<Integer> currentPath, List<List<Integer>> result) {
        if (node == target) {
            System.out.println(currentPath);
            result.add(new ArrayList<>(currentPath));
            System.out.println(result);
            return;
        }

        for (int neighbour: adjacency.getOrDefault(node, new ArrayList<>())) {
            currentPath.add(neighbour);
            allPathsSourceTargetRecursive(neighbour, target, adjacency, currentPath, result);
            currentPath.remove(currentPath.size() - 1);
        }
    }

    public static void main(String[] args) {
        var sol = new AllPathsFromSrcToTarget();
        sol.allPathsSourceTarget(
                        new int[][]{
                                {2,3}, {3,4}, {3,4}, {4}, {}
                        }
                );

        // [[2,3],[3,4],[3,4],[4],[]]
    }
}
