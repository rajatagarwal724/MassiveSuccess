package coding.linkedin;

import java.util.*;

public class MergeIntervals {

    class Interval {
        int start;
        int end;

        public Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    public List<Interval> merge(List<Interval> intervals) {
        if (intervals.size() < 2) {
            return intervals;
        }
        List<Interval> mergedIntervals = new LinkedList<Interval>();

        Collections.sort(intervals, (a, b) -> a.start - b.start);

        Iterator<Interval> intervalIterator = intervals.iterator();
        var interval = intervalIterator.next();

        var start = interval.start;
        var end = interval.end;

        while (intervalIterator.hasNext()) {
            var nextInterval = intervalIterator.next();

            if (nextInterval.start <= end) {
                end = Math.max(end, nextInterval.end);
            } else {
                mergedIntervals.add(new Interval(start, end));
                start = nextInterval.start;
                end = nextInterval.end;
            }
        }
        mergedIntervals.add(new Interval(start, end));
        return mergedIntervals;
    }

    public static void main(String[] args) {
        var sol = new MergeIntervals();


    }
}
