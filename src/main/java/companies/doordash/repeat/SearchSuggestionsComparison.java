package companies.doordash.repeat;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Complete comparison of Search Suggestions System implementations:
 * 1. Approach A: Store suggestions in TrieNode (faster search, more memory)
 * 2. Approach B: Don't store suggestions (less memory, slower search)
 * 3. Approach C: Binary search approach for comparison
 * 
 * Time Complexity Comparison:
 * - Approach A: Construction O(N×L×log K), Search O(M×K log K)
 * - Approach B: Construction O(N×L), Search O(M + W + W log W)
 * - Approach C: Construction O(N×L×log N), Search O(M×log N×L)
 * 
 * Space Complexity Comparison:
 * - Approach A: O(N×L×K) - stores K suggestions per node
 * - Approach B: O(N×L) - basic trie structure only
 * - Approach C: O(N×L) - sorted array only
 * 
 * Where: N=products, L=avg length, M=search word length, K=suggestions count, W=words with prefix
 */
public class SearchSuggestionsComparison {

    // ==================== APPROACH A: WITH SUGGESTIONS STORED ====================
    
    static class TrieWithSuggestions {
        class TrieNode {
            Map<Character, TrieNode> children;
            PriorityQueue<String> suggestedWords; // Max heap to keep top K lexicographically smallest
            
            public TrieNode() {
                this.children = new HashMap<>();
                this.suggestedWords = new PriorityQueue<>(Comparator.reverseOrder());
            }
        }
        
        private TrieNode root;
        private int K;
        
        public TrieWithSuggestions(int k) {
            this.root = new TrieNode();
            this.K = k;
        }
        
        /**
         * Insert word into trie with suggestions stored at each node
         * Time: O(L × log K) where L is word length
         */
        public void insert(String word) {
            if (word == null || word.isEmpty()) return;
            
            TrieNode curr = root;
            for (char ch : word.toCharArray()) {
                curr.children.putIfAbsent(ch, new TrieNode());
                curr = curr.children.get(ch);
                
                // Add suggestion to current node
                curr.suggestedWords.offer(word);
                if (curr.suggestedWords.size() > K) {
                    curr.suggestedWords.poll(); // Remove lexicographically largest
                }
            }
        }
        
        /**
         * Search for suggestions with given prefix
         * Time: O(M + K log K) where M is prefix length
         */
        public List<String> search(String prefix) {
            if (prefix == null || prefix.isEmpty()) return new ArrayList<>();
            
            TrieNode curr = root;
            // Navigate to prefix node
            for (char ch : prefix.toCharArray()) {
                if (!curr.children.containsKey(ch)) {
                    return new ArrayList<>();
                }
                curr = curr.children.get(ch);
            }
            
            // Convert PriorityQueue to sorted list
            List<String> result = new ArrayList<>(curr.suggestedWords);
            Collections.sort(result);
            return result;
        }
        
        /**
         * Get suggestions for each prefix of search word
         * Time: O(M² + M×K log K) - can be optimized to O(M×K log K)
         */
        public List<List<String>> suggestedProducts(String[] products, String searchWord) {
            // Build trie
            for (String product : products) {
                insert(product);
            }
            
            List<List<String>> result = new ArrayList<>();
            // Current implementation: O(M²) due to restarting from root each time
            for (int i = 0; i < searchWord.length(); i++) {
                String prefix = searchWord.substring(0, i + 1);
                result.add(search(prefix));
            }
            
            return result;
        }
        
        /**
         * Optimized version that tracks current node to avoid O(M²) complexity
         * Time: O(M×K log K)
         */
        public List<List<String>> suggestedProductsOptimized(String[] products, String searchWord) {
            // Build trie
            for (String product : products) {
                insert(product);
            }
            
            List<List<String>> result = new ArrayList<>();
            TrieNode curr = root;
            
            for (int i = 0; i < searchWord.length(); i++) {
                char ch = searchWord.charAt(i);
                
                if (curr != null && curr.children.containsKey(ch)) {
                    curr = curr.children.get(ch);
                    List<String> suggestions = new ArrayList<>(curr.suggestedWords);
                    Collections.sort(suggestions);
                    result.add(suggestions);
                } else {
                    curr = null;
                    result.add(new ArrayList<>());
                }
            }
            
            return result;
        }
    }
    
    // ==================== APPROACH B: WITHOUT SUGGESTIONS STORED ====================
    
    static class TrieWithoutSuggestions {
        class TrieNode {
            Map<Character, TrieNode> children;
            boolean isEndOfWord;
            
            public TrieNode() {
                this.children = new HashMap<>();
                this.isEndOfWord = false;
            }
        }
        
        private TrieNode root;
        
        public TrieWithoutSuggestions() {
            this.root = new TrieNode();
        }
        
        /**
         * Insert word into basic trie structure
         * Time: O(L) where L is word length
         */
        public void insert(String word) {
            if (word == null || word.isEmpty()) return;
            
            TrieNode curr = root;
            for (char ch : word.toCharArray()) {
                curr.children.putIfAbsent(ch, new TrieNode());
                curr = curr.children.get(ch);
            }
            curr.isEndOfWord = true;
        }
        
        /**
         * Search for suggestions with given prefix using DFS
         * Time: O(M + W + W log W) where W is number of words with prefix
         */
        public List<String> search(String prefix, int k) {
            if (prefix == null || prefix.isEmpty()) return new ArrayList<>();
            
            TrieNode curr = root;
            // Navigate to prefix node
            for (char ch : prefix.toCharArray()) {
                if (!curr.children.containsKey(ch)) {
                    return new ArrayList<>();
                }
                curr = curr.children.get(ch);
            }
            
            // DFS to find all words with this prefix
            List<String> allWords = new ArrayList<>();
            dfs(curr, prefix, allWords);
            
            // Sort and return top K
            Collections.sort(allWords);
            return allWords.stream().limit(k).collect(Collectors.toList());
        }
        
        /**
         * DFS helper to collect all words from current node
         */
        private void dfs(TrieNode node, String current, List<String> result) {
            if (node.isEndOfWord) {
                result.add(current);
            }
            
            for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
                dfs(entry.getValue(), current + entry.getKey(), result);
            }
        }
        
        /**
         * Get suggestions for each prefix of search word
         * Time: O(M × (W + W log W)) where W varies per prefix
         */
        public List<List<String>> suggestedProducts(String[] products, String searchWord, int k) {
            // Build trie
            for (String product : products) {
                insert(product);
            }
            
            List<List<String>> result = new ArrayList<>();
            for (int i = 0; i < searchWord.length(); i++) {
                String prefix = searchWord.substring(0, i + 1);
                result.add(search(prefix, k));
            }
            
            return result;
        }
    }
    
    // ==================== APPROACH C: BINARY SEARCH ====================
    
    static class BinarySearchApproach {
        
        /**
         * Binary search approach for search suggestions
         * Time: O(N×L×log N) construction + O(M×log N×L) per search
         */
        public List<List<String>> suggestedProducts(String[] products, String searchWord, int k) {
            // Sort products lexicographically
            Arrays.sort(products);
            
            List<List<String>> result = new ArrayList<>();
            
            for (int i = 0; i < searchWord.length(); i++) {
                String prefix = searchWord.substring(0, i + 1);
                
                // Find first product that starts with prefix
                int left = findFirst(products, prefix);
                if (left == -1) {
                    result.add(new ArrayList<>());
                    continue;
                }
                
                // Find last product that starts with prefix
                int right = findLast(products, prefix);
                
                // Extract up to k products
                List<String> suggestions = new ArrayList<>();
                for (int j = left; j <= right && suggestions.size() < k; j++) {
                    suggestions.add(products[j]);
                }
                
                result.add(suggestions);
            }
            
            return result;
        }
        
        private int findFirst(String[] products, String prefix) {
            int left = 0, right = products.length - 1;
            int result = -1;
            
            while (left <= right) {
                int mid = left + (right - left) / 2;
                if (products[mid].startsWith(prefix)) {
                    result = mid;
                    right = mid - 1; // Continue searching left
                } else if (products[mid].compareTo(prefix) < 0) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
            
            return result;
        }
        
        private int findLast(String[] products, String prefix) {
            int left = 0, right = products.length - 1;
            int result = -1;
            
            while (left <= right) {
                int mid = left + (right - left) / 2;
                if (products[mid].startsWith(prefix)) {
                    result = mid;
                    left = mid + 1; // Continue searching right
                } else if (products[mid].compareTo(prefix) < 0) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
            
            return result;
        }
    }
    
    // ==================== PERFORMANCE TESTING ====================
    
    public static void performanceTest() {
        String[] products = {
            "mobile", "mouse", "moneypot", "monitor", "mousepad", "motorcycle",
            "mountain", "movie", "music", "museum", "mushroom", "mustard"
        };
        String searchWord = "mouse";
        int k = 3;
        
        System.out.println("=== PERFORMANCE COMPARISON ===");
        System.out.println("Products: " + Arrays.toString(products));
        System.out.println("Search word: " + searchWord);
        System.out.println("K: " + k);
        System.out.println();
        
        // Test Approach A: With Suggestions Stored
        long startTime = System.nanoTime();
        TrieWithSuggestions trieA = new TrieWithSuggestions(k);
        List<List<String>> resultA = trieA.suggestedProductsOptimized(products, searchWord);
        long endTime = System.nanoTime();
        
        System.out.println("Approach A (With Suggestions Stored):");
        System.out.println("Result: " + resultA);
        System.out.println("Time: " + (endTime - startTime) / 1000000.0 + " ms");
        System.out.println();
        
        // Test Approach B: Without Suggestions Stored
        startTime = System.nanoTime();
        TrieWithoutSuggestions trieB = new TrieWithoutSuggestions();
        List<List<String>> resultB = trieB.suggestedProducts(products, searchWord, k);
        endTime = System.nanoTime();
        
        System.out.println("Approach B (Without Suggestions Stored):");
        System.out.println("Result: " + resultB);
        System.out.println("Time: " + (endTime - startTime) / 1000000.0 + " ms");
        System.out.println();
        
        // Test Approach C: Binary Search
        startTime = System.nanoTime();
        BinarySearchApproach binarySearch = new BinarySearchApproach();
        List<List<String>> resultC = binarySearch.suggestedProducts(products, searchWord, k);
        endTime = System.nanoTime();
        
        System.out.println("Approach C (Binary Search):");
        System.out.println("Result: " + resultC);
        System.out.println("Time: " + (endTime - startTime) / 1000000.0 + " ms");
        System.out.println();
    }
    
    // ==================== MAIN METHOD ====================
    
    public static void main(String[] args) {
        performanceTest();
        
        // Additional test cases
        System.out.println("\n=== ADDITIONAL TEST CASES ===");
        
        String[] restaurantList = {
            "Panda Express", "Panera Bread", "Papa John's",
            "Pizza Hut", "Pieology", "Panini Place"
        };
        
        // Test with restaurant data
        TrieWithSuggestions restaurantTrie = new TrieWithSuggestions(3);
        for (String restaurant : restaurantList) {
            restaurantTrie.insert(restaurant);
        }
        
        String[] searchQueries = {"Pan", "Pie", "Pa"};
        for (String query : searchQueries) {
            List<String> suggestions = restaurantTrie.search(query);
            System.out.println(query + " -> " + suggestions);
        }
    }
} 