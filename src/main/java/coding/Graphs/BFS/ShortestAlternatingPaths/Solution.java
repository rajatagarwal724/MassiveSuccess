package coding.Graphs.BFS.ShortestAlternatingPaths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Solution {

    public int[] shortestAlternatingPaths(int n, int[][] redEdges, int[][] blueEdges) {
        int[] answer = new int[n];
        Map<Integer, List<Integer>> adjList = new HashMap<>();

        for (int[] redEdge: redEdges) {
            adjList.computeIfAbsent(redEdge[0], vertex -> new ArrayList<>()).add(redEdge[1]);
        }

        for (int[] blueEdge: blueEdges) {
            adjList.computeIfAbsent(blueEdge[0], vertex -> new ArrayList<>()).add(blueEdge[1]);
        }

        for (int endVertex = 1; endVertex < n; endVertex++) {
            Queue<Integer> queue = new LinkedList<>();
            boolean[] visited = new boolean[n];

            queue.offer(0);
            visited[0] = true;

            while (!queue.isEmpty()) {
                int currentVertex = queue.poll();
                if (currentVertex == endVertex) {

                }
                List<Integer> neighbours = adjList.getOrDefault(currentVertex, new ArrayList<>());
                for (int neighbour: neighbours) {
                    if (!visited[neighbour]) {
                        queue.offer(neighbour);
                    }
                }
            }
        }



        // ToDo: Write Your Code Here.
        return answer;
    }
}
