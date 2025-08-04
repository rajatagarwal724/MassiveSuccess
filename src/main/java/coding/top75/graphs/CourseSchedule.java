package coding.top75.graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class CourseSchedule {

    // Method to check if all courses can be finished
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        Map<Integer, List<Integer>> adjacencyMap = new HashMap<>();
        Map<Integer, Integer> indegreeMap = new HashMap<>();

        for (int i = 0; i < numCourses; i++) {
            adjacencyMap.put(i, new ArrayList<>());
            indegreeMap.put(i, 0);
        }

        for (int[] prerequisite: prerequisites) {
            adjacencyMap.computeIfAbsent(prerequisite[1], s -> new ArrayList<>()).add(prerequisite[0]);
            indegreeMap.put(prerequisite[0], indegreeMap.get(prerequisite[0]) + 1);
        }
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        indegreeMap.entrySet().forEach(entry -> {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
            }
        });

        while (!queue.isEmpty()) {
            var node = queue.poll();
            visited.add(node);
            var neighbours = adjacencyMap.get(node);
            for (int neighbour: neighbours) {
                if (indegreeMap.containsKey(neighbour)) {
                    indegreeMap.put(neighbour, indegreeMap.get(neighbour) - 1);
                    if (indegreeMap.get(neighbour) == 0) {
                        queue.offer(neighbour);
                    }
                }
            }
        }

        return visited.size() == numCourses;
    }

    public static void main(String[] args) {
        var sol = new CourseSchedule();
        System.out.println(
                sol.canFinish(3, new int[][]{{2, 0}, {2, 1}})
        );

        System.out.println(
                sol.canFinish(4, new int[][]{{1, 0}, {2, 1}, {3, 2}, {1, 3}})
        );

        System.out.println(
                sol.canFinish(5, new int[][]{{1, 0}, {2, 1}, {3, 2}, {4, 3}})
        );
    }
}
