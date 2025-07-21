package coding.linkedin;

public class SmallestProduct {

    public long kthSmallestProduct(int[] nums1, int[] nums2, long k) {
//        long left = (long) -1e10 - 1;
//        long right = (long) 1e10 + 1;
        long left = -10_000_000_000L, right = 10_000_000_000L;
        while (left < right) {
            long mid = left + (right - left) / 2;
            if (countLessOrEqual(nums1, nums2, mid) < k) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }

    private long countLessOrEqual(int[] nums1, int[] nums2, long target) {
        long count = 0;
        for (int num1 : nums1) {
            if (num1 == 0) {
                if (target >= 0) {
                    count += nums2.length;
                }
            } else if (num1 > 0) {
                // Find the largest nums2[j] <= target / num1
                long maxVal = target / num1;
//                if (target % num1 != 0 || target < 0 && num1 > 0) {
//                    // For example, target = 7, num1=3: 7/3=2.333... so max nums2[j] is 2
//                    // So no adjustment needed for floor division in Java
//                }
                int idx = upperBound(nums2, (long) Math.floor((double) target / num1));
                count += idx;
            } else {
                // num1 < 0: find nums2[j] >= target / num1 (since division by negative flips inequality)
                long minVal = (long) Math.ceil((double) target / num1);
                int idx = lowerBound(nums2, minVal);
                count += nums2.length - idx;
            }
        }
        return count;
    }

    // Finds the first index in nums where nums[i] > val
    private int upperBound(int[] nums, long val) {
        int left = 0;
        int right = nums.length;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] <= val) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }

    // Finds the first index in nums where nums[i] >= val
    private int lowerBound(int[] nums, long val) {
        int left = 0;
        int right = nums.length;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] < val) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }

    public static void main(String[] args) {
        var sol = new SmallestProduct();
        System.out.println(
                sol.kthSmallestProduct(
                new int[] {-4,-2,0,3}, new int[] {2,4}, 6
        ));
    }
}
