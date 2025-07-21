//package companies.doordash;
//
//import java.util.*;
//
///**
// * TRIE-BASED SEARCH SUGGESTIONS SOLUTION
// *
// * This solution implements a Trie (prefix tree) data structure with heaps to efficiently
// * provide search suggestions for restaurants based on prefixes.
// *
// * PROBLEM DESCRIPTION:
// * Given a list of restaurants and search words, return the top K lexicographically
// * smallest restaurant names that start with each search word prefix.
// *
// * ALGORITHM APPROACH:
// * 1. Build a Trie where each node contains:
// *    - A map of character -> child TrieNode
// *    - A min-heap (PriorityQueue) storing top K words for this prefix
// *
// * 2. For each word insertion:
// *    - Traverse the Trie character by character
// *    - At each node, add the word to the heap
// *    - Maintain only K words by removing the lexicographically smallest when heap size > K
// *
// * 3. For each search query:
// *    - Traverse to the node representing the prefix
// *    - Return the sorted list of words stored in that node's heap
// *
// * TIME COMPLEXITY:
// * - Building Trie: O(N * M * log K) where N = number of words, M = average word length
// * - Each query: O(P + K log K) where P = prefix length, K = result size
// *
// * SPACE COMPLEXITY:
// * - O(N * M * K) where N = number of words, M = average word length, K = heap size per node
// */
//
///**
// * TrieNode represents each node in the Trie data structure
// */
//class TrieNode {
//    // Map to store child nodes for each character
//    Map<Character, TrieNode> children;
//
//    // Min-heap to store the top K lexicographically largest words for this prefix
//    // We use min-heap because PriorityQueue in Java naturally orders strings lexicographically
//    // When heap size exceeds K, we remove the smallest (lexicographically first) element
//    // This keeps the K lexicographically largest elements
//    PriorityQueue<String> wordsHeap;
//
//    public TrieNode() {
//        this.children = new HashMap<>();
//        // PriorityQueue with natural ordering for strings (lexicographic order)
//        this.wordsHeap = new PriorityQueue<>();
//    }
//}
//
///**
// * Trie class implements the prefix tree with heap functionality
// */
//class Trie {
//    private TrieNode root;
//    private int K; // Maximum number of suggestions to keep per prefix
//
//    public Trie(int K) {
//        this.root = new TrieNode();
//        this.K = K;
//    }
//
//    /**
//     * Insert a word into the Trie
//     * At each node along the path, we add this word to the heap and maintain top K words
//     *
//     * @param word The word to insert
//     */
//    public void insert(String word) {
//        TrieNode node = root;
//
//        // Traverse each character in the word
//        for (char ch : word.toCharArray()) {
//            // Create child node if it doesn't exist
//            if (!node.children.containsKey(ch)) {
//                node.children.put(ch, new TrieNode());
//            }
//            node = node.children.get(ch);
//
//            // Add word to this node's heap (represents all words with this prefix)
//            node.wordsHeap.offer(word);
//
//            // Maintain only K words - remove lexicographically smallest if heap exceeds K
//            // This ensures we keep the K lexicographically largest words
//            if (node.wordsHeap.size() > K) {
//                node.wordsHeap.poll(); // Remove the smallest element
//            }
//        }
//    }
//
//    /**
//     * Search for all words with the given prefix
//     *
//     * @param prefix The prefix to search for
//     * @return List of top K words with this prefix, sorted lexicographically
//     */
//    public List<String> searchPrefix(String prefix) {
//        TrieNode node = root;
//
//        // Navigate to the node representing this prefix
//        for (char ch : prefix.toCharArray()) {
//            if (!node.children.containsKey(ch)) {
//                return new ArrayList<>(); // Prefix not found
//            }
//            node = node.children.get(ch);
//        }
//
//        // Convert heap to sorted list
//        // The heap contains the K lexicographically largest words for this prefix
//        List<String> result = new ArrayList<>(node.wordsHeap);
//        Collections.sort(result); // Sort to get final order
//        return result;
//    }
//}
//
///**
// * Main solution class for restaurant search suggestions
// */
//public class TrieSearchSuggestionsWithExplanation {
//
//    /**
//     * Main algorithm to provide search suggestions
//     *
//     * @param restaurants Array of restaurant names
//     * @param searchWords Array of search prefixes
//     * @param K Maximum number of suggestions per prefix
//     * @return Map of search word -> list of matching restaurant suggestions
//     */
//    public Map<String, List<String>> searchSuggestions(String[] restaurants, String[] searchWords, int K) {
//        // Step 1: Build the Trie
//        Trie trie = new Trie(K);
//
//        // Step 2: Insert restaurants in lexicographically sorted order
//        // This helps maintain consistency in the heap operations
//        String[] sortedRestaurants = restaurants.clone();
//        Arrays.sort(sortedRestaurants);
//
//        for (String restaurant : sortedRestaurants) {
//            trie.insert(restaurant);
//        }
//
//        // Step 3: Process each search query
//        Map<String, List<String>> result = new HashMap<>();
//        for (String searchWord : searchWords) {
//            List<String> matches = trie.searchPrefix(searchWord);
//            result.put(searchWord, matches);
//        }
//
//        return result;
//    }
//
//    /**
//     * Helper method to print results in a formatted way
//     */
//    public void printResults(Map<String, List<String>> results) {
//        System.out.println("=== SEARCH SUGGESTIONS RESULTS ===");
//        for (Map.Entry<String, List<String>> entry : results.entrySet()) {
//            System.out.printf("Search: '%s' -> Suggestions: %s%n",
//                            entry.getKey(), entry.getValue());
//        }
//        System.out.println("===================================");
//    }
//
//    /**
//     * Example usage and test cases
//     */
//    public static void main(String[] args) {
//        // Test data
//        String[] restaurantList = {
//            "Panda Express", "Panera Bread", "Papa John's",
//            "Pizza Hut", "Pieology", "Panini Place",
//            "Panda Garden", "Papa Murphy's", "Pancho's"
//        };
//
//        String[] searchWords = {"Pan", "Pie", "Pa", "Pizza"};
//        int K = 3; // Top 3 suggestions per search
//
//        // Run the algorithm
//        TrieSearchSuggestionsWithExplanation solution = new TrieSearchSuggestionsWithExplanation();
//        Map<String, List<String>> results = solution.searchSuggestions(restaurantList, searchWords, K);
//
//        // Display results
//        solution.printResults(results);
//
//        // Additional test case with K=2
//        System.out.println("\n--- Testing with K=2 ---");
//        Map<String, List<String>> results2 = solution.searchSuggestions(restaurantList, searchWords, 2);
//        solution.printResults(results2);
//
//        // Performance demonstration
//        demonstratePerformance();
//    }
//
//    /**
//     * Demonstrates the performance benefits of the Trie approach
//     */
//    private static void demonstratePerformance() {
//        System.out.println("\n=== PERFORMANCE ANALYSIS ===");
//        System.out.println("For N restaurants and Q queries:");
//        System.out.println("- Naive approach: O(Q * N * M) where M is average name length");
//        System.out.println("- Trie approach: O(N * M * log K) preprocessing + O(Q * (P + K log K)) queries");
//        System.out.println("- Where P is prefix length, K is result size");
//        System.out.println("- Trie is especially efficient for multiple queries on the same dataset");
//
//        // Example with timing (simplified)
//        long startTime = System.nanoTime();
//
//        String[] largeRestaurantList = new String[1000];
//        for (int i = 0; i < 1000; i++) {
//            largeRestaurantList[i] = "Restaurant" + String.format("%04d", i);
//        }
//
//        TrieSearchSuggestionsWithExplanation solution = new TrieSearchSuggestionsWithExplanation();
//        solution.searchSuggestions(largeRestaurantList, new String[]{"Rest", "Resta", "Restaurant1"}, 5);
//
//        long endTime = System.nanoTime();
//        System.out.printf("Processing 1000 restaurants with 3 queries took: %.2f ms%n",
//                         (endTime - startTime) / 1_000_000.0);
//        System.out.println("===============================");
//    }
//}