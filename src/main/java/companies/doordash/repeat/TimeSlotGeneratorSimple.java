package companies.doordash.repeat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TimeSlotGeneratorSimple {

    private static final Map<String, Integer> DAYS = Map.of(
            "mon", 1, "tue", 2, "wed", 3, "thu", 4,
            "fri", 5, "sat", 6, "sun", 7
    );

    public List<Integer> getTimeSlots(String[] times) {
        int start = parseTime(times[0], false);
        int end = parseTime(times[1], true);

        // Create end buffer (stop 1-5 minutes before end)
        Set<Integer> stopTimes = new HashSet<>();
        for (int i = 1; i <= 5; i++) {
            stopTimes.add(addMinutes(end, i));
        }

        List<Integer> slots = new ArrayList<>();
        int current = start;

        while (!stopTimes.contains(current)) {
            slots.add(current);
            current = addMinutes(current, 5);
        }

        return slots;
    }

    private int parseTime(String timeStr, boolean isEnd) {
        String[] parts = timeStr.split(" ");
        int day = DAYS.get(parts[0]);
        String[] hm = parts[1].split(":");
        int hour = Integer.parseInt(hm[0]);
        int minute = Integer.parseInt(hm[1]);

        // Convert to 24-hour format
        if (parts[2].equals("pm") && hour < 12) hour += 12;
        if (parts[2].equals("am") && hour == 12) hour = 0;

        // Round to 5-minute increments
        int remainder = minute % 5;
        if (remainder >= 3 && !isEnd) {
            return addMinutes(day * 10000 + hour * 100 + minute - remainder, 5);
        }

        return day * 10000 + hour * 100 + (minute - remainder);
    }

    private int addMinutes(int time, int minutes) {
        int day = time / 10000;
        int hour = (time % 10000) / 100;
        int min = time % 100;

        min += minutes;
        if (min >= 60) {
            min = 0;
            hour++;
            if (hour >= 24) {
                hour = 0;
                day++;
                if (day > 7) day = 1;
            }
        }

        return day * 10000 + hour * 100 + min;
    }

    public static void main(String[] args) {
        TimeSlotGeneratorSimple generator = new TimeSlotGeneratorSimple();

        String[] times = {"mon 11:56 pm", "tue 00:29 am"};
        List<Integer> slots = generator.getTimeSlots(times);

        slots.forEach(System.out::println);
//        System.out.println("Generated " + slots.size() + " slots");
//        System.out.println("First 5: " + slots.subList(0, 5));
//        System.out.println("Last 5: " + slots.subList(slots.size() - 5, slots.size()));
    }
}
