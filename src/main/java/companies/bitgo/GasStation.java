package companies.bitgo;

public class GasStation {

    public int canCompleteCircuit(int[] gas, int[] cost) {
        int cur_gain = 0, total_gain = 0, answer = 0;

        for (int i = 0; i < gas.length; i++) {
            int gain = gas[i] - cost[i];

            total_gain += gain;
            cur_gain += gain;

            if (cur_gain < 0) {
                cur_gain = 0;
                answer = i + 1;
            }
        }

        return total_gain >= 0 ? answer : -1;
    }
}
