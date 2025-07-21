package coding.linkedin;

import java.util.Arrays;

/**
 * LeetCode 1235: Maximum Profit in Job Scheduling
 * 
 * We have n jobs, where every job is scheduled to be done from startTime[i] to endTime[i], 
 * obtaining a profit of profit[i].
 * 
 * You're given the startTime, endTime and profit arrays, return the maximum profit you can 
 * take such that there are no two jobs in the subset with overlapping time range.
 * 
 * If you choose a job that ends at time X you will be able to start another job that starts at time X.
 * 
 * Time Complexity: O(n log n) - sorting + binary search for each job
 * Space Complexity: O(n) - for the dp array and job objects
 */
public class MaximumProfitJobScheduling {
    
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
        
        // Sort jobs by end time
        Arrays.sort(jobs, (a, b) -> a.end - b.end);
        
        // dp[i] represents the maximum profit we can get from jobs 0 to i
        int[] dp = new int[n];
        dp[0] = jobs[0].profit;
        
        for (int i = 1; i < n; i++) {
            // Option 1: Don't take current job
            int profitWithoutCurrent = dp[i - 1];
            
            // Option 2: Take current job
            int profitWithCurrent = jobs[i].profit;
            
            // Find the latest job that doesn't conflict with current job
            int latestNonConflictingJob = findLatestNonConflictingJob(jobs, i);
            
            if (latestNonConflictingJob != -1) {
                profitWithCurrent += dp[latestNonConflictingJob];
            }
            
            // Take maximum of both options
            dp[i] = Math.max(profitWithoutCurrent, profitWithCurrent);
        }
        
        return dp[n - 1];
    }
    
    /**
     * Binary search to find the latest job that ends before or at the start time of job at index i
     */
    private int findLatestNonConflictingJob(Job[] jobs, int i) {
        int left = 0, right = i - 1;
        int result = -1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (jobs[mid].end <= jobs[i].start) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        MaximumProfitJobScheduling solution = new MaximumProfitJobScheduling();
        
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
    }
} 