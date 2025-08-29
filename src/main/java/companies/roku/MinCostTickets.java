package companies.roku;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class MinCostTickets {

    public int mincostTickets(int[] days, int[] costs) {
        int lastDay = days[days.length - 1];
        Set<Integer> isTravelAllowed = Arrays.stream(days).boxed().collect(Collectors.toSet());
        int[] travelDp = new int[lastDay + 1];
        return dp(1, lastDay, days, costs, isTravelAllowed, travelDp);
    }

    private int dp(int day, int lastDay, int[] days, int[] costs, Set<Integer> isTravelAllowed, int[] travelDp) {
        if (day > lastDay) {
            return 0;
        }

        if (!isTravelAllowed.contains(day)) {
            return dp(day + 1, lastDay, days, costs, isTravelAllowed, travelDp);
        }

        if (travelDp[day] != 0) {
            return travelDp[day];
        }

        int one = costs[0] + dp(day +1, lastDay, days, costs, isTravelAllowed, travelDp);
        int seven = costs[1] + dp(day + 7, lastDay, days, costs, isTravelAllowed, travelDp);
        int thirty = costs[2] + dp(day + 30, lastDay, days, costs, isTravelAllowed, travelDp);

        travelDp[day] = Math.min(one, Math.min(seven, thirty));
        return travelDp[day];
    }

    public int mincostTicketsOpt(int[] days, int[] costs) {
        int lastDay = days[days.length - 1];
        int[] dp = new int[lastDay + 1];
        int idx = 0;
        for (int day = 1; day <= lastDay; day++) {
            if (day < days[idx]) {
                dp[day] = dp[day - 1];
            } else {
                idx++;
                dp[day] = Math.min(
                        costs[0] + dp[day - 1],
                        Math.min((costs[1] + dp[Math.max(0, day - 7)]),
                                costs[2] + dp[Math.max(0, day - 30)]
                        )
                );
            }

        }
        return dp[lastDay];
    }

    public static void main(String[] args) {
        var sol = new MinCostTickets();
        System.out.println(
                sol.mincostTickets(new int[] {1,4,6,7,8,20}, new int[] {2,7,15})
        );

        System.out.println(
                sol.mincostTickets(new int[] {1,2,3,4,5,6,7,8,9,10,30,31}, new int[] {2,7,15})
        );

        System.out.println(
                sol.mincostTicketsOpt(new int[] {1,4,6,7,8,20}, new int[] {2,7,15})
        );

        System.out.println(
                sol.mincostTicketsOpt(new int[] {1,2,3,4,5,6,7,8,9,10,30,31}, new int[] {2,7,15})
        );
    }
}
