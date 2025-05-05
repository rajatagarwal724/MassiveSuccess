package companies.splunk;

import java.util.Arrays;
import java.util.Comparator;

public class MeetingRooms {

    public boolean canAttendMeetings(int[][] intervals) {
        if (null == intervals || intervals.length < 2) {
            return true;
        }
        Arrays.sort(intervals, (o1, o2) -> o1[0] - o2[0]);
        Arrays.sort(intervals, Comparator.comparingInt(o -> o[0]));

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] < intervals[i-1][1]) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        var sol = new MeetingRooms();
        int[][] scheds = new int[][]{
                {0, 30},
                {5, 10},
                {15, 20}
        };
        System.out.println(sol.canAttendMeetings(scheds));
        scheds = new int[][] {
                {7, 10},
                {2, 4}
        };
        System.out.println(sol.canAttendMeetings(scheds));
    }
}
