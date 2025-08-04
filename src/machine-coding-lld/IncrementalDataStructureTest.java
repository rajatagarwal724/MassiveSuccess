package machine.coding.lld;

import java.util.*;

/**
 * Comprehensive test cases for IncrementalDataStructure
 * Tests all three stages: Basic Operations, Search Operations, TTL Support
 */
public class IncrementalDataStructureTest {
    
    private IncrementalDataStructure ds;
    private long baseTime;
    
    public void setUp() {
        ds = new IncrementalDataStructure();
        baseTime = System.currentTimeMillis();
    }
    
    // ==================== Question 1: Basic Operations Tests ====================
    
    public void testBasicOperations() {
        System.out.println("=== Testing Basic Operations ===");
        setUp();
        
        // Test Set and Get
        ds.set("key1", "value1");
        ds.set("key2", "value2");
        ds.set("key3", "value3");
        
        assert "value1".equals(ds.get("key1")) : "Get key1 failed";
        assert "value2".equals(ds.get("key2")) : "Get key2 failed";
        assert "value3".equals(ds.get("key3")) : "Get key3 failed";
        assert ds.get("nonexistent") == null : "Get nonexistent key should return null";
        
        // Test Update
        assert ds.update("key1", "updated_value1") : "Update existing key should return true";
        assert "updated_value1".equals(ds.get("key1")) : "Updated value not retrieved correctly";
        assert !ds.update("nonexistent", "value") : "Update nonexistent key should return false";
        
        // Test Delete
        assert ds.delete("key2") : "Delete existing key should return true";
        assert ds.get("key2") == null : "Deleted key should return null";
        assert !ds.delete("nonexistent") : "Delete nonexistent key should return false";
        
        // Test Size and Empty
        assert ds.size() == 2 : "Size should be 2 after operations";
        assert !ds.isEmpty() : "Should not be empty";
        
        ds.delete("key1");
        ds.delete("key3");
        assert ds.isEmpty() : "Should be empty after deleting all keys";
        
        System.out.println("✓ Basic Operations tests passed");
    }
    
    public void testEdgeCases() {
        System.out.println("=== Testing Edge Cases ===");
        setUp();
        
        // Test null handling
        try {
            ds.set(null, "value");
            assert false : "Should throw exception for null key";
        } catch (IllegalArgumentException e) {
            // Expected
        }
        
        try {
            ds.set("key", null);
            assert false : "Should throw exception for null value";
        } catch (IllegalArgumentException e) {
            // Expected
        }
        
        assert ds.get(null) == null : "Get null key should return null";
        assert !ds.update(null, "value") : "Update null key should return false";
        assert !ds.delete(null) : "Delete null key should return false";
        
        // Test overwrite
        ds.set("key", "value1");
        ds.set("key", "value2");
        assert "value2".equals(ds.get("key")) : "Key should be overwritten";
        
        System.out.println("✓ Edge Cases tests passed");
    }
    
    // ==================== Question 2: Search Operations Tests ====================
    
    public void testPrefixSearch() {
        System.out.println("=== Testing Prefix Search ===");
        setUp();
        
        // Setup test data
        ds.set("user1", "Alice");
        ds.set("user2", "Bob");
        ds.set("userProfile", "Profile");
        ds.set("admin", "Admin");
        ds.set("customer1", "Customer1");
        ds.set("customer2", "Customer2");
        
        // Test prefix search
        List<String> userKeys = ds.prefixSearch("user");
        assert userKeys.size() == 3 : "Should find 3 keys with prefix 'user'";
        assert userKeys.contains("user1") : "Should contain user1";
        assert userKeys.contains("user2") : "Should contain user2";
        assert userKeys.contains("userProfile") : "Should contain userProfile";
        
        List<String> customerKeys = ds.prefixSearch("customer");
        assert customerKeys.size() == 2 : "Should find 2 keys with prefix 'customer'";
        
        List<String> noMatch = ds.prefixSearch("xyz");
        assert noMatch.isEmpty() : "Should find no keys with prefix 'xyz'";
        
        List<String> emptyPrefix = ds.prefixSearch("");
        assert emptyPrefix.size() == 6 : "Empty prefix should return all keys";
        
        assert ds.prefixSearch(null).isEmpty() : "Null prefix should return empty list";
        
        System.out.println("✓ Prefix Search tests passed");
    }
    
    public void testContainsSearch() {
        System.out.println("=== Testing Contains Search ===");
        setUp();
        
        // Setup test data
        ds.set("user_profile", "Profile");
        ds.set("admin_user", "Admin");
        ds.set("customer_data", "Data");
        ds.set("test", "Test");
        
        // Test contains search
        assert ds.containsSearch("user") : "Should find keys containing 'user'";
        assert ds.containsSearch("admin") : "Should find keys containing 'admin'";
        assert ds.containsSearch("_") : "Should find keys containing '_'";
        assert !ds.containsSearch("xyz") : "Should not find keys containing 'xyz'";
        assert !ds.containsSearch(null) : "Null substring should return false";
        
        // Test getAllContaining
        List<String> userContaining = ds.getAllContaining("user");
        assert userContaining.size() == 2 : "Should find 2 keys containing 'user'";
        assert userContaining.contains("user_profile") : "Should contain user_profile";
        assert userContaining.contains("admin_user") : "Should contain admin_user";
        
        List<String> underscoreContaining = ds.getAllContaining("_");
        assert underscoreContaining.size() == 3 : "Should find 3 keys containing '_'";
        
        System.out.println("✓ Contains Search tests passed");
    }
    
    // ==================== Question 3: TTL Support Tests ====================
    
    public void testTTLOperations() {
        System.out.println("=== Testing TTL Operations ===");
        setUp();
        
        long t0 = baseTime;
        long t5 = baseTime + 5000;  // 5 seconds later
        long t10 = baseTime + 10000; // 10 seconds later
        long t15 = baseTime + 15000; // 15 seconds later
        
        // Set keys with different TTLs
        ds.set("temp5", "value5", 5, t0);    // 5 second TTL
        ds.set("temp10", "value10", 10, t0); // 10 second TTL
        ds.set("permanent", "valuePerm");     // No TTL
        
        // Test at t0
        assert "value5".equals(ds.get("temp5", t0)) : "temp5 should be available at t0";
        assert "value10".equals(ds.get("temp10", t0)) : "temp10 should be available at t0";
        assert "valuePerm".equals(ds.get("permanent", t0)) : "permanent should be available at t0";
        assert ds.size(t0) == 3 : "Size should be 3 at t0";
        
        // Test at t5 (temp5 expired)
        assert ds.get("temp5", t5) == null : "temp5 should be expired at t5";
        assert "value10".equals(ds.get("temp10", t5)) : "temp10 should be available at t5";
        assert "valuePerm".equals(ds.get("permanent", t5)) : "permanent should be available at t5";
        assert ds.size(t5) == 2 : "Size should be 2 at t5";
        
        // Test at t10 (temp10 expired)
        assert ds.get("temp5", t10) == null : "temp5 should be expired at t10";
        assert ds.get("temp10", t10) == null : "temp10 should be expired at t10";
        assert "valuePerm".equals(ds.get("permanent", t10)) : "permanent should be available at t10";
        assert ds.size(t10) == 1 : "Size should be 1 at t10";
        
        System.out.println("✓ TTL Operations tests passed");
    }
    
    public void testTTLWithOperations() {
        System.out.println("=== Testing TTL with Update/Delete Operations ===");
        setUp();
        
        long t0 = baseTime;
        long t5 = baseTime + 5000;
        
        // Set key with TTL
        ds.set("tempKey", "value", 5, t0);
        
        // Test update before expiration
        assert ds.update("tempKey", "newValue", t0) : "Should be able to update before expiration";
        assert "newValue".equals(ds.get("tempKey", t0)) : "Updated value should be retrieved";
        
        // Test update after expiration
        assert !ds.update("tempKey", "anotherValue", t5) : "Should not be able to update after expiration";
        
        // Test delete before expiration
        ds.set("tempKey2", "value2", 5, t0);
        assert ds.delete("tempKey2", t0) : "Should be able to delete before expiration";
        assert ds.get("tempKey2", t0) == null : "Deleted key should return null";
        
        // Test delete after expiration
        ds.set("tempKey3", "value3", 5, t0);
        assert !ds.delete("tempKey3", t5) : "Should not be able to delete after expiration";
        
        System.out.println("✓ TTL with Operations tests passed");
    }
    
    public void testTTLWithSearch() {
        System.out.println("=== Testing TTL with Search Operations ===");
        setUp();
        
        long t0 = baseTime;
        long t5 = baseTime + 5000;
        
        // Set keys with TTL
        ds.set("temp_user1", "value1", 5, t0);
        ds.set("temp_user2", "value2", 10, t0);
        ds.set("perm_user", "value3"); // No TTL
        
        // Test prefix search at t0
        List<String> prefixT0 = ds.prefixSearch("temp", t0);
        assert prefixT0.size() == 2 : "Should find 2 temp keys at t0";
        
        // Test prefix search at t5 (temp_user1 expired)
        List<String> prefixT5 = ds.prefixSearch("temp", t5);
        assert prefixT5.size() == 1 : "Should find 1 temp key at t5";
        assert prefixT5.contains("temp_user2") : "Should contain temp_user2";
        
        // Test contains search
        assert ds.containsSearch("temp", t0) : "Should find temp keys at t0";
        assert ds.containsSearch("temp", t5) : "Should find temp keys at t5";
        
        List<String> userContaining = ds.getAllContaining("user", t0);
        assert userContaining.size() == 3 : "Should find 3 user keys at t0";
        
        List<String> userContainingT5 = ds.getAllContaining("user", t5);
        assert userContainingT5.size() == 2 : "Should find 2 user keys at t5";
        
        System.out.println("✓ TTL with Search tests passed");
    }
    
    public void testSystemState() {
        System.out.println("=== Testing System State ===");
        setUp();
        
        long t0 = baseTime;
        long t5 = baseTime + 5000;
        
        ds.set("key1", "value1", 5, t0);
        ds.set("key2", "value2", 10, t0);
        ds.set("key3", "value3"); // No TTL
        
        Map<String, String> stateT0 = ds.getSystemState(t0);
        assert stateT0.size() == 3 : "System state should have 3 keys at t0";
        assert "value1".equals(stateT0.get("key1")) : "key1 should have correct value";
        
        Map<String, String> stateT5 = ds.getSystemState(t5);
        assert stateT5.size() == 2 : "System state should have 2 keys at t5";
        assert !stateT5.containsKey("key1") : "key1 should be expired at t5";
        assert "value2".equals(stateT5.get("key2")) : "key2 should have correct value";
        assert "value3".equals(stateT5.get("key3")) : "key3 should have correct value";
        
        System.out.println("✓ System State tests passed");
    }
    
    public void testCleanup() {
        System.out.println("=== Testing Cleanup ===");
        setUp();
        
        long t0 = baseTime;
        long t5 = baseTime + 5000;
        
        ds.set("temp1", "value1", 3, t0);
        ds.set("temp2", "value2", 3, t0);
        ds.set("perm", "value3");
        
        assert ds.size(t0) == 3 : "Should have 3 keys before cleanup";
        
        int cleaned = ds.cleanupExpiredKeys(t5);
        assert cleaned == 2 : "Should clean up 2 expired keys";
        assert ds.size(t5) == 1 : "Should have 1 key after cleanup";
        assert "value3".equals(ds.get("perm", t5)) : "Permanent key should remain";
        
        System.out.println("✓ Cleanup tests passed");
    }
    
    // ==================== Performance Tests ====================
    
    public void testPerformance() {
        System.out.println("=== Testing Performance ===");
        setUp();
        
        long startTime = System.currentTimeMillis();
        
        // Test with large dataset
        int numKeys = 10000;
        for (int i = 0; i < numKeys; i++) {
            ds.set("key" + i, "value" + i);
        }
        
        long setTime = System.currentTimeMillis();
        System.out.println("Set " + numKeys + " keys in " + (setTime - startTime) + "ms");
        
        // Test get performance
        for (int i = 0; i < numKeys; i++) {
            String value = ds.get("key" + i);
            assert ("value" + i).equals(value) : "Get failed for key" + i;
        }
        
        long getTime = System.currentTimeMillis();
        System.out.println("Get " + numKeys + " keys in " + (getTime - setTime) + "ms");
        
        // Test prefix search performance
        List<String> results = ds.prefixSearch("key1");
        long searchTime = System.currentTimeMillis();
        System.out.println("Prefix search returned " + results.size() + " results in " + (searchTime - getTime) + "ms");
        
        System.out.println("✓ Performance tests completed");
    }
    
    // ==================== Integration Tests ====================
    
    public void testIntegrationScenario() {
        System.out.println("=== Testing Integration Scenario ===");
        setUp();
        
        long t0 = baseTime;
        long t3 = baseTime + 3000;
        long t6 = baseTime + 6000;
        long t10 = baseTime + 10000;
        
        // Simulate a real-world scenario
        // User sessions with different TTLs
        ds.set("session_user1", "active", 5, t0);
        ds.set("session_user2", "active", 8, t0);
        ds.set("session_admin", "active", 15, t0);
        
        // Permanent data
        ds.set("config_timeout", "30");
        ds.set("config_maxUsers", "1000");
        
        // Cache data with TTL
        ds.set("cache_user1_profile", "profile_data", 10, t0);
        ds.set("cache_user2_profile", "profile_data", 10, t0);
        
        // Test at t3 - all should be active
        assert ds.size(t3) == 7 : "All keys should be active at t3";
        List<String> sessions = ds.prefixSearch("session", t3);
        assert sessions.size() == 3 : "All sessions should be active at t3";
        
        // Test at t6 - session_user1 expired
        assert ds.size(t6) == 6 : "One session should be expired at t6";
        sessions = ds.prefixSearch("session", t6);
        assert sessions.size() == 2 : "Two sessions should be active at t6";
        assert !sessions.contains("session_user1") : "session_user1 should be expired";
        
        // Test at t10 - more sessions expired
        assert ds.size(t10) == 4 : "More keys should be expired at t10";
        sessions = ds.prefixSearch("session", t10);
        assert sessions.size() == 1 : "Only admin session should be active at t10";
        assert sessions.contains("session_admin") : "session_admin should still be active";
        
        // Config should always be available
        assert ds.containsSearch("config", t10) : "Config keys should always be available";
        
        System.out.println("✓ Integration Scenario tests passed");
    }
    
    // ==================== Main Test Runner ====================
    
    public static void main(String[] args) {
        IncrementalDataStructureTest test = new IncrementalDataStructureTest();
        
        try {
            // Question 1 Tests
            test.testBasicOperations();
            test.testEdgeCases();
            
            // Question 2 Tests
            test.testPrefixSearch();
            test.testContainsSearch();
            
            // Question 3 Tests
            test.testTTLOperations();
            test.testTTLWithOperations();
            test.testTTLWithSearch();
            test.testSystemState();
            test.testCleanup();
            
            // Additional Tests
            test.testPerformance();
            test.testIntegrationScenario();
            
            System.out.println("\n🎉 ALL TESTS PASSED! 🎉");
            System.out.println("The IncrementalDataStructure is ready for all 3 questions!");
            
        } catch (AssertionError e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
