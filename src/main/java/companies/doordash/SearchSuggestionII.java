package companies.doordash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class SearchSuggestionII {

    class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        PriorityQueue<String> wordsHeap = new PriorityQueue<>();

    }

    class Trie {
        TrieNode root;
        int K;

        public Trie(int k) {
            K = k;
            root = new TrieNode();
        }

        public void insert(String word) {
            TrieNode node = root;
            for (char c : word.toCharArray()) {
                if (!node.children.containsKey(c)) {
                    node.children.put(c, new TrieNode());
                }
                node = node.children.get(c);

                node.wordsHeap.offer(word);
                if (node.wordsHeap.size() > K) {
                    node.wordsHeap.poll();
                }
            }
        }

        public List<String> search(String prefix) {
            TrieNode node = root;
            for (char c : prefix.toCharArray()) {
                if (!node.children.containsKey(c)) {
                    return new ArrayList<>();
                }
                node = node.children.get(c);
            }
            List<String> result = new ArrayList<>(node.wordsHeap);
            Collections.sort(result);
            return result;
        }
    }


    public void suggestedProducts(String[] products, String searchWord) {
        String[] productsClone = products.clone();
        Arrays.sort(productsClone);

        Trie trie = new Trie(2);

        for (String product : productsClone) {
            trie.insert(product);
        }

        System.out.println(trie.search(searchWord));
    }


    public static void main(String[] args) {
        var sol = new SearchSuggestionII();

        String[] restaurantList = {
                "Panda Express", "Panera Bread", "Papa John's",
                "Pizza Hut", "Pieology", "Panini Place"
        };

        String[] searchWords = {"Pan", "Pie", "Pa"};

        Arrays.stream(searchWords).forEach(word -> {
            sol.suggestedProducts(
                    restaurantList, word
            );
        });

    }
}
