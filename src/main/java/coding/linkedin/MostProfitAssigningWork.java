package coding.linkedin;

import java.util.*;

/**
 * LeetCode 826: Most Profit Assigning Work
 * 
 * You have n jobs and m workers. You are given three arrays: difficulty, profit, and worker where:
 * - difficulty[i] and profit[i] are the difficulty and the profit of the ith job, and
 * - worker[j] is the ability of the jth worker (i.e., the jth worker can only complete a job with difficulty at most worker[j]).
 * 
 * Every worker can be assigned at most one job, but one job can be completed multiple times.
 * 
 * For example, if three workers attempt the same job that pays $1, then the total profit will be $3. 
 * If a worker cannot complete any job, their profit is $0.
 * 
 * Return the maximum profit we can achieve after assigning the workers to jobs.
 * 
 * Example 1:
 * Input: difficulty = [2,4,6,8,10], profit = [10,20,30,40,50], worker = [4,5,6,10]
 * Output: 100
 * Explanation: Workers are assigned jobs of difficulty [4,4,6,10] and they get a profit of [20,20,30,50] respectively.
 * 
 * Example 2:
 * Input: difficulty = [85,47,57], profit = [24,66,99], worker = [40,25,25]
 * Output: 0
 * Explanation: All workers are not able to complete any job.
 * 
 * Time Complexity: O(n log n + m log n) where n = jobs, m = workers
 * Space Complexity: O(n) for sorting and job array
 */
public class MostProfitAssigningWork {
    
    /**
     * Approach 1: Greedy with Sorting and Binary Search
     * 
     * Algorithm:
     * 1. Create job objects and sort by difficulty
     * 2. For each job, calculate the maximum profit achievable up to that difficulty
     * 3. For each worker, binary search to find the best job they can do
     */
    public int maxProfitAssignment(int[] difficulty, int[] profit, int[] worker) {
        int n = difficulty.length;
        
        // Create job objects with difficulty and profit
        Job[] jobs = new Job[n];
        for (int i = 0; i < n; i++) {
            jobs[i] = new Job(difficulty[i], profit[i]);
        }
        
        // Sort jobs by difficulty
        Arrays.sort(jobs, (a, b) -> a.difficulty - b.difficulty);
        
        // Calculate max profit achievable at each difficulty level
        // This ensures we always pick the best job for a given difficulty
        for (int i = 1; i < n; i++) {
            jobs[i].profit = Math.max(jobs[i].profit, jobs[i-1].profit);
        }
        
        int totalProfit = 0;
        
        // For each worker, find the best job they can do
        for (int ability : worker) {
            // Binary search to find the rightmost job with difficulty <= ability
            int jobIndex = findBestJob(jobs, ability);
            if (jobIndex != -1) {
                totalProfit += jobs[jobIndex].profit;
            }
        }
        
        return totalProfit;
    }
    
    /**
     * Binary search to find the rightmost job with difficulty <= ability
     */
    private int findBestJob(Job[] jobs, int ability) {
        int left = 0, right = jobs.length - 1;
        int result = -1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (jobs[mid].difficulty <= ability) {
                result = mid;
                left = mid + 1;  // Look for a job with higher difficulty
            } else {
                right = mid - 1;
            }
        }
        
        return result;
    }
    
    /**
     * Approach 2: Two Pointers (Alternative Solution)
     * 
     * Algorithm:
     * 1. Sort jobs by difficulty and workers by ability
     * 2. Use two pointers to match workers with best available jobs
     * 
     * Time Complexity: O(n log n + m log m)
     * Space Complexity: O(n)
     */
    public int maxProfitAssignmentTwoPointers(int[] difficulty, int[] profit, int[] worker) {
        int n = difficulty.length;
        
        // Create and sort jobs
        Job[] jobs = new Job[n];
        for (int i = 0; i < n; i++) {
            jobs[i] = new Job(difficulty[i], profit[i]);
        }
        Arrays.sort(jobs, (a, b) -> a.difficulty - b.difficulty);
        
        // Calculate max profit at each difficulty level
        for (int i = 1; i < n; i++) {
            jobs[i].profit = Math.max(jobs[i].profit, jobs[i-1].profit);
        }
        
        // Sort workers by ability
        Arrays.sort(worker);
        
        int totalProfit = 0;
        int jobIndex = 0;
        
        // For each worker, find the best job they can do
        for (int ability : worker) {
            // Move job pointer to the rightmost job this worker can do
            while (jobIndex < n && jobs[jobIndex].difficulty <= ability) {
                jobIndex++;
            }
            
            // If we found any job, add the profit (jobIndex-1 is the best job)
            if (jobIndex > 0) {
                totalProfit += jobs[jobIndex - 1].profit;
            }
        }
        
        return totalProfit;
    }
    
    /**
     * Approach 3: TreeMap Solution (Alternative)
     * 
     * Uses TreeMap for efficient range queries
     */
    public int maxProfitAssignmentTreeMap(int[] difficulty, int[] profit, int[] worker) {
        // TreeMap: difficulty -> max profit achievable at that difficulty
        TreeMap<Integer, Integer> difficultyToMaxProfit = new TreeMap<>();
        
        for (int i = 0; i < difficulty.length; i++) {
            difficultyToMaxProfit.put(difficulty[i], 
                Math.max(difficultyToMaxProfit.getOrDefault(difficulty[i], 0), profit[i]));
        }
        
        // Calculate cumulative max profit
        int maxProfitSoFar = 0;
        for (Map.Entry<Integer, Integer> entry : difficultyToMaxProfit.entrySet()) {
            maxProfitSoFar = Math.max(maxProfitSoFar, entry.getValue());
            entry.setValue(maxProfitSoFar);
        }
        
        int totalProfit = 0;
        
        // For each worker, find the best job they can do
        for (int ability : worker) {
            Map.Entry<Integer, Integer> bestJob = difficultyToMaxProfit.floorEntry(ability);
            if (bestJob != null) {
                totalProfit += bestJob.getValue();
            }
        }
        
        return totalProfit;
    }
    
    /**
     * Helper class to represent a job
     */
    static class Job {
        int difficulty;
        int profit;
        
        Job(int difficulty, int profit) {
            this.difficulty = difficulty;
            this.profit = profit;
        }
        
        @Override
        public String toString() {
            return "Job{difficulty=" + difficulty + ", profit=" + profit + "}";
        }
    }
    
    public static void main(String[] args) {
        MostProfitAssigningWork solution = new MostProfitAssigningWork();
        
        // Test case 1
        System.out.println("=== Test Case 1 ===");
        int[] difficulty1 = {2, 4, 6, 8, 10};
        int[] profit1 = {10, 20, 30, 40, 50};
        int[] worker1 = {4, 5, 6, 10};
        System.out.println("Expected: 100");
        System.out.println("Binary Search: " + solution.maxProfitAssignment(difficulty1, profit1, worker1));
        System.out.println("Two Pointers: " + solution.maxProfitAssignmentTwoPointers(difficulty1, profit1, worker1));
        System.out.println("TreeMap: " + solution.maxProfitAssignmentTreeMap(difficulty1, profit1, worker1));
        
        // Test case 2
        System.out.println("\n=== Test Case 2 ===");
        int[] difficulty2 = {85, 47, 57};
        int[] profit2 = {24, 66, 99};
        int[] worker2 = {40, 25, 25};
        System.out.println("Expected: 0");
        System.out.println("Binary Search: " + solution.maxProfitAssignment(difficulty2, profit2, worker2));
        System.out.println("Two Pointers: " + solution.maxProfitAssignmentTwoPointers(difficulty2, profit2, worker2));
        System.out.println("TreeMap: " + solution.maxProfitAssignmentTreeMap(difficulty2, profit2, worker2));
        
        // Test case 3
        System.out.println("\n=== Test Case 3 ===");
        int[] difficulty3 = {13, 37, 58};
        int[] profit3 = {4, 90, 96};
        int[] worker3 = {34, 73, 45};
        System.out.println("Expected: 190");
        System.out.println("Binary Search: " + solution.maxProfitAssignment(difficulty3, profit3, worker3));
        System.out.println("Two Pointers: " + solution.maxProfitAssignmentTwoPointers(difficulty3, profit3, worker3));
        System.out.println("TreeMap: " + solution.maxProfitAssignmentTreeMap(difficulty3, profit3, worker3));
        
        // Test case 4: Edge case with duplicate difficulties
        System.out.println("\n=== Test Case 4 ===");
        int[] difficulty4 = {1, 1, 1};
        int[] profit4 = {1, 2, 3};
        int[] worker4 = {1, 1, 1};
        System.out.println("Expected: 9");
        System.out.println("Binary Search: " + solution.maxProfitAssignment(difficulty4, profit4, worker4));
        System.out.println("Two Pointers: " + solution.maxProfitAssignmentTwoPointers(difficulty4, profit4, worker4));
        System.out.println("TreeMap: " + solution.maxProfitAssignmentTreeMap(difficulty4, profit4, worker4));
    }
} 