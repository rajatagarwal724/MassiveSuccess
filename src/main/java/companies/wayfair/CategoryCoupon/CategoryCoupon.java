package companies.wayfair.CategoryCoupon;

import java.util.HashMap;
import java.util.Map;

public class CategoryCoupon {



    public static void main(String[] args) {
        loadCategoryCoupons();
    }

    private static void loadCategoryCoupons() {
        // {categoryName, couponName}
        String [][] coupons = {
                {"Comforter Sets", "Comforters Sale"},
                {"Bedding", "Savings on Bedding"},
                {"Bed & Bath", "Low price for Bed & Bath"}
        };

        // {categoryName, categoryParentName}
        String [][] categories = {
                {"Comforter Sets", "Bedding"},
                {"Bedding", "Bed & Bath"},
                {"Bed & Bath", null},
                {"Soap Dispensers", "Bathroom Accessories"},
                {"Bathroom Accessories", "Bed & Bath"},
                {"Toy Organizers", "Baby And Kids"},
                {"Baby And Kids", null}
        };

        Map<String, String> categoryCouponMap = new HashMap<>();

        for (int i = 0; i < coupons.length; i++) {
            String[] categoryCouponMapping = coupons[i];
            categoryCouponMap.put(categoryCouponMapping[0], categoryCouponMapping[1]);
        }

        Map<String, String> categoryHierarchyMap = new HashMap<>();

        for (int i = 0; i < categories.length; i++) {
            String[] categoryParentMapping = categories[i];
            if (null != categoryParentMapping[1]) {
                categoryHierarchyMap.put(categoryParentMapping[0], categoryParentMapping[1]);
            }
        }

        for (Map.Entry<String, String> entry: categoryHierarchyMap.entrySet()) {
            String category = entry.getKey();
            String parentCategory = entry.getValue();

        }

        System.out.println(categoryCouponMap);
    }
}
