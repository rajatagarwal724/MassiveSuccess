package companies.doordash.repeat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidPickupDeliveries {

    private List<String> longestValidSubArrayOptimized(String[] activities) {
        int start = 0, maxLen = 0, maxStart = 0;

        Set<String> picked = new HashSet<>();
        Set<String> delivered = new HashSet<>();

        for (int end = 0; end < activities.length; end++) {
            String activity = activities[end];
            boolean isPicked = activity.startsWith("P");
            String id = activity.substring(1);

            if (isPicked) {
                if (picked.contains(id)) {
                    start = end;
                    picked.clear();
                    delivered.clear();
                }
                picked.add(id);
            } else {
                if (delivered.contains(id) || !picked.contains(id)) {
                    start = end + 1;
                    picked.clear();
                    delivered.clear();
                }
                delivered.add(id);
            }

            if (end - start + 1 > maxLen) {
                maxLen = end - start + 1;
                maxStart = start;
            }
        }

        return Arrays.stream(activities).toList().subList(maxStart, Math.max(activities.length, maxLen));
    }

    private List<String> longestValidSubArray(String[] activities) {
        List<String> longest = new ArrayList<>();
        for (int i = 0; i < activities.length; i++) {
            Set<String> picked = new HashSet<>();
            Set<String> delivered = new HashSet<>();
            boolean isValid = true;
            for (int j = i; j < activities.length; j++) {
                String activity = activities[j];
                boolean isPicked = "P".equals(activity.substring(0,1));
                String id = activity.substring(1);

                if (isPicked) {
                    if (picked.contains(id)) {
                        isValid = false;
                    }
                    picked.add(id);
                } else {
                    if (delivered.contains(id) || !picked.contains(id)) {
                        isValid = false;
                    }
                    delivered.add(id);
                }

                if (isValid && delivered.size() == picked.size() && (j - i + 1) > longest.size()) {
                    longest = Arrays.stream(activities).collect(Collectors.toList()).subList(i, j + 1);
                }
            }
        }

        return longest;
    }

    public static void main(String[] args) {
        var sol = new ValidPickupDeliveries();
//        System.out.println(
//                sol.validate(new String[] {
//                        "P1", "P3", "P2", "D3", "P4",
//                        "P404", "D2", "D1", "D404", "D4",
//                        "P33", "D33"
//                })
//        );
//
//        System.out.println(
//                sol.validate(new String[] {
//                        "P1", "P1"
//                })
//        );
//
//        System.out.println(
//                sol.validate(new String[] {
//                        "P1", "D1", "D1"
//                })
//        );
//
//        System.out.println(
//                sol.validate(new String[] {
//                        "D1", "P1"
//                })
//        );
//
//        System.out.println(
//                sol.validate(new String[] {
//                        "P1", "P2", "D1"
//                })
//        );

        System.out.println(
                sol.longestValidSubArray(new String[] {
                        "P1", "P1", "D1"
                })
        );

        System.out.println(
                sol.longestValidSubArrayOptimized(new String[] {
                        "P1", "P1", "D1"
                })
        );
    }

    private boolean validate(String[] strings) {
        Set<String> pickUp = new HashSet<>();
        Set<String> drop = new HashSet<>();

        for (String activity: strings) {
            String type = activity.substring(0, 1);
            String id = activity.substring(1);

            if (type.equals("P")) {
                if (pickUp.contains(id)) {
                    return false;
                }
                pickUp.add(id);
            } else if (type.equals("D")) {
                if (drop.contains(id) || !pickUp.contains(id)) {
                    return false;
                }
                drop.add(id);
            }
        }

        return pickUp.size() == drop.size();
    }
}
