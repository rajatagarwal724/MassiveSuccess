package companies.doordash.repeat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GetActiveTime {

    public int get_active_time(String[] activities) {
        List<Integer> pickUps = new ArrayList<>();
        List<Integer> drops = new ArrayList<>();
        int smallestPickUp = Integer.MAX_VALUE;
        int highestDropOff = Integer.MIN_VALUE;

        for (String activity: activities) {
            activity = activity.trim();
            String[] parts = activity.split("\\|");
            int time = get_mins(parts[0].trim());
            String type = parts[1].trim();

            if ("pickup".equals(type)) {
                pickUps.add(time);
                smallestPickUp = Math.min(smallestPickUp, time);
            } else if ("dropoff".equals(type)) {
                drops.add(time);
                highestDropOff = Math.max(highestDropOff, time);
            }
        }

        int total_active_time = highestDropOff - smallestPickUp;
        System.out.println(total_active_time);
        List<int[]> intervals = new ArrayList<>();

        for (int i = 0; i < pickUps.size(); i++) {
            intervals.add(new int[] {pickUps.get(i), drops.get(i)});
        }
        int idle_time = 0;

        Iterator<int[]> iterator = intervals.stream().iterator();

        int[] interval = iterator.next();
        int start = interval[0];
        int end = interval[1];

        while (iterator.hasNext()) {
            int[] next = iterator.next();

            if (next[0] > end) {
                idle_time += (next[0] - end);
            }

            start = Math.min(start, next[0]);
            end = Math.max(end, next[1]);
        }

//        for (int i = 0; i < intervals.size() - 1; i++) {
//            int[] curr = intervals.get(i);
//            int[] next = intervals.get(i + 1);
//
//            if (curr[1] < next[0]) {
//                idle_time += (next[0] - curr[1]);
//            } else {
//                next[1] = Math.max(curr[1], next[1]);
//            }
//        }

        return total_active_time - idle_time;
    }

    private int get_mins(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1].substring(0 , parts[1].length() - 2));
        String ampm = parts[1].substring(parts[1].length() - 2);

        if (ampm.equals("pm")) {
            if (hours < 12) {
                hours += 12;
            }
        } else {
            if (hours == 12) {
                hours = 0;
            }
        }

        return 60 * hours + minutes;
    }

    public static void main(String[] args) {
        String[] activity = new String[]{
                "8:30am | pickup",
                "9:10am | dropoff",
                "10:20am| pickup",
                "12:15pm| pickup",
                "12:45pm| dropoff",
                "2:25pm | dropoff"
        };

        var sol = new GetActiveTime();
        System.out.println(
                sol.get_active_time(activity)
        );

    }
}
