package coding.linkedin;

import java.util.*;

/**
 * Accounts Merge Problem
 * 
 * Merge accounts that belong to the same person based on common emails.
 * After merging, the first element of each account is the name, and the rest
 * are emails in sorted order.
 */
public class AccountsMerge {
    
    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        // Map to store email to parent email (for Union-Find)
        Map<String, String> emailToParent = new HashMap<>();
        
        // Map to store email to account name
        Map<String, String> emailToName = new HashMap<>();
        
        // Initialize each email as its own parent
        for (List<String> account : accounts) {
            String name = account.get(0);
            for (int i = 1; i < account.size(); i++) {
                String email = account.get(i);
                emailToParent.putIfAbsent(email, email); // Initialize email as its own parent
                emailToName.put(email, name);            // Map email to name
            }
        }
        
        // Union emails that belong to the same account
        for (List<String> account : accounts) {
            String firstEmail = account.get(1);
            for (int i = 2; i < account.size(); i++) {
                union(emailToParent, firstEmail, account.get(i));
            }
        }
        
        // Group emails by their parent email
        Map<String, List<String>> components = new HashMap<>();
        for (String email : emailToParent.keySet()) {
            String parent = find(emailToParent, email);
            components.computeIfAbsent(parent, k -> new ArrayList<>()).add(email);
        }
        
        // Create result list with sorted emails
        List<List<String>> result = new ArrayList<>();
        for (List<String> emails : components.values()) {
            Collections.sort(emails);  // Sort emails alphabetically
            String name = emailToName.get(emails.get(0));
            List<String> account = new ArrayList<>();
            account.add(name);         // Add name as first element
            account.addAll(emails);    // Add sorted emails
            result.add(account);
        }
        
        return result;
    }
    
    // Union-Find: Find operation with path compression
    private String find(Map<String, String> parent, String x) {
        if (!parent.get(x).equals(x)) {
            parent.put(x, find(parent, parent.get(x)));
        }
        return parent.get(x);
    }
    
    // Union-Find: Union operation
    private void union(Map<String, String> parent, String x, String y) {
        parent.put(find(parent, x), find(parent, y));
    }
    
    public static void main(String[] args) {
        AccountsMerge solution = new AccountsMerge();
        
        // Example 1
        List<List<String>> accounts1 = new ArrayList<>();
        accounts1.add(Arrays.asList("John", "johnsmith@mail.com", "john_newyork@mail.com"));
        accounts1.add(Arrays.asList("John", "johnsmith@mail.com", "john00@mail.com"));
        accounts1.add(Arrays.asList("Mary", "mary@mail.com"));
        accounts1.add(Arrays.asList("John", "johnnybravo@mail.com"));
        
        System.out.println("Example 1 Output:");
        List<List<String>> result1 = solution.accountsMerge(accounts1);
        for (List<String> account : result1) {
            System.out.println(account);
        }
        
        // Example 2
        List<List<String>> accounts2 = new ArrayList<>();
        accounts2.add(Arrays.asList("Gabe", "Gabe0@m.co", "Gabe3@m.co", "Gabe1@m.co"));
        accounts2.add(Arrays.asList("Kevin", "Kevin3@m.co", "Kevin5@m.co", "Kevin0@m.co"));
        accounts2.add(Arrays.asList("Ethan", "Ethan5@m.co", "Ethan4@m.co", "Ethan0@m.co"));
        accounts2.add(Arrays.asList("Hanzo", "Hanzo3@m.co", "Hanzo1@m.co", "Hanzo0@m.co"));
        accounts2.add(Arrays.asList("Fern", "Fern5@m.co", "Fern1@m.co", "Fern0@m.co"));
        
        System.out.println("\nExample 2 Output:");
        List<List<String>> result2 = solution.accountsMerge(accounts2);
        for (List<String> account : result2) {
            System.out.println(account);
        }
    }
}
