package companies.doordash.repeat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class CourseScheduleII {

    public int[] findOrder(int numCourses, int[][] prerequisites) {
        Map<Integer, List<Integer>> adjacency = new HashMap<>();
        Map<Integer, Integer> inDegreeMap = new HashMap<>();
        int[] res = new int[numCourses];
        int resIdx = 0;

        for (int i = 0; i < numCourses; i++) {
            adjacency.put(i, new ArrayList<>());
            inDegreeMap.put(i, 0);
        }

        for (int[] prerequisite: prerequisites) {
            int parent = prerequisite[0];
            int child = prerequisite[1];

            adjacency.get(parent).add(child);
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
            res[resIdx++] = node;
            visited.add(node);

            for (int child: adjacency.get(node)) {
                if (!visited.contains(child)) {
                    inDegreeMap.put(child, inDegreeMap.get(child) - 1);
                    if (inDegreeMap.get(child) == 0) {
                        queue.offer(child);
                    }
                }
            }
        }
        if (visited.size() == numCourses) {
            return res;
        }
        return new int[0];
    }

    public static void main(String[] args) {
        var sol = new CourseScheduleII();

        System.out.println(
                Arrays.toString(sol.findOrder(2, new int[][]{{1,0}}))
        );

    }
}
