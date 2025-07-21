package coding.linkedin;

import java.util.*;

public class KthSmallestProductDebug2 {
    
    public static void main(String[] args) {
        // Test Case 3
        System.out.println("===== TEST CASE 3 =====");
        int[] nums1 = {-6, -4, -3, 0, 1, 3, 4, 7};
        int[] nums2 = {-5, 2, 3, 4};
        long k = 40;
        
        System.out.println("nums1: " + Arrays.toString(nums1));
        System.out.println("nums2: " + Arrays.toString(nums2));
        System.out.println("k = " + k);
        System.out.println();
        
        // Generate all products
        List<Long> products = new ArrayList<>();
        for (int i = 0; i < nums1.length; i++) {
            for (int j = 0; j < nums2.length; j++) {
                long product = (long) nums1[i] * nums2[j];
                products.add(product);
                System.out.println("nums1[" + i + "] * nums2[" + j + "] = " + nums1[i] + " * " + nums2[j] + " = " + product);
            }
        }
        
        System.out.println("\nAll products: " + products);
        
        // Sort products
        Collections.sort(products);
        System.out.println("Sorted products: " + products);
        
        System.out.println("\nThe " + k + "th smallest product is: " + products.get((int)k - 1));
        
        // Show first few smallest and largest
        System.out.println("\nFirst 5 smallest products:");
        for (int i = 0; i < Math.min(5, products.size()); i++) {
            System.out.println((i + 1) + "th smallest: " + products.get(i));
        }
        
        System.out.println("\nLast 5 largest products:");
        for (int i = Math.max(0, products.size() - 5); i < products.size(); i++) {
            System.out.println((i + 1) + "th largest: " + products.get(i));
        }
        
        // Test Case 4
        System.out.println("\n\n===== TEST CASE 4 =====");
        nums1 = new int[]{-2, -1, 0, 1, 2};
        nums2 = new int[]{-3, -1, 2, 4, 5};
        k = 3;
        
        System.out.println("nums1: " + Arrays.toString(nums1));
        System.out.println("nums2: " + Arrays.toString(nums2));
        System.out.println("k = " + k);
        System.out.println();
        
        // Generate all products
        products.clear();
        for (int i = 0; i < nums1.length; i++) {
            for (int j = 0; j < nums2.length; j++) {
                long product = (long) nums1[i] * nums2[j];
                products.add(product);
                System.out.println("nums1[" + i + "] * nums2[" + j + "] = " + nums1[i] + " * " + nums2[j] + " = " + product);
            }
        }
        
        System.out.println("\nAll products: " + products);
        
        // Sort products
        Collections.sort(products);
        System.out.println("Sorted products: " + products);
        
        System.out.println("\nThe " + k + "rd smallest product is: " + products.get((int)k - 1));
        
        // Show first few smallest and largest
        System.out.println("\nFirst 5 smallest products:");
        for (int i = 0; i < Math.min(5, products.size()); i++) {
            System.out.println((i + 1) + "th smallest: " + products.get(i));
        }
        
        System.out.println("\nLast 5 largest products:");
        for (int i = Math.max(0, products.size() - 5); i < products.size(); i++) {
            System.out.println((i + 1) + "th largest: " + products.get(i));
        }
    }
} 