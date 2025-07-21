import java.util.*;

public class PickupDeliveryValidatorStack {
    
    /**
     * Stack-based validation approach
     * Uses a stack to track pending pickups and validates deliveries
     */
    public boolean isValidSequence(String[] orders) {
        if (orders == null || orders.length == 0) {
            return true;
        }
        
        Stack<String> pendingPickups = new Stack<>();
        Set<String> processedOrders = new HashSet<>();
        
        for (String order : orders) {
            if (order.startsWith("P")) {
                String orderId = order.substring(1);
                
                // Check for duplicate pickup
                if (processedOrders.contains("P" + orderId)) {
                    return false;
                }
                
                pendingPickups.push(orderId);
                processedOrders.add("P" + orderId);
                
            } else if (order.startsWith("D")) {
                String orderId = order.substring(1);
                
                // Check for duplicate delivery
                if (processedOrders.contains("D" + orderId)) {
                    return false;
                }
                
                // Check if there's a corresponding pickup
                if (pendingPickups.isEmpty() || !pendingPickups.contains(orderId)) {
                    return false;
                }
                
                // Remove the pickup from pending
                pendingPickups.remove(orderId);
                processedOrders.add("D" + orderId);
            }
        }
        
        // All pickups should be delivered
        return pendingPickups.isEmpty();
    }
    
    /**
     * More efficient approach using counter-based validation
     * This is the most optimal solution for validation
     */
    public boolean isValidSequenceOptimal(String[] orders) {
        if (orders == null || orders.length == 0) {
            return true;
        }
        
        Map<String, Integer> balance = new HashMap<>(); // positive = pending pickups, zero = completed
        Set<String> seen = new HashSet<>();
        
        for (String order : orders) {
            // Check for duplicates
            if (seen.contains(order)) {
                return false;
            }
            seen.add(order);
            
            if (order.startsWith("P")) {
                String orderId = order.substring(1);
                balance.put(orderId, balance.getOrDefault(orderId, 0) + 1);
            } else if (order.startsWith("D")) {
                String orderId = order.substring(1);
                int currentBalance = balance.getOrDefault(orderId, 0);
                
                // Delivery before pickup
                if (currentBalance <= 0) {
                    return false;
                }
                
                balance.put(orderId, currentBalance - 1);
            }
        }
        
        // All orders should be balanced (completed)
        for (int bal : balance.values()) {
            if (bal != 0) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Finds longest valid subarray using the optimal validation
     */
    public List<String> findLongestValidSubarray(String[] orders) {
        if (orders == null || orders.length == 0) {
            return new ArrayList<>();
        }
        
        int maxLength = 0;
        List<String> longestValid = new ArrayList<>();
        
        // Try all possible subarrays
        for (int i = 0; i < orders.length; i++) {
            for (int j = i; j < orders.length; j++) {
                String[] subarray = Arrays.copyOfRange(orders, i, j + 1);
                
                if (isValidSequenceOptimal(subarray) && subarray.length > maxLength) {
                    maxLength = subarray.length;
                    longestValid = Arrays.asList(subarray);
                }
            }
        }
        
        return longestValid;
    }
    
    /**
     * Advanced approach: finds longest valid subarray using dynamic programming
     * This considers all possible valid subsequences, not just subarrays
     */
    public List<String> findLongestValidSubsequence(String[] orders) {
        if (orders == null || orders.length == 0) {
            return new ArrayList<>();
        }
        
        // Use backtracking to find the longest valid subsequence
        List<String> result = new ArrayList<>();
        List<String> current = new ArrayList<>();
        
        findLongestValidSubsequenceHelper(orders, 0, current, result);
        
        return result;
    }
    
    private void findLongestValidSubsequenceHelper(String[] orders, int index, 
                                                  List<String> current, List<String> result) {
        // Check if current subsequence is valid
        if (isValidSequenceOptimal(current.toArray(new String[0]))) {
            if (current.size() > result.size()) {
                result.clear();
                result.addAll(current);
            }
        }
        
        // Try including each remaining element
        for (int i = index; i < orders.length; i++) {
            current.add(orders[i]);
            findLongestValidSubsequenceHelper(orders, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
    
    public static void main(String[] args) {
        PickupDeliveryValidatorStack validator = new PickupDeliveryValidatorStack();
        
        // Test validation
        System.out.println("=== Stack-based Validation Tests ===");
        
        String[][] testCases = {
            {"P1", "P2", "D1", "D2"},     // valid
            {"P1", "D1", "P2", "D2"},     // valid
            {"P1", "D2", "D1", "P2"},     // invalid
            {"P1", "D2"},                 // invalid
            {"P1", "P2"},                 // invalid
            {"P1", "D1", "D1"},           // invalid
            {},                           // valid
            {"P1", "P1", "D1"},           // invalid
            {"P1", "P1", "D1", "D1"},     // invalid
            {"P1", "D1", "P1"},           // invalid
            {"P1", "D1", "P1", "D1"}      // invalid
        };
        
        for (String[] test : testCases) {
            boolean result1 = validator.isValidSequence(test);
            boolean result2 = validator.isValidSequenceOptimal(test);
            System.out.println(Arrays.toString(test) + " => Stack: " + (result1 ? "valid" : "invalid") + 
                             ", Optimal: " + (result2 ? "valid" : "invalid"));
        }
        
        // Test longest valid subarray
        System.out.println("\n=== Longest Valid Subarray Tests ===");
        
        String[][] longestTests = {
            {"P1", "P1", "D1"},
            {"P1", "P1", "D1", "D1"},
            {"P1", "D1", "P2", "D2", "P3"},
            {"P1", "D2", "P2", "D1"},
            {"P1", "P2", "D1", "D2", "P3", "D3"}
        };
        
        for (String[] test : longestTests) {
            List<String> result = validator.findLongestValidSubarray(test);
            System.out.println(Arrays.toString(test) + " => " + result);
        }
        
        // Test longest valid subsequence (more complex)
        System.out.println("\n=== Longest Valid Subsequence Tests ===");
        String[] complexTest = {"P1", "P2", "P1", "D1", "D2"}; // Has duplicate P1
        List<String> subsequence = validator.findLongestValidSubsequence(complexTest);
        System.out.println(Arrays.toString(complexTest) + " => " + subsequence);
    }
} 