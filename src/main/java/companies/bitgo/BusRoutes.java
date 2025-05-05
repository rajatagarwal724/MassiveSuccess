package companies.bitgo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BusRoutes {

    public int numBusesToDestination(int[][] routes, int source, int target) {
        Map<Integer, List<Integer>> stopeToRouteMap = new HashMap<>();
        for (int i = 0; i < routes.length; i++) {
            for (int stop: routes[i]) {
                stopeToRouteMap.computeIfAbsent(stop, route -> new ArrayList<>()).add(i);
            }
        }

        Set<Integer> visitedRoutes = new HashSet<>();
        int busCount = 1;
        Queue<Integer> currentRoute = new LinkedList<>();
        currentRoute.addAll(stopeToRouteMap.getOrDefault(source, new ArrayList<>()));
        visitedRoutes.addAll(stopeToRouteMap.getOrDefault(source, new ArrayList<>()));

        while (!currentRoute.isEmpty()) {
            int size = currentRoute.size();

            for (int i = 0; i < size; i++) {
                int route = currentRoute.poll();

                for (int stop: routes[route]) {
                    if (target == stop) {
                        return busCount;
                    }

                    for (int nextRoute: stopeToRouteMap.getOrDefault(stop, new ArrayList<>())) {
                        if (!visitedRoutes.contains(nextRoute)) {
                            visitedRoutes.add(nextRoute);
                            currentRoute.offer(nextRoute);
                        }
                    }
                }
            }
            busCount++;
        }
        return -1;
    }
}
