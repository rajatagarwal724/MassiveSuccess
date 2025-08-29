package companies.roku;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Ticket {

    public int mincostTickets(int[] days, int[] costs) {
        Set<Integer> isTravelAllowed = Arrays.stream(days).boxed().collect(Collectors.toSet());
        int lastDay = days[days.length - 1];
        int[] dp = new int[lastDay + 1];
        Arrays.fill(dp, -1);

        return solve(isTravelAllowed, days, costs, dp, 1);
    }

    private int solve(Set<Integer> isTravelAllowed, int[] days, int[] costs, int[] dp, int curDay) {
        if (curDay > days[days.length - 1]) {
            return 0;
        }

        if (!isTravelAllowed.contains(curDay)) {
            return solve(isTravelAllowed, days, costs, dp, curDay + 1);
        }

        if (dp[curDay] != -1) {
            return dp[curDay];
        }

        int one = costs[0] + solve(isTravelAllowed, days, costs, dp, curDay + 1);
        int seven = costs[1] + solve(isTravelAllowed, days, costs, dp, curDay + 7);
        int thirty = costs[2] + solve(isTravelAllowed, days, costs, dp, curDay + 30);

        dp[curDay] = Math.min(one, Math.min(seven, thirty));
        return dp[curDay];
    }


    public static void main(String[] args) {
        var sol = new Ticket();
        System.out.println(sol.mincostTickets(new int[] {1,4,6,7,8,20}, new int[] {2,7,15}));
        System.out.println(sol.mincostTickets(new int[] {1,2,3,4,5,6,7,8,9,10,30,31}, new int[] {2,7,15}));
    }
}