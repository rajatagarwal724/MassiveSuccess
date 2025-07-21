package companies.doordash.repeat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MostProfitAssigningWork {

    class Job {
        int difficulty;
        int profit;

        public Job(int difficulty, int profit) {
            this.difficulty = difficulty;
            this.profit = profit;
        }
    }

    public int maxProfitAssignment(int[] difficulty, int[] profit, int[] worker) {
        List<Job> jobs = new ArrayList<>();
        for (int i = 0; i < difficulty.length; i++) {
            jobs.add(new Job(difficulty[i], profit[i]));
        }

        Collections.sort(jobs, (j1, j2) -> j1.difficulty);

        for (int i = 1; i < jobs.size(); i++) {
            jobs.get(i).profit = Math.max(jobs.get(i - 1).profit, jobs.get(i).profit);
        }
        int totalProfit = 0;

        for (int ability: worker) {
            int jobIdx = findBestJob(jobs, ability);
            if (jobIdx != -1) {
                totalProfit += jobs.get(jobIdx).profit;
            }
        }

        return totalProfit;
    }

    private int findBestJob(List<Job> jobs, int ability) {
        int left = 0, right = jobs.size() - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left)/2;
            if (jobs.get(mid).difficulty <= ability) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    public int maxProfitAssignment_(int[] difficulty, int[] profit, int[] worker) {
        TreeMap<Integer, Integer> map = new TreeMap<>();

        for (int i = 0; i < difficulty.length; i++) {
            map.put(
                    difficulty[i],
                    Math.max(map.getOrDefault(difficulty[i], 0), profit[i])
            );
        }

        int maxProfitValue = 0;
        for (Map.Entry<Integer, Integer> entry: map.entrySet()) {
            maxProfitValue = Math.max(maxProfitValue, entry.getValue());
            entry.setValue(maxProfitValue);
        }

        int totalProfit = 0;

        for (int ability: worker) {
            Map.Entry<Integer, Integer> floorEntry = map.floorEntry(ability);
            if (null != floorEntry) {
                totalProfit += floorEntry.getValue();
            }
        }

        return totalProfit;
    }

    public static void main(String[] args) {
        var sol = new MostProfitAssigningWork();

        System.out.println(
                sol.maxProfitAssignment(
                        new int[] {2,4,6,8,10},
                        new int[] {10,20,30,40,50},
                        new int[] {4,5,6,7}
                )
        );

        System.out.println(
                sol.maxProfitAssignment(
                        new int[] {85,47,57},
                        new int[] {24,66,99},
                        new int[] {40,25,25}
                )
        );

        System.out.println(
                sol.maxProfitAssignment_(
                        new int[] {2,4,6,8,10},
                        new int[] {10,20,30,40,50},
                        new int[] {4,5,6,7}
                )
        );

        System.out.println(
                sol.maxProfitAssignment_(
                        new int[] {85,47,57},
                        new int[] {24,66,99},
                        new int[] {40,25,25}
                )
        );
    }
}
