//package coding.linkedin;
//
//import jakarta.validation.constraints.Max;
//
//import java.util.TreeMap;
//import java.util.TreeSet;
//
//public class MaxStack {
//
//    private TreeSet<int[]> stack;
//    private TreeSet<int[]> sortedByValueSet;
//    private int currentIndex = 0;
//
//    public MaxStack() {
//        this.stack = new TreeSet<>((a, b) -> {
//            if (a[0] == b[0]) {
//                return a[1] - b[1];
//            }
//            return a[0] - b[0];
//        });
//        this.sortedByValueSet = new TreeSet<>(
//                (a, b) -> {
//                    if (a[0] == b[0]) {
//                        return a[1] - b[1];
//                    }
//                    return a[0] - b[0];
//                }
//        );
//    }
//
//    public void push(int x) {
//        stack.add(new int[] {currentIndex, x});
//        sortedByValueSet.add(new int[] {x, currentIndex});
//        currentIndex++;
//    }
//
//    public int pop() {
//        var last = stack.getLast();
//        var index = last[0];
//        var value = last[1];
//
//        sortedByValueSet.remove(new int[] {value, index});
//        stack.pollLast();
//        return value;
//    }
//
//    public int top() {
//        return stack.last()[1];
//    }
//
//    public int peekMax() {
//        return sortedByValueSet.last()[0];
//    }
//
//    public int popMax() {
//        var lastMax = sortedByValueSet.last();
//        var value = lastMax[0];
//        var index = lastMax[1];
//
//        stack.remove(new int[] {index, value});
//        sortedByValueSet.pollLast();
//        return value;
//    }
//
//    public static void main(String[] args) {
//        var maxStack = new MaxStack();
//
//        maxStack.push(5);
//        maxStack.push(1);
//        maxStack.push(5);
//
//        System.out.println(maxStack.top());
//        System.out.println(maxStack.popMax());
//        System.out.println(maxStack.top());
//    }
//}
