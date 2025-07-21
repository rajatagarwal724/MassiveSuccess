package companies.doordash.repeat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Search Suggestions System
 * 
 * Time Complexity Analysis:
 * 1. Trie Construction:
 *    - Inserting N products of average length L: O(N * L * log K)
 *    - For each character in each word: O(L)
 *    - Priority queue operations at each node: O(log K)
 * 
 * 2. Search Operation (current implementation):
 *    - For M prefixes of the search word (M = length of search word):
 *    - Each search starts from root, creating quadratic behavior: O(M²)
 *    - Plus sorting the suggestions for each prefix: O(M * K log K)
 *    - Total search complexity: O(M² + M * K log K)
 * 
 * 3. Overall Time Complexity:
 *    - Current implementation: O(N * L * log K + M² + M * K log K)
 *    - Since K is fixed at 3, simplifies to: O(N * L + M²)
 * 
 * 4. Potential Optimization:
 *    - By tracking the current node during searches, we can reduce to: O(N * L + M * K log K)
 *    - Which further simplifies with fixed K=3 to: O(N * L + M)
 * 
 * Space Complexity:
 * 1. Trie Structure:
 *    - Worst case with unique prefixes: O(N * L)
 *    - Each node stores up to K suggestions: O(K) per node
 *    - Total: O(N * L * K)
 * 
 * 2. Result Storage:
 *    - For M prefixes, storing up to K suggestions each: O(M * K)
 * 
 * 3. Overall Space Complexity: O(N * L * K + M * K)
 *    - With K=3 constant: O(N * L + M)
 */
public class SearchSuggestions {

    class TrieNode {
        Character prefix;
        Map<Character, TrieNode> children;
        PriorityQueue<String> suggestedWords;

        public TrieNode(Character prefix) {
            this.prefix = prefix;
            this.children = new HashMap<>();
            this.suggestedWords = new PriorityQueue<>(Comparator.reverseOrder());
        }
    }

    class Trie {
        TrieNode root;
        int K;

        public Trie(int k) {
            this.root = new TrieNode('/');
            K = k;
        }

        public void insert(String word) {
            if (null == word || word.isEmpty()) {
                return;
            }
            TrieNode curr = root;
            for (char ch: word.toCharArray()) {
                if (!curr.children.containsKey(ch)) {
                    var node = new TrieNode(ch);
                    curr.children.put(ch, node);
                }
                curr = curr.children.get(ch);

                curr.suggestedWords.offer(word);

                if (curr.suggestedWords.size() > K) {
                    curr.suggestedWords.poll();
                }
            }
        }

        public List<String> search(String word) {
            if (null == word || word.isEmpty()) {
                return new ArrayList<>();
            }
            TrieNode curr = root;

            for (char ch: word.toCharArray()) {
                var elem = ch;

                if (!curr.children.containsKey(elem)) {
                    return new ArrayList<>();
                }
                curr = curr.children.get(elem);
            }

            List<String> result = new ArrayList<>(curr.suggestedWords);
            Collections.sort(result);
            return result;
        }
    }



    public static void main(String[] args) {
        String[] restaurantList = {
                "Panda Express", "Panera Bread", "Papa John's",
                "Pizza Hut", "Pieology", "Panini Place"
        };

        String[] searchWords = {"Pan", "Pie", "Pa"};

        int K = 2;

        var sol = new SearchSuggestions();
        Map<String, List<String>> output = sol.searchSuggestions(restaurantList, searchWords, K);

        for (Map.Entry<String, List<String>> entry : output.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        List<List<String>> result = sol.suggestedProducts(new String[]{"mobile","mouse","moneypot","monitor","mousepad"}, "mouse");
        result.forEach(System.out::println);
    }

    /**
     * Returns a list of suggested product lists for each prefix of the search word.
     * 
     * Current implementation has O(M²) search complexity due to starting each search from root.
     * Can be optimized to O(M) by tracking the current node between prefix searches.
     * 
     * @param products Array of product strings to build suggestions from
     * @param searchWord The search query to get suggestions for
     * @return List of suggestion lists for each prefix of searchWord
     */
    public List<List<String>> suggestedProducts(String[] products, String searchWord) {
        Trie trie = new Trie(3);
        Arrays.stream(products).forEach(trie::insert);
        List<List<String>> result = new ArrayList<>();

        for (int i = 0; i < searchWord.length(); i++) {
            String prefix = searchWord.substring(0, i + 1);
            result.add(trie.search(prefix));
        }

        return result;
    }

    /**
     * Optimized version that would reduce search complexity from O(M²) to O(M):
     * 
     * public List<List<String>> suggestedProductsOptimized(String[] products, String searchWord) {
     *     Trie trie = new Trie(3);
     *     Arrays.stream(products).forEach(trie::insert);
     *     List<List<String>> result = new ArrayList<>();
     *     
     *     // Start from the root node
     *     TrieNode current = trie.root;
     *     
     *     // Iterate through each character of the search word
     *     for (int i = 0; i < searchWord.length(); i++) {
     *         char c = searchWord.charAt(i);
     *         
     *         // Check if the current character exists in children
     *         if (current.children.containsKey(c)) {
     *             current = current.children.get(c);
     *             List<String> suggestions = new ArrayList<>(current.suggestedWords);
     *             Collections.sort(suggestions);
     *             result.add(suggestions);
     *         } else {
     *             // If character not found, add empty lists for remaining prefixes
     *             current = null;
     *             result.add(new ArrayList<>());
     *         }
     *         
     *         // If current becomes null, we won't find any more matches
     *         if (current == null) {
     *             for (int j = i + 1; j < searchWord.length(); j++) {
     *                 result.add(new ArrayList<>());
     *             }
     *             break;
     *         }
     *     }
     *     
     *     return result;
     * }
     */

    private Map<String, List<String>> searchSuggestions(String[] restaurantList, String[] searchWords, int k) {
        Trie trie = new Trie(k);
        Arrays.stream(restaurantList).forEach(trie::insert);

        Map<String, List<String>> result = new HashMap<>();
        for (String searchWord: searchWords) {
            result.put(searchWord, trie.search(searchWord));
        }

        return result;
    }
}
