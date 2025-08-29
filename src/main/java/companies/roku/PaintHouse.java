package companies.roku;

public class PaintHouse {
    
    public int minCost(int[][] costs) {
        if (costs == null || costs.length == 0) return 0;
        
        int n = costs.length;
        
        // dp[i][j] = minimum cost to paint house i with color j
        int[][] dp = new int[n][3];
        
        // Base case: First house
        dp[0][0] = costs[0][0]; // Red
        dp[0][1] = costs[0][1]; // Blue  
        dp[0][2] = costs[0][2]; // Green
        
        // Fill DP table
        for (int house = 1; house < n; house++) {
            // Paint current house RED (0) - previous house must be BLUE(1) or GREEN(2)
            dp[house][0] = costs[house][0] + Math.min(dp[house-1][1], dp[house-1][2]);
            
            // Paint current house BLUE (1) - previous house must be RED(0) or GREEN(2)
            dp[house][1] = costs[house][1] + Math.min(dp[house-1][0], dp[house-1][2]);
            
            // Paint current house GREEN (2) - previous house must be RED(0) or BLUE(1)
            dp[house][2] = costs[house][2] + Math.min(dp[house-1][0], dp[house-1][1]);
        }
        
        // Return minimum cost among all colors for the last house
        return Math.min(Math.min(dp[n-1][0], dp[n-1][1]), dp[n-1][2]);
    }
    
    // Space-optimized version - O(1) space
    public int minCostOptimal(int[][] costs) {
        if (costs == null || costs.length == 0) return 0;
        
        // Only need to track previous house costs
        int prevRed = costs[0][0];
        int prevBlue = costs[0][1];
        int prevGreen = costs[0][2];
        
        for (int house = 1; house < costs.length; house++) {
            int currentRed = costs[house][0] + Math.min(prevBlue, prevGreen);
            int currentBlue = costs[house][1] + Math.min(prevRed, prevGreen);
            int currentGreen = costs[house][2] + Math.min(prevRed, prevBlue);
            
            // Update for next iteration
            prevRed = currentRed;
            prevBlue = currentBlue;
            prevGreen = currentGreen;
        }
        
        return Math.min(Math.min(prevRed, prevBlue), prevGreen);
    }
    
    // Method to visualize the DP process
    public void visualizeDP(int[][] costs) {
        if (costs == null || costs.length == 0) return;
        
        int n = costs.length;
        int[][] dp = new int[n][3];
        
        System.out.println("Paint House DP Visualization");
        System.out.println("============================");
        System.out.println("Input costs matrix:");
        printMatrix(costs, "Cost");
        
        // Base case
        dp[0][0] = costs[0][0];
        dp[0][1] = costs[0][1];
        dp[0][2] = costs[0][2];
        
        System.out.println("\nDP Process:");
        System.out.printf("House 0: Red=%d, Blue=%d, Green=%d (Base case)\n", 
            dp[0][0], dp[0][1], dp[0][2]);
        
        // Fill and show each step
        for (int house = 1; house < n; house++) {
            dp[house][0] = costs[house][0] + Math.min(dp[house-1][1], dp[house-1][2]);
            dp[house][1] = costs[house][1] + Math.min(dp[house-1][0], dp[house-1][2]);
            dp[house][2] = costs[house][2] + Math.min(dp[house-1][0], dp[house-1][1]);
            
            System.out.printf("House %d:\n", house);
            System.out.printf("  Red   = %d + min(%d, %d) = %d\n", 
                costs[house][0], dp[house-1][1], dp[house-1][2], dp[house][0]);
            System.out.printf("  Blue  = %d + min(%d, %d) = %d\n", 
                costs[house][1], dp[house-1][0], dp[house-1][2], dp[house][1]);
            System.out.printf("  Green = %d + min(%d, %d) = %d\n", 
                costs[house][2], dp[house-1][0], dp[house-1][1], dp[house][2]);
        }
        
        System.out.println("\nFinal DP table:");
        printMatrix(dp, "MinCost");
        
        int result = Math.min(Math.min(dp[n-1][0], dp[n-1][1]), dp[n-1][2]);
        System.out.printf("\nMinimum total cost: min(%d, %d, %d) = %d\n", 
            dp[n-1][0], dp[n-1][1], dp[n-1][2], result);
    }
    
    private void printMatrix(int[][] matrix, String label) {
        System.out.printf("%s matrix:\n", label);
        System.out.println("House | Red  | Blue | Green");
        System.out.println("------|------|------|------");
        for (int i = 0; i < matrix.length; i++) {
            System.out.printf("  %d   | %4d | %4d | %4d\n", 
                i, matrix[i][0], matrix[i][1], matrix[i][2]);
        }
    }
    
    public static void main(String[] args) {
        PaintHouse solution = new PaintHouse();
        
        // Test case 1: Example from problem
        int[][] costs1 = {
            {17, 2, 17},
            {16, 16, 5},
            {14, 3, 19}
        };
        
        System.out.println("=== Test Case 1 ===");
        solution.visualizeDP(costs1);
        System.out.println("Result (DP): " + solution.minCost(costs1));
        System.out.println("Result (Optimal): " + solution.minCostOptimal(costs1));
        
        System.out.println("\n==================================================\n");
        
        // Test case 2: Simple case
        int[][] costs2 = {
            {7, 6, 2}
        };
        
        System.out.println("=== Test Case 2 (Single House) ===");
        solution.visualizeDP(costs2);
        System.out.println("Result: " + solution.minCost(costs2));
        
        System.out.println("\n==================================================\n");
        
        // Test case 3: Larger example
        int[][] costs3 = {
            {5, 8, 6},
            {19, 14, 13},
            {7, 5, 12},
            {14, 15, 17}
        };
        
        System.out.println("=== Test Case 3 (4 Houses) ===");
        solution.visualizeDP(costs3);
        System.out.println("Result: " + solution.minCost(costs3));
    }
}
