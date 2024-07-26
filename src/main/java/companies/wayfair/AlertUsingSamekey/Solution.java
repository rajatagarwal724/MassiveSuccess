package companies.wayfair.AlertUsingSamekey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * https://leetcode.com/problems/alert-using-same-key-card-three-or-more-times-in-a-one-hour-period/description/
 *
 * LeetCode company workers use key-cards to unlock office doors. Each time a worker uses their key-card, the security system saves the worker's name and the time when it was used. The system emits an alert if any worker uses the key-card three or more times in a one-hour period.
 *
 * You are given a list of strings keyName and keyTime where [keyName[i], keyTime[i]] corresponds to a person's name and the time when their key-card was used in a single day.
 *
 * Access times are given in the 24-hour time format "HH:MM", such as "23:51" and "09:49".
 *
 * Return a list of unique worker names who received an alert for frequent keycard use. Sort the names in ascending order alphabetically.
 *
 * Notice that "10:00" - "11:00" is considered to be within a one-hour period, while "22:51" - "23:52" is not considered to be within a one-hour period.
 *
 *
 *
 * Example 1:
 *
 * Input: keyName = ["daniel","daniel","daniel","luis","luis","luis","luis"], keyTime = ["10:00","10:40","11:00","09:00","11:00","13:00","15:00"]
 * Output: ["daniel"]
 * Explanation: "daniel" used the keycard 3 times in a one-hour period ("10:00","10:40", "11:00").
 * Example 2:
 *
 * Input: keyName = ["alice","alice","alice","bob","bob","bob","bob"], keyTime = ["12:01","12:00","18:00","21:00","21:20","21:30","23:00"]
 * Output: ["bob"]
 * Explanation: "bob" used the keycard 3 times in a one-hour period ("21:00","21:20", "21:30").
 *
 *
 * Constraints:
 *
 * 1 <= keyName.length, keyTime.length <= 105
 * keyName.length == keyTime.length
 * keyTime[i] is in the format "HH:MM".
 * [keyName[i], keyTime[i]] is unique.
 * 1 <= keyName[i].length <= 10
 * keyName[i] contains only lowercase English letters.
 *
 */
public class Solution {
    public List<String> alertNames(String[] keyName, String[] keyTime) {
        Map<String, Set<Integer>> alertMap = new TreeMap<>();
        List<String> result = new ArrayList<>();
        for (int index = 0; index < keyName.length; index++) {
            String name = keyName[index];
            String time = keyTime[index];
            int minutes = getTimeInMinutes(time);
            Set<Integer> times = alertMap.getOrDefault(name, new TreeSet<>());
            times.add(minutes);
            alertMap.put(name, times);
        }

        for (Map.Entry<String, Set<Integer>> entry: alertMap.entrySet()) {
            List<Integer> times = new ArrayList<>(entry.getValue());
            for (int i = 0; i < times.size() - 2; i++) {
                if (Math.abs(times.get(i + 2) - times.get(i)) <= 60) {
                    result.add(entry.getKey());
                    break;
                }
            }
        }
        return result;
    }

    private Integer getTimeInMinutes(String time) {
        String[] arr = time.split(":");
        return Integer.parseInt(arr[0]) * 60 + Integer.parseInt(arr[1]);
    }

    public static void main(String[] args) {
        var solution = new Solution();
//        solution.alertNames(
//                new String[]{"daniel","daniel","daniel","luis","luis","luis","luis"},
//                new String[]{"10:00","10:40","11:00","09:00","11:00","13:00","15:00"}
//                ).forEach(System.out::println);
//        System.out.println("######################");
//        solution.alertNames(
//                new String[]{"alice","alice","alice","bob","bob","bob","bob"},
//                new String[]{"12:01","12:00","18:00","21:00","21:20","21:30","23:00"}
//        ).forEach(System.out::println);
//        System.out.println("######################");
//        solution.alertNames(
//                new String[]{"john","john","john"},
//                new String[]{"23:58","23:59","00:01"}
//        ).forEach(System.out::println);
//        System.out.println("######################");
        solution.alertNames(
                new String[]{"a","a","a","a","a","b","b","b","b","b","b"},
                new String[]{"04:48","23:53","06:36","07:45","12:16","00:52","10:59","17:16","00:36","01:26","22:42"}
        ).forEach(System.out::println);
        System.out.println("######################");
    }
}
