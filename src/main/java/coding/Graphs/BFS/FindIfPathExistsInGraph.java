package coding.Graphs.BFS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class FindIfPathExistsInGraph {

    public boolean validPathDfs(int n, int[][] edges, int source, int destination) {
        boolean[] seen = new boolean[n];
        Map<Integer, Set<Integer>> adjGraph = new HashMap<>();

        for (int[] edge: edges) {
            adjGraph.computeIfAbsent(edge[0], vertex -> new HashSet<>()).add(edge[1]);
            adjGraph.computeIfAbsent(edge[1], vertex -> new HashSet<>()).add(edge[0]);
        }

        Stack<Integer> stack = new Stack<>();
        stack.push(source);
        seen[source] = true;

        while (!stack.isEmpty()) {
            int node = stack.pop();

            if (node == destination) {
                return true;
            }

            for (int neighbour: adjGraph.get(node)) {
                if (!seen[neighbour]) {
                    seen[neighbour] = true;
                    stack.push(neighbour);
                }
            }
        }

        return false;
    }


    public boolean validPath(int n, int[][] edges, int source, int destination) {
        boolean[] visited = new boolean[n];

        Map<Integer, Set<Integer>> adjGraph = new HashMap<>();
        for (int[] edge: edges) {
            adjGraph.computeIfAbsent(edge[0], vertex -> new HashSet<>()).add(edge[1]);
            adjGraph.computeIfAbsent(edge[1], vertex -> new HashSet<>()).add(edge[0]);
        }

        Queue<Integer> queue = new LinkedList<>();
        queue.offer(source);
        visited[source] = true;

        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            if (vertex == destination) {
                return true;
            }
            for (int neighbour: adjGraph.getOrDefault(vertex, new HashSet<>())) {
                if (!visited[neighbour]) {
                    queue.offer(neighbour);
                    visited[neighbour] = true;
                }
            }
        }

        return false;
    }

    public static void main(String[] args) {
        var sol = new FindIfPathExistsInGraph();
        System.out.println(sol.validPath(3, new int[][]{ {0, 1}, {1, 2}, {2, 0} }, 0, 2));
        System.out.println(sol.validPath(6, new int[][]{ {0, 1}, {0, 2}, {3, 5}, {5, 4}, {4, 3} }, 0, 5));

        System.out.println(sol.validPath(
                10,
                new int[][]{ {0, 7}, {0, 8}, {6, 1}, {2, 0}, {0, 4}, {5, 8}, {4, 7}, {1, 3}, {3, 5}, {6, 5} },
                7,
                5
            )
        );

        System.out.println(sol.validPathDfs(3, new int[][]{ {0, 1}, {1, 2}, {2, 0} }, 0, 2));
        System.out.println(sol.validPathDfs(6, new int[][]{ {0, 1}, {0, 2}, {3, 5}, {5, 4}, {4, 3} }, 0, 5));

        System.out.println(sol.validPathDfs(
                        10,
                        new int[][]{ {0, 7}, {0, 8}, {6, 1}, {2, 0}, {0, 4}, {5, 8}, {4, 7}, {1, 3}, {3, 5}, {6, 5} },
                        7,
                        5
                )
        );
    }
}
