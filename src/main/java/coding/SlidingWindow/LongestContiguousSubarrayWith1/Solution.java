package coding.SlidingWindow.LongestContiguousSubarrayWith1;

import java.util.HashMap;
import java.util.Map;

public class Solution {

    public int findLength(int[] arr, int k) {
        int maxLength = 0, windowStart = 0, maxCountOf1s = 0;
        for (int windowEnd = 0; windowEnd < arr.length; windowEnd++) {
            int elemAtRight = arr[windowEnd];
            if (elemAtRight == 1) {
                maxCountOf1s++;
            }
            if ((windowEnd - windowStart + 1 - maxCountOf1s) > k) {
                if (arr[windowStart] == 1) {
                    maxCountOf1s--;
                }
                windowStart++;
            }
            maxLength = Math.max(maxLength, windowEnd - windowStart + 1);
        }
        return maxLength;
    }

    public static void main(String[] args) {
        var sol = new Solution();
        System.out.println(sol.findLength(new int[] {0, 1, 1, 0, 0, 0, 1, 1, 0, 1, 1}, 2));
        System.out.println(sol.findLength(new int[] {0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1}, 3));
        System.out.println(sol.findLength(new int[] {1, 0, 0, 1, 1, 0, 1, 1}, 2));
    }
}
