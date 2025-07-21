package coding.linkedin;

import java.util.*;

public class KthSmallestProductDebug {
    
    public static void main(String[] args) {
        // Test Case 3 - Debug
        int[] nums1 = {-2, -1, 0, 1, 2};
        int[] nums2 = {-3, -1, 2, 4, 5};
        long k = 3;
        
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
        
        System.out.println("\nThe " + k + "rd smallest product is: " + products.get((int)k - 1));
        
        // Show first few smallest
        System.out.println("\nFirst 5 smallest products:");
        for (int i = 0; i < Math.min(5, products.size()); i++) {
            System.out.println((i + 1) + "th smallest: " + products.get(i));
        }
    }
} 