package companies.leetcode;

import java.util.Arrays;

/**
 * Paint House II - LeetCode Problem
 * 
 * Problem: Given n houses and k colors, find the minimum cost to paint all houses
 * such that no two adjacent houses have the same color.
 * 
 * Input: costs[n][k] where costs[i][j] = cost to paint house i with color j
 * Output: Minimum total cost
 */
public class PaintHouseII {
    
    /**
     * Basic Solution - Time: O(n*k²), Space: O(k)
     * 
     * For each house and each color, find the minimum cost from the previous house
     * excluding the current color.
     */
    public int minCostII(int[][] costs) {
        if (costs == null || costs.length == 0) return 0;
        
        int numHouses = costs.length;
        int numColors = costs[0].length;
        
        // Start with the first house's costs
        int[] previousCosts = costs[0].clone();
        
        // Process each house starting from the second
        for (int houseIndex = 1; houseIndex < numHouses; ++houseIndex) {
            int[] currentCosts = costs[houseIndex].clone();
            
            // For each color of current house
            for (int colorIndex = 0; colorIndex < numColors; ++colorIndex) {
                int minCost = Integer.MAX_VALUE;
                
                // Find minimum cost from previous house (excluding same color)
                for (int prevColorIndex = 0; prevColorIndex < numColors; ++prevColorIndex) {
                    if (prevColorIndex != colorIndex) {
                        minCost = Math.min(minCost, previousCosts[prevColorIndex]);
                    }
                }
                
                // Add minimum previous cost to current house's color cost
                currentCosts[colorIndex] += minCost;
            }
            
            // Update for next iteration
            previousCosts = currentCosts;
        }
        
        // Return minimum cost from the last house
        return Arrays.stream(previousCosts).min().getAsInt();
    }
    
    /**
     * Optimized Solution - Time: O(n*k), Space: O(k)
     * 
     * Instead of finding minimum for each color, we find the overall minimum
     * and second minimum of the previous house's costs.
     */
    public int minCostIIOptimized(int[][] costs) {
        if (costs == null || costs.length == 0) return 0;
        
        int numHouses = costs.length;
        int numColors = costs[0].length;
        
        // Start with the first house's costs
        int[] previousCosts = costs[0].clone();
        
        // Process each house starting from the second
        for (int houseIndex = 1; houseIndex < numHouses; ++houseIndex) {
            int[] currentCosts = costs[houseIndex].clone();
            
            // Find minimum and second minimum of previous house's costs
            int min1 = Integer.MAX_VALUE, min2 = Integer.MAX_VALUE;
            int minIndex = -1;
            
            for (int i = 0; i < numColors; ++i) {
                if (previousCosts[i] < min1) {
                    min2 = min1;
                    min1 = previousCosts[i];
                    minIndex = i;
                } else if (previousCosts[i] < min2) {
                    min2 = previousCosts[i];
                }
            }
            
            // For each color, use min1 if previous color != current color, else min2
            for (int colorIndex = 0; colorIndex < numColors; ++colorIndex) {
                if (colorIndex == minIndex) {
                    currentCosts[colorIndex] += min2; // Use second minimum
                } else {
                    currentCosts[colorIndex] += min1; // Use minimum
                }
            }
            
            // Update for next iteration
            previousCosts = currentCosts;
        }
        
        // Return minimum cost from the last house
        return Arrays.stream(previousCosts).min().getAsInt();
    }
    
    /**
     * Alternative optimized solution using tracking of min indices
     */
    public int minCostIIAlternative(int[][] costs) {
        if (costs == null || costs.length == 0) return 0;
        
        int numHouses = costs.length;
        int numColors = costs[0].length;
        
        int[] previousCosts = costs[0].clone();
        
        for (int houseIndex = 1; houseIndex < numHouses; ++houseIndex) {
            int[] currentCosts = costs[houseIndex].clone();
            
            // Find minimum and its index
            int minCost = Integer.MAX_VALUE;
            int minIndex = -1;
            int secondMinCost = Integer.MAX_VALUE;
            
            for (int i = 0; i < numColors; ++i) {
                if (previousCosts[i] < minCost) {
                    secondMinCost = minCost;
                    minCost = previousCosts[i];
                    minIndex = i;
                } else if (previousCosts[i] < secondMinCost) {
                    secondMinCost = previousCosts[i];
                }
            }
            
            // Update current costs
            for (int colorIndex = 0; colorIndex < numColors; ++colorIndex) {
                currentCosts[colorIndex] += (colorIndex == minIndex) ? secondMinCost : minCost;
            }
            
            previousCosts = currentCosts;
        }
        
        return Arrays.stream(previousCosts).min().getAsInt();
    }
    
    /**
     * Test the solutions
     */
    public static void main(String[] args) {
        PaintHouseII solution = new PaintHouseII();
        
        // Test case 1
        int[][] costs1 = {
            {1, 5, 3},
            {2, 3, 1},
            {3, 1, 2}
        };
        
        System.out.println("Test Case 1:");
        System.out.println("Input: " + Arrays.deepToString(costs1));
        System.out.println("Basic Solution: " + solution.minCostII(costs1));
        System.out.println("Optimized Solution: " + solution.minCostIIOptimized(costs1));
        System.out.println("Alternative Solution: " + solution.minCostIIAlternative(costs1));
        System.out.println();
        
        // Test case 2
        int[][] costs2 = {
            {1, 3},
            {2, 4}
        };
        
        System.out.println("Test Case 2:");
        System.out.println("Input: " + Arrays.deepToString(costs2));
        System.out.println("Basic Solution: " + solution.minCostII(costs2));
        System.out.println("Optimized Solution: " + solution.minCostIIOptimized(costs2));
        System.out.println("Alternative Solution: " + solution.minCostIIAlternative(costs2));
        System.out.println();
        
        // Test case 3 - Single house
        int[][] costs3 = {
            {1, 2, 3, 4}
        };
        
        System.out.println("Test Case 3 (Single House):");
        System.out.println("Input: " + Arrays.deepToString(costs3));
        System.out.println("Basic Solution: " + solution.minCostII(costs3));
        System.out.println("Optimized Solution: " + solution.minCostIIOptimized(costs3));
        System.out.println("Alternative Solution: " + solution.minCostIIAlternative(costs3));
        System.out.println();
        
        // Test case 4 - Large example
        int[][] costs4 = {
            {1, 5, 3, 2},
            {2, 3, 1, 4},
            {3, 1, 2, 5},
            {4, 2, 5, 1},
            {5, 4, 1, 3}
        };
        
        System.out.println("Test Case 4 (Large Example):");
        System.out.println("Input: " + Arrays.deepToString(costs4));
        System.out.println("Basic Solution: " + solution.minCostII(costs4));
        System.out.println("Optimized Solution: " + solution.minCostIIOptimized(costs4));
        System.out.println("Alternative Solution: " + solution.minCostIIAlternative(costs4));
    }
    
    /**
     * Helper method to print the step-by-step process
     */
    public void printStepByStep(int[][] costs) {
        System.out.println("Step-by-step process:");
        System.out.println("Initial costs: " + Arrays.deepToString(costs));
        
        int numHouses = costs.length;
        int numColors = costs[0].length;
        int[] previousCosts = costs[0].clone();
        
        System.out.println("House 0 costs: " + Arrays.toString(previousCosts));
        
        for (int houseIndex = 1; houseIndex < numHouses; ++houseIndex) {
            int[] currentCosts = costs[houseIndex].clone();
            
            System.out.println("\nProcessing House " + houseIndex + ":");
            System.out.println("Original costs: " + Arrays.toString(currentCosts));
            
            for (int colorIndex = 0; colorIndex < numColors; ++colorIndex) {
                int minCost = Integer.MAX_VALUE;
                
                for (int prevColorIndex = 0; prevColorIndex < numColors; ++prevColorIndex) {
                    if (prevColorIndex != colorIndex) {
                        minCost = Math.min(minCost, previousCosts[prevColorIndex]);
                    }
                }
                
                currentCosts[colorIndex] += minCost;
                System.out.println("  Color " + colorIndex + ": " + costs[houseIndex][colorIndex] + " + " + minCost + " = " + currentCosts[colorIndex]);
            }
            
            previousCosts = currentCosts;
            System.out.println("Updated costs: " + Arrays.toString(previousCosts));
        }
        
        System.out.println("\nFinal result: " + Arrays.stream(previousCosts).min().getAsInt());
    }
} 