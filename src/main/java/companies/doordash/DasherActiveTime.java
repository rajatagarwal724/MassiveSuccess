package companies.doordash;

import java.util.*;

public class DasherActiveTime {
    
    public static int getActiveTime(List<String> activity) {
        List<Integer> pickups = new ArrayList<>();
        List<Integer> dropoffs = new ArrayList<>();
        int smallestPickup = Integer.MAX_VALUE;
        int highestDropoff = Integer.MIN_VALUE;
        
        for (String a : activity) {
            String[] parts = a.split("\\|");
            String type = parts[1].trim();
            int minutes = getMinutes(parts[0].trim());
            
            if (type.equals("pickup")) {
                pickups.add(minutes);
                smallestPickup = Math.min(smallestPickup, minutes);
            } else {
                dropoffs.add(minutes);
                highestDropoff = Math.max(highestDropoff, minutes);
            }
        }
        
        List<int[]> intervals = new ArrayList<>();
        for (int i = 0; i < pickups.size(); i++) {
            intervals.add(new int[]{pickups.get(i), dropoffs.get(i)});
        }
        
        int totalTime = highestDropoff - smallestPickup;
        int idleTime = 0;
        
        for (int i = 0; i < intervals.size() - 1; i++) {
            int[] curr = intervals.get(i);
            int[] next = intervals.get(i + 1);
            
            if (curr[1] < next[0]) {
                idleTime += next[0] - curr[1];
            } else {
                next[1] = Math.max(next[1], curr[1]);
            }
        }
        
        return totalTime - idleTime;
    }
    
    public static int getMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        String minutesPart = parts[1];
        
        int minutes = Integer.parseInt(minutesPart.substring(0, minutesPart.length() - 2));
        String ampm = minutesPart.substring(minutesPart.length() - 2);
        
        if (ampm.equals("pm")) {
            if (hours < 12) {
                hours += 12;
            }
        } else { // am
            if (hours == 12) {
                hours = 0;
            }
        }
        
        return 60 * hours + minutes;
    }
    
    public static void main(String[] args) {
        List<String> activity = Arrays.asList(
            "8:30am | pickup",
            "9:10am | dropoff",
            "10:20am| pickup",
            "12:15pm| pickup",
            "12:45pm| dropoff",
            "2:25pm | dropoff"
        );
        
        int activeTime = getActiveTime(activity);
        System.out.println("Active time: " + activeTime + " minutes");
    }
} 