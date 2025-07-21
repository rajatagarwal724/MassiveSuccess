package companies.doordash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class CourseScheduleII {

    public int[] findOrder(int numCourses, int[][] prerequisites) {
        var adjacencyList = new HashMap<Integer, List<Integer>>();
        var inDegreeMap = new HashMap<Integer, Integer>();
        var visited = new HashSet<Integer>();
        var queue = new LinkedList<Integer>();
        int res[] = new int[numCourses];

        for (int i = 0; i < numCourses; i++) {
            adjacencyList.put(i, new ArrayList<>());
            inDegreeMap.put(i, 0);
        }

        for (int[] prereq: prerequisites) {
            var parent = prereq[0];
            var child = prereq[1];
            adjacencyList.get(parent).add(child);
            inDegreeMap.put(child, inDegreeMap.get(child) + 1);
        }

        for (var entry: inDegreeMap.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
                inDegreeMap.remove(entry.getKey());
            }
        }
        int index = 0;

        while (!queue.isEmpty()) {
            var node = queue.poll();
            visited.add(node);
            res[index] = node;
            index++;

            for (int neighbour: adjacencyList.get(node)) {
                if (inDegreeMap.containsKey(neighbour)) {
                    inDegreeMap.put(neighbour, inDegreeMap.get(neighbour) - 1);
                    if (inDegreeMap.get(neighbour) == 0 && !visited.contains(neighbour)) {
                        queue.offer(neighbour);
                    }
                }
            }
        }

        if (index == numCourses) {
            reverse(res);
            return res;
        } else {
            return new int[0];
        }
    }

    private void reverse(int[] arr) {
        int i = 0, j = arr.length - 1;

        while (i < j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
            i++;
            j--;
        }
    }

    public int[] findOrder_1(int numCourses, int[][] prerequisites) {
        Map<Integer, List<Integer>> adjList = new HashMap<
                Integer,
                List<Integer>
                >();
        int[] indegree = new int[numCourses];
        int[] topologicalOrder = new int[numCourses];

        // Create the adjacency list representation of the graph
        for (int i = 0; i < prerequisites.length; i++) {
            int dest = prerequisites[i][0];
            int src = prerequisites[i][1];
            List<Integer> lst = adjList.getOrDefault(
                    src,
                    new ArrayList<Integer>()
            );
            lst.add(dest);
            adjList.put(src, lst);

            // Record in-degree of each vertex
            indegree[dest] += 1;
        }

        // Add all vertices with 0 in-degree to the queue
        Queue<Integer> q = new LinkedList<Integer>();
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) {
                q.add(i);
            }
        }

        int i = 0;
        // Process until the Q becomes empty
        while (!q.isEmpty()) {
            int node = q.remove();
            topologicalOrder[i++] = node;

            // Reduce the in-degree of each neighbor by 1
            if (adjList.containsKey(node)) {
                for (Integer neighbor : adjList.get(node)) {
                    indegree[neighbor]--;

                    // If in-degree of a neighbor becomes 0, add it to the Q
                    if (indegree[neighbor] == 0) {
                        q.add(neighbor);
                    }
                }
            }
        }

        // Check to see if topological sort is possible or not.
        if (i == numCourses) {
            return topologicalOrder;
        }

        return new int[0];
    }

    public static void main(String[] args) {
        var sol = new CourseScheduleII();
//        System.out.println(
//                Arrays.toString(sol.findOrder(2, new int[][]{{1, 0}}))
//        );
//
//        System.out.println(
//                Arrays.toString(sol.findOrder(
//                        4,
//                        new int[][]{
//                                {1,0},
//                                {2,0},
//                                {3,1},
//                                {3,2}
//                        }
//                )
//                )
//        );

        System.out.println(
                Arrays.toString(sol.findOrder_1(2, new int[][]{{1, 0}}))
        );
    }
}
