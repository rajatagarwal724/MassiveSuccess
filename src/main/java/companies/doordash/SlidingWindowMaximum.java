package companies.doordash;

import java.util.ArrayDeque;
import java.util.Arrays;

public class SlidingWindowMaximum {

    public int[] maxSlidingWindow(int[] nums, int k) {
        int n = nums.length;
        int[] res = new int[n - k + 1];
        int resIdx = 0;
        ArrayDeque<Integer> deque = new ArrayDeque<>();

        for (int i = 0; i < k; i++) {
            while (!deque.isEmpty() && nums[i] >= nums[deque.peekLast()]) {
                deque.pollLast();
            }

            deque.offerLast(i);
        }

        res[resIdx++] = nums[deque.peekFirst()];

        for (int i = k; i < n; i++) {
            if ((i - k) == deque.peekFirst()) {
                deque.pollFirst();
            }

            while (!deque.isEmpty() && nums[i] >= nums[deque.peekLast()]) {
                deque.pollLast();
            }

            deque.offerLast(i);
            res[resIdx++] = nums[deque.peekFirst()];
        }

        return res;
    }

    public static void main(String[] args) {
        var sol = new SlidingWindowMaximum();
        System.out.println(
                Arrays.toString(
                        sol.maxSlidingWindow(new int[] {1,3,-1,-3,5,3,6,7}, 3)
                )
        );

        System.out.println(
                Arrays.toString(
                        sol.maxSlidingWindow(new int[] {1}, 1)
                )
        );
    }
}
