import java.util.*;

public class ProductRatingTracker {
    // Map to store product_id to its likes count
    private Map<String, Integer> productLikes;
    // Map to store product_id to its dislikes count
    private Map<String, Integer> productDislikes;
    // TreeMap to maintain products sorted by likes (value -> list of products with that value)
    private TreeMap<Integer, Set<String>> likesSortedMap;
    // TreeMap to maintain products sorted by dislikes (value -> list of products with that value)
    private TreeMap<Integer, Set<String>> dislikesSortedMap;

    public ProductRatingTracker() {
        productLikes = new HashMap<>();
        productDislikes = new HashMap<>();
        likesSortedMap = new TreeMap<>(Collections.reverseOrder()); // Sort in descending order
        dislikesSortedMap = new TreeMap<>(Collections.reverseOrder()); // Sort in descending order
    }

    public void addLike(String productId) {
        // Remove from old likes count if exists
        int oldLikes = productLikes.getOrDefault(productId, 0);
        if (oldLikes > 0) {
            Set<String> products = likesSortedMap.get(oldLikes);
            products.remove(productId);
            if (products.isEmpty()) {
                likesSortedMap.remove(oldLikes);
            }
        }

        // Add new like
        int newLikes = oldLikes + 1;
        productLikes.put(productId, newLikes);
        likesSortedMap.computeIfAbsent(newLikes, k -> new HashSet<>()).add(productId);
    }

    public void addDislike(String productId) {
        // Remove from old dislikes count if exists
        int oldDislikes = productDislikes.getOrDefault(productId, 0);
        if (oldDislikes > 0) {
            Set<String> products = dislikesSortedMap.get(oldDislikes);
            products.remove(productId);
            if (products.isEmpty()) {
                dislikesSortedMap.remove(oldDislikes);
            }
        }

        // Add new dislike
        int newDislikes = oldDislikes + 1;
        productDislikes.put(productId, newDislikes);
        dislikesSortedMap.computeIfAbsent(newDislikes, k -> new HashSet<>()).add(productId);
    }

    public String getMostLikedProduct() {
        if (likesSortedMap.isEmpty()) {
            return null;
        }
        // Get first entry (highest likes) and return any product from its set
        return likesSortedMap.firstEntry().getValue().iterator().next();
    }

    public String getMostDislikedProduct() {
        if (dislikesSortedMap.isEmpty()) {
            return null;
        }
        // Get first entry (highest dislikes) and return any product from its set
        return dislikesSortedMap.firstEntry().getValue().iterator().next();
    }

    public int getLikes(String productId) {
        return productLikes.getOrDefault(productId, 0);
    }

    public int getDislikes(String productId) {
        return productDislikes.getOrDefault(productId, 0);
    }
} 