package coding.Graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MinimumVerticesToReachAllNodes {

    public List<Integer> findSmallestSetOfVertices(int n, List<List<Integer>> edges) {
        Map<Integer, Integer> inDegreeMap = new HashMap<>();
        for (int i = 0; i < n; i++) {
            inDegreeMap.put(i, 0);
        }
        for (List<Integer> edge: edges) {
            int child = edge.get(1);

            inDegreeMap.put(child, inDegreeMap.get(child) + 1);
        }

        return inDegreeMap.entrySet().stream().filter(entry -> entry.getValue() == 0).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        MinimumVerticesToReachAllNodes solution = new MinimumVerticesToReachAllNodes();

        // Test cases
        List<List<Integer>> edges1 = List.of(List.of(0,1), List.of(0,2), List.of(2,5), List.of(3,4), List.of(4,2));
        System.out.println(solution.findSmallestSetOfVertices(6, edges1));  // Expected: [0, 3]

        List<List<Integer>> edges2 = List.of(List.of(0,1), List.of(3,1), List.of(1,2));
        System.out.println(solution.findSmallestSetOfVertices(4, edges2));  // Expected: [0, 3]

        List<List<Integer>> edges3 = List.of(List.of(2,0), List.of(3,2));
        System.out.println(solution.findSmallestSetOfVertices(4, edges3));  // Expected: [1, 3]
    }
}
