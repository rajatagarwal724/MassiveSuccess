package companies.roku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteAndEarn {

    public int deleteAndEarn(int[] nums) {
        Map<Integer, Integer> points = new HashMap<>();
        int maxNum = 0;
        for (int num: nums) {
            points.put(num, points.getOrDefault(num, 0) + num);
            maxNum = Math.max(maxNum, num);
        }

        int[] dp = new int[maxNum + 1];
        dp[1] = points.getOrDefault(1, 0);

        for (int i = 2; i <= maxNum; i++) {
            int gain = points.getOrDefault(i, 0);
            int picked = dp[i - 2] + gain;
            int notPicked = dp[i - 1];

            dp[i] = Math.max(picked, notPicked);
        }

        return dp[dp.length - 1];
    }


    public static void main(String[] args) {
        var sol = new DeleteAndEarn();
        System.out.println(sol.deleteAndEarn(new int[] {3,4,2}));
        System.out.println(sol.deleteAndEarn(new int[] {2,2,3,3,3,4}));
        System.out.println(sol.deleteAndEarn(new int[] {1,1,1,2,4,5,5,5,6}));
    }
}
