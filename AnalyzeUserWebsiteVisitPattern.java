import java.util.*;

public class AnalyzeUserWebsiteVisitPattern {
    
    public List<String> mostVisitedPattern(String[] username, int[] timestamp, String[] website) {
        // Step 1: Create a map to group visits by user
        Map<String, List<Visit>> userVisits = new HashMap<>();
        
        for (int i = 0; i < username.length; i++) {
            userVisits.computeIfAbsent(username[i], k -> new ArrayList<>())
                     .add(new Visit(timestamp[i], website[i]));
        }
        
        // Step 2: Generate all possible 3-sequences for each user
        Map<List<String>, Integer> sequenceCount = new HashMap<>();
        
        for (List<Visit> visits : userVisits.values()) {
            // Sort visits by timestamp
            Collections.sort(visits, (a, b) -> Integer.compare(a.timestamp, b.timestamp));
            
            // Generate all possible 3-sequences
            generateSequences(visits, sequenceCount);
        }
        
        // Step 3: Find the most common sequence
        List<String> result = null;
        int maxCount = 0;
        
        for (Map.Entry<List<String>, Integer> entry : sequenceCount.entrySet()) {
            List<String> sequence = entry.getKey();
            int count = entry.getValue();
            
            if (count > maxCount || 
                (count == maxCount && (result == null || isLexicographicallySmaller(sequence, result)))) {
                maxCount = count;
                result = new ArrayList<>(sequence);
            }
        }
        
        return result;
    }
    
    private void generateSequences(List<Visit> visits, Map<List<String>, Integer> sequenceCount) {
        int n = visits.size();
        
        // Use a set to avoid counting the same sequence multiple times for the same user
        Set<List<String>> userSequences = new HashSet<>();
        
        // Generate all possible 3-sequences
        for (int i = 0; i < n - 2; i++) {
            for (int j = i + 1; j < n - 1; j++) {
                for (int k = j + 1; k < n; k++) {
                    List<String> sequence = Arrays.asList(
                        visits.get(i).website,
                        visits.get(j).website,
                        visits.get(k).website
                    );
                    userSequences.add(sequence);
                }
            }
        }
        
        // Count sequences for this user
        for (List<String> sequence : userSequences) {
            sequenceCount.put(sequence, sequenceCount.getOrDefault(sequence, 0) + 1);
        }
    }
    
    private boolean isLexicographicallySmaller(List<String> seq1, List<String> seq2) {
        for (int i = 0; i < 3; i++) {
            int comparison = seq1.get(i).compareTo(seq2.get(i));
            if (comparison != 0) {
                return comparison < 0;
            }
        }
        return false; // They are equal
    }
    
    static class Visit {
        int timestamp;
        String website;
        
        Visit(int timestamp, String website) {
            this.timestamp = timestamp;
            this.website = website;
        }
    }
    
    public static void main(String[] args) {
        AnalyzeUserWebsiteVisitPattern solution = new AnalyzeUserWebsiteVisitPattern();
        
        // Test case 1
        String[] username1 = {"joe","joe","joe","james","james","james","james","mary","mary","mary"};
        int[] timestamp1 = {1,2,3,4,5,6,7,8,9,10};
        String[] website1 = {"home","about","career","home","cart","maps","home","home","about","career"};
        
        List<String> result1 = solution.mostVisitedPattern(username1, timestamp1, website1);
        System.out.println("Test 1: " + result1); // Expected: ["home","about","career"]
        
        // Test case 2
        String[] username2 = {"ua","ua","ua","ub","ub","ub"};
        int[] timestamp2 = {1,2,3,4,5,6};
        String[] website2 = {"a","b","a","a","b","c"};
        
        List<String> result2 = solution.mostVisitedPattern(username2, timestamp2, website2);
        System.out.println("Test 2: " + result2); // Expected: ["a","b","a"]
    }
} 