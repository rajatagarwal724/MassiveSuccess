package companies.doordash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuggestedSearch {
    // Equivalent code for lower_bound in Java

    public List<List<String>> suggestedProducts(String[] products, String searchWord) {
        // Sort the products array for binary search to work correctly
        Arrays.sort(products);
        
        int start = 0, binary_search_start = 0;
        String prefix = "";
        List<List<String>> result = new ArrayList<>();

        for (char ch: searchWord.toCharArray()) {
            prefix += ch;

            start = lower_bound(products, binary_search_start, prefix);
            result.add(new ArrayList<>());
            for (int i = start; i < Math.min(start + 3, products.length); i++) {
                if (products[i].length() < prefix.length() || !products[i].startsWith(prefix)) {
                    break;
                }
                result.get(result.size() - 1).add(products[i]);
            }

            binary_search_start = start;
        }

        return result;
    }

    private int lower_bound(String[] products, int low, String word) {
        int left = low, right = products.length - 1;
        while (left < right) {
            int mid = left + (right - left)/2;

            if (products[mid].compareTo(word) >= 0) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    public static void main(String[] args) {
        var sol = new SuggestedSearch();
        sol.suggestedProducts(
                new String[]{"mobile","mouse","moneypot","monitor","mousepad"},
                "mouse"
        )
                .forEach(System.out::println);


        String[] restaurantList = {
                "Panda Express", "Panera Bread", "Papa John's",
                "Pizza Hut", "Pieology", "Panini Place"
        };

        String[] searchWords = {"Pan", "Pie", "Pa"};

        for (int i = 0; i < searchWords.length; i++) {
            System.out.println(sol.suggestedProducts(restaurantList, searchWords[i]));
        }
    }
}
