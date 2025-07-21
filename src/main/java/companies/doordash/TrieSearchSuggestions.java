package companies.doordash;

import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children;
    PriorityQueue<String> wordsHeap; // min-heap to store top K words
    
    public TrieNode() {
        this.children = new HashMap<>();
        this.wordsHeap = new PriorityQueue<>(); // Natural ordering for strings (lexicographic)
    }
}

class Trie {
    private TrieNode root;
    private int K;
    
    public Trie(int K) {
        this.root = new TrieNode();
        this.K = K;
    }
    
    public void insert(String word) {
        TrieNode node = root;
        for (char ch : word.toCharArray()) {
            if (!node.children.containsKey(ch)) {
                node.children.put(ch, new TrieNode());
            }
            node = node.children.get(ch);
            
            // Push word into min-heap
            node.wordsHeap.offer(word);
            // If more than K words, pop smallest lex word
            if (node.wordsHeap.size() > K) {
                node.wordsHeap.poll();
            }
        }
    }
    
    public List<String> searchPrefix(String prefix) {
        TrieNode node = root;
        for (char ch : prefix.toCharArray()) {
            if (!node.children.containsKey(ch)) {
                return new ArrayList<>();
            }
            node = node.children.get(ch);
        }
        
        // Return sorted list of top K matches for this prefix
        List<String> result = new ArrayList<>(node.wordsHeap);
        Collections.sort(result);
        return result;
    }
}

public class TrieSearchSuggestions {
    public Map<String, List<String>> searchSuggestions(String[] restaurants, String[] searchWords, int K) {
        // Build the Trie
        Trie trie = new Trie(K);
        
        // Insert in lex sorted order to help keep heap consistent
        String[] sortedRestaurants = restaurants.clone();
        Arrays.sort(sortedRestaurants);
        
        for (String word : sortedRestaurants) {
            trie.insert(word);
        }
        
        // Query each search word
        Map<String, List<String>> result = new HashMap<>();
        for (String word : searchWords) {
            List<String> matches = trie.searchPrefix(word);
            result.put(word, matches);
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        String[] restaurantList = {
            "Panda Express", "Panera Bread", "Papa John's", 
            "Pizza Hut", "Pieology", "Panini Place"
        };
        
        String[] searchWords = {"Pan", "Pie", "Pa"};
        
        int K = 2;
        
        TrieSearchSuggestions sol = new TrieSearchSuggestions();
        Map<String, List<String>> output = sol.searchSuggestions(restaurantList, searchWords, K);
        
        for (Map.Entry<String, List<String>> entry : output.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
} 