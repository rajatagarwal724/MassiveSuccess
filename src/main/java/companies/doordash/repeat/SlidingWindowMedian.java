package companies.doordash.repeat;

import java.util.Arrays;
import java.util.TreeMap;

public class SlidingWindowMedian {

    TreeMap<Integer, Integer> left, right;
    int leftSize, rightSize;


    public SlidingWindowMedian() {
        left = new TreeMap<>((n1, n2) -> n2 - n1);
        right = new TreeMap<>();
        leftSize = 0;
        rightSize = 0;
    }

    public double[] medianSlidingWindow(int[] nums, int k) {
        int n = nums.length;
        double[] res = new double[n - k + 1];
        int resIdx = 0;
        for (int i = 0; i < nums.length; i++) {
            addNumber(nums[i]);

            if (i >= k) {
                removeNum(nums[i - k]);
            }

            if (i >= k - 1) {
                res[resIdx++] = findMedian(k);
            }
        }

        return res;
    }

    private void removeNum(int num) {
        if (left.containsKey(num)) {
            left.put(num, left.get(num) - 1);
            if (left.get(num) == 0) {
                left.remove(num);
            }
            leftSize--;
        } else {
            right.put(num, right.get(num) - 1);
            if (right.get(num) == 0) {
                right.remove(num);
            }
            rightSize--;
        }
        balance();
    }

    private double findMedian(int k) {
        if (k % 2 == 0) {
            return (left.firstKey() + right.firstKey())/2.0;
        }
        return left.firstKey();
    }

    private void addNumber(int num) {
        if (leftSize == 0 || num <= left.firstKey()) {
            left.put(num, left.getOrDefault(num, 0) + 1);
            leftSize++;
        } else {
            right.put(num, right.getOrDefault(num, 0) + 1);
            rightSize++;
        }

        balance();
    }

    private void balance() {
        if (leftSize > (rightSize + 1)) {
            int elem = left.firstKey();
            left.put(elem, left.get(elem) - 1);
            if (left.get(elem) == 0) {
                left.remove(elem);
            }
            leftSize--;

            right.put(elem, right.getOrDefault(elem, 0) + 1);
            rightSize++;
        } else if (rightSize > leftSize) {
            int elem = right.firstKey();
            right.put(elem, right.get(elem) - 1);
            if (right.get(elem) == 0) {
                right.remove(elem);
            }
            rightSize--;

            left.put(elem, left.getOrDefault(elem, 0) + 1);
            leftSize++;
        }
    }

    public static void main(String[] args) {
        SlidingWindowMedian sol = new SlidingWindowMedian();
        System.out.println(Arrays.toString(sol.medianSlidingWindow(
                new int[] {2, 3, 4}, 3
        ))
        );
        SlidingWindowMedian sol2 = new SlidingWindowMedian();
        System.out.println(Arrays.toString(sol2.medianSlidingWindow(
                        new int[] {1,2,3,4}, 4
                ))
        );

        SlidingWindowMedian sol3 = new SlidingWindowMedian();
        System.out.println(Arrays.toString(sol3.medianSlidingWindow(
                        new int[] {1,3,-1,-3,5,3,6,7}, 3
                ))
        );
    }
}
