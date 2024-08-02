package coding.TopologicalSort.TaskScheduling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

public class TaskScheduling {

    public boolean isSchedulingPossible(int tasks, int[][] prerequisites) {
        List<Integer> sorted = new ArrayList<>();
        Map<Integer, Integer> inDegree = new HashMap<>();
        Map<Integer, List<Integer>> graph = new HashMap<>();

        for (int task = 0; task < tasks; task++) {
            inDegree.put(task, 0);
            graph.put(task, new ArrayList<>());
        }

        for (int[] prerequisite: prerequisites) {
            int parent = prerequisite[0];
            int child = prerequisite[1];
            graph.get(parent).add(child);
            inDegree.put(child, inDegree.get(child) + 1);
        }

        Queue<Integer> sources = inDegree
                .entrySet()
                .stream().filter(entry -> entry.getValue() == 0)
                .map(entry -> entry.getKey())
                .collect(Collectors.toCollection(LinkedList::new));

        while (!sources.isEmpty()) {
            int vertex = sources.poll();
            sorted.add(vertex);
            List<Integer> children = graph.get(vertex);
            if (!children.isEmpty()) {
                for (int child: children) {
                    inDegree.put(child, inDegree.get(child) - 1);
                    if (inDegree.get(child) == 0) {
                        sources.offer(child);
                    }
                }
            }
        }
        return sorted.size() == tasks;
    }

    public static void main(String[] args) {

    }
}
