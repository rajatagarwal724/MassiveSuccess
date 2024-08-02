package coding.TopologicalSort.TasksScheduling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

public class TasksSchedulingWithOrder {

    public List<Integer> findOrder(int tasks, int[][] prerequisites) {
        List<Integer> result = new ArrayList<>();

        if (tasks <= 0) {
            return result;
        }

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
                .stream()
                .filter(entry -> entry.getValue() == 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedList::new));

        while (!sources.isEmpty()) {
            int vertex = sources.poll();
            result.add(vertex);

            List<Integer> children = graph.get(vertex);
            for (int child: children) {
                inDegree.put(child, inDegree.get(child) - 1);
                if (inDegree.get(child) == 0) {
                    sources.offer(child);
                }
            }
        }

        return result.size() == tasks ? result : new ArrayList<>();
    }

    public static void main(String[] args) {

    }
}
