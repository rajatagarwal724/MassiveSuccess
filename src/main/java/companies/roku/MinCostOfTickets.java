package companies.roku;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MinCostOfTickets {
    public int mincostTickets(int[] days, int[] costs) {
        Set<Integer> isTravelNeed = Arrays.stream(days).boxed().collect(Collectors.toSet());
        int lastDay = days[days.length - 1];
        int[] dp = new int[lastDay + 1];
        Arrays.fill(dp, -1);

        return mincostTickets(days, costs, dp, 1, isTravelNeed);
    }

    private int mincostTickets(int[] days, int[] costs, int[] dp, int currDay, Set<Integer> isTravelNeed) {
        if (currDay > days[days.length - 1]) {
            return 0;
        }
        if (!isTravelNeed.contains(currDay)) {
            return mincostTickets(days, costs, dp, currDay + 1, isTravelNeed);
        }

        if (dp[currDay] != -1) {
            return dp[currDay];
        }

        int oneDay = costs[0] + mincostTickets(days, costs, dp, currDay + 1, isTravelNeed);
        int sevenDay = costs[1] + mincostTickets(days, costs, dp, currDay + 7, isTravelNeed);
        int thirtyDay = costs[2] + mincostTickets(days, costs, dp, currDay + 30, isTravelNeed);

        dp[currDay] = Math.min(oneDay, Math.min(sevenDay, thirtyDay));
        return dp[currDay];
    }

    public int mincostTicketsBottomUp(int[] days, int[] costs) {
        int lastDay = days[days.length - 1];
        int[] dp = new int[lastDay + 1];
        Arrays.fill(dp, 0);
        int i = 0;
        for (int day = 1; day <= lastDay; day++) {
            if (day < days[i]) {
                dp[day] = dp[day - 1];
            } else {
                i++;
                dp[day] = Math.min(
                        dp[day - 1] + costs[0],
                        Math.min(dp[Math.max(0, day - 7)] + costs[1], costs[2] + dp[Math.max(0, day - 30)])
                );
            }
        }

        return dp[lastDay];
    }

    public static void main(String[] args) {
        var sol = new MinCostOfTickets();
        System.out.println(sol.mincostTickets(new int[] {1,4,6,7,8,20}, new int[] {2, 7, 15}));
        System.out.println(sol.mincostTickets(new int[] {1,2,3,4,5,6,7,8,9,10,30,31}, new int[] {2, 7, 15}));

        System.out.println(sol.mincostTicketsBottomUp(new int[] {1,4,6,7,8,20}, new int[] {2, 7, 15}));
        System.out.println(sol.mincostTicketsBottomUp(new int[] {1,2,3,4,5,6,7,8,9,10,30,31}, new int[] {2, 7, 15}));
    }
}
