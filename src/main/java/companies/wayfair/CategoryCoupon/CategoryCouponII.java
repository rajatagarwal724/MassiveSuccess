package companies.wayfair.CategoryCoupon;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;
import java.util.TreeSet;

public class CategoryCouponII {

    private static Map<String, TreeSet<Coupon>> categoryCouponMap = new HashMap<>();
    private static Map<String, String> categoryHierarchyMap = new HashMap<>();
    static class Coupon implements Comparable<Coupon> {
        String couponName;
        LocalDate date;

        public Coupon(String couponName, LocalDate date) {
            this.couponName = couponName;
            this.date = date;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Coupon coupon)) return false;

            if (!Objects.equals(couponName, coupon.couponName)) return false;
            return Objects.equals(date, coupon.date);
        }

        @Override
        public int hashCode() {
            int result = couponName != null ? couponName.hashCode() : 0;
            result = 31 * result + (date != null ? date.hashCode() : 0);
            return result;
        }

        @Override
        public int compareTo(Coupon o) {
            if (!this.date.isEqual(o.date)) {
                return o.date.compareTo(this.date);
            }
            return this.couponName.compareTo(o.couponName);
        }
    }

    private static String findCoupon(String categoryName) {
        TreeSet<Coupon> coupons = categoryCouponMap.getOrDefault(categoryName, new TreeSet<>());
        return coupons.stream().filter(coupon -> coupon.date.isBefore(LocalDate.now())).findFirst().map(coupon -> coupon.couponName).orElse(null);
    }

    public static void main(String[] args) {
        loadCategoryCoupons();
        System.out.println(findCoupon("Bed & Bath"));
        System.out.println(findCoupon("Bedding"));
        System.out.println(findCoupon("Bathroom Accessories"));
        System.out.println(findCoupon("Comforter Sets"));
    }

    private static void loadCategoryCoupons() {
        String[][] categoryCoupons = new String[][] {
                { "CategoryName:Comforter Sets", "CouponName:Comforters Sale", "DateModified:2020-01-01"},
                { "CategoryName:Comforter Sets", "CouponName:Cozy Comforter Coupon", "DateModified:2021-01-01" },
                { "CategoryName:Bedding", "CouponName:Best Bedding Bargains", "DateModified:2019-01-01" },
        { "CategoryName:Bedding", "CouponName:Savings on Bedding", "DateModified:2019-01-01" },
        { "CategoryName:Bed & Bath", "CouponName:Low price for Bed & Bath", "DateModified:2018-01-01" },
        { "CategoryName:Bed & Bath", "CouponName:Bed & Bath extravaganza", "DateModified:2019-01-01" },
        { "CategoryName:Bed & Bath", "CouponName:Big Savings for Bed & Bath", "DateModified:2030-01-01" }
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

        for (String[] categoryCoupon: categoryCoupons) {
            String category = categoryCoupon[0].split(":")[1];
            String coupon = categoryCoupon[1].split(":")[1];
            LocalDate date = LocalDate.parse(categoryCoupon[2].split(":")[1]);
            categoryCouponMap.computeIfAbsent(category, s -> new TreeSet<Coupon>()).add(new Coupon(coupon, date));
        }

        for (String[] categoryHierarchy: categories) {
            if (categoryHierarchy[1] != null) {
                categoryHierarchyMap.put(categoryHierarchy[0], categoryHierarchy[1]);
            }
        }

        for (Map.Entry<String, String> categoryHierarchyEntry: categoryHierarchyMap.entrySet()) {
            String category = categoryHierarchyEntry.getKey();
            if (!categoryCouponMap.containsKey(category)) {
                String parent = categoryHierarchyEntry.getValue();
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
