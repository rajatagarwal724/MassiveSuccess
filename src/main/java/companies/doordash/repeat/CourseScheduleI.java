package companies.doordash.repeat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class CourseScheduleI {

    public boolean canFinish(int numCourses, int[][] prerequisites) {
        int n = numCourses;
        Map<Integer, List<Integer>> adjacencyList = new HashMap<>();
        Map<Integer, Integer> inDegreeMap = new HashMap<>();

        for (int i = 0; i < numCourses; i++) {
            adjacencyList.put(i, new ArrayList<>());
            inDegreeMap.put(i, 0);
        }

        for (int[] prerequisite: prerequisites) {
            int parent = prerequisite[0];
            int child = prerequisite[1];

            adjacencyList.get(parent).add(child);
            inDegreeMap.put(child, inDegreeMap.get(child) + 1);
        }

        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();

        for (Map.Entry<Integer, Integer> entry: inDegreeMap.entrySet()) {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
            }
        }

        while (!queue.isEmpty()) {
            var node = queue.poll();
            visited.add(node);

            for (Integer children: adjacencyList.get(node)) {
                if (!visited.contains(children)) {
                    inDegreeMap.put(children, inDegreeMap.get(children) - 1);
                    if (inDegreeMap.get(children) == 0) {
                        queue.offer(children);
                    }
                }
            }
        }

        return visited.size() == n;
    }

    public static void main(String[] args) {
        var sol = new CourseScheduleI();
        System.out.println(
                sol.canFinish(2, new int[][]{{1,0}})
        );

        System.out.println(
                sol.canFinish(2, new int[][]{{1,0}, {0,1}})
        );
    }
}
