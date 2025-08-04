package companies.roku;

import java.util.ArrayDeque;
import java.util.Arrays;

public class SlidingWindowMaximum {

    public int[] maxSlidingWindow(int[] nums, int k) {
        int[] res = new int[nums.length - k + 1];
        int resIdx = 0;
        ArrayDeque<Integer> deque = new ArrayDeque<>();

        for (int i = 0; i < k; i++) {
            var elem = nums[i];
            while (!deque.isEmpty() && elem >= nums[deque.peekLast()]) {
                deque.pollLast();
            }
            deque.offerLast(i);
        }
        res[resIdx++] = nums[deque.peekFirst()];

        for (int i = k; i < nums.length; i++) {
            if (deque.peekFirst() == i - k) {
                deque.pollFirst();
            }

            var elem = nums[i];
            while (!deque.isEmpty() && elem >= nums[deque.peekLast()]) {
                deque.pollLast();
            }
            deque.offerLast(i);
            res[resIdx++] = nums[deque.peekFirst()];
        }
        return res;
    }

    public static void main(String[] args) {
        var sol = new SlidingWindowMaximum();
        System.out.println(Arrays.toString(sol.maxSlidingWindow(new int[] {1,3,-1,-3,5,3,6,7}, 3)));
        System.out.println(Arrays.toString(sol.maxSlidingWindow(new int[] {1}, 1)));
    }
}
