package companies.splunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class MinimumHeightTrees {

    public List<Integer> findTrees(int nodes, int[][] edges) {
        List<Integer> minHeightTrees = new ArrayList<>();
        if (nodes < 2) {
            for (int node = 0; node < nodes; node++) {
                minHeightTrees.add(node);
            }
            return minHeightTrees;
        }

        Map<Integer, Set<Integer>> adjList = new HashMap<>();
        Map<Integer, Integer> inDegreeMap = new HashMap<>();

        for (int node = 0; node < nodes; node++) {
            adjList.put(node, new HashSet<>());
            inDegreeMap.put(node, 0);
        }

        for (int[] edge: edges) {
            int n1 = edge[0];
            int n2 = edge[1];

            adjList.get(n1).add(n2);
            adjList.get(n2).add(n1);

            inDegreeMap.put(n1, inDegreeMap.get(n1) + 1);
            inDegreeMap.put(n2, inDegreeMap.get(n2) + 1);
        }

        Queue<Integer> leaves = new LinkedList<>();

        for (Map.Entry<Integer, Integer> entry: inDegreeMap.entrySet()) {
            if (entry.getValue() == 1) {
                leaves.offer(entry.getKey());
            }
        }

        int totalNodes = nodes;

        while (totalNodes > 2) {
            int leavesSize = leaves.size();
            totalNodes = totalNodes - leavesSize;
            for (int i = 0; i < leavesSize; i++) {
                int leave = leaves.poll();
                Set<Integer> children = adjList.get(leave);
                for (int child: children) {
                    if (inDegreeMap.containsKey(child)) {
                        inDegreeMap.put(child, inDegreeMap.get(child) - 1);
                        if (inDegreeMap.get(child) == 1) {
                            leaves.offer(child);
                        }
                    }
                }
            }
        }

        minHeightTrees.addAll(leaves);
        return minHeightTrees;
    }

    public static void main(String[] args) {
        var sol = new MinimumHeightTrees();
        List<Integer> result = sol.findTrees(5, new int[][] {
                new int[] { 0, 1 }, new int[] { 1, 2 }, new int[] { 1, 3 }, new int[] { 2, 4 } });
        System.out.println("Roots of MHTs: " + result);

        result = sol.findTrees(4,
                new int[][] { new int[] { 0, 1 }, new int[] { 0, 2 }, new int[] { 2, 3 } });
        System.out.println("Roots of MHTs: " + result);

        result = sol.findTrees(4,
                new int[][] { new int[] { 0, 1 }, new int[] { 1, 2 }, new int[] { 1, 3 } });
        System.out.println("Roots of MHTs: " + result);
    }
}
