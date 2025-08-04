package coding.dfs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeNeededToInformAllEmployees {

    public int numOfMinutes(int n, int headID, int[] manager, int[] informTime) {
        Map<Integer, List<Integer>> adjacency = new HashMap<>();
        for (int i = 0; i < manager.length; i++) {
            if (manager[i] == -1) {
                continue;
            }

            int managerId = manager[i];

            adjacency.computeIfAbsent(managerId, s -> new ArrayList<>()).add(i);
        }

        System.out.println(adjacency);

        return 0;
    }

    public static void main(String[] args) {

        var sol = new TimeNeededToInformAllEmployees();
    }
}
