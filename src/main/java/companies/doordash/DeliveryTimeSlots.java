package companies.doordash;

import java.util.*;

public class DeliveryTimeSlots {
    
    private static final Map<String, Integer> DAYS = new HashMap<>();
    static {
        DAYS.put("mon", 1);
        DAYS.put("tue", 2);
        DAYS.put("wed", 3);
        DAYS.put("thu", 4);
        DAYS.put("fri", 5);
        DAYS.put("sat", 6);
        DAYS.put("sun", 7);
    }
    
    public static List<Long> getIncrements(String[] times) {
        long start = cleanTime(times[0], false);
        long end = cleanTime(times[1], true);
        
        Set<Long> endBorders = new HashSet<>();
        for (int minutes = 1; minutes <= 5; minutes++) {
            endBorders.add(addDelta(end, minutes));
        }
        
        List<Long> result = new ArrayList<>();
        long curr = start;
        while (!endBorders.contains(curr)) {
            result.add(curr);
            curr = addDelta(curr, 5);
        }
        
        return result;
    }


    // "tue 00:29 am"
    private static long cleanTime(String timeStr, boolean isEnd) {
        String[] parts = timeStr.split(" ");
        int day = DAYS.get(parts[0]);
        String[] hoursMinutes = parts[1].split(":");
        int hours = Integer.parseInt(hoursMinutes[0]);
        int minutes = Integer.parseInt(hoursMinutes[1]);
        
        // Convert AM/PM to 24-hour format
        if (parts[2].equals("pm")) {
            if (hours < 12) {
                hours += 12;
            }
        } else { // am
            if (hours == 12) {
                hours = 0;
            }
        }
        
        // Round to nearest 5-minute increment
        int nearest = minutes % 5;
        if (nearest < 3) {
            minutes -= nearest;
        } else {
            if (!isEnd) {
                return addDelta(encodeTime(day, hours, minutes), 5 - nearest);
            }
        }
        
        return encodeTime(day, hours, minutes);
    }
    
    private static long addDelta(long encodedTime, int deltaMinutes) {
        int day = (int) (encodedTime / 10000);
        int hours = (int) ((encodedTime / 100) % 100);
        int minutes = (int) (encodedTime % 100);
        
        minutes += deltaMinutes;
        if (minutes >= 60) {
            minutes = 0;
            hours++;
            if (hours >= 24) {
                hours = 0;
                day++;
                if (day > 7) {
                    day = 1;
                }
            }
        }
        
        return encodeTime(day, hours, minutes);
    }
    
    private static long encodeTime(int day, int hours, int minutes) {
        return day * 10000L + hours * 100L + minutes;
    }
    
    private static String formatTime(long encodedTime) {
        int day = (int) (encodedTime / 10000);
        int hours = (int) ((encodedTime / 100) % 100);
        int minutes = (int) (encodedTime % 100);
        
        String[] dayNames = {"", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        return String.format("%s %02d:%02d", dayNames[day], hours, minutes);
    }
    
    public static void main(String[] args) {

//        System.out.println(29 % 5);

        String[] times = {"tue 00:29 am", "mon 11:56 pm"};

        // Debug the time parsing
        long start = cleanTime(times[0], false);
        long end = cleanTime(times[1], true);

        System.out.println("Start: " + start + " -> " + formatTime(start));
        System.out.println("End: " + end + " -> " + formatTime(end));

        List<Long> slots = getIncrements(times);

        System.out.println("First 10 slots:");
        for (int i = 0; i < Math.min(10, slots.size()); i++) {
            System.out.println(slots.get(i) + " -> " + formatTime(slots.get(i)));
        }

        System.out.println("Last 10 slots:");
        for (int i = Math.max(0, slots.size() - 10); i < slots.size(); i++) {
            System.out.println(slots.get(i) + " -> " + formatTime(slots.get(i)));
        }

        System.out.println("Total slots: " + slots.size());
    }
} 