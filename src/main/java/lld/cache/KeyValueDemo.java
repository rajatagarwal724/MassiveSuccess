package lld.cache;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;

public class KeyValueDemo {
    public static void main(String[] args) throws InterruptedException {
        // Create a key-value store with String keys and Object values
        KeyValue<String, Object> store = new KeyValue<>();
        
        System.out.println("===== Basic Operations =====");
        
        // Set some values
        store.set("name", "John Doe");
        store.set("age", 30);
        store.set("active", true, 5); // expires in 5 seconds
        
        // Set a collection
        Collection<Object> hobbies = new ArrayList<>();
        hobbies.add("Reading");
        hobbies.add("Coding");
        hobbies.add("Swimming");
        store.setCollection("hobbies", hobbies);
        
        // Get values
        System.out.println("name: " + store.get("name").orElse(null));
        System.out.println("age: " + store.get("age").orElse(null));
        System.out.println("hobbies: " + store.getCollection("hobbies").orElse(null));
        
        System.out.println("\n===== Expiry =====");
        
        System.out.println("active initially: " + store.get("active").orElse(null));
        System.out.println("Waiting 6 seconds for expiry...");
        Thread.sleep(6000); // Wait for the key to expire
        System.out.println("active after expiry: " + store.get("active").orElse("(expired)"));
        
        System.out.println("\n===== Bulk Operations =====");
        
        // Multiple set
        Map<String, Object> newValues = new HashMap<>();
        newValues.put("city", "New York");
        newValues.put("country", "USA");
        newValues.put("zip", 10001);
        store.mSet(newValues);
        
        // Multiple get
        List<String> keysToGet = Arrays.asList("name", "city", "country", "zip");
        Map<String, Object> retrievedValues = store.mGet(keysToGet);
        System.out.println("Multiple values: " + retrievedValues);
        
        System.out.println("\n===== Transactions =====");
        
        System.out.println("Before transaction - age: " + store.get("age").orElse(null));
        
        // Start a transaction
        store.beginTransaction();
        
        // Make changes in the transaction
        store.set("age", 31);
        store.set("newKey", "This will be rolled back");
        store.remove("city");
        
        System.out.println("During transaction - age: " + store.get("age").orElse(null));
        System.out.println("During transaction - newKey: " + store.get("newKey").orElse(null));
        System.out.println("During transaction - city: " + store.get("city").orElse("(removed in transaction)"));
        
        // Rollback the transaction
        store.rollback();
        
        System.out.println("After rollback - age: " + store.get("age").orElse(null));
        System.out.println("After rollback - newKey: " + store.get("newKey").orElse("(doesn't exist)"));
        System.out.println("After rollback - city: " + store.get("city").orElse("(doesn't exist)"));
        
        // Start another transaction and commit
        store.beginTransaction();
        store.set("age", 32);
        store.set("status", "Updated");
        
        System.out.println("\nBefore commit - age: " + store.get("age").orElse(null));
        System.out.println("Before commit - status: " + store.get("status").orElse(null));
        
        store.commit();
        
        System.out.println("After commit - age: " + store.get("age").orElse(null));
        System.out.println("After commit - status: " + store.get("status").orElse(null));
        
        System.out.println("\n===== System Operations =====");
        
        System.out.println("Total keys: " + store.size());
        
        // Flush all
        store.flushAll();
        System.out.println("After flush - Total keys: " + store.size());
    }
}
