package coding.linkedin;

import java.util.*;

/**
 * Alternative solution for Maximum Profit in Job Scheduling using Priority Queue
 * 
 * This approach uses a greedy strategy with a priority queue to keep track of
 * the best profit achievable at each time point.
 * 
 * Time Complexity: O(n log n) - sorting + priority queue operations
 * Space Complexity: O(n) - for priority queue and job objects
 */
public class MaximumProfitJobSchedulingPQ {
    
    static class Job {
        int start;
        int end;
        int profit;
        
        Job(int start, int end, int profit) {
            this.start = start;
            this.end = end;
            this.profit = profit;
        }
    }
    
    public int jobScheduling(int[] startTime, int[] endTime, int[] profit) {
        int n = startTime.length;
        Job[] jobs = new Job[n];
        
        // Create job objects
        for (int i = 0; i < n; i++) {
            jobs[i] = new Job(startTime[i], endTime[i], profit[i]);
        }
        
        // Sort jobs by start time
        Arrays.sort(jobs, (a, b) -> a.start - b.start);
        
        // Priority queue to store [endTime, maxProfit] pairs
        // Min heap based on end time
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        
        int maxProfit = 0;
        
        for (Job job : jobs) {
            // Remove all jobs that have ended before current job starts
            // and update maxProfit with the best profit seen so far
            while (!pq.isEmpty() && pq.peek()[0] <= job.start) {
                maxProfit = Math.max(maxProfit, pq.poll()[1]);
            }
            
            // Add current job with profit = maxProfit + current job's profit
            pq.offer(new int[]{job.end, maxProfit + job.profit});
        }
        
        // Process remaining jobs in priority queue
        while (!pq.isEmpty()) {
            maxProfit = Math.max(maxProfit, pq.poll()[1]);
        }
        
        return maxProfit;
    }
    
    /**
     * Alternative approach using TreeMap for better understanding
     */
    public int jobSchedulingTreeMap(int[] startTime, int[] endTime, int[] profit) {
        int n = startTime.length;
        Job[] jobs = new Job[n];
        
        for (int i = 0; i < n; i++) {
            jobs[i] = new Job(startTime[i], endTime[i], profit[i]);
        }
        
        // Sort by start time
        Arrays.sort(jobs, (a, b) -> a.start - b.start);
        
        // TreeMap to store time -> maxProfit mapping
        TreeMap<Integer, Integer> dp = new TreeMap<>();
        dp.put(0, 0); // Base case
        
        for (Job job : jobs) {
            // Find the maximum profit we can achieve before this job starts
            int prevProfit = dp.floorEntry(job.start).getValue();
            
            // Current profit if we take this job
            int currentProfit = prevProfit + job.profit;
            
            // Update the map with the best profit at job's end time
            dp.put(job.end, Math.max(dp.getOrDefault(job.end, 0), currentProfit));
            
            // Clean up: remove entries that are dominated by later entries
            Integer nextKey = dp.higherKey(job.end);
            while (nextKey != null && dp.get(nextKey) <= currentProfit) {
                Integer toRemove = nextKey;
                nextKey = dp.higherKey(nextKey);
                dp.remove(toRemove);
            }
        }
        
        return dp.isEmpty() ? 0 : Collections.max(dp.values());
    }
    
    public static void main(String[] args) {
        MaximumProfitJobSchedulingPQ solution = new MaximumProfitJobSchedulingPQ();
        
        System.out.println("=== Priority Queue Approach ===");
        
        // Test case 1: Expected output = 120
        System.out.println("Test 1: " + solution.jobScheduling(
            new int[]{1, 2, 3, 3}, 
            new int[]{3, 4, 5, 6}, 
            new int[]{50, 10, 40, 70}
        ));
        
        // Test case 2: Expected output = 150
        System.out.println("Test 2: " + solution.jobScheduling(
            new int[]{1, 2, 3, 4, 6}, 
            new int[]{3, 5, 10, 6, 9}, 
            new int[]{20, 20, 100, 70, 60}
        ));
        
        // Test case 3: Expected output = 6
        System.out.println("Test 3: " + solution.jobScheduling(
            new int[]{1, 1, 1}, 
            new int[]{2, 3, 4}, 
            new int[]{5, 6, 4}
        ));
        
        System.out.println("\n=== TreeMap Approach ===");
        
        // Test the TreeMap approach
        System.out.println("Test 1: " + solution.jobSchedulingTreeMap(
            new int[]{1, 2, 3, 3}, 
            new int[]{3, 4, 5, 6}, 
            new int[]{50, 10, 40, 70}
        ));
        
        System.out.println("Test 2: " + solution.jobSchedulingTreeMap(
            new int[]{1, 2, 3, 4, 6}, 
            new int[]{3, 5, 10, 6, 9}, 
            new int[]{20, 20, 100, 70, 60}
        ));
        
        System.out.println("Test 3: " + solution.jobSchedulingTreeMap(
            new int[]{1, 1, 1}, 
            new int[]{2, 3, 4}, 
            new int[]{5, 6, 4}
        ));
    }
} 