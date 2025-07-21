//package coding.linkedin;
//
//import java.util.Comparator;
//import java.util.TreeSet;
//
//class MaxStack {
//
//    private final TreeSet<int[]> stack;
//    private final TreeSet<int[]> sortedByValueSet;
//    private int count;
//
//    public MaxStack() {
//
//        Comparator<int[]> comp = (a, b) -> a[0] == b[0] ? a[1] - b[1]: a[0] - b[0];
//
//        this.stack = new TreeSet<>((a, b) -> a[0] == b[0] ? a[1] - b[1] : a[0] - b[0]);
//        this.sortedByValueSet = new TreeSet<>(
//                (a, b) -> a[0] == b[0] ? a[1] - b[1] : a[0] - b[0]
//        );
//        this.count = 0;
//    }
//
//    public void push(int x) {
//        this.stack.add(new int[] {count, x});
//        this.sortedByValueSet.add(new int[] {x, count});
//        count++;
//    }
//
//    public int pop() {
//        var stackPop = stack.pollLast();
//        sortedByValueSet.remove(new int[] {stackPop[1], stackPop[0]});
//        return stackPop[1];
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
//        var lastMaxPair = sortedByValueSet.pollLast();
//        stack.remove(new int[] {lastMaxPair[1], lastMaxPair[0]});
//        return lastMaxPair[0];
//    }
//
//    public static void main(String[] args) {
//        MaxStack_1 stk = new MaxStack_1();
//        stk.push(5);   // [5] the top of the stack and the maximum number is 5.
//        stk.push(1);   // [5, 1] the top of the stack is 1, but the maximum is 5.
//        stk.push(5);   // [5, 1, 5] the top of the stack is 5, which is also the maximum, because it is the top most one.
//        System.out.println(stk.top());     // return 5, [5, 1, 5] the stack did not change.
//        System.out.println(stk.popMax());  // return 5, [5, 1] the stack is changed now, and the top is different from the max.
//        System.out.println(stk.top());     // return 1, [5, 1] the stack did not change.
//        System.out.println(stk.peekMax()); // return 5, [5, 1] the stack did not change.
//        System.out.println(stk.pop());     // return 1, [5] the top of the stack and the max element is now 5.
//        System.out.println(stk.top());     // return 5, [5] the stack did not change.
//    }
//}
