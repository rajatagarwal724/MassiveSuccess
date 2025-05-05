package companies.splunk;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class MeetingRoomsII {

    public int minMeetingRooms(int[][] intervals) {
        int res = 1;
        if (intervals.length < 2) {
            return res;
        }
        Arrays.sort(intervals, (i1, i2) -> (i1[0] - i2[0]));
        Queue<int[]> minHeapByEndTime = new PriorityQueue<>(
                (i1, i2) -> (i1[1] - i2[1])
        );
        minHeapByEndTime.offer(intervals[0]);

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] >= minHeapByEndTime.peek()[1]) {
                minHeapByEndTime.poll();
            }
            minHeapByEndTime.offer(intervals[i]);
            res = Math.max(res, minHeapByEndTime.size());
        }
        return res;
    }

    public static void main(String[] args) {
        var sol = new MeetingRoomsII();
        System.out.println(sol.minMeetingRooms(new int[][]{{0, 30}, {5, 10}, {15, 20}}));
        System.out.println(sol.minMeetingRooms(new int[][]{{7, 10}, {2, 4}}));
        System.out.println(sol.minMeetingRooms(new int[][]{{9, 10}, {4, 9}, {4, 17}}));
    }
}
