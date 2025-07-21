package companies.doordash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class Job {
    int start;
    int end;
    int profit;

    public Job(int start, int end, int profit) {
        this.start = start;
        this.end = end;
        this.profit = profit;
    }
}
public class MaxProfitInJobScheduling {

    public int jobScheduling(int[] startTime, int[] endTime, int[] profit) {
        List<Job> jobs = new ArrayList<>();
        for (int i = 0; i < startTime.length; i++) {
            jobs.add(new Job(startTime[i], endTime[i], profit[i]));
        }

        Collections.sort(jobs, (o1, o2) -> o1.end - o2.end);

        int[] dp = new int[startTime.length];

        dp[0] = jobs.get(0).profit;

        for (int i = 1; i < jobs.size(); i++) {
            int profitWithoutCurrent = dp[ i - 1];
            int profitWithCurrent = jobs.get(i).profit;

            int latestConflictingSchedule = findLatestNonConflictingSchedule(jobs, i);
            if (latestConflictingSchedule != -1) {
                profitWithCurrent += dp[latestConflictingSchedule];
            }

            dp[i] = Math.max(profitWithCurrent, profitWithoutCurrent);
        }

        return dp[dp.length - 1];
    }

    private int findLatestNonConflictingSchedule(List<Job> jobs, int i) {
        int result = -1, left = 0, right = i - 1;

        while(left <= right) {
            int mid = left + (right - left)/2;

            if (jobs.get(mid).end <= jobs.get(i).start) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        var sol = new MaxProfitInJobScheduling();
        System.out.println(
                sol.jobScheduling(
                        new int[] {1,2,3,3},
                        new int[] {3,4,5,6},
                        new int[] {50,10,40,70}
                )
        );
    }
}
