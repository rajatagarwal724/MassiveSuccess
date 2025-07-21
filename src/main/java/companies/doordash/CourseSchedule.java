package companies.doordash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class CourseSchedule {

    public boolean canFinish(int numCourses, int[][] prerequisites) {
        Map<Integer, List<Integer>> adjacencyList = new HashMap<>();
        Map<Integer, Integer> inDegree = new HashMap<>();
        Set<Integer> visited = new HashSet<>();

        for (int i = 0; i < numCourses; i++) {
            adjacencyList.putIfAbsent(i, new ArrayList<>());
            inDegree.put(i, 0);
        }

        for (int[] prerequisite : prerequisites) {
            int parent = prerequisite[0];
            int child = prerequisite[1];

            adjacencyList.get(parent).add(child);
            inDegree.put(child, inDegree.get(child) + 1);
        }
        Queue<Integer> queue = new LinkedList<>();

//        var iterator = inDegree.entrySet().iterator();
        for (Map.Entry<Integer, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        while (!queue.isEmpty()) {
            int course = queue.poll();
            visited.add(course);

            var neighbours = adjacencyList.get(course);
            for (var neighbour: neighbours) {
                if (inDegree.containsKey(neighbour)) {
                    inDegree.put(neighbour, inDegree.get(neighbour) - 1);
                    if (inDegree.get(neighbour) == 0 && !visited.contains(neighbour)) {
                        queue.offer(neighbour);
                    }
                }
            }
        }

        return visited.size() == numCourses;
    }

    public static void main(String[] args) {
        var sol = new CourseSchedule();
        System.out.println(sol.canFinish(2, new int[][] {{1,0}, {0,1}}));

    }
}
