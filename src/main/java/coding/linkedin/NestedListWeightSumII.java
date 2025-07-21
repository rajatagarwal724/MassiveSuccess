package coding.linkedin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class NestedListWeightSumII {
    interface NestedInteger {
//    // Constructor initializes an empty nested list.
//    public NestedInteger();
//
//    // Constructor initializes a single integer.
//    public NestedInteger(int value);

        // @return true if this NestedInteger holds a single integer, rather than a nested list.
        public boolean isInteger();

        // @return the single integer that this NestedInteger holds, if it holds a single integer
        // The result is undefined if this NestedInteger holds a nested list
        public Integer getInteger();

        // Set this NestedInteger to hold a single integer.
        public void setInteger(int value);

        // Set this NestedInteger to hold a nested list and adds a nested integer to it.
        public void add(NestedListWeightSum.NestedInteger ni);

        // @return the nested list that this NestedInteger holds, if it holds a nested list
        // The result is undefined if this NestedInteger holds a single integer
        public List<NestedInteger> getList();
    }

    static class Pair {
        int elemValue;
        int depth;

        public Pair(int elemValue, int depth) {
            this.elemValue = elemValue;
            this.depth = depth;
        }
    }

    public int depthSumInverse(List<NestedInteger> nestedList) {
        Queue<NestedInteger> queue = new LinkedList<>();
        List<Pair> elemsByDepth = new ArrayList<>();

        queue.addAll(nestedList);

        int depth = 1;

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                var node = queue.poll();
                if (node.isInteger()) {
                    System.out.println("Depth: " + depth + " elem val: " + node.getInteger());
                    elemsByDepth.add(new Pair(node.getInteger(), depth));
                } else {
                    queue.addAll(node.getList());
                }
            }
            depth++;
        }

        int finalDepth = depth;
        System.out.println("Max Depth: " + finalDepth);
        return elemsByDepth.stream().mapToInt(pair -> pair.elemValue * (finalDepth - pair.depth + 1)).sum();
    }

    public static void main(String[] args) {

    }
}
