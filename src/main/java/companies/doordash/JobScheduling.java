package companies.doordash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class JobScheduling {

    class The_Comparator implements Comparator<ArrayList<Integer>> {
        public int compare(ArrayList<Integer> list1, ArrayList<Integer> list2) {
            return list1.get(0) - list2.get(0);
        }
    }

    private int findMaxProfit(List<List<Integer>> jobs) {
        int n = jobs.size();
        int maxProfit = 0;
        PriorityQueue<List<Integer>> minHeapByEndTime = new PriorityQueue<>((l1, l2) -> l1.get(0) - l2.get(0));

        for (List<Integer> job : jobs) {
            var start = job.get(0);
            var end = job.get(1);
            var profit = job.get(2);

            while (!minHeapByEndTime.isEmpty() && start >= minHeapByEndTime.peek().get(0)) {
                maxProfit = Math.max(maxProfit, minHeapByEndTime.peek().get(1));
                minHeapByEndTime.poll();
            }

            profit = profit + maxProfit;
            minHeapByEndTime.offer(List.of(end, profit));
        }

        while (!minHeapByEndTime.isEmpty()) {
            maxProfit = Math.max(maxProfit, minHeapByEndTime.peek().get(1));
            minHeapByEndTime.poll();
        }

        return maxProfit;
    }

    public int jobScheduling(int[] startTime, int[] endTime, int[] profit) {
        List<List<Integer>> jobs = new ArrayList<>();

        for (int i = 0; i < startTime.length; i++) {
            jobs.add(List.of(startTime[i], endTime[i], profit[i]));
        }

        Collections.sort(jobs, (l1, l2) -> l1.get(0) - l2.get(0));

        return findMaxProfit(jobs);
    }

    public static void main(String[] args) {
        var sol = new JobScheduling();
        System.out.println(
                sol.jobScheduling(
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
    }
}
