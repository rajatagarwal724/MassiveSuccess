package companies.doordash.repeat;

import java.util.*;

/**
 * Generate time slots in 5-minute increments between two given times
 * 
 * Problem: Given start and end times with day/hour/minute, generate all 
 * available time slots in 5-minute increments, handling day overflow
 * 
 * Time Complexity: O(n) where n = number of 5-minute slots between start and end
 * Space Complexity: O(n) for storing the result list
 */
public class TimeSlotGenerator {
    
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
    
    /**
     * Generate incremental time slots between start and end times
     * @param times Array of two time strings ["start_time", "end_time"]
     * @return List of time slots as integers
     */
    public List<Integer> getIncrements(String[] times) {
        long start = cleanTime(times[0], false);
        long end = cleanTime(times[1], true);
        
        // Create end borders (1-5 minutes after end time)
        Set<Long> endBorders = new HashSet<>();
        for (int minutes = 1; minutes <= 5; minutes++) {
            endBorders.add(addDelta(end, minutes));
        }
        
        List<Integer> result = new ArrayList<>();
        long current = start;
        
        // Generate slots until we hit an end border
        while (!endBorders.contains(current)) {
            result.add((int) current);
            current = addDelta(current, 5);
        }
        
        return result;
    }
    
    /**
     * Clean and normalize time string to numerical format
     * @param timeStr Time string like "tue 00:29 am"
     * @param isEnd Whether this is the end time
     * @return Normalized time as long (format: DHHMMM)
     *
     * "tue 00:29 am", "mon 11:56 pm"
     */
    private long cleanTime(String timeStr, boolean isEnd) {
        String[] parts = timeStr.split(" ");
        String dayStr = parts[0];
        String[] hourMin = parts[1].split(":");
        String amPm = parts[2];
        
        int day = DAYS.get(dayStr.toLowerCase());
        int hours = Integer.parseInt(hourMin[0]);
        int minutes = Integer.parseInt(hourMin[1]);
        
        // Convert to 24-hour format
        if (amPm.equals("pm")) {
            if (hours < 12) {
                hours += 12;
            }
        } else { // am
            if (hours == 12) {
                hours = 0;
            }
        }
        
        // Round to nearest 5-minute increment
        int remainder = minutes % 5;
        if (remainder < 3) {
            minutes -= remainder;
        } else {
            if (!isEnd) {
                // For start time, round up by adding the remaining minutes
                return addDelta(formatTime(day, hours, minutes), 5 - remainder);
            } else {
                // For end time, round down
                minutes -= remainder;
            }
        }
        
        return formatTime(day, hours, minutes);
    }
    
    /**
     * Add delta minutes to a time, handling overflow
     * @param time Current time in DHHMMM format
     * @param deltaMinutes Minutes to add
     * @return New time after adding delta
     */
    private long addDelta(long time, int deltaMinutes) {
        int day = (int) (time / 10000);
        int hours = (int) ((time % 10000) / 100);
        int minutes = (int) (time % 100);
        
        minutes += deltaMinutes;
        
        // Handle minute overflow
        if (minutes >= 60) {
            minutes = 0;
            hours++;
            
            // Handle hour overflow
            if (hours >= 24) {
                hours = 0;
                day++;
                
                // Handle day overflow (week cycle)
                if (day > 7) {
                    day = 1;
                }
            }
        }
        
        return formatTime(day, hours, minutes);
    }
    
    /**
     * Format time components into DHHMMM format
     */
    private long formatTime(int day, int hours, int minutes) {
        return day * 10000 + hours * 100 + minutes;
    }
    
    /**
     * Convert time back to readable format for debugging
     */
    public String timeToString(long time) {
        int day = (int) (time / 10000);
        int hours = (int) ((time % 10000) / 100);
        int minutes = (int) (time % 100);
        
        String[] dayNames = {"", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        String amPm = hours >= 12 ? "PM" : "AM";
        int displayHours = hours == 0 ? 12 : (hours > 12 ? hours - 12 : hours);
        
        return String.format("%s %02d:%02d %s", 
                           dayNames[day], displayHours, minutes, amPm);
    }
    
    /**
     * Enhanced version with custom increment and better time handling
     */
    public List<Integer> getIncrementsCustom(String[] times, int incrementMinutes) {
        long start = cleanTime(times[0], false);
        long end = cleanTime(times[1], true);
        
        Set<Long> endBorders = new HashSet<>();
        for (int minutes = 1; minutes <= incrementMinutes; minutes++) {
            endBorders.add(addDelta(end, minutes));
        }
        
        List<Integer> result = new ArrayList<>();
        long current = start;
        int maxSlots = 10000; // Safety limit to prevent infinite loops
        int slotCount = 0;
        
        while (!endBorders.contains(current) && slotCount < maxSlots) {
            result.add((int) current);
            current = addDelta(current, incrementMinutes);
            slotCount++;
        }
        
        return result;
    }
    
    /**
     * Get time slots with readable string format
     */
    public List<String> getReadableTimeSlots(String[] times) {
        List<Integer> slots = getIncrements(times);
        List<String> readable = new ArrayList<>();
        
        for (Integer slot : slots) {
            readable.add(timeToString(slot));
        }
        
        return readable;
    }
    
    /**
     * Validate if a time slot is within business hours
     */
    public boolean isBusinessHours(long time) {
        int hours = (int) ((time % 10000) / 100);
        int day = (int) (time / 10000);
        
        // Monday to Friday, 9 AM to 5 PM
        return day >= 1 && day <= 5 && hours >= 9 && hours < 17;
    }
    
    /**
     * Filter time slots to only include business hours
     */
    public List<Integer> getBusinessHourSlots(String[] times) {
        List<Integer> allSlots = getIncrements(times);
        List<Integer> businessSlots = new ArrayList<>();
        
        for (Integer slot : allSlots) {
            if (isBusinessHours(slot)) {
                businessSlots.add(slot);
            }
        }
        
        return businessSlots;
    }
    
    public static void main(String[] args) {
        TimeSlotGenerator generator = new TimeSlotGenerator();
        
        System.out.println("=== TIME SLOT GENERATOR ===");
        
        // Original Python example for exact comparison
        String[] originalExample = {"mon 11:56 pm", "tue 00:29 am"};
        System.out.println("Original Python Example: " + Arrays.toString(originalExample));
        List<Integer> originalSlots = generator.getIncrements(originalExample);
        System.out.println("Total slots generated: " + originalSlots.size());
        System.out.println("First 5 slots: " + originalSlots.subList(0, Math.min(5, originalSlots.size())));
        System.out.println("Last 5 slots: " + originalSlots.subList(Math.max(0, originalSlots.size() - 5), originalSlots.size()));
        
        // Show some readable times for verification
        System.out.println("First 5 readable times:");
        for (int i = 0; i < Math.min(500, originalSlots.size()); i++) {
            System.out.println("  " + originalSlots.get(i) + " -> " + generator.timeToString(originalSlots.get(i)));
        }
        System.out.println();
        
//        // Test case 1: Original example
//        String[] times1 = {"tue 00:29 am", "mon 11:56 pm"};
//        System.out.println("Test 1: " + Arrays.toString(times1));
//        List<Integer> slots1 = generator.getIncrements(times1);
//        System.out.println("Generated slots: " + slots1.size());
//        System.out.println("First 10 slots: " + slots1.subList(0, Math.min(10, slots1.size())));
//        System.out.println("Last 10 slots: " + slots1.subList(Math.max(0, slots1.size() - 10), slots1.size()));
//        System.out.println();
//
//        // Test case 2: Same day
//        String[] times2 = {"mon 09:00 am", "mon 05:00 pm"};
//        System.out.println("Test 2: " + Arrays.toString(times2));
//        List<Integer> slots2 = generator.getIncrements(times2);
//        System.out.println("Generated slots: " + slots2.size());
//        System.out.println("All slots: " + slots2);
//        System.out.println();
//
//        // Test case 3: Readable format
//        String[] times3 = {"wed 10:13 am", "wed 02:47 pm"};
//        System.out.println("Test 3 (Readable): " + Arrays.toString(times3));
//        List<String> readableSlots = generator.getReadableTimeSlots(times3);
//        System.out.println("Readable slots (" + readableSlots.size() + "):");
//        for (int i = 0; i < Math.min(10, readableSlots.size()); i++) {
//            System.out.println("  " + readableSlots.get(i));
//        }
//        System.out.println();
//
//        // Test case 4: Business hours only
//        String[] times4 = {"mon 08:00 am", "fri 06:00 pm"};
//        System.out.println("Test 4 (Business Hours): " + Arrays.toString(times4));
//        List<Integer> businessSlots = generator.getBusinessHourSlots(times4);
//        System.out.println("Business hour slots: " + businessSlots.size());
//        System.out.println("Sample business slots: " + businessSlots.subList(0, Math.min(5, businessSlots.size())));
//        System.out.println();
//
//        // Test case 5: Custom increment
//        String[] times5 = {"thu 01:00 pm", "thu 03:00 pm"};
//        System.out.println("Test 5 (15-minute increment): " + Arrays.toString(times5));
//        List<Integer> customSlots = generator.getIncrementsCustom(times5, 15);
//        System.out.println("Custom increment slots: " + customSlots);
//        for (Integer slot : customSlots) {
//            System.out.println("  " + generator.timeToString(slot));
//        }
    }
} 