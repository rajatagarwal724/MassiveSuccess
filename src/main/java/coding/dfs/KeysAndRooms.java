package coding.dfs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class KeysAndRooms {

    // Method to determine if all rooms can be visited
    public boolean canVisitAllRooms(List<List<Integer>> rooms) {
        Map<Integer, List<Integer>> adjacencyList = new HashMap<>();

        for (int i = 0; i < rooms.size(); i++) {
            adjacencyList.computeIfAbsent(i, s -> new ArrayList<>()).addAll(new ArrayList<>(rooms.get(i)));
        }

        Set<Integer> visited = new HashSet<>();
        Stack<Integer> stack = new Stack<>();
        stack.push(0);

        while (!stack.isEmpty()) {
            var node = stack.pop();
            visited.add(node);

            for (int neighbour: adjacencyList.getOrDefault(node, new ArrayList<>())) {
                if (!visited.contains(neighbour)) {
                    stack.push(neighbour);
                }
            }
        }

        return visited.size() == rooms.size();
    }


    public static void main(String[] args) {
        var sol = new KeysAndRooms();
//        System.out.println(
//                sol.canVisitAllRooms(
//                        List.of(
//                                List.of(1, 2),
//                                List.of(1),
//                                List.of(4, 3),
//                                List.of(4),
//                                List.of(3)
//                        )
//                )
//        );

//        System.out.println(
//                sol.canVisitAllRooms(
//                        List.of(
//                                List.of(1, 2, 3),
//                                List.of(),
//                                List.of(),
//                                List.of()
//                        )
//                )
//        );


        System.out.println(
                sol.canVisitAllRooms(
                        List.of(
                                List.of(3),
                                List.of(1),
                                List.of(2),
                                List.of()
                        )
                )
        );
    }
}
