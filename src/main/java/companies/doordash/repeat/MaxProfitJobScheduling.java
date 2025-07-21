package companies.doordash.repeat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class MaxProfitJobScheduling {

    record Job(int start, int end, int profit) {}

    record Schedule(int end, int profit, List<Integer> jobsIds) {}

    public int jobScheduling(int[] startTime, int[] endTime, int[] profit) {
        List<Job> jobs = new ArrayList<>();

        for (int i = 0; i < startTime.length; i++) {
            jobs.add(new Job(startTime[i], endTime[i], profit[i]));
        }

        Collections.sort(jobs, (j1, j2) -> j1.start() - j2.start());

        int maxProfit = 0;

        Queue<Job> minHeap = new PriorityQueue<>((j1, j2) -> j1.end() - j2.end());

        for (int i = 0; i < jobs.size(); i++) {
            var job = jobs.get(i);
            int start = job.start();
            int end = job.end();
            int jobProfit = job.profit();

            while (!minHeap.isEmpty() && start >= minHeap.peek().end()) {
                var polledJob = minHeap.poll();
                maxProfit = Math.max(maxProfit, polledJob.profit());
            }

            minHeap.offer(new Job(start, end, jobProfit + maxProfit));
        }

        while (!minHeap.isEmpty()) {
            maxProfit = Math.max(maxProfit, minHeap.poll().profit());
        }

        return maxProfit;
    }

    public int jobScheduling_(int[] startTime, int[] endTime, int[] profit) {
        List<Job> jobs = new ArrayList<>();

        for (int i = 0; i < startTime.length; i++) {
            jobs.add(new Job(startTime[i], endTime[i], profit[i]));
        }

        Collections.sort(jobs, (j1, j2) -> j1.start() - j2.start());

        int maxProfit = 0;

        Queue<Schedule> minHeap = new PriorityQueue<>((j1, j2) -> j1.end() - j2.end());

        Schedule bestSchedule = new Schedule(0, 0, new ArrayList<>());

        for (int i = 0; i < jobs.size(); i++) {
            var job = jobs.get(i);
            int start = job.start();
            int end = job.end();
            int jobProfit = job.profit();

            while (!minHeap.isEmpty() && start >= minHeap.peek().end()) {
                var polledJob = minHeap.poll();
                if (polledJob.profit > bestSchedule.profit()) {
                    bestSchedule = polledJob;
                }
//                maxProfit = Math.max(maxProfit, polledJob.profit());
            }
            List<Integer> jobIds = new ArrayList<>(bestSchedule.jobsIds);
            jobIds.add(i);
            Schedule schedule = new Schedule(end, jobProfit + bestSchedule.profit(), jobIds);
            minHeap.offer(schedule);
        }

        while (!minHeap.isEmpty()) {
            var polledJob = minHeap.poll();
            if (polledJob.profit > bestSchedule.profit()) {
                bestSchedule = polledJob;
            }
//            maxProfit = Math.max(maxProfit, minHeap.poll().profit());
        }
        System.out.println(bestSchedule.jobsIds);
        return bestSchedule.profit();
    }

    public static void main(String[] args) {
        var sol = new MaxProfitJobScheduling();
        System.out.println(
                sol.jobScheduling(
                        new int[] {1,2,3,3},
                        new int[] {3,4,5,6},
                        new int[] {50,10,40,70}
                )
        );

        System.out.println(
                sol.jobScheduling_(
                        new int[] {1,2,3,3},
                        new int[] {3,4,5,6},
                        new int[] {50,10,40,70}
                )
        );

        System.out.println(
                sol.jobScheduling(
                        new int[] {1,2,3,4,6},
                        new int[] {3,5,10,6,9},
                        new int[] {20,20,100,70,60}
                )
        );

        System.out.println(
                sol.jobScheduling_(
                        new int[] {1,2,3,4,6},
                        new int[] {3,5,10,6,9},
                        new int[] {20,20,100,70,60}
                )
        );
    }
}
