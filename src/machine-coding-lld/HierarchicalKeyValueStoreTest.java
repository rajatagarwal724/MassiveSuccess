package machine.coding.lld;

import java.util.*;

/**
 * Comprehensive test cases for HierarchicalKeyValueStore
 * Tests all 4 levels: Basic Operations, Advanced Queries, History/Range Operations, Maintenance
 */
public class HierarchicalKeyValueStoreTest {
    
    private HierarchicalKeyValueStore store;
    private long baseTime;
    
    public void setUp() {
        store = new HierarchicalKeyValueStore();
        baseTime = System.currentTimeMillis();
    }
    
    // ==================== LEVEL 1: Basic Operations Tests ====================
    
    public void testBasicSetAndGet() {
        System.out.println("=== Testing Basic Set and Get ===");
        setUp();
        
        // Test basic set and get
        store.set("user1", "name", "Alice", baseTime);
        store.set("user1", "email", "alice@example.com", baseTime + 1000);
        store.set("user2", "name", "Bob", baseTime + 2000);
        
        assert "Alice".equals(store.getLatest("user1", "name")) : "Should get Alice for user1 name";
        assert "alice@example.com".equals(store.getLatest("user1", "email")) : "Should get email for user1";
        assert "Bob".equals(store.getLatest("user2", "name")) : "Should get Bob for user2 name";
        assert store.getLatest("user1", "nonexistent") == null : "Should return null for nonexistent sub_key";
        assert store.getLatest("nonexistent", "name") == null : "Should return null for nonexistent key";
        
        // Test overwrite with newer timestamp
        store.set("user1", "name", "Alice Smith", baseTime + 3000);
        assert "Alice Smith".equals(store.getLatest("user1", "name")) : "Should get updated name";
        
        System.out.println("✓ Basic Set and Get tests passed");
    }
    
    public void testDeleteOperations() {
        System.out.println("=== Testing Delete Operations ===");
        setUp();
        
        // Setup test data
        store.set("user1", "name", "Alice", baseTime);
        store.set("user1", "email", "alice@example.com", baseTime + 1000);
        store.set("user2", "name", "Bob", baseTime + 2000);
        
        // Test delete sub_key
        assert store.deleteSubKey("user1", "email") : "Should successfully delete sub_key";
        assert store.getLatest("user1", "email") == null : "Deleted sub_key should return null";
        assert "Alice".equals(store.getLatest("user1", "name")) : "Other sub_keys should remain";
        
        // Test delete nonexistent sub_key
        assert !store.deleteSubKey("user1", "nonexistent") : "Should return false for nonexistent sub_key";
        assert !store.deleteSubKey("nonexistent", "name") : "Should return false for nonexistent key";
        
        // Test delete entire key
        assert store.deleteKey("user2") : "Should successfully delete key";
        assert store.getLatest("user2", "name") == null : "Deleted key should return null";
        
        // Test delete nonexistent key
        assert !store.deleteKey("nonexistent") : "Should return false for nonexistent key";
        
        // Test delete last sub_key removes key
        assert store.containsKey("user1") : "user1 should exist before deleting last sub_key";
        store.deleteSubKey("user1", "name");
        assert !store.containsKey("user1") : "user1 should be removed after deleting last sub_key";
        
        System.out.println("✓ Delete Operations tests passed");
    }
    
    public void testEdgeCases() {
        System.out.println("=== Testing Edge Cases ===");
        setUp();
        
        // Test null handling
        try {
            store.set(null, "subkey", "value");
            assert false : "Should throw exception for null key";
        } catch (IllegalArgumentException e) {
            // Expected
        }
        
        try {
            store.set("key", null, "value");
            assert false : "Should throw exception for null sub_key";
        } catch (IllegalArgumentException e) {
            // Expected
        }
        
        try {
            store.set("key", "subkey", null);
            assert false : "Should throw exception for null value";
        } catch (IllegalArgumentException e) {
            // Expected
        }
        
        assert store.getLatest(null, "subkey") == null : "Should return null for null key";
        assert store.getLatest("key", null) == null : "Should return null for null sub_key";
        
        System.out.println("✓ Edge Cases tests passed");
    }
    
    // ==================== LEVEL 2: Advanced Queries Tests ====================
    
    public void testGetAllLatestValues() {
        System.out.println("=== Testing Get All Latest Values ===");
        setUp();
        
        // Setup test data
        store.set("user1", "name", "Alice", baseTime);
        store.set("user1", "email", "alice@example.com", baseTime + 1000);
        store.set("user1", "age", "25", baseTime + 2000);
        store.set("user1", "name", "Alice Smith", baseTime + 3000); // Update name
        
        Map<String, String> allValues = store.getAllLatestValues("user1");
        assert allValues.size() == 3 : "Should have 3 sub_keys";
        assert "Alice Smith".equals(allValues.get("name")) : "Should have latest name";
        assert "alice@example.com".equals(allValues.get("email")) : "Should have email";
        assert "25".equals(allValues.get("age")) : "Should have age";
        
        // Test nonexistent key
        Map<String, String> empty = store.getAllLatestValues("nonexistent");
        assert empty.isEmpty() : "Should return empty map for nonexistent key";
        
        // Test null key
        Map<String, String> nullResult = store.getAllLatestValues(null);
        assert nullResult.isEmpty() : "Should return empty map for null key";
        
        System.out.println("✓ Get All Latest Values tests passed");
    }
    
    public void testGetValueAtTimestamp() {
        System.out.println("=== Testing Get Value At Timestamp ===");
        setUp();
        
        // Setup test data with different timestamps
        store.set("user1", "name", "Alice", baseTime);
        store.set("user1", "name", "Alice Johnson", baseTime + 2000);
        store.set("user1", "name", "Alice Smith", baseTime + 4000);
        
        // Test getting values at different timestamps
        assert "Alice".equals(store.getValueAtTimestamp("user1", "name", baseTime)) : 
               "Should get Alice at baseTime";
        assert "Alice".equals(store.getValueAtTimestamp("user1", "name", baseTime + 1000)) : 
               "Should get Alice at baseTime + 1000";
        assert "Alice Johnson".equals(store.getValueAtTimestamp("user1", "name", baseTime + 2000)) : 
               "Should get Alice Johnson at baseTime + 2000";
        assert "Alice Johnson".equals(store.getValueAtTimestamp("user1", "name", baseTime + 3000)) : 
               "Should get Alice Johnson at baseTime + 3000";
        assert "Alice Smith".equals(store.getValueAtTimestamp("user1", "name", baseTime + 4000)) : 
               "Should get Alice Smith at baseTime + 4000";
        assert "Alice Smith".equals(store.getValueAtTimestamp("user1", "name", baseTime + 5000)) : 
               "Should get Alice Smith at future time";
        
        // Test timestamp before any value
        assert store.getValueAtTimestamp("user1", "name", baseTime - 1000) == null : 
               "Should return null for timestamp before any value";
        
        System.out.println("✓ Get Value At Timestamp tests passed");
    }
    
    public void testGetAllValuesAtTimestamp() {
        System.out.println("=== Testing Get All Values At Timestamp ===");
        setUp();
        
        // Setup test data
        store.set("user1", "name", "Alice", baseTime);
        store.set("user1", "email", "alice@example.com", baseTime + 1000);
        store.set("user1", "age", "25", baseTime + 2000);
        store.set("user1", "name", "Alice Smith", baseTime + 3000);
        
        // Test at different timestamps
        Map<String, String> valuesAt1500 = store.getAllValuesAtTimestamp("user1", baseTime + 1500);
        assert valuesAt1500.size() == 2 : "Should have 2 values at baseTime + 1500";
        assert "Alice".equals(valuesAt1500.get("name")) : "Should have original name";
        assert "alice@example.com".equals(valuesAt1500.get("email")) : "Should have email";
        
        Map<String, String> valuesAt4000 = store.getAllValuesAtTimestamp("user1", baseTime + 4000);
        assert valuesAt4000.size() == 3 : "Should have 3 values at baseTime + 4000";
        assert "Alice Smith".equals(valuesAt4000.get("name")) : "Should have updated name";
        assert "alice@example.com".equals(valuesAt4000.get("email")) : "Should have email";
        assert "25".equals(valuesAt4000.get("age")) : "Should have age";
        
        System.out.println("✓ Get All Values At Timestamp tests passed");
    }
    
    // ==================== LEVEL 3: History and Range Operations Tests ====================
    
    public void testGetHistory() {
        System.out.println("=== Testing Get History ===");
        setUp();
        
        // Setup test data
        store.set("user1", "name", "Alice", baseTime);
        store.set("user1", "name", "Alice Johnson", baseTime + 2000);
        store.set("user1", "name", "Alice Smith", baseTime + 4000);
        
        List<HierarchicalKeyValueStore.TimestampedValue> history = store.getHistory("user1", "name");
        assert history.size() == 3 : "Should have 3 historical values";
        assert "Alice".equals(history.get(0).getValue()) : "First value should be Alice";
        assert "Alice Johnson".equals(history.get(1).getValue()) : "Second value should be Alice Johnson";
        assert "Alice Smith".equals(history.get(2).getValue()) : "Third value should be Alice Smith";
        
        // Test timestamps are in order
        assert history.get(0).getTimestamp() == baseTime : "First timestamp should be baseTime";
        assert history.get(1).getTimestamp() == baseTime + 2000 : "Second timestamp should be baseTime + 2000";
        assert history.get(2).getTimestamp() == baseTime + 4000 : "Third timestamp should be baseTime + 4000";
        
        // Test nonexistent key/sub_key
        List<HierarchicalKeyValueStore.TimestampedValue> empty = store.getHistory("nonexistent", "name");
        assert empty.isEmpty() : "Should return empty list for nonexistent key";
        
        System.out.println("✓ Get History tests passed");
    }
    
    public void testGetValuesInRange() {
        System.out.println("=== Testing Get Values In Range ===");
        setUp();
        
        // Setup test data
        store.set("user1", "name", "Alice", baseTime);
        store.set("user1", "name", "Alice Johnson", baseTime + 2000);
        store.set("user1", "name", "Alice Smith", baseTime + 4000);
        store.set("user1", "name", "Alice Brown", baseTime + 6000);
        
        // Test range query
        List<HierarchicalKeyValueStore.TimestampedValue> rangeValues = 
            store.getValuesInRange("user1", "name", baseTime + 1000, baseTime + 5000);
        
        assert rangeValues.size() == 2 : "Should have 2 values in range";
        assert "Alice Johnson".equals(rangeValues.get(0).getValue()) : "First value should be Alice Johnson";
        assert "Alice Smith".equals(rangeValues.get(1).getValue()) : "Second value should be Alice Smith";
        
        // Test empty range
        List<HierarchicalKeyValueStore.TimestampedValue> emptyRange = 
            store.getValuesInRange("user1", "name", baseTime + 7000, baseTime + 8000);
        assert emptyRange.isEmpty() : "Should return empty list for range with no values";
        
        System.out.println("✓ Get Values In Range tests passed");
    }
    
    public void testGetSubKeysAndKeys() {
        System.out.println("=== Testing Get SubKeys and Keys ===");
        setUp();
        
        // Setup test data
        store.set("user1", "name", "Alice", baseTime);
        store.set("user1", "email", "alice@example.com", baseTime + 1000);
        store.set("user2", "name", "Bob", baseTime + 2000);
        store.set("config", "timeout", "30", baseTime + 3000);
        
        // Test get sub_keys
        Set<String> user1SubKeys = store.getSubKeys("user1");
        assert user1SubKeys.size() == 2 : "user1 should have 2 sub_keys";
        assert user1SubKeys.contains("name") : "Should contain name";
        assert user1SubKeys.contains("email") : "Should contain email";
        
        Set<String> user2SubKeys = store.getSubKeys("user2");
        assert user2SubKeys.size() == 1 : "user2 should have 1 sub_key";
        assert user2SubKeys.contains("name") : "Should contain name";
        
        // Test get all keys
        Set<String> allKeys = store.getAllKeys();
        assert allKeys.size() == 3 : "Should have 3 keys";
        assert allKeys.contains("user1") : "Should contain user1";
        assert allKeys.contains("user2") : "Should contain user2";
        assert allKeys.contains("config") : "Should contain config";
        
        System.out.println("✓ Get SubKeys and Keys tests passed");
    }
    
    // ==================== LEVEL 4: Maintenance Operations Tests ====================
    
    public void testCompactHistory() {
        System.out.println("=== Testing Compact History ===");
        setUp();
        
        // Setup test data with multiple versions
        store.set("user1", "name", "Alice", baseTime);
        store.set("user1", "name", "Alice Johnson", baseTime + 1000);
        store.set("user1", "name", "Alice Smith", baseTime + 2000);
        store.set("user1", "name", "Alice Brown", baseTime + 3000);
        store.set("user1", "name", "Alice Wilson", baseTime + 4000);
        
        assert store.getHistory("user1", "name").size() == 5 : "Should have 5 historical values";
        
        // Compact to keep only latest 2
        int removed = store.compactHistory("user1", "name", 2);
        assert removed == 3 : "Should remove 3 old values";
        
        List<HierarchicalKeyValueStore.TimestampedValue> compactedHistory = store.getHistory("user1", "name");
        assert compactedHistory.size() == 2 : "Should have 2 values after compaction";
        assert "Alice Brown".equals(compactedHistory.get(0).getValue()) : "Should keep Alice Brown";
        assert "Alice Wilson".equals(compactedHistory.get(1).getValue()) : "Should keep Alice Wilson";
        
        // Test compacting when already within limit
        int removedAgain = store.compactHistory("user1", "name", 5);
        assert removedAgain == 0 : "Should remove 0 values when already within limit";
        
        System.out.println("✓ Compact History tests passed");
    }
    
    public void testCleanupOldValues() {
        System.out.println("=== Testing Cleanup Old Values ===");
        setUp();
        
        // Setup test data
        store.set("user1", "name", "Alice", baseTime);
        store.set("user1", "name", "Alice Johnson", baseTime + 2000);
        store.set("user1", "name", "Alice Smith", baseTime + 4000);
        store.set("user1", "name", "Alice Brown", baseTime + 6000);
        
        // Cleanup values older than baseTime + 3000
        int removed = store.cleanupOldValues("user1", "name", baseTime + 3000);
        assert removed == 2 : "Should remove 2 old values";
        
        List<HierarchicalKeyValueStore.TimestampedValue> remainingHistory = store.getHistory("user1", "name");
        assert remainingHistory.size() == 2 : "Should have 2 values remaining";
        assert "Alice Smith".equals(remainingHistory.get(0).getValue()) : "Should keep Alice Smith";
        assert "Alice Brown".equals(remainingHistory.get(1).getValue()) : "Should keep Alice Brown";
        
        System.out.println("✓ Cleanup Old Values tests passed");
    }
    
    public void testStatistics() {
        System.out.println("=== Testing Statistics ===");
        setUp();
        
        // Setup test data
        store.set("user1", "name", "Alice", baseTime);
        store.set("user1", "email", "alice@example.com", baseTime + 1000);
        store.set("user1", "name", "Alice Smith", baseTime + 2000); // Second version
        store.set("user2", "name", "Bob", baseTime + 3000);
        store.set("config", "timeout", "30", baseTime + 4000);
        
        Map<String, Object> stats = store.getStatistics();
        assert (Integer) stats.get("totalKeys") == 3 : "Should have 3 keys";
        assert (Integer) stats.get("totalSubKeys") == 4 : "Should have 4 sub_keys";
        assert (Integer) stats.get("totalValues") == 5 : "Should have 5 total values";
        
        double avgSubKeysPerKey = (Double) stats.get("averageSubKeysPerKey");
        assert Math.abs(avgSubKeysPerKey - 4.0/3.0) < 0.001 : "Average sub_keys per key should be 4/3";
        
        double avgValuesPerSubKey = (Double) stats.get("averageValuesPerSubKey");
        assert Math.abs(avgValuesPerSubKey - 5.0/4.0) < 0.001 : "Average values per sub_key should be 5/4";
        
        System.out.println("✓ Statistics tests passed");
    }
    
    // ==================== Utility Methods Tests ====================
    
    public void testUtilityMethods() {
        System.out.println("=== Testing Utility Methods ===");
        setUp();
        
        // Test empty store
        assert store.size() == 0 : "Empty store should have size 0";
        assert store.isEmpty() : "Empty store should be empty";
        
        // Add some data
        store.set("user1", "name", "Alice", baseTime);
        store.set("user2", "name", "Bob", baseTime + 1000);
        
        assert store.size() == 2 : "Store should have size 2";
        assert !store.isEmpty() : "Store should not be empty";
        
        // Test contains methods
        assert store.containsKey("user1") : "Should contain user1";
        assert store.containsKey("user2") : "Should contain user2";
        assert !store.containsKey("user3") : "Should not contain user3";
        
        assert store.containsSubKey("user1", "name") : "Should contain user1 name";
        assert !store.containsSubKey("user1", "email") : "Should not contain user1 email";
        assert !store.containsSubKey("user3", "name") : "Should not contain user3 name";
        
        // Test clear
        store.clear();
        assert store.isEmpty() : "Store should be empty after clear";
        assert store.size() == 0 : "Store should have size 0 after clear";
        
        System.out.println("✓ Utility Methods tests passed");
    }
    
    // ==================== Integration Tests ====================
    
    public void testComplexScenario() {
        System.out.println("=== Testing Complex Integration Scenario ===");
        setUp();
        
        // Simulate a user profile system with version history
        long t0 = baseTime;
        long t1 = baseTime + 1000;
        long t2 = baseTime + 2000;
        long t3 = baseTime + 3000;
        long t4 = baseTime + 4000;
        
        // User registration
        store.set("user123", "name", "John Doe", t0);
        store.set("user123", "email", "john@example.com", t0);
        store.set("user123", "status", "active", t0);
        
        // User updates profile
        store.set("user123", "name", "John Smith", t1);
        store.set("user123", "phone", "555-1234", t1);
        
        // User changes email
        store.set("user123", "email", "john.smith@example.com", t2);
        
        // User gets married, changes name again
        store.set("user123", "name", "John Johnson", t3);
        store.set("user123", "spouse", "Jane Johnson", t3);
        
        // Verify current state
        Map<String, String> currentProfile = store.getAllLatestValues("user123");
        assert "John Johnson".equals(currentProfile.get("name")) : "Current name should be John Johnson";
        assert "john.smith@example.com".equals(currentProfile.get("email")) : "Current email should be updated";
        assert "555-1234".equals(currentProfile.get("phone")) : "Phone should be present";
        assert "Jane Johnson".equals(currentProfile.get("spouse")) : "Spouse should be present";
        assert "active".equals(currentProfile.get("status")) : "Status should still be active";
        
        // Verify historical queries
        assert "John Doe".equals(store.getValueAtTimestamp("user123", "name", t0)) : "Original name at t0";
        assert "John Smith".equals(store.getValueAtTimestamp("user123", "name", t1)) : "Updated name at t1";
        assert "John Johnson".equals(store.getValueAtTimestamp("user123", "name", t4)) : "Final name at t4";
        
        // Verify profile state at different times
        Map<String, String> profileAtT1 = store.getAllValuesAtTimestamp("user123", t1);
        assert profileAtT1.size() == 4 : "Should have 4 fields at t1";
        assert "John Smith".equals(profileAtT1.get("name")) : "Name should be updated at t1";
        assert "john@example.com".equals(profileAtT1.get("email")) : "Email should be original at t1";
        
        // Test history tracking
        List<HierarchicalKeyValueStore.TimestampedValue> nameHistory = store.getHistory("user123", "name");
        assert nameHistory.size() == 3 : "Should have 3 name changes";
        
        // Test range queries
        List<HierarchicalKeyValueStore.TimestampedValue> emailChanges = 
            store.getValuesInRange("user123", "email", t0, t4);
        assert emailChanges.size() == 2 : "Should have 2 email changes in range";
        
        System.out.println("✓ Complex Integration Scenario tests passed");
    }
    
    // ==================== Performance Tests ====================
    
    public void testPerformance() {
        System.out.println("=== Testing Performance ===");
        setUp();
        
        long startTime = System.currentTimeMillis();
        
        // Test with large dataset
        int numKeys = 1000;
        int numSubKeys = 10;
        int numVersions = 5;
        
        for (int i = 0; i < numKeys; i++) {
            for (int j = 0; j < numSubKeys; j++) {
                for (int v = 0; v < numVersions; v++) {
                    store.set("key" + i, "subkey" + j, "value" + i + "_" + j + "_" + v, baseTime + v * 1000);
                }
            }
        }
        
        long setTime = System.currentTimeMillis();
        System.out.println("Set " + (numKeys * numSubKeys * numVersions) + " values in " + (setTime - startTime) + "ms");
        
        // Test get performance
        for (int i = 0; i < numKeys; i++) {
            for (int j = 0; j < numSubKeys; j++) {
                String value = store.getLatest("key" + i, "subkey" + j);
                assert value != null : "Should get value for key" + i + " subkey" + j;
            }
        }
        
        long getTime = System.currentTimeMillis();
        System.out.println("Get " + (numKeys * numSubKeys) + " latest values in " + (getTime - setTime) + "ms");
        
        // Test historical queries
        for (int i = 0; i < 100; i++) {
            String value = store.getValueAtTimestamp("key" + i, "subkey0", baseTime + 2500);
            assert value != null : "Should get historical value";
        }
        
        long historyTime = System.currentTimeMillis();
        System.out.println("100 historical queries in " + (historyTime - getTime) + "ms");
        
        Map<String, Object> stats = store.getStatistics();
        System.out.println("Final statistics: " + stats);
        
        System.out.println("✓ Performance tests completed");
    }
    
    // ==================== Main Test Runner ====================
    
    public static void main(String[] args) {
        HierarchicalKeyValueStoreTest test = new HierarchicalKeyValueStoreTest();
        
        try {
            // Level 1 Tests
            test.testBasicSetAndGet();
            test.testDeleteOperations();
            test.testEdgeCases();
            
            // Level 2 Tests
            test.testGetAllLatestValues();
            test.testGetValueAtTimestamp();
            test.testGetAllValuesAtTimestamp();
            
            // Level 3 Tests
            test.testGetHistory();
            test.testGetValuesInRange();
            test.testGetSubKeysAndKeys();
            
            // Level 4 Tests
            test.testCompactHistory();
            test.testCleanupOldValues();
            test.testStatistics();
            
            // Additional Tests
            test.testUtilityMethods();
            test.testComplexScenario();
            test.testPerformance();
            
            System.out.println("\n🎉 ALL TESTS PASSED! 🎉");
            System.out.println("The HierarchicalKeyValueStore is ready for all 4 levels!");
            
        } catch (AssertionError e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
