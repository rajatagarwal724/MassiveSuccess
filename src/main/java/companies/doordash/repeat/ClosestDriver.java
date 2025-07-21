package companies.doordash.repeat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class ClosestDriver {


//    class Location {
//        double longitude;
//        double latitude;
//
//        Location(double longitude, double latitude) {
//            this.longitude = longitude;
//            this.latitude = latitude;
//        }
//    }
//
//    class Dasher {
//        long id;
//        Location lastLocation;
//        int rating;
//
//        public Dasher(long id, Location lastLocation, int rating) {
//            this.id = id;
//            this.lastLocation = lastLocation;
//            this.rating = rating;
//        }
//    }
//
//    public class DasherAssignment {
//
//        public static List<Long> findNearestDashers(Location restaurantLocation) {
//            List<Dasher> dashers = GetDashers();
//            int K = 3;
//
//            // Max-heap: worst (farthest/lowest rated) at the top
//            PriorityQueue<Dasher> maxHeap = new PriorityQueue<>((a, b) -> {
//                double distA = calculateDistance(a.lastLocation, restaurantLocation);
//                double distB = calculateDistance(b.lastLocation, restaurantLocation);
//
//                if (Double.compare(distA, distB) == 0) {
//                    return Integer.compare(a.rating, b.rating); // lower rating = worse
//                }
//                return Double.compare(distB, distA); // farthest = worse
//            });
//
//            for (Dasher dasher : dashers) {
//                if (maxHeap.size() < K) {
//                    maxHeap.offer(dasher);
//                } else {
//                    Dasher worst = maxHeap.peek();
//                    double distCurrent = calculateDistance(dasher.lastLocation, restaurantLocation);
//                    double distWorst = calculateDistance(worst.lastLocation, restaurantLocation);
//
//                    if (distCurrent < distWorst ||
//                            (distCurrent == distWorst && dasher.rating > worst.rating)) {
//                        maxHeap.poll();
//                        maxHeap.offer(dasher);
//                    }
//                }
//            }
//
//            // Extract and sort results by distance and rating
//            List<Dasher> topDashers = new ArrayList<>(maxHeap);
//            topDashers.sort((a, b) -> {
//                double distA = calculateDistance(a.lastLocation, restaurantLocation);
//                double distB = calculateDistance(b.lastLocation, restaurantLocation);
//
//                if (Double.compare(distA, distB) == 0) {
//                    return Integer.compare(b.rating, a.rating);
//                }
//                return Double.compare(distA, distB);
//            });
//
//            List<Long> result = new ArrayList<>();
//            for (Dasher d : topDashers) {
//                result.add(d.id);
//            }
//
//            return result;
//        }
//
//        private static double calculateDistance(Location a, Location b) {
//            double dx = a.longitude - b.longitude;
//            double dy = a.latitude - b.latitude;
//            return Math.sqrt(dx * dx + dy * dy);
//        }
//
//        private static List<Dasher> GetDashers() {
//            return Arrays.asList(
//                    new Dasher(11, new Location(10.0, 20.0), 95),
//                    new Dasher(14, new Location(11.0, 20.1), 90),
//                    new Dasher(17, new Location(10.5, 20.5), 85),
//                    new Dasher(21, new Location(50.0, 50.0), 99),
//                    new Dasher(22, new Location(10.1, 20.1), 92),
//                    new Dasher(33, new Location(9.9, 19.9), 96)
//            );
//        }
//
//        public static void main(String[] args) {
//            Location restaurant = new Location(10.0, 20.0);
//            List<Long> nearestDashers = findNearestDashers(restaurant);
//            System.out.println(nearestDashers); // Example output: [11, 33, 22]
//        }
//    }
//
//
//    class Dasher {
//        long id;
//        Location lastLocation;
//        int rating;
//
//        public Dasher(long id, Location lastLocation, int rating) {
//            this.id = id;
//            this.lastLocation = lastLocation;
//            this.rating = rating;
//        }
//    }
//
//    class Location {
//        double longitude;
//        double lattitude;
//
//        Location(double longitude, double lattitude) {
//            this.longitude = longitude;
//            this.lattitude = lattitude;
//        }
//    }
//
//    private double distance(Location restaurant, Dasher dasher) {
//        double deltaX = Math.abs(restaurant.lattitude - dasher.lastLocation.lattitude);
//        double deltaY = Math.abs(restaurant.longitude - dasher.lastLocation.longitude)
//        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
//    }
//
//    public List<Long> findClosestDashers(Location restaurant, List<Dasher> dashers) {
//        PriorityQueue<Dasher> maxHeap = new PriorityQueue<>(
//                (d1, d2) -> {
//                    double d1Distance = distance(restaurant, d1);
//                    double d2Distance = distance(restaurant, d2);
//
//                    if (d1Distance == d2Distance) {
//                        return d1.rating - d2.rating;
//                    }
//                    return Double.compare(d2Distance, d1Distance);
//                }
//        );
//
//        for (Dasher dasher: dashers) {
//            maxHeap.offer(dasher);
//            if (maxHeap.size() > 3) {
//                maxHeap.poll();
//            }
//        }
//
//        List<Dasher> result = new ArrayList<>(maxHeap);
//
//        Collections.sort(result, (d1, d2) -> {
//            double d1Distance = distance(restaurant, d1);
//            double d2Distance = distance(restaurant, d2);
//
//            if (d1Distance == d2Distance) {
//                return d2.rating - d1.rating;
//            }
//            return Double.compare(d1Distance, d2Distance);
//        });
//
//        return result.stream().map(dasher -> dasher.id).collect(Collectors.toList());
//    }
}
