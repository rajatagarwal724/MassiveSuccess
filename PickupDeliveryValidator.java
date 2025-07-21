import java.util.*;

public class PickupDeliveryValidator {
    
    /**
     * Validates if a pickup-delivery sequence is valid
     * Rules:
     * 1. Each pickup must have exactly one corresponding delivery
     * 2. Delivery cannot happen before pickup for the same order
     * 3. No duplicate pickups or deliveries for the same order
     */
    public boolean isValidSequence(String[] orders) {
        if (orders == null || orders.length == 0) {
            return true;
        }
        
        Map<String, Integer> pickupCount = new HashMap<>();
        Map<String, Integer> deliveryCount = new HashMap<>();
        
        for (String order : orders) {
            if (order.startsWith("P")) {
                String orderId = order.substring(1);
                pickupCount.put(orderId, pickupCount.getOrDefault(orderId, 0) + 1);
                
                // Check if we have more pickups than deliveries + 1 (invalid)
                if (pickupCount.get(orderId) > deliveryCount.getOrDefault(orderId, 0) + 1) {
                    return false;
                }
            } else if (order.startsWith("D")) {
                String orderId = order.substring(1);
                deliveryCount.put(orderId, deliveryCount.getOrDefault(orderId, 0) + 1);
                
                // Check if delivery happens before pickup (invalid)
                if (deliveryCount.get(orderId) > pickupCount.getOrDefault(orderId, 0)) {
                    return false;
                }
            }
        }
        
        // Final validation: each order should have exactly one pickup and one delivery
        for (String orderId : pickupCount.keySet()) {
            if (pickupCount.get(orderId) != 1 || deliveryCount.getOrDefault(orderId, 0) != 1) {
                return false;
            }
        }
        
        for (String orderId : deliveryCount.keySet()) {
            if (deliveryCount.get(orderId) != 1 || pickupCount.getOrDefault(orderId, 0) != 1) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Finds the longest valid subarray of pickup-delivery sequence
     * Uses sliding window approach to find the maximum valid subsequence
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
                
                if (isValidSequence(subarray) && subarray.length > maxLength) {
                    maxLength = subarray.length;
                    longestValid = Arrays.asList(subarray);
                }
            }
        }
        
        return longestValid;
    }
    
    /**
     * Optimized version using dynamic programming approach
     * Finds longest valid subarray more efficiently
     */
    public List<String> findLongestValidSubarrayOptimized(String[] orders) {
        if (orders == null || orders.length == 0) {
            return new ArrayList<>();
        }
        
        int maxLength = 0;
        int bestStart = 0, bestEnd = -1;
        
        // For each starting position
        for (int start = 0; start < orders.length; start++) {
            Map<String, Integer> pickupCount = new HashMap<>();
            Map<String, Integer> deliveryCount = new HashMap<>();
            boolean isValid = true;
            
            // Extend from this starting position
            for (int end = start; end < orders.length && isValid; end++) {
                String order = orders[end];
                
                if (order.startsWith("P")) {
                    String orderId = order.substring(1);
                    pickupCount.put(orderId, pickupCount.getOrDefault(orderId, 0) + 1);
                    
                    // Check for duplicate pickups
                    if (pickupCount.get(orderId) > 1) {
                        isValid = false;
                        break;
                    }
                } else if (order.startsWith("D")) {
                    String orderId = order.substring(1);
                    deliveryCount.put(orderId, deliveryCount.getOrDefault(orderId, 0) + 1);
                    
                    // Check if delivery happens before pickup or duplicate delivery
                    if (deliveryCount.get(orderId) > pickupCount.getOrDefault(orderId, 0) || 
                        deliveryCount.get(orderId) > 1) {
                        isValid = false;
                        break;
                    }
                }
                
                // Check if current subarray is valid and complete
                if (isValid && isCompleteValidSequence(pickupCount, deliveryCount)) {
                    int currentLength = end - start + 1;
                    if (currentLength > maxLength) {
                        maxLength = currentLength;
                        bestStart = start;
                        bestEnd = end;
                    }
                }
            }
        }
        
        if (bestEnd >= bestStart) {
            return Arrays.asList(Arrays.copyOfRange(orders, bestStart, bestEnd + 1));
        }
        
        return new ArrayList<>();
    }
    
    private boolean isCompleteValidSequence(Map<String, Integer> pickupCount, 
                                          Map<String, Integer> deliveryCount) {
        // Check if all orders have exactly one pickup and one delivery
        for (String orderId : pickupCount.keySet()) {
            if (pickupCount.get(orderId) != 1 || deliveryCount.getOrDefault(orderId, 0) != 1) {
                return false;
            }
        }
        
        for (String orderId : deliveryCount.keySet()) {
            if (deliveryCount.get(orderId) != 1 || pickupCount.getOrDefault(orderId, 0) != 1) {
                return false;
            }
        }
        
        return true;
    }
    
    public static void main(String[] args) {
        PickupDeliveryValidator validator = new PickupDeliveryValidator();
        
        // Test validation
        System.out.println("=== Validation Tests ===");
        
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
            boolean result = validator.isValidSequence(test);
            System.out.println(Arrays.toString(test) + " => " + (result ? "valid" : "invalid"));
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
            List<String> result = validator.findLongestValidSubarrayOptimized(test);
            System.out.println(Arrays.toString(test) + " => " + result);
        }
    }
} 