package companies.wayfair.CategoryCoupon;

import java.util.HashMap;
import java.util.Map;

public class CategoryCoupon {
    private static Map<String, String> categoryCouponMap = new HashMap<>();
    private static Map<String, String> categoryHierarchyMap = new HashMap<>();

    private static String findCoupon(String categoryName) {
        return categoryCouponMap.get(categoryName);
    }

    public static void main(String[] args) {
        loadCategoryCoupons();
        System.out.println(categoryCouponMap);
    }

    private static void loadCategoryCoupons() {
        // {categoryName, couponName}
        String[][] coupons = {
                {"Comforter Sets", "Comforters Sale"},
                {"Bedding", "Savings on Bedding"},
                {"Bed & Bath", "Low price for Bed & Bath"}
        };

        // {categoryName, categoryParentName}
        String[][] categories = {
                {"Comforter Sets", "Bedding"},
                {"Bedding", "Bed & Bath"},
                {"Bed & Bath", null},
                {"Soap Dispensers", "Bathroom Accessories"},
                {"Bathroom Accessories", "Bed & Bath"},
                {"Toy Organizers", "Baby And Kids"},
                {"Baby And Kids", null}
        };

        for (int i = 0; i < coupons.length; i++) {
            String[] categoryCouponMapping = coupons[i];
            categoryCouponMap.put(categoryCouponMapping[0], categoryCouponMapping[1]);
        }

        for (int i = 0; i < categories.length; i++) {
            String[] categoryParentMapping = categories[i];
            if (null != categoryParentMapping[1]) {
                categoryHierarchyMap.put(categoryParentMapping[0], categoryParentMapping[1]);
            }
        }

        for (Map.Entry<String, String> entry : categoryHierarchyMap.entrySet()) {
            String category = entry.getKey();
            if (!categoryCouponMap.containsKey(category)) {
                String parent = entry.getValue();
                while (null != parent && !categoryCouponMap.containsKey(parent)) {
                    parent = categoryHierarchyMap.get(parent);
                }
                if (null != parent && categoryCouponMap.containsKey(parent)) {
                    categoryCouponMap.put(category, categoryCouponMap.get(parent));
                }
            }
        }
    }
}
