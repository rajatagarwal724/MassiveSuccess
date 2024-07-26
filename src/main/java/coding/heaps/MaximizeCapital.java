package coding.heaps;

import java.util.PriorityQueue;
import java.util.Queue;

public class MaximizeCapital {

    public static int findMaximumCapital(
            int[] capital,
            int[] profits,
            int numberOfProjects,
            int initialCapital
    ) {
        Queue<Integer> minCapital = new PriorityQueue<>((o1, o2) -> capital[o1] - capital[o2]);
        Queue<Integer> maxProfit = new PriorityQueue<>((o1, o2) -> profits[o2] - profits[o1]);

        int noOfProjectsProcessed = 0;
        int totalRevenue = initialCapital;

        int indexProcessed = 0;

        while (noOfProjectsProcessed < numberOfProjects) {

            for (int index = indexProcessed; index < capital.length && totalRevenue >= capital[index]; index++) {
                minCapital.offer(index);
                maxProfit.offer(index);
//                totalRevenue = totalRevenue - capital[index];
            }

            if (!maxProfit.isEmpty()) {
                int indexOfProjectSelected = maxProfit.poll();
                int profit = profits[indexOfProjectSelected];
//                int capitalUsed = capital[indexOfProjectSelected];
                minCapital.remove(indexOfProjectSelected);
                totalRevenue = totalRevenue + profit;
                noOfProjectsProcessed++;
            }
        }

        return totalRevenue;
    }

    public static void main(String[] args) {
        System.out.println(MaximizeCapital.findMaximumCapital(new int[]{0,1,2}, new int[]{1,2,3}, 2, 1));
        System.out.println(MaximizeCapital.findMaximumCapital(new int[]{0,1,2,3}, new int[]{1,2,3,5}, 3, 0));
        System.out.println(MaximizeCapital.findMaximumCapital(new int[]{0, 1, 2, 3}, new int[]{4, 3, 2, 1}, 3, 0));
    }
}
