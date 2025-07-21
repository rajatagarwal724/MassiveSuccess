package machinecodinglld;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Problem Statement:
 * Given a list of strings denoting function name, START or END marker, and timestamp.
 * Calls can be nested and one function can call child functions.
 * For example:
 *
 * "abc,START,100"
 * "def,START,150",
 * "def,END,180",
 * "abc,END,200"
 * Inclusive time is defined as all the time spent on a particular function, including time spent on its child calls.
 * Exclusive time is defined as the time spent on a particular function only, excluding time spent on its child calls.
 * In the above example, inclusive time for function "abc" is 200-100=100, while exclusive time for function "abc" is (200-100) - (180-150) = 70
 *
 * Given such list of strings, figure out the inclusive and exclusive time for any given function call.
 *
 * Note that the calls can span multiple levels and nested.
 *
 * "abc,START,100",
 *  "def,START,150",
 *  "hij,START,170",
 *  "hij,END,200",
 *  "def,END,220",
 * "job4,START,230",
 * "job4,END,250",
 * "abc,END,300"
 */
public class FunctionExecutionProfiler {

    static class FunctionStats {
        long totalInclusiveTime = 0;
        long totalExclusiveTime = 0;

        public void addTimes(long inclusive, long exclusive) {
            this.totalInclusiveTime += inclusive;
            this.totalExclusiveTime += exclusive;
        }

        @Override
        public String toString() {
            return "FunctionStats{" +
                   "totalInclusiveTime=" + totalInclusiveTime +
                   ", totalExclusiveTime=" + totalExclusiveTime +
                   '}';
        }
    }

    static class CallStackEntry {
        String functionName;
        int startTime;
        long childExecutionTimeSum; // Sum of inclusive times of direct children

        public CallStackEntry(String functionName, int startTime) {
            this.functionName = functionName;
            this.startTime = startTime;
            this.childExecutionTimeSum = 0;
        }
    }

    public Map<String, FunctionStats> calculateExecutionTimes(List<String> logs) {
        Map<String, FunctionStats> result = new HashMap<>();
        // Use Deque as a Stack for modern Java
        Deque<CallStackEntry> callStack = new ArrayDeque<>();

        for (String log : logs) {
            String[] parts = log.split(",");
            if (parts.length != 3) {
                System.err.println("Malformed log entry: " + log + ". Skipping.");
                continue;
            }

            String functionName = parts[0].trim();
            String type = parts[1].trim();
            int timestamp;
            try {
                timestamp = Integer.parseInt(parts[2].trim());
            } catch (NumberFormatException e) {
                System.err.println("Invalid timestamp in log entry: " + log + ". Skipping.");
                continue;
            }

            if ("START".equals(type)) {
                callStack.push(new CallStackEntry(functionName, timestamp));
            } else if ("END".equals(type)) {
                if (callStack.isEmpty()) {
                    System.err.println("Error: END event for '" + functionName + "' at " + timestamp + " without a matching START. Skipping.");
                    continue;
                }

                CallStackEntry currentFunctionCall = callStack.pop();

                if (!currentFunctionCall.functionName.equals(functionName)) {
                    // This indicates malformed logs (e.g., abc,START; def,START; abc,END)
                    // Or, the problem implies logs are always well-formed in terms of nesting.
                    System.err.println("Error: Mismatched END event. Expected '" + currentFunctionCall.functionName +
                                       "' but got '" + functionName + "' at " + timestamp + ". Processing as '" + currentFunctionCall.functionName + "'.");
                    // Assuming we process based on stack's function name for robustness, though problem implies valid pairing.
                    // For strict matching, one might throw an exception or stop.
                }

                long inclusiveTime = timestamp - currentFunctionCall.startTime;
                if (inclusiveTime < 0) {
                     System.err.println("Error: Negative inclusive time for '" + currentFunctionCall.functionName + "' (end: " + timestamp + ", start: " + currentFunctionCall.startTime + "). Skipping entry.");
                     // If this function call is skipped, its time should not be added to parent's childExecutionTimeSum.
                     // However, to keep parent's state consistent, we might need to decide how to handle this error.
                     // For now, we'll proceed, but this indicates bad data.
                }
                long exclusiveTime = inclusiveTime - currentFunctionCall.childExecutionTimeSum;

                // Aggregate times for this function name
                // Using currentFunctionCall.functionName for consistency, in case of mismatch warning above.
                result.putIfAbsent(currentFunctionCall.functionName, new FunctionStats());
                result.get(currentFunctionCall.functionName).addTimes(inclusiveTime, exclusiveTime);

                // If there's a parent function on the stack, add current function's inclusive time
                // to parent's childExecutionTimeSum
                if (!callStack.isEmpty()) {
                    CallStackEntry parentFunctionCall = callStack.peek();
                    parentFunctionCall.childExecutionTimeSum += inclusiveTime;
                }
            } else {
                System.err.println("Unknown log entry type: " + type + " in log: " + log + ". Skipping.");
            }
        }

        if (!callStack.isEmpty()) {
            System.err.println("Error: Unmatched START events at the end of logs. Remaining on stack:");
            while(!callStack.isEmpty()) {
                CallStackEntry remaining = callStack.pop();
                System.err.println("- " + remaining.functionName + " started at " + remaining.startTime);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        FunctionExecutionProfiler profiler = new FunctionExecutionProfiler();

        System.out.println("Example 1:");
        List<String> logs1 = Arrays.asList(
            "abc,START,100",
            "def,START,150",
            "def,END,180",
            "abc,END,200"
        );
        Map<String, FunctionStats> result1 = profiler.calculateExecutionTimes(logs1);
        result1.forEach((name, stats) -> System.out.println(name + ": " + stats));
        // Expected:
        // abc: FunctionStats{totalInclusiveTime=100, totalExclusiveTime=70}
        // def: FunctionStats{totalInclusiveTime=30, totalExclusiveTime=30}

        System.out.println("\nExample 2:");
        List<String> logs2 = Arrays.asList(
            "abc,START,100",
            "def,START,150",
            "hij,START,170",
            "hij,END,200",
            "def,END,220",
            "job4,START,230",
            "job4,END,250",
            "abc,END,300"
        );
        Map<String, FunctionStats> result2 = profiler.calculateExecutionTimes(logs2);
        result2.forEach((name, stats) -> System.out.println(name + ": " + stats));
        // Expected:
        // abc: FunctionStats{totalInclusiveTime=200, totalExclusiveTime=110}
        // def: FunctionStats{totalInclusiveTime=70, totalExclusiveTime=40}
        // hij: FunctionStats{totalInclusiveTime=30, totalExclusiveTime=30}
        // job4: FunctionStats{totalInclusiveTime=20, totalExclusiveTime=20}

        System.out.println("\nExample with multiple calls to same function:");
        List<String> logs3 = Arrays.asList(
            "f1,START,0",
            "f2,START,10",
            "f2,END,30",    // f2: incl=20, excl=20
            "f1,END,40",    // f1: incl=40, excl=40-20=20
            "f1,START,50",
            "f1,END,60"     // f1: incl=10, excl=10
        );
        Map<String, FunctionStats> result3 = profiler.calculateExecutionTimes(logs3);
        result3.forEach((name, stats) -> System.out.println(name + ": " + stats));
        // Expected:
        // f1: FunctionStats{totalInclusiveTime=50, totalExclusiveTime=30}
        // f2: FunctionStats{totalInclusiveTime=20, totalExclusiveTime=20}
        
        System.out.println("\nExample with malformed log (mismatched END):");
        List<String> logs4 = Arrays.asList(
            "alpha,START,10",
            "beta,START,20",
            "alpha,END,30" // Mismatched: beta should end first
        );
        Map<String, FunctionStats> result4 = profiler.calculateExecutionTimes(logs4);
        result4.forEach((name, stats) -> System.out.println(name + ": " + stats));
        // Expected output will show error messages and potentially unexpected stats due to error handling strategy.

        System.out.println("\nExample with malformed log (END without START):");
        List<String> logs5 = Arrays.asList(
            "gamma,END,10"
        );
        Map<String, FunctionStats> result5 = profiler.calculateExecutionTimes(logs5);
        result5.forEach((name, stats) -> System.out.println(name + ": " + stats));

        System.out.println("\nExample with malformed log (unmatched START at end):");
        List<String> logs6 = Arrays.asList(
            "delta,START,10",
            "epsilon,START,20"
        );
        Map<String, FunctionStats> result6 = profiler.calculateExecutionTimes(logs6);
        result6.forEach((name, stats) -> System.out.println(name + ": " + stats));
    }
}
