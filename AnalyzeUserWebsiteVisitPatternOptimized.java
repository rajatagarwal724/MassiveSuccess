import java.util.*;

public class AnalyzeUserWebsiteVisitPatternOptimized {
    
    public List<String> mostVisitedPattern(String[] username, int[] timestamp, String[] website) {
        // Step 1: Group visits by user
        Map<String, List<Visit>> userVisits = new HashMap<>();
        
        for (int i = 0; i < username.length; i++) {
            userVisits.computeIfAbsent(username[i], k -> new ArrayList<>())
                     .add(new Visit(timestamp[i], website[i]));
        }
        
        // Step 2: Count sequences across all users
        Map<Sequence, Integer> sequenceCount = new HashMap<>();
        
        for (List<Visit> visits : userVisits.values()) {
            // Sort by timestamp
            Collections.sort(visits, (a, b) -> Integer.compare(a.timestamp, b.timestamp));
            
            // Generate all 3-sequences for this user
            Set<Sequence> userSequences = new HashSet<>();
            generateSequences(visits, userSequences);
            
            // Count each unique sequence for this user
            for (Sequence seq : userSequences) {
                sequenceCount.put(seq, sequenceCount.getOrDefault(seq, 0) + 1);
            }
        }
        
        // Step 3: Find the most common sequence
        Sequence result = null;
        int maxCount = 0;
        
        for (Map.Entry<Sequence, Integer> entry : sequenceCount.entrySet()) {
            Sequence sequence = entry.getKey();
            int count = entry.getValue();
            
            if (count > maxCount || (count == maxCount && sequence.compareTo(result) < 0)) {
                maxCount = count;
                result = sequence;
            }
        }
        
        return result != null ? result.toList() : new ArrayList<>();
    }
    
    private void generateSequences(List<Visit> visits, Set<Sequence> sequences) {
        int n = visits.size();
        
        // Generate all possible 3-sequences
        for (int i = 0; i < n - 2; i++) {
            for (int j = i + 1; j < n - 1; j++) {
                for (int k = j + 1; k < n; k++) {
                    sequences.add(new Sequence(
                        visits.get(i).website,
                        visits.get(j).website,
                        visits.get(k).website
                    ));
                }
            }
        }
    }
    
    static class Visit {
        int timestamp;
        String website;
        
        Visit(int timestamp, String website) {
            this.timestamp = timestamp;
            this.website = website;
        }
    }
    
    static class Sequence implements Comparable<Sequence> {
        String first, second, third;
        
        Sequence(String first, String second, String third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Sequence sequence = (Sequence) obj;
            return Objects.equals(first, sequence.first) &&
                   Objects.equals(second, sequence.second) &&
                   Objects.equals(third, sequence.third);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(first, second, third);
        }
        
        @Override
        public int compareTo(Sequence other) {
            if (other == null) return -1;
            
            int comp1 = first.compareTo(other.first);
            if (comp1 != 0) return comp1;
            
            int comp2 = second.compareTo(other.second);
            if (comp2 != 0) return comp2;
            
            return third.compareTo(other.third);
        }
        
        public List<String> toList() {
            return Arrays.asList(first, second, third);
        }
        
        @Override
        public String toString() {
            return "[" + first + ", " + second + ", " + third + "]";
        }
    }
    
    public static void main(String[] args) {
        AnalyzeUserWebsiteVisitPatternOptimized solution = new AnalyzeUserWebsiteVisitPatternOptimized();
        
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
        
        // Test case 3: Edge case with no valid 3-sequences
        String[] username3 = {"user1", "user1"};
        int[] timestamp3 = {1, 2};
        String[] website3 = {"site1", "site2"};
        
        List<String> result3 = solution.mostVisitedPattern(username3, timestamp3, website3);
        System.out.println("Test 3: " + result3); // Expected: []
    }
} 