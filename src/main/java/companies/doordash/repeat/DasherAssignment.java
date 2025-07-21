package companies.doordash.repeat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class DasherAssignment {

    static class Location {
        double longitude;
        double latitude;

        Location(double longitude, double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }
    }

    class Dasher {
        long id;
        Location lastLocation;
        int rating;

        public Dasher(long id, Location lastLocation, int rating) {
            this.id = id;
            this.lastLocation = lastLocation;
            this.rating = rating;
        }
    }

    public List<Long> findNearestDashers(Location restaurantLocation) {
        List<Dasher> dashers = GetDashers();
        int K = 3;

        // Max-heap: worst (farthest/lowest rated) at the top
        PriorityQueue<Dasher> maxHeap = new PriorityQueue<>((a, b) -> {
            double distA = calculateDistance(a.lastLocation, restaurantLocation);
            double distB = calculateDistance(b.lastLocation, restaurantLocation);

            if (Double.compare(distA, distB) == 0) {
                return Integer.compare(a.rating, b.rating); // lower rating = worse
            }
            return Double.compare(distB, distA); // farthest = worse
        });

        for (Dasher dasher : dashers) {
            if (maxHeap.size() < K) {
                maxHeap.offer(dasher);
            } else {
                Dasher worst = maxHeap.peek();
                double distCurrent = calculateDistance(dasher.lastLocation, restaurantLocation);
                double distWorst = calculateDistance(worst.lastLocation, restaurantLocation);

                if (distCurrent < distWorst ||
                        (distCurrent == distWorst && dasher.rating > worst.rating)) {
                    maxHeap.poll();
                    maxHeap.offer(dasher);
                }
            }
        }

        // Extract and sort results by distance and rating
        List<Dasher> topDashers = new ArrayList<>(maxHeap);
        topDashers.sort((a, b) -> {
            double distA = calculateDistance(a.lastLocation, restaurantLocation);
            double distB = calculateDistance(b.lastLocation, restaurantLocation);

            if (Double.compare(distA, distB) == 0) {
                return Integer.compare(b.rating, a.rating);
            }
            return Double.compare(distA, distB);
        });

        List<Long> result = new ArrayList<>();
        for (Dasher d : topDashers) {
            result.add(d.id);
        }

        return result;
    }

    private static double calculateDistance(Location a, Location b) {
        double dx = a.longitude - b.longitude;
        double dy = a.latitude - b.latitude;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private List<Dasher> GetDashers() {
        return Arrays.asList(
                new Dasher(11, new Location(10.0, 20.0), 95),
                new Dasher(14, new Location(11.0, 20.1), 90),
                new Dasher(17, new Location(10.5, 20.5), 85),
                new Dasher(21, new Location(50.0, 50.0), 99),
                new Dasher(22, new Location(10.1, 20.1), 92),
                new Dasher(33, new Location(9.9, 19.9), 96)
        );
    }

    public static void main(String[] args) {
        var sol = new DasherAssignment();
        Location restaurant = new Location(10.0, 20.0);
        List<Long> nearestDashers = sol.findNearestDashers(restaurant);
        System.out.println(nearestDashers); // Example output: [11, 33, 22]
    }
}
